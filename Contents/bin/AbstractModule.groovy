
/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 27/08/2012
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractModule extends AgentListener{

    protected Agent agent;
    Files files;

    boolean canBeInstalled(Agent agent){
        return true;
    }

    final void install(Agent agent){
        assert files, "Files must be set!"
        if( !canBeInstalled(agent) || this.agent != null) return

        this.agent = agent
        agent.addListener(this)
        afterInstall()

    }

    void afterInstall() {
    }

    final void uninstall(){
        if(this.agent == null) return
        agent.removeListener(this)
        afterUninstall()
        agent = null;
    }

    void afterUninstall() {
    }
}
