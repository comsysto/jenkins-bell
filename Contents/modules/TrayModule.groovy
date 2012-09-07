import groovy.transform.Field

import java.awt.Image
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon

@Field Option<TrayIcon> trayIcon = Option.none()
@Field Option menuController = Option.none()

void onStartMonitoring() {
    if (!SystemTray.supported) return

    def useTray = onAModule.getConfig().defaultOrMap(false){
        it.trayEnabled
    }

    if(!useTray) return

    trayIcon = onAModule.createLogoImage(64).map { icon ->
        def popupMenu = new PopupMenu();
        menuController = onAModule.createMenuController(popupMenu)
        new TrayIcon(icon, "JenkinsBell", popupMenu)
    }
    trayIcon.ifSome {
        it.setImageAutoSize(true)
        SystemTray.systemTray.add(it)
    }


}

private Image updateTrayIcon() {
    onAModule.createLogoImage(64).ifSome { image ->
        trayIcon.ifSome { tray ->
            tray.setImage(image)
        }
    }
}

void onBuildStateChanged(Build build) {
    updateTrayIcon()
}

void onStopMonitoring() {
    trayIcon.ifSome { tray ->
        SystemTray.systemTray.remove(tray)
        trayIcon = Option.none()
        menuController = Option.none()
    }
}