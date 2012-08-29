import java.awt.*

class TrayModule extends AbstractMenuModule{


    TrayIcon trayIcon
    PopupMenu popupMenu

    @Override
    boolean canBeInstalled(Agent agent) {
        SystemTray.supported
    }

    void afterInstall() {
        createTrayIcon()
        setupState(null)
    }

    void afterUninstall(){
        SystemTray.getSystemTray().remove(trayIcon)
        trayIcon = null
        logo = null
        popupMenu = null

        super.afterUninstall()
    }

    @Override
    void setupPollStateInUiThread(Build build) {
        trayIcon.setImage(logo.makeImage())
    }

    private void createTrayIcon(){
        SystemTray sysTray = SystemTray.getSystemTray();
        popupMenu = new PopupMenu();
        logo = new Logo()
        logo.setSize(256, 256)
        trayIcon = new TrayIcon(logo.makeImage(), "JenkinsBell", popupMenu);
        trayIcon.setImageAutoSize(true);
        def items = createMenuItems()
        items.each {popupMenu.add(it)}
        sysTray.add(trayIcon);
        trayIcon
    }

}