// monitor configure

import groovy.transform.Field
import static Option.*
import groovy.xml.MarkupBuilder

@Field
Option<Map> config = none()

@Field
Map configFiles = [build: "build-config.xml", user: "user-config.xml"]

synchronized Option<Map> getConfig() {
    config = config.ifNoneThanSome{

        def slurpers = [:]
        configFiles.each { configName, configFile ->
            def slurper = onAModule.configFile(configFile).defaultOrMap(null) {
                it.withReader("UTF-8") { reader ->
                    def text = reader.text
                    if(text)
                        new XmlSlurper().parseText(text)
                    else
                        null
                }
            }

            slurpers[configName] = slurper
        }

        def newConfig = [:]
        onEachModule.readConfigElement(slurpers, newConfig)
        newConfig
    }
}

synchronized void storeConfig(Map config) {
    config.buildConfigs = config.buildConfigs.findAll {it.name || it.job || it.server}
    storeConfigToFileRec(new HashMap(configFiles), [:], config)
}

private void storeConfigToFileRec(Map configFiles, Map builders, Map config){
    if (configFiles.isEmpty()){
        onEachModule.writeConfigElement(builders, config)
    }else{
        def firstConfigName = configFiles.keySet().iterator().next()
        def configFile = configFiles.remove(firstConfigName)
        onAModule.configFile(configFile).ifSome {
            it.withWriter("UTF-8") { writer ->

                StringWriter sw = new StringWriter()
                def builder = new MarkupBuilder(sw)
                builders[firstConfigName] = builder
                builder.config{
                    storeConfigToFileRec(configFiles, builders, config)
                }
                writer << sw.toString()
            }
        }
    }

}