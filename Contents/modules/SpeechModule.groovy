import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.JPanel

void onBuildStateChanged(Build build) {

    onAModule.getConfig().ifSome { config ->
        if (build.anyStateFetchError || build.building || !build.stateChanged || !build.buildState || !config.popupEnabled) return

        String message = "Build - $build.name - changed state from $build.lastBuildState to $build.buildState"
        [config.speechCmd, message].execute()
        Thread.sleep(5000)
    }


}

void readConfigElement(slurpers, config) {
    config.speechEnabled = (slurpers?.user?.speechEnabled?.text()?: "true").toBoolean()
    config.speechCmd = slurpers?.user?.speechCmd?.text()?:"say"
}

void writeConfigElement(builders, config) {
    builders?.user?.speechEnabled config.speechEnabled
    builders?.user?.speechCmd config.speechCmd
}

Option<List<JPanel>> configElementPanel(config) {
    def builder = new SwingBuilder()
    Option.some([
            builder.panel() {
                borderLayout()
                label(text: "speechEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: config.speechEnabled, actionPerformed: {e -> config.speechEnabled = e.source.selected})
            },
            builder.panel() {
                borderLayout()
                label(text: "speechCmd", constraints: BorderLayout.WEST)
                textField(text: config.speechCmd, focusLost: {e -> config.speechCmd = e.source.text})
            }])
}