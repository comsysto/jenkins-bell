import java.awt.EventQueue
import java.awt.Color

class DockModule extends AbstractModule {

    Logo logo
    def appleApplication

    DockModule() {
        try{
            def clazz = getClass().getClassLoader().loadClass("com.apple.eawt.Application")
            appleApplication = clazz.application
        } catch(Exception e) {
            // not on MacOS...
        }
    }

    private void setupLogo() {
        if(appleApplication == null) return
        def allBuildsSuccessful = agent.allBuildsSuccessful
        Logo myLogo = logo
        EventQueue.invokeLater {
            if (!myLogo) return
            println "--CHANGED DOCK IMAGE--"
            myLogo.bellColor = allBuildsSuccessful ? null : Color.RED
            appleApplication.setDockIconImage(myLogo.makeImage())
            //appleApplication.setDockIconBadge("JenkinsBell")
            appleApplication.requestUserAttention(true)
        }
    }

    @Override
    void onBuildChangedState(Build build) {
        setupLogo()
    }

    @Override
    boolean canBeInstalled(Agent agent) {
        appleApplication != null
    }

    @Override
    void afterInstall() {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JenkinsBell");
        logo = new Logo()
        logo.setSize(256, 256)
        setupLogo()
    }

    void afterUninstall(){
        logo = null
    }

}
