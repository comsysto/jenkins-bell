import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.event.ActionListener
import java.awt.event.WindowEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JTextArea
import java.awt.event.WindowAdapter
import javax.swing.JScrollPane
import java.awt.Color

class PopupModule extends AbstractModule {

    List<Build> buildsToShow = []
    JFrame currentFrame = null


    @Override
    void onBuildChangedState(Build build) {
        synchronized(buildsToShow){
            buildsToShow.add(build)
            tryOpenFrame()
        }
    }

    void tryOpenFrame(){
        synchronized(buildsToShow){
            if(currentFrame == null && !buildsToShow.empty){
                openFrame(buildsToShow.remove(0))
            }
        }
    }

    JFrame openFrame(Build build) {

        def authors = !build.authors ? "" :
            """
            |authors:
            |${build.authors.collect {"  * " + it}.join("\n")}
            |
            """.stripMargin()

        def changes = !build.changes ? "" :
            """
            |changes:
            |${build.changes.collect {"  * " + it}.join("\n")}
            |
            """.stripMargin()

        def text =
            """
            |$build.name
            |------------------------------
            |$build.job@$build.server
            |$build.date
            |------------------------------
            |$authors
            |$changes
            |------------------------------
            |$build.lastBuildState -> $build.buildState
            """.stripMargin()

        JFrame frame = new JFrame("JenkinsBell Notification");
        def pane = new JTextArea(text)
        pane.editable = false
        pane.background = build.stateSuccess ? new Color(0xCCFF99) : new Color(0xFF9999);
        frame.contentPane.layout = new BorderLayout()
        frame.contentPane.add(new JScrollPane(pane))
        def openButton = new JButton("open")
        openButton.addActionListener({ action ->
            openInBrowser(build)
        }.asType(ActionListener))
        frame.getContentPane().add(openButton, BorderLayout.NORTH)
        frame.setSize(400, 400)
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)

        def windowClosed = {
            tryOpenFrame()
        }

        def closeWindow = {
            if (!frame.isVisible()) return
            frame.setVisible(false)
            frame.dispose()
            windowClosed()
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            void windowDeactivated(WindowEvent e) {
                if (agent.config.afterLoseFocusClosePopup) {
                    Thread.start({
                        Thread.sleep(500)
                        closeWindow()
                    })
                }
            }

            @Override
            void windowClosing(WindowEvent e) {
                closeWindow()
            }
        })


        frame.setVisible(true)
        frame.setAlwaysOnTop(true)
        frame
    }

    private def openInBrowser(Build build) {
        Desktop.getDesktop().browse(build.buildUri)
    }


}
