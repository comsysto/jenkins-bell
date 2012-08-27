import java.awt.event.ActionListener
import java.awt.*
import java.awt.font.TextAttribute
import javax.swing.UIManager
import javax.swing.JFrame

class TrayModule extends AbstractModule{


    TrayIcon trayIcon
    Logo logo
    Map<Build, MenuItem> menuItemForBuild = [:]
    PopupMenu popupMenu

    @Override
    boolean canBeInstalled(Agent agent) {
        SystemTray.supported
    }

    void afterInstall() {
        createTrayIcon()
        updateTray()
    }

    void afterUninstall(){
        SystemTray.getSystemTray().remove(trayIcon)
        trayIcon = null
        logo = null
        popupMenu = null
        menuItemForBuild.clear()
    }

    @Override
    void onBuildChangedState(Build build) {
        updateTray(build)
    }

    private void updateTray(Build build) {
        def allBuildsSuccessful = agent.allBuildsSuccessful
        Logo myLogo = logo
        EventQueue.invokeLater {
            if (!myLogo) return
            myLogo.bellColor = allBuildsSuccessful ? null : Color.RED
            trayIcon.setImage(myLogo.makeImage())
            if(build != null){
                def item = menuItemForBuild[build]
                setupLabel(item, build)
                if(!build.stateSuccess){
                    popupMenu.remove(item)
                    popupMenu.insert(item, 0)
                }
            }
            println "--CHANGED DOCK IMAGE--"
        }
    }

    private void setupLabel(MenuItem item, Build build) {
        item.setLabel(build.stateSuccess ? "Go to $build.name" : "!> Go to $build.name")
        Hashtable attributes = new Hashtable();
        attributes.put(TextAttribute.WEIGHT, build.stateSuccess ? TextAttribute.WEIGHT_REGULAR :TextAttribute.WEIGHT_BOLD);
        Font font = UIManager.getDefaults().getFont("MenuItem.font").deriveFont(attributes)
        item.setFont(font)
    }

    private void createTrayIcon(){
        SystemTray sysTray = SystemTray.getSystemTray();
        popupMenu = new PopupMenu();
        logo = new Logo()
        logo.setSize(256, 256)
        trayIcon = new TrayIcon(logo.makeImage(), "JenkinsBell", popupMenu);
        trayIcon.setImageAutoSize(true);
        sysTray.add(trayIcon);

        agent.builds.each {
            def item = new MenuItem()
            item.addActionListener({ e ->
                Desktop.getDesktop().browse(it.buildUri)
            } as ActionListener)
            menuItemForBuild[it] = item

            if(it.stateSuccess){
                popupMenu.add(item)
            }else{
                popupMenu.insert(item, 0)
            }

            setupLabel(item, it)
        }

        MenuItem configItem = new MenuItem("Open Configuration ...")
        configItem.addActionListener({e ->
            agent.doCommand("config", [:])
        } as ActionListener)
        popupMenu.add(configItem)

        trayIcon
    }

}