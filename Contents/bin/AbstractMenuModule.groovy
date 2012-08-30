import java.awt.Color
import java.awt.EventQueue
import java.awt.MenuItem

import java.awt.Desktop
import java.awt.event.ActionListener
import java.awt.PopupMenu
import java.awt.MenuContainer
import java.awt.Menu
/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 29/08/2012
 * Time: 09:18
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractMenuModule extends AbstractModule{

    final COLOR_FOR_STATE = [
            (BuildState.SUCCESS): null,
            (BuildState.FETCH_ERROR) : Color.LIGHT_GRAY,
            (BuildState.UNSTABLE) : Color.YELLOW,
            (BuildState.FAILURE) : Color.RED,
    ].asImmutable()


    protected volatile Logo logo;
    protected highestBuildState;
    protected Integer buildStateCount;
    protected Color color;
    protected Map<Build, MenuItem> menuItemForBuild = [:]

    @Override
    void onEndPoll() {
        super.onEndPoll()

    }

    @Override
    void onBuildChangedState(Build build) {
        setupState(build)
    }

    protected void setupLabel(MenuItem item, Build build) {
        item.setLabel(!build.fetchError && build.stateSuccess ? "Go to $build.name" : "!> Go to $build.name (${build.fetchError ? "FETCH ERROR" : build.buildState})")
    }


    void setupState(Build build){
        if(logo == null){
            logo = new Logo()
            logo.setSize(256, 256)
        }

        highestBuildState = agent.getHighestBuildState()
        color = COLOR_FOR_STATE[highestBuildState]
        buildStateCount = agent.buildStateCount[highestBuildState]

        EventQueue.invokeLater {
            logo.bellColor = color

            if(build)
                setupLabel(menuItemForBuild[build], build)
            setupPollStateInUiThread(build)
        }
    }

    void populateMenu(Menu menuContainer){
        agent.builds.sort{it.name}.each {
            def item = new MenuItem()
            item.addActionListener({ e ->
                Desktop.getDesktop().browse(it.buildUri)
            } as ActionListener)
            menuItemForBuild[it] = item
            setupLabel(item, it)
            menuContainer.add(item)
        }

        menuContainer.addSeparator()

        MenuItem configItem = new MenuItem("Open Configuration ...")
        configItem.addActionListener({e ->
            agent.doCommand("config")
        } as ActionListener)

        menuContainer.add(configItem)
    }

    @Override
    void afterInstall() {
        super.afterInstall()
    }

    @Override
    void afterUninstall() {
        logo = null
        highestBuildState = null
        buildStateCount = null
        color = null
        menuItemForBuild.clear()
        super.afterUninstall()

    }

    abstract void setupPollStateInUiThread(Build build)

}
