import groovy.swing.SwingBuilder
import groovy.transform.Field

import java.awt.BorderLayout
import java.awt.EventQueue
import javax.swing.BoxLayout
import javax.swing.JFrame

@Field
JFrame frame

@Field
Option menuContribution

menuContribution = onAModule.contributeToMenu(["Open Configuration ...": {-> openConfigWindow(false)}])



void openConfigWindow(boolean exitOnClose = false) {

    if (frame) return

    EventQueue.invokeLater {
        def configOption = onAModule.getConfig()
        if (configOption.isNone()) return

        def config = configOption.value

        def closeFrame = {->
            if (!frame) return
            frame.dispose()
            this.@frame = null
            if (exitOnClose) System.exit(0)
        }

        def saveConfigAndRestart = {->
            onAModule.storeConfig(config)
            onAModule.restartMonitoring()
            closeFrame()
        }


        SwingBuilder swing = new SwingBuilder()
        frame = swing.frame(id: "frame", title: "JenkinsBell - Config", size: [800, 800], show: true, windowClosing: {e -> closeFrame()}) {
            borderLayout()
            panel(id: "configPanelParent", constraints: BorderLayout.CENTER) {
                boxLayout(axis: BoxLayout.PAGE_AXIS)
                def panels = onEachModule.configElementPanel(config)*.toList().flatten()
                panels.each {
                    configPanelParent.add(it)
                }
            }
            panel(constraints: BorderLayout.SOUTH) {
                gridLayout(rows: 1, columns: 2)
                button(text: "save", actionPerformed: {e -> saveConfigAndRestart()})
                button(text: "cancel", actionPerformed: {e -> closeFrame()})
            }


        }


        frame.requestFocus()
        onAModule.requestForeground()
    }

}

void openConfigWindowAndExit() {
    openConfigWindow(true)
}


