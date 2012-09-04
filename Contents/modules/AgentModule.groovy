// monitor


import groovy.transform.Field

import static Option.none

@Field
Object loopLock = new Object()

@Field
boolean isStopped = false

@Field
boolean isLooping = false

@Field
Option<List<Build>> builds = none()

@Field
Map<String, Closure> labelsAndActions = [:]

@Field
Option menuContribution = none()

menuContribution = onAModule.contributeToMenu(labelsAndActions)

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
    menuContribution.ifSome { contribution ->

        labelsAndActions.clear()
        builds.ifSome {
            it.sort {it.name}.each { build ->
                def label = !build.fetchError && build.stateSuccess ? "Go to $build.name" : "!> Go to $build.name (${build.fetchError ? "FETCH ERROR" : build.buildState})"
                def action = {-> onAModule.openInBrowser(build)}

                labelsAndActions.put(label, action)
            }
        }

        contribution.update()

    }


}


private void initBuilds() {
    builds = onAModule.getConfig().map { config ->
        config.buildConfigs.findAll {it.name && it.job && it.server}.collect {
            def build = new Build(name: it.name, job: it.job, server: it.server)
            onAModule.stateFile(build).ifSome {
                def text = it.text.trim()
                if (text)
                    build.buildState = BuildState.forName(text)
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
        it.each {
            withCatch {-> onEachModule.onBeginPoll() }

            pollBuild(it)
            withCatch {-> onEachModule.onEndPoll() }
        }
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
    build.fetch()

    if (build.stateChanged) {
        println("STATE CHANGE: $build.name $build.stateDescriptionWithColor")

        onEachModule.stateFile(build).each {
            it.text = build.buildState
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
