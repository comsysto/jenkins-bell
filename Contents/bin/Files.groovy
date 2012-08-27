/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 26/08/2012
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
class Files {


    private final File configFolder
    private final File configFile
    private final File logFile

    Files(File configFolder) {
        this.configFolder = new File(new File(System.getProperty("user.home")), ".jenkins-bell")
        this.configFile = new File(configFolder, "config.xml")
        this.logFile = new File(configFolder, "log")
    }

    File configFile(){
        configFile
    }

    File stateFolder(){
        folder(configFolder, "state")
    }

    File reportFolder(){
        folder(configFolder, "report")
    }

    File stateFile(String name){
        file(stateFolder(), name)
    }

    File reportFile(String name) {
        file(reportFolder(), name)
    }

    File folder(File parent, String name){
        assert name, "Illegal file name not set!"
        def f = new File(parent, name)
        assert f.exists() || f.mkdirs(), "Failed making directory '$f'"
        f
    }

    File file(File parent, String name){
        assert name, "Illegal file name not set!"
        def f = new File(parent, name)
        assert f.exists() || f.createNewFile(), "Failed making file '$f'"
        f
    }

    void withLogFilePrintWriter(Closure c){
        logFile.withPrintWriter("UTF-8", c)
    }

}