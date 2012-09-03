//import com.apple.eawt.Application


import groovy.transform.Field

import java.awt.PopupMenu

//    Application appleApplication
@Field def appleApplication
@Field Option menuController = Option.none()

try {
    def clazz = getClass().getClassLoader().loadClass("com.apple.eawt.Application")
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JenkinsBell");
    appleApplication = clazz.application

} catch (Exception e) {
    // not on MacOS...
}


void onStartMonitoring() {
    if(!appleApplication) return

    PopupMenu popup = new PopupMenu();

    menuController = onAModule.createMenuController(popup)
    menuController.ifSome{
        appleApplication.setDockMenu(popup)
    }

    updateDockIcon()

}

void onBuildStateChanged(Build build) {
    if(! appleApplication) return
    updateDockIcon()
    menuController.ifSome{
        it.update(build)
    }
}

void updateDockIcon() {
    if(! appleApplication) return

     onAModule.getHighestBuildState().ifSome{ highestBuildState ->

         onAModule.getBuildStateCount().ifSome{ buildStateCounts ->

             def buildStateCount = buildStateCounts[highestBuildState]

             if (highestBuildState?.exceptional && buildStateCount && buildStateCount > 0) {
                 appleApplication.setDockIconBadge(buildStateCount.toString())
             } else {
                 appleApplication.setDockIconBadge(null)
             }
         }
    }

    onAModule.createLogoImage(1024).ifSome{
        appleApplication.setDockIconImage(it)
    }

}

void requestUserAttention() {
    if(! appleApplication) return

    appleApplication.requestUserAttention(true)
}

void requestForeground() {
    if(! appleApplication) return

    appleApplication.requestForeground(true)
}


void onStopMonitoring() {
    if(! appleApplication) return

    menuController = Option.none()

}
