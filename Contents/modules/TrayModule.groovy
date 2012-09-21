import groovy.transform.Field

import java.awt.Image
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.swing.JPanel
import groovy.swing.SwingBuilder
import java.awt.BorderLayout

@Field Option<TrayIcon> trayIcon = Option.none()
@Field Option menuController = Option.none()
@Field boolean buildChanged = true

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
    buildChanged = true


}

private Image updateTrayIcon() {
    onAModule.createLogoImage(64).ifSome { image ->
        trayIcon.ifSome { tray ->
            tray.setImage(image)
        }
    }
}

void onBuildStateChanged(Build build) {
    buildChanged = true
}

void onEndPoll() {
    if(buildChanged){
        updateTrayIcon()
        buildChanged = false
    }
}

void onStopMonitoring() {
    trayIcon.ifSome { tray ->
        SystemTray.systemTray.remove(tray)
        trayIcon = Option.none()
        menuController = Option.none()
    }
}

void readConfigElement(slurper, config){
   config.trayEnabled = (slurper?.trayEnabled?:"true").toBoolean()
}

void writeConfigElement(builder, config){
    builder.trayEnabled config.trayEnabled
}

Option<List<JPanel>> configElementPanel(config){
    Option.some([new SwingBuilder().panel(){
        borderLayout()
        label(text: "trayEnabled", constraints: BorderLayout.WEST)
        checkBox(selected: config.trayEnabled, actionPerformed: {e -> config.trayEnabled = e.source.selected})
    }])
}