import javax.swing.JComponent
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.RenderingHints
import java.awt.geom.Arc2D
import java.awt.Color
import javax.swing.JFrame
import java.awt.BorderLayout

import java.awt.Shape
import java.awt.geom.Area
import java.awt.image.BufferedImage
import com.apple.eawt.Application
import java.awt.Dimension
import java.awt.font.GlyphVector
import java.awt.Font
/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 25/08/2012
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
//@Grab("org.codehaus.griffon:gfxbuilder-core:0.6")
class Logo extends JComponent{

    def makeImage(){
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
        paintComponent(image.getGraphics())
        return image
    }

    Color bellColor = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0)

    private Area bell(int dx = 0, int dy = 0){
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


    private Area bellArea(){
        Arc2D innerArc = new Arc2D.Double()
        innerArc.setArc(-30, -35, 60, 140, 0, 180, Arc2D.CHORD)
        new Area(innerArc)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2 = g.create()
        try{
            //def transformation = AffineTransform.getScaleInstance(1, 1)
            def transformation = AffineTransform.getScaleInstance(getWidth() / 100d,  getHeight() / 100d)
            transformation.translate(50, 50)
            g2.transform(transformation)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.setColor(Color.white)
            g2.fill(bell(1, 1))
            if (bellColor != null){
                g2.setColor(bellColor)
                g2.fill(bellArea())
            }
            g2.setColor(Color.black)
            g2.fill(bell())

        }finally{
            g2.dispose()
        }



    }

    static void main(String[] args){
        JFrame frame = new JFrame()
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.contentPane.layout = new BorderLayout()
        def logo = new Logo()
        frame.contentPane.add(logo)
        frame.setSize(200, 200)
        frame.setVisible(true)

        int alphaIncrement = 8


        Logo dockLogo = new Logo()
        dockLogo.setFont(frame.getFont())
        dockLogo.setSize(256, 256)


        while(true){
            Color c = dockLogo.bellColor

            int newAlpha = c.getAlpha() + alphaIncrement
            if(newAlpha < 0 || newAlpha > 172){
                alphaIncrement *= -1
                newAlpha += alphaIncrement

            }
            dockLogo.bellColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), newAlpha)

            Application.application.setDockIconImage(dockLogo.makeImage())
            Application.application.requestUserAttention(true)
            Thread.sleep(100)

        }
    }
}
