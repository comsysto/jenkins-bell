import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.JPanel

@Override
void onBuildChangedState(Build build) {

    onAModule.getConfig().ifSome { config ->
        if (build.anyStateFetchError || !config.speechEnabled) return
        String message = "Build - $build.name - changed state from $build.lastBuildState to $build.buildState"
        [config.speechCmd, message].execute()
        Thread.sleep(5000)
    }


}

void readConfigElement(slurper, config) {
    config.speechEnabled = (slurper?.speechEnabled ?: "true").toBoolean()
    config.speechCmd = slurper?.speechCmd?:"say"
}

void writeConfigElement(builder, config) {
    builder.speechEnabled config.speechEnabled
    builder.speechCmd config.speechCmd
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