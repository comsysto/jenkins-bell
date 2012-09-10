void startBuild(build) {

}

void updateBuild(build) {

    def url = new URL("http://${build.server}/job/${build.job}/lastBuild/api/json")
    def jsonText
    try {
        jsonText = url.text
        fetchError = null
    } catch (Exception e) {
        fetchError = e
        lastBuildState = buildState
        buildState = BuildState.FETCH_ERROR
        return
    }

    def json = new groovy.json.JsonSlurper().parseText(jsonText)

    def result = BuildState.forName(json.result)

    build.building = json.building
    // fixing state if build drops state to null
    if (!build.building) {
        build.lastBuildState = build.buildState
        build.buildState = result
    }


    build.date = new Date(json.timestamp)
    build.authors = json.culprits?.fullName.collect {it.toString()}
    build.changes = json.changeSet?.items?.collect {it.msg.trim().split("\n")}.flatten()
}