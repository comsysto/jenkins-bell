import groovy.swing.SwingBuilder
import groovy.transform.Field

import java.awt.BorderLayout
import javax.swing.JPanel

@Field
Map<BuildState, List<String>> argsForState = [
        (null): ["red", "off", "yellow", "off", "green", "off"],
        (BuildState.FETCH_ERROR): ["red", "off", "yellow", "off", "green", "on"],
        (BuildState.IGNORED): ["red", "off", "yellow", "off", "green", "on"],
        (BuildState.SUCCESS): ["red", "off", "yellow", "off", "green", "on"],
        (BuildState.UNSTABLE): ["red", "off", "yellow", "on", "green", "off"],
        (BuildState.FAILURE): ["red", "on", "yellow", "off", "green", "off"],
]

void onStartMonitoring(){
    tryUpdateTrafficLight()
}

void onBuildStateChanged(Build build) {
    tryUpdateTrafficLight()
}

private void tryUpdateTrafficLight() {
    onAModule.getConfig().ifSome { config ->
        if (!config.trafficLightEnabled || config.trafficLightPath.isEmpty()) return

        onAModule.getHighestBuildState().ifSome { highestBuildState ->
            updateTrafficLight(highestBuildState, config);
        }
    }
}

void updateTrafficLight(BuildState state, config){
    def args = argsForState[state];
    def cmd = ([config.trafficLightPath] + args)
    println("Sending command " + cmd + " to traffic light")
    try{
        cmd.execute()
    }catch(Exception e){
        println("Failed " + cmd + " !")
        e.printStackTrace();
    }
}

void readConfigElement(slurpers, config) {
    config.trafficLightEnabled = (slurpers?.user?.trafficLightEnabled?.text()?: "true").toBoolean()
    config.trafficLightPath = slurpers?.user?.trafficLightPath?.text()?:""
}

void writeConfigElement(builders, config) {
    builders?.user?.trafficLightEnabled config.trafficLightEnabled
    builders?.user?.trafficLightPath config.trafficLightPath
}

Option<List<JPanel>> configElementPanel(config) {
    def builder = new SwingBuilder()
    Option.some([
            builder.panel() {
                borderLayout()
                label(text: "trafficLightEnabled", constraints: BorderLayout.WEST)
                checkBox(selected: config.trafficLightEnabled, actionPerformed: {e -> config.trafficLightEnabled = e.source.selected})
            },
            builder.panel() {
                borderLayout()
                label(text: "trafficLightJarPath", constraints: BorderLayout.WEST)
                textField(text: config.trafficLightPath, focusLost: {e -> config.trafficLightPath = e.source.text})
            }])
}