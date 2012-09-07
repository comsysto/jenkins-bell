import groovy.transform.Field

import java.awt.event.ActionListener
import java.awt.Menu
import java.awt.MenuItem
import java.util.List

import static Option.some
import java.awt.Toolkit
import java.awt.EventQueue
import java.awt.AWTEvent


@Field
def contributionList = [];

@Field
def controllerList = [];


Option<MenuController> createMenuController(Menu menu) {
    def controller = new MenuController(menu)
    controllerList << controller
    some(controller)
}

void openInBrowser(Build build) {
    openInBrowser(build.buildUri)
}

void openInBrowser(String url) {
    java.awt.Desktop.getDesktop().browse(new URI(url))
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

Option<MenuContribution> contributeToMenu(Map<String, Object> map) {
    def contribution = new MenuContribution(map: map, updateClosure: this.&updateMenuControllers)
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

            appendMapAsMenu(menu, contribution.map)
        }
    }
}

private void appendMapAsMenu(Menu parent, Map<String, Object> map){
    map.each { entry ->

        if(entry.value instanceof Map){

            Menu subMenu = new Menu(entry.key)
            appendMapAsMenu(subMenu, entry.value)

            parent.add(subMenu)
        }else if(entry.value instanceof Closure){

            MenuItem configItem = new MenuItem(entry.key)
            configItem.addActionListener({e ->
                entry.value()
            } as ActionListener)

            parent.add(configItem)
        }else{
            println("Illegal value in menu map: $entry.value")
        }
    }
}

class MenuContribution {
    Map<String, Closure> map;
    Closure updateClosure

    void update() {
        updateClosure()
    }

    void remove() {
        contributionList.remove(this)
    }

}
