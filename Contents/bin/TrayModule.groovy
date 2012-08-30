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
        populateMenu(popupMenu)
        sysTray.add(trayIcon);
        trayIcon
    }

}