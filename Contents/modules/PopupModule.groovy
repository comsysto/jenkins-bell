import groovy.transform.Field

import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

@Field List<Build> buildsToShow = []
@Field JFrame currentFrame = null

void onBuildChangedState(Build build) {
    if (build.anyStateFetchError) return
    synchronized (buildsToShow) {
        buildsToShow.add(build)
        tryOpenFrame()
    }
}

void onStopMonitoring() {
    synchronized (buildsToShow) {
        buildsToShow.clear()
    }
}

private void tryOpenFrame() {
    synchronized (buildsToShow) {
        if (currentFrame == null && !buildsToShow.empty) {
            openPopupWindow(buildsToShow.remove(0))
        }
    }
}

void openPopupWindow(Build build) {

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
        onAModule.openInBrowser(build)
    }.asType(ActionListener))
    frame.getContentPane().add(openButton, BorderLayout.NORTH)

    def closeWindow = {
        synchronized (buildsToShow) {
            if (currentFrame != null) {
                currentFrame.setVisible(false)
                currentFrame.dispose()
                currentFrame = null
            }
            tryOpenFrame()
        }
    }

    JPanel closeButtonPanel = new JPanel(new GridLayout(1, 2))

    JButton closeButton = new JButton("close")
    closeButton.addActionListener({e -> closeWindow()} as ActionListener)
    closeButtonPanel.add(closeButton)

    JButton closeAllButton = new JButton("close all")
    closeAllButton.addActionListener({e ->
        synchronized (buildsToShow) {
            buildsToShow.clear()
        }
        closeWindow()
    } as ActionListener)
    closeButtonPanel.add(closeAllButton)

    frame.getContentPane().add(closeButtonPanel, BorderLayout.SOUTH)


    frame.setSize(400, 400)
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)

    frame.addWindowListener(new WindowAdapter() {
        @Override
        void windowDeactivated(WindowEvent e) {
            if (kernel.required.getConfig().afterLoseFocusClosePopup) {
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

    currentFrame = frame
    frame.setVisible(true)
    frame.setAlwaysOnTop(true)
}


