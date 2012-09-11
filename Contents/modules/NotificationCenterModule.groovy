import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.JPanel
import java.awt.Desktop

void onBuildStateChanged(Build build) {
    onAModule.getConfig().ifSome { config ->
        if (build.anyStateFetchError || build.building || !build.stateChanged || !build.buildState || !config.notificationCenterEnabled || !config.notifierAppPath) return

        [
                "$config.notifierAppPath/Contents/MacOS/terminal-notifier",
                "-title", "JenkinsBell",
                "-group", "$build.name",
                "-message", "${build.name}: ${build.buildState}",
                "-open", "http://$build.server/job/$build.job/lastBuild"
        ].execute()
    }
}

void readConfigElement(slurper, config) {
    config.notificationCenterEnabled = (slurper?.notificationCenterEnabled ?: "false").toBoolean()
    config.notifierAppPath = slurper?.notifierAppPath?:"/Applications/terminal-notifier.app"
}

void writeConfigElement(builder, config) {
    builder.notificationCenterEnabled config.notificationCenterEnabled
    builder.notifierAppPath config.notifierAppPath
}

Option<List<JPanel>> configElementPanel(config) {
    def builder = new SwingBuilder()
    Option.some([
            builder.panel() {
                borderLayout()
                label(text: "notificationCenterEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: config.notificationCenterEnabled, actionPerformed: {e -> config.notificationCenterEnabled = e.source.selected})
            },
            builder.panel() {
                borderLayout()
                label(text: "terminal-notifier.app path", constraints: BorderLayout.WEST)
                textField(text: config.notifierAppPath, focusLost: {e -> config.notifierAppPath = e.source.text})
                button(text: "Download Page", constraints: BorderLayout.EAST, actionPerformed: {e -> Desktop.desktop.browse(new URI("https://github.com/alloy/terminal-notifier/downloads"))})
            }])
}