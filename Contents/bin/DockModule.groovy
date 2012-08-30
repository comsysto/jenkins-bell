//import com.apple.eawt.Application
import java.awt.PopupMenu

class DockModule extends AbstractMenuModule {


//    Application appleApplication
    def appleApplication

    DockModule() {
        try{
            def clazz = getClass().getClassLoader().loadClass("com.apple.eawt.Application")
            appleApplication = clazz.application
        } catch(Exception e) {
            // not on MacOS...
        }
    }

    @Override
    void setupPollStateInUiThread(Build build) {
        appleApplication.setDockIconImage(logo.makeImage())

        if(highestBuildState?.exceptional && buildStateCount && buildStateCount > 0){
            appleApplication.setDockIconBadge(buildStateCount.toString())
        }else{
            appleApplication.setDockIconBadge(null)
        }

        appleApplication.requestUserAttention(true)
    }


    @Override
    boolean canBeInstalled(Agent agent) {
        appleApplication != null && super.canBeInstalled(agent)
    }

    @Override
    void afterInstall() {
        super.afterInstall()
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JenkinsBell");
        PopupMenu popup = new PopupMenu();
        def items = createMenuItems();
        items.each {popup.add(it)}
        appleApplication.setDockMenu(popup)
        setupState(null)
    }

    void afterUninstall(){
        super.afterUninstall()
    }

    @Override
    Object onCommand(Map<String, Object> args, String cmd, Object... varargs) {
        if(cmd == "requestForeground"){
            appleApplication.requestForeground(true)
        } else if(cmd == "requestUserAttention"){
            appleApplication.requestUserAttention(true)
        }

    }
}
