
/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 27/08/2012
 * Time: 06:50
 * To change this template use File | Settings | File Templates.
 */
class Build {

    String name
    String job
    String server
    List<String> groups
    Throwable fetchError
    BuildState buildState
    BuildState lastBuildState
    boolean building
    boolean lastBuilding
    Date date
    List<String> authors
    List<String> changes
    boolean ignored = false

//    def fetch() {
//        def url = new URL("http://${server}/job/${job}/lastBuild/api/json")
//        def jsonText
//        try {
//            jsonText = url.text
//            fetchError = null
//        } catch (Exception e) {
//            fetchError = e
//            lastBuildState = buildState
//            buildState = BuildState.FETCH_ERROR
//            return false
//        }
//
//        def json = new groovy.json.JsonSlurper().parseText(jsonText)
//
//        def result = BuildState.forName(json.result)
//
//        building = json.building
//        // fixing state if build drops state to null
//        if(!building){
//            lastBuildState = buildState
//            buildState = result
//        }
//
//
//        date = new Date(json.timestamp)
//        authors = json.culprits?.fullName.collect {it.toString()}
//        changes = json.changeSet?.items?.collect {it.msg.trim().split("\n")}.flatten()
//        return true
//    }

    def boolean isStateChanged(){
        buildState != lastBuildState
    }

    def boolean isStateSuccess(){
        isSuccess(buildState)
    }

    def boolean isSuccess(BuildState state){
        return state == BuildState.SUCCESS
    }

    def String getLastBuildStateWithColor(){
        isSuccess(lastBuildState) ? TColor.greenFg(lastBuildState) : (lastBuildState == BuildState.UNSTABLE ? TColor.yellowFg(lastBuildState): TColor.redFg(lastBuildState))
    }

    def String getBuildStateWithColor(){
        isSuccess(buildState) ? TColor.greenFg(buildState) : (buildState == BuildState.UNSTABLE ? TColor.yellowFg(buildState): TColor.redFg(buildState));
    }

    def String getStateDescriptionWithColor(){
        building ? "${TColor.yellowBg("BUILDING")}  ${getBuildStateWithColor()}" : (lastBuildState ? "$lastBuildStateWithColor -> $buildStateWithColor" : "$buildStateWithColor")
    }

    def String getFetchMessageWithColor(){
        fetchError? TColor.redFg(fetchError.toString()) : TColor.greenFg("SUCCESS")
    }

    def String getFileName(){
        name.replaceAll("[^\\w]", "_")
    }

    def URI getBuildUri(){
        "http://$server/job/$job/lastBuild"
    }

    def boolean isAnyStateFetchError(){
        buildState == BuildState.FETCH_ERROR || lastBuildState == BuildState.FETCH_ERROR
    }


    boolean isBuildingChanged(){
        building != lastBuilding
    }



}
