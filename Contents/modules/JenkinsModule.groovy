import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.JPanel

void startBuild(build) {

}

void startJob(build) {

    def url = new URL("http://$build.server/job/$build.job/build")
    def urlConnection = url.openConnection()
    onAModule.getConfig().ifSome { config ->
        if (config.authToken && config.authName)
            urlConnection.setRequestProperty("Authorization", "Basic " + "$config.authName:$config.authToken".getBytes("UTF-8").encodeBase64());
    }
    def stream = urlConnection.getInputStream()
    try {
        stream.text
    } finally {
        if (stream) {
            stream.close()
        }
    }

}

void updateBuild(build) {

    def url = new URL("http://${build.server}/job/${build.job}/lastBuild/api/json")
    def jsonText
    try {
        jsonText = url.text
        fetchError = null
    } catch (Exception e) {
        fetchError = e
        build.lastBuildState = build.buildState
        buildState = BuildState.FETCH_ERROR
        return
    }

    def json = new groovy.json.JsonSlurper().parseText(jsonText)

    def result = BuildState.forName(json.result)
    build.lastBuilding = build.building
    build.building = json.building


    build.lastBuildState = build.buildState

    // fixing state if build drops state to null
    if (!build.building) {
        build.buildState = result
    }


    build.date = new Date(json.timestamp)
    build.authors = json.culprits?.fullName.collect {it.toString()}
    build.changes = json.changeSet?.items?.collect {it.msg.trim().split("\n")}.flatten()
}

void readConfigElement(slurper, config) {
    config.authToken = slurper?.authToken
    config.authName = slurper?.authName
}

void writeConfigElement(builder, config) {
    builder.authToken config.authToken
    builder.authName config.authName
}

Option<List<JPanel>> configElementPanel(config) {
    def builder = new SwingBuilder()
    Option.some([
            builder.panel() {
                borderLayout()
                label(text: "authName", constraints: BorderLayout.WEST)
                textField(text: config.authName, focusLost: {e -> config.authName = e.source.text})
            },
            builder.panel() {
                borderLayout()
                label(text: "authToken", constraints: BorderLayout.WEST)
                textField(text: config.authToken, focusLost: {e -> config.authToken = e.source.text})
            }
    ])
}