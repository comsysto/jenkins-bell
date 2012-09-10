void startBuild(build) {

}

void updateBuild(build) {
    build.with {
        def url = new URL("http://${server}/job/${job}/lastBuild/api/json")
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

        building = json.building
        // fixing state if build drops state to null
        if (!building) {
            lastBuildState = buildState
            buildState = result
        }


        date = new Date(json.timestamp)
        authors = json.culprits?.fullName.collect {it.toString()}
        changes = json.changeSet?.items?.collect {it.msg.trim().split("\n")}.flatten()
    }
}