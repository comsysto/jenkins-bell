import groovy.swing.SwingBuilder
import groovy.transform.Field

import java.awt.BorderLayout
import java.awt.EventQueue
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JScrollPane

@Field
JFrame frame

@Field
Option menuContribution

void start(){
    menuContribution = onAModule.contributeToMenu(["Open Configuration ...": {-> openConfigWindow(false)}])
}

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
        frame = swing.frame(id: "frame", title: "JenkinsBell - Config", location: [200, 200], size: [600, 600], show: true, windowClosing: {e -> closeFrame()}) {
            borderLayout()
            scrollPane(constraints: BorderLayout.CENTER, horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED) {
                panel() {
                    borderLayout()
                    panel(id: "configPanelParent", constraints: BorderLayout.NORTH) {
                        boxLayout(axis: BoxLayout.PAGE_AXIS)
                        def panels = onEachModule.configElementPanel(config)*.toList().flatten()
                        panels.each {
                            widget(it)
                            glue(minimumSize: [10, 10], preferredSize: [10, 10])
                        }
                    }
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


