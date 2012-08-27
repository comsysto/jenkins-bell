import groovy.beans.Bindable
import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.event.ListSelectionListener
import javax.swing.JFrame
import java.awt.EventQueue

/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 27/08/2012
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
class ConfigModule extends AbstractModule {

    static void main(String[] args) {
        new ConfigModule().openFrame()
    }

    App app
    JFrame frame

    ConfigModule(App app) {
        this.app = app
    }

    @Override
    void onCommand(String cmd, Map<String, Object> args) {
        if (cmd == "config") {
            EventQueue.invokeLater{
                openFrame()
            }
        }
    }

    void openFrame(boolean exitOnClose = false) {

        if(frame) return


        Config config = app.loadConfig()

        def closeFrame = {->
            frame.dispose()
            frame = null
            if(exitOnClose) System.exit(0)
        }

        def saveConfigAndRestart = {->
            app.storeConfig(config)
            app.restart()
            closeFrame()
        }

        SwingBuilder swing = new SwingBuilder()
        frame = swing.frame(id: "frame", title: "JenkinsBell - Config", size: [400, 400], show: true) {
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
                    textField(text: bind(source: config, sourceProperty: "speechCmd", mutual: true))
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

    }



}
