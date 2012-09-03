import groovy.transform.Field

import java.awt.event.ActionListener
import java.awt.Image
import java.awt.Menu
import java.awt.MenuItem
import java.awt.Color
import java.awt.Desktop
import static Option.*

@Field
final static COLOR_FOR_STATE = [
        (BuildState.FETCH_ERROR): Color.LIGHT_GRAY,
        (BuildState.UNSTABLE): Color.YELLOW,
        (BuildState.FAILURE): Color.RED,
].asImmutable()

@Field
protected Logo logo;

synchronized Option<Image> createLogoImage(int size) {
    if (logo == null) {
        logo = new Logo()
    }

    logo.setSize(size, size)
    def color = onAModule.getHighestBuildState().defaultOrMap(null){
        COLOR_FOR_STATE[it]
    }

    logo.bellColor = color
    some(logo.makeImage() as Image)
}

Option<MenuController> createMenuController(Menu menu){
    option(onAModule.getBuilds().map{
        new MenuController(it, menu, {-> onAModule.openConfigWindow()})
    })
}

class MenuController {

    MenuController(List<Build> builds, Menu menu, Closure openConfigWindow) {
        builds.sort {it.name}.each {

            def item = new MenuItem()

            item.addActionListener({ e ->
                Desktop.getDesktop().browse(it.buildUri)
            } as ActionListener)

            itemForBuild[it] = item
            menu.add(item)
        }

        builds.each {update(it)}

        MenuItem configItem = new MenuItem("Open Configuration ...")

        configItem.addActionListener({e ->
           openConfigWindow()
        } as ActionListener)

        menu.add(configItem)

    }

    private Map<Build, MenuItem> itemForBuild = [:]

    void update(Build build) {
        def item = itemForBuild[build]
        if (!item) return
        item.setLabel(!build.fetchError && build.stateSuccess ? "Go to $build.name" : "!> Go to $build.name (${build.fetchError ? "FETCH ERROR" : build.buildState})")
    }
}

void openInBrowser(Build build) {
    Desktop.getDesktop().browse(build.buildUri)
}




