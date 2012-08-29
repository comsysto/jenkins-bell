#!/usr/bin/env groovy
import java.awt.EventQueue

def userHome = new File(System.getProperty("user.home"))
def configDir = new File(userHome, ".jenkins-bell")
App app = new App(new Files(configDir))
if (args.size() < 1) {
    println "usage <run | configure>"
}
def mode = args[0]
if (mode == "configure") {
    EventQueue.invokeLater {
        app.configure()
    }
} else if (mode == "run") {
    EventQueue.invokeLater {
        app.restart()
    }
}
