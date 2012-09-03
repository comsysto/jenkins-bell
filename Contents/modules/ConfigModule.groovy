// monitor configure

import groovy.transform.Field
import static Option.*

@Field
Option<Config> config = none()

synchronized Option<Config> getConfig() {
    config = config.ifNoneThanSome{

        def newConfig = defaultConfig()

        onAModule.configFile("config.xml").ifSome {
            it.withReader("UTF-8") { reader ->
                def text = reader.text
                if(text)
                    newConfig.fromXml(text)
            }
        }

        newConfig
    }
}

synchronized void storeConfig(Config config) {

    config.buildConfigs = config.buildConfigs.findAll {it.name || it.job || it.server}

    onAModule.configFile("config.xml").ifSome {
        it.withWriter("UTF-8") { writer ->
            writer << config.toXml()
        }
    }
    config = null

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
