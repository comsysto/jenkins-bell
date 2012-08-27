/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 27/08/2012
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
class App {
    Agent agent = new Agent()
    Config config = null
    Map<AbstractModule, Closure> modulesAndShouldInstall = [
            (new LogModule()) : null,
            (new DockModule()): null,
            (new TrayModule()) : {-> config.trayEnabled},
            (new ReportModule()) : null,
            (new PopupModule()) : {-> config.popupEnabled},
            (new SpeechModule()) : {-> config.speechEnabled},
            (new ConfigModule(this)) : null
    ]

    Files files

    App(Files files) {
        this.files = files
        agent.files = files
        modulesAndShouldInstall.keySet().each {
            it.files = files
        }
    }

    void configure(){
        new ConfigModule(this).openFrame(true)
    }

    synchronized void restart(){
        agent.stopLooping()
        modulesAndShouldInstall.keySet().each {
            it.uninstall()
        }

        config = loadConfig()
        agent.configure(config)

        modulesAndShouldInstall.each {module, enabledClosure ->
            if(enabledClosure != null && !enabledClosure()) return

            module.install(agent)
        }
        agent.startLooping()
    }

    synchronized void shutdown(){
        agent.stopLooping()
    }

    Config defaultConfig(){
        Config config = new Config()
        config.buildConfigs = [
                new BuildConfig(name: "Test", server: "localhost:8080", job:"TestJob")
        ]
        config.pollIntervalMillis = 10000
        config.speechCmd = "say"
        config.lastModified = new Date()
        config.trayEnabled = true
        config.speechEnabled = true
        config
    }

    Config loadConfig(){
        File configFile = files.configFile()
        if(!configFile.exists()){
            println "CREATING DEFAULT CONFIG"
            return createAndStoreDefaultConfig(configFile)
        }

        println "READING CONFIG"
        def config = new Config()
        configFile.withReader("UTF-8"){
            config.fromXml(it.text)
        }
        println config.toXml()
        config
    }

    private Config createAndStoreDefaultConfig(File configFile) {
        def configDir = configFile.parentFile
        assert configDir.exists() || configDir.mkdirs(), "Failed creating config dir '$configDir'"

        Config defaultConfig = defaultConfig()
        storeConfig(defaultConfig)
        defaultConfig
    }

    void storeConfig(Config config){
        files.configFile().withWriter("UTF-8") {
            it << config.toXml()
        }
    }


}
