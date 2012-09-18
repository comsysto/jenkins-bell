import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.JPanel
import java.awt.Desktop
import groovy.transform.Field

if(!"Mac OS X".equals(System.getProperty("os.name"))){
    println "Not Mac OS X. Operation System: ${System.getProperty("os.name")}"
    return false;
}

if(! System.getProperty("os.version")?.startsWith("10.8")){
    println "Requires Mac OS X Version 10.8.x. Version: ${System.getProperty("os.version")}"
    return false;
}


void onBuildStateChanged(Build build) {

    onAModule.getConfig().ifSome { config ->
        if (build.anyStateFetchError || build.building || !build.stateChanged || !build.buildState || !config.notificationCenterEnabled) return

        def command = [
                new File("../Resources/JenkinsBellNotifier.app/Contents/MacOS/JenkinsBellNotifier").canonicalPath,
//                "-title", "JenkinsBell",
                "-title", "${build.name}",
                "-group", "$build.name",
                "-subtitle", "${build.buildState}",
                "-message", "Changed state to ${build.buildState} from ${build.lastBuildState}",
                "-open", "http://$build.server/job/$build.job/lastBuild"
        ]
        println("--Notifier Command:\n$command")
        command.execute()
    }
}

void readConfigElement(slurper, config) {
    config.notificationCenterEnabled = (slurper?.notificationCenterEnabled ?: "false").toBoolean()
}

void writeConfigElement(builder, config) {
    builder.notificationCenterEnabled config.notificationCenterEnabled
}

Option<List<JPanel>> configElementPanel(config) {
    def builder = new SwingBuilder()
    Option.some([
            builder.panel() {
                borderLayout()
                label(text: "notificationCenterEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: config.notificationCenterEnabled, actionPerformed: {e -> config.notificationCenterEnabled = e.source.selected})
            }
    ])
}
