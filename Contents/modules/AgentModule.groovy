// monitor


import groovy.transform.Field

import static Option.none
import javax.swing.JPanel
import groovy.swing.SwingBuilder
import java.awt.BorderLayout

@Field
Object loopLock = new Object()

@Field
boolean isStopped = false

@Field
boolean isLooping = false

@Field
Option<List<Build>> builds = none()

@Field
Map<String, Closure> menu = [:]

@Field
Option menuContribution = none()

menuContribution = onAModule.contributeToMenu(menu)

void monitor() {
    synchronized (loopLock) {
        if (isLooping) return;
        println "--LOOPING--"
        isLooping = true
        initBuilds()
        onEachModule.onStartMonitoring()

        updateBuildsMenuContribution()

        while (!isStopped) {
            poll()
            loopLock.wait(onAModule.getConfig().defaultOrMap(60000) {it.pollIntervalMillis})
        }
        isLooping = false
        loopLock.notifyAll()
    }
    onEachModule.onStopMonitoring()
}

private void updateBuildsMenuContribution() {

    def createOpenAction = {url -> onAModule.openInBrowser(url)}

    def createBuildMap = {build ->
        def label = build.stateSuccess ? "$build.name" : "!> $build.name (${build.buildState})"

        def subMenu = [
                "Go to Last Build": {-> createOpenAction("http://$build.server/job/$build.job/lastBuild")},
                "Go to Job": {-> createOpenAction("http://$build.server/job/$build.job")},
                "Start Build": {->
                    onAModule.startJob(build)

                }
        ]

        [(label): subMenu]
    }


    menuContribution.ifSome { contribution ->

        menu.clear()
        builds.ifSome {
            def allBuilds = it.sort{it.name}
            def groups = allBuilds.collect {it.groups}.flatten().unique().sort()

            def topLevelBuilds = allBuilds.findAll {!it.groups}.collectEntries {createBuildMap(it)}
            menu.putAll(topLevelBuilds)

            menu["seperator1"] = null

            groups.each { group ->
                def groupMenus = allBuilds.findAll {group in it.groups}.collect { build ->
                    createBuildMap(build)
                }

                menu[group] = groupMenus.sum()
            }
        }

        contribution.update()

    }


}


private void initBuilds() {
    builds = onAModule.getConfig().map { config ->
        config.buildConfigs.findAll {it.name && it.job && it.server}.collect {

            def groups = (it.groups?:"").split("[,;: ]").findAll {it?.trim()}

            def build = new Build(name: it.name, job: it.job, server: it.server, groups: groups)
            onAModule.stateFile(build).ifSome {
                def text = it.text.trim()
                def states = text?.split(":")
                if (states){
                    if(states[0]){
                        build.buildState = BuildState.forName(states[0])
                    }
                    if(states.size() > 1 && states[1]){
                        build.lastBuildState = BuildState.forName(states[1])
                    }
                }
            }
            build
        }
    }
}

void stopMonitoring() {
    synchronized (loopLock) {
        if (!isLooping) return
        isStopped = true
        loopLock.notifyAll()
        while (isLooping) {
            loopLock.wait(100)
        }
        isStopped = false
        builds = none()
        println "--STOPPED LOOPING--"
    }
}

void startMonitoring() {
    Thread.start {
        monitor()
    }
}

void restartMonitoring() {
    stopMonitoring()
    startMonitoring()
}

Option<Boolean> isLooping() {
    synchronized (loopLock) {
        Option.some(isLooping)
    }
}

private def void poll() {
    println "--POLL @ ${new Date()}--"
    builds.ifSome {
        withCatch {-> onEachModule.onBeginPoll() }
        it.each {
            pollBuild(it)
        }
        withCatch {-> onEachModule.onEndPoll() }
    }
}

private def withCatch(Closure c) {
    try {
        c()
    } catch (Exception e) {
        e.printStackTrace()
    }
}

Option<File> stateFile(Build build) {
    onAModule.configFile("state/$build.fileName")
}


def void pollBuild(Build build) {
    onAModule.updateBuild(build)

    if (build.stateChanged || build.buildingChanged) {
        println("STATE CHANGE: $build.name $build.stateDescriptionWithColor [stateChanged: $build.stateChanged, buildingChanged: $build.buildingChanged]")

        onAModule.stateFile(build).ifSome {
            it.text = (build.buildState?.name()?:"") + ":" + (build.lastBuildState?.name()?:"")
        }
        withCatch {-> onEachModule.onBuildStateChanged(build)}
    }

}

void onBuildStateChanged(Build build){
    updateBuildsMenuContribution()
}

synchronized Option<List<Build>> getBuilds() {
    builds
}

Option<Map<BuildState, Integer>> getBuildStateCount() {
    builds.map {
        it.countBy {it.buildState}
    }
}

Option<BuildState> getHighestBuildState() {
    builds.map { Option.option(it.max {build -> build.buildState}?.buildState)}.flatten()
}

void readConfigElement(slurper, config) {
    config.pollIntervalMillis = (slurper?.pollIntervalMillis ?: "60000").toInteger()
    config.buildConfigs = (slurper?.builds?.build?:[]).collect{ build ->
        [
                name: build.name.text(),
                server: build.server.text(),
                job:  build.job.text(),
                groups: build.groups.text()
        ]
    }
}

void writeConfigElement(builder, config) {
    builder.pollIntervalMillis config.pollIntervalMillis
    builder.builds{
        (config.buildConfigs?:[]).each {build ->
            builder.build{
                name build.name
                server build.server
                job build.job
                groups build.groups?:""
            }
        }
    }
}

Option<List<JPanel>> configElementPanel(config) {
    def builder = new SwingBuilder()
    Option.some(
            [
                    builder.panel(border: builder.titledBorder(title: "BuildConfig"), minimumSize: [300, 300], preferredSize: [300, 300]) {
                        borderLayout()
                        scrollPane {
                            table(id: 'buildConfigTable') {
                                tableModel(list: config.buildConfigs) {
                                    propertyColumn(header: "Name", propertyName: 'name')
                                    propertyColumn(header: "Server", propertyName: 'server')
                                    propertyColumn(header: "Job", propertyName: 'job')
                                    propertyColumn(header: "Groups", propertyName: 'groups')
                                }
                            }
                        }


                        panel(constraints: BorderLayout.SOUTH) {
                            gridLayout(rows: 1, columns: 2)
                            button(text: "add", actionPerformed: {e ->
                                config.buildConfigs.add([name: "", server: "", job: "", groups: ""])
                                builder.buildConfigTable.model.fireTableDataChanged()
                                def index = config.buildConfigs.size() - 1
                                builder.buildConfigTable.selectionModel.setSelectionInterval(index, index)
                            })
                            button(text: "remove", actionPerformed: {e ->
                                config.buildConfigs.remove(builder.buildConfigTable.selectionModel.leadSelectionIndex)
                                builder.buildConfigTable.model.fireTableDataChanged()
                            })
                        }
                    },
                    builder.panel() {
                        borderLayout()
                        label(text: "pollIntervalMillis", constraints: BorderLayout.WEST)
                        spinner(value: config.pollIntervalMillis, stateChanged: {e -> config.pollIntervalMillis = e.source.value})
                    }
            ]
    )
}

