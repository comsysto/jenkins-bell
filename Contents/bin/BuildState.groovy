
/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 29/08/2012
 * Time: 08:20
 * To change this template use File | Settings | File Templates.
 */
enum BuildState {

    SUCCESS, IGNORED, FETCH_ERROR, UNSTABLE, FAILURE;

    boolean isExceptional(){
        this in [UNSTABLE, FAILURE]
    }

    static BuildState forName(String name){
        values().find {it.name() == name}
    }

}
