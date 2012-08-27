def userHome = new File(System.getProperty("user.home"))
def configDir = new File(userHome, ".jenkins-bell")
App app = new App(new Files(configDir))
if(args.size() < 1 ){
    println "usage <loop | configure>"
}
def mode = args[0]
if(mode == "configure"){
    app.configure()
} else if(mode == "run"){
    app.restart()
}
