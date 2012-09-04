import groovy.transform.Field

import java.awt.Image
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon

@Field Option<TrayIcon> trayIcon = Option.none()
@Field Option menuController = Option.none()

void onStartMonitoring() {
    if (!SystemTray.supported) return

    trayIcon = onAModule.createLogoImage(32).map { icon ->
        def popupMenu = new PopupMenu();
        menuController = onAModule.createMenuController(popupMenu)
        new TrayIcon(icon, "JenkinsBell", popupMenu)
    }
    trayIcon.ifSome { SystemTray.systemTray.add(it) }


}

private Image updateTrayIcon() {
    onAModule.createLogoImage(32).ifSome { image ->
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