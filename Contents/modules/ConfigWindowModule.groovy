import groovy.swing.SwingBuilder
import groovy.transform.Field

import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JFrame

@Field
JFrame frame

@Field
Option menuContribution

menuContribution = onAModule.contributeToMenu(["Open Configuration ...": {-> openConfigWindow(false)}])



void openConfigWindow(boolean exitOnClose = false) {

    if (frame) return


    def configOption = onAModule.getConfig()
    if(configOption.isNone()) return

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
        panel(constraints: BorderLayout.CENTER) {
            boxLayout(axis: BoxLayout.PAGE_AXIS)
            panel(border: titledBorder(title: "BuildConfig")) {
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
                        config.buildConfigs.add(new BuildConfig())
                        swing.buildConfigTable.model.fireTableDataChanged()
                        def index = config.buildConfigs.size() - 1
                        swing.buildConfigTable.selectionModel.setSelectionInterval(index, index)
                    })
                    button(text: "remove", actionPerformed: {e ->
                        config.buildConfigs.remove(swing.buildConfigTable.selectionModel.leadSelectionIndex)
                        swing.buildConfigTable.model.fireTableDataChanged()
                    })
                }
            }

            panel() {
                borderLayout()
                label(text: "pollIntervalMillis", constraints: BorderLayout.WEST)
                spinner(value: bind(source: config, "pollIntervalMillis", mutual: true))
            }
            panel() {
                borderLayout()
                label(text: "speechEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: bind(source: config, sourceProperty: "speechEnabled"), actionPerformed: {e -> config.speechEnabled = e.source.selected})

            }

            panel() {
                borderLayout()
                label(text: "speechCmd", constraints: BorderLayout.WEST)
                textField(text: bind(source: config, sourceProperty: "speechCmd", mutual: false), focusLost: {e -> config.speechCmd = e.source.text})
            }

            panel() {
                borderLayout()
                label(text: "popupEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: bind(source: config, sourceProperty: "popupEnabled"), actionPerformed: {e -> config.popupEnabled = e.source.selected})
            }

            panel() {
                borderLayout()
                label(text: "afterLoseFocusClosePopup", constraints: BorderLayout.WEST)
                checkBox(selected: bind(source: config, sourceProperty: "afterLoseFocusClosePopup"), actionPerformed: {e -> config.afterLoseFocusClosePopup = e.source.selected})
            }
            panel() {
                borderLayout()
                label(text: "trayEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: bind(source: config, sourceProperty: "trayEnabled"), actionPerformed: {e -> config.trayEnabled = e.source.selected})
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

void openConfigWindowAndExit(){
    openConfigWindow(true)
}


