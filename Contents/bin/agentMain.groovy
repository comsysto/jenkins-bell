#!/usr/bin/env groovy

def userHome = new File(System.getProperty("user.home"))
def configDir = new File(userHome, ".jenkins-bell")
App app = new App(new Files(configDir))
app.uiEnabled = false
app.restart()
