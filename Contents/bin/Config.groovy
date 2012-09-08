import groovy.xml.MarkupBuilder
import groovy.beans.Bindable

class Config {

    @Bindable List<BuildConfig> buildConfigs = []
    @Bindable int pollIntervalMillis
    @Bindable String speechCmd
    @Bindable boolean popupEnabled
    @Bindable boolean afterLoseFocusClosePopup
    @Bindable boolean speechEnabled
    @Bindable boolean trayEnabled
    Date lastModified


    String toXml(){

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.config{
            builds{
                buildConfigs.each {build ->
                    xml.build{
                        name build.name
                        server build.server
                        job build.job
                        favorite build.favorite
                    }
                }
            }

            xml.pollIntervalMillis pollIntervalMillis
            xml.speechCmd speechCmd
            xml.speechEnabled speechEnabled
            xml.popupEnabled popupEnabled
            xml.trayEnabled trayEnabled
            xml.afterLoseFocusClosePopup afterLoseFocusClosePopup
            xml.lastModified lastModified.time
        }
        writer.toString()


    }

    void fromXml(String xml){
        def root = new XmlSlurper().parseText(xml)
        buildConfigs = root.builds.build.collect{ build ->
            new BuildConfig(
                name: build.name.text(),
                server: build.server.text(),
                job:  build.job.text(),
                favorite: (build.favorite?.text()?:"true").toBoolean()
            )
        }
        pollIntervalMillis = root.pollIntervalMillis.text() as int
        speechCmd = root.speechCmd.text()
        speechEnabled = root.speechEnabled.text().toBoolean()
        popupEnabled = root.popupEnabled.text().toBoolean()
        trayEnabled = root.trayEnabled.text() .toBoolean()
        afterLoseFocusClosePopup = root.afterLoseFocusClosePopup.text().toBoolean()
        lastModified = new Date(root.lastModified.text().toLong())


    }


}
