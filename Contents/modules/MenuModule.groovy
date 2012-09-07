import groovy.transform.Field

import java.awt.event.ActionListener
import javax.swing.JComponent
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.RenderingHints
import java.awt.geom.Arc2D
import java.awt.Color

import java.awt.Shape
import java.awt.geom.Area
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Image
import java.awt.Menu
import java.awt.MenuItem
import java.util.List

import static Option.some


@Field
final static COLOR_FOR_STATE = [
        (BuildState.FETCH_ERROR): Color.LIGHT_GRAY,
        (BuildState.UNSTABLE): Color.YELLOW,
        (BuildState.FAILURE): Color.RED,
].asImmutable()

@Field
private Logo logo;

@Field
def contributionList = [];

@Field
def controllerList = [];

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

Option<MenuController> createMenuController(Menu menu) {
    def controller = new MenuController(menu)
    controllerList << controller
    some(controller)
}

class MenuController {
    Menu menu

    MenuController(Menu menu) {
        this.menu = menu
    }

    void remove(){
        controllerList.remove(this)
    }
}

Option<MenuContribution> contributeToMenu(Map<String, Closure> labelsAndActions) {
    def contribution = new MenuContribution(actionAndLabels: labelsAndActions, updateClosure: this.&updateMenuControllers)
    contributionList << contribution
    updateMenuControllers()
    some(contribution)
}

void updateMenuControllers() {

    controllerList.each { controller ->
        controller.menu.removeAll()
        Menu menu = controller.menu

        contributionList.each { contribution ->

            if(menu.getItemCount() != 0)
                menu.addSeparator()

            contribution.actionAndLabels.each { entry ->
                MenuItem configItem = new MenuItem(entry.key)

                configItem.addActionListener({e ->
                    entry.value()
                } as ActionListener)

                menu.add(configItem)
            }

        }
    }
}

class MenuContribution {
    Map<String, Closure> actionAndLabels;
    Closure updateClosure

    void update() {
        updateClosure()
    }

    void remove() {
        contributionList.remove(this)
    }

}


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
    
    void writeIconImagesToDisk(){
        Logo logo = new Logo()
        def sizes = [512, 256, 128, 32, 16]
        sizes.each{
            logo.setSize(it, it)
            def image = logo.makeImage()
            ImageIO.write(image, "png", new File("../Resources/JenkinsBell-${it}.png"))
        }
    }

}



