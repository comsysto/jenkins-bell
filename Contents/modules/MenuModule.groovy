import groovy.transform.Field

import java.awt.event.ActionListener
import java.awt.*
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

void openInBrowser(Build build) {
    Desktop.getDesktop().browse(build.buildUri)
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




