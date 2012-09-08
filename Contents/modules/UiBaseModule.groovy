import groovy.swing.SwingBuilder
import groovy.transform.Field

import java.awt.geom.AffineTransform
import java.awt.geom.Arc2D
import java.awt.geom.Area
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JComponent
import java.awt.*

import static Option.some
import java.lang.reflect.UndeclaredThrowableException

@Field
final static COLOR_FOR_STATE = [
        (BuildState.FETCH_ERROR): Color.LIGHT_GRAY,
        (BuildState.UNSTABLE): Color.YELLOW,
        (BuildState.FAILURE): Color.RED,
].asImmutable()


@Field
private Logo logo;

// custom EventQueue implementation to handle Exceptions in the EDT
Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueue() {
    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable t) {
            handleEventQueueErrorInternal(t)
        }
    }
});

// bridge method to call from inner class to binding.
private void handleEventQueueErrorInternal(Throwable t){
    onAModule.handleError(t)
}

void handleError(Throwable t) {

    t.printStackTrace()

    if(t instanceof UndeclaredThrowableException){
        t = t.getCause()
    }

    EventQueue.invokeLater {
        def writer = new StringWriter()
        t.printStackTrace(new PrintWriter(writer))
        def stackTrace = writer.toString()
        SwingBuilder swing = new SwingBuilder()
        def frame = swing.frame(id: "frame", title: "JenkinsBell - Error", size: [800, 400]) {
            borderLayout()
            label(constraints: BorderLayout.NORTH, text: "Exception occured:")
            scrollPane(id: "sp", constraints: BorderLayout.CENTER) {
                textPane(id: "tp", editable: false, text: stackTrace, caretPosition: 0)
            }
            button(text: "close", constraints: BorderLayout.SOUTH, actionPerformed: {e -> swing.frame.dispose()})
        }
        frame.setVisible(true)
        frame.requestFocus()
        onAModule.requestForeground()
    }
}


synchronized Option<Image> createLogoImage(int size) {
    if (logo == null) {
        logo = new Logo()
    }

    logo.setSize(size, size)
    def color = onAModule.getHighestBuildState().defaultOrMap(null) {
        COLOR_FOR_STATE[it]
    }

    logo.bellColor = color
    some(logo.makeImage() as Image)
}

void openInBrowser(Build build) {
    openInBrowser(build.buildUri)
}

void openInBrowser(String url) {
    java.awt.Desktop.getDesktop().browse(new URI(url))
}


class Logo extends JComponent {

    def makeImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
        paintComponent(image.getGraphics())
        return image
    }

    Color bellColor = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0)

    private Area bell(int dx = 0, int dy = 0) {
        Arc2D outerArc = new Arc2D.Double()
        outerArc.setArc(-40, -40, 80, 160, 0, 180, Arc2D.CHORD)


        Arc2D bottomArc = new Arc2D.Double()
        bottomArc.setArc(-5, 37, 10, 10, 0, -180, Arc2D.CHORD)

        Shape bell = new Area(outerArc)
        bell.subtract(bellArea())
        bell.add(new Area(bottomArc))
        bell.transform(AffineTransform.getTranslateInstance(dx, dy))
        bell
    }


    private Area bellArea() {
        Arc2D innerArc = new Arc2D.Double()
        innerArc.setArc(-30, -35, 60, 140, 0, 180, Arc2D.CHORD)
        new Area(innerArc)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2 = g.create()
        try {
            //def transformation = AffineTransform.getScaleInstance(1, 1)
            def transformation = AffineTransform.getScaleInstance(getWidth() / 100d, getHeight() / 100d)
            transformation.translate(50, 50)
            g2.transform(transformation)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.setColor(Color.white)
            g2.fill(bell(1, 1))
            if (bellColor != null) {
                g2.setColor(bellColor)
                g2.fill(bellArea())
            }
            g2.setColor(Color.black)
            g2.fill(bell())

        } finally {
            g2.dispose()
        }

    }

    void writeIconImagesToDisk() {
        Logo logo = new Logo()
        def sizes = [512, 256, 128, 32, 16]
        sizes.each {
            logo.setSize(it, it)
            def image = logo.makeImage()
            ImageIO.write(image, "png", new File("../Resources/JenkinsBell-${it}.png"))
        }
    }

}
