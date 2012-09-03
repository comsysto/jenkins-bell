#! /usr/bin/env groovy
def commandsDir = new File("../commands")
if(args.length < 1){
    println "usage groovy main.groovy <${commandsDir.list()*.replaceAll("\\.groovy", "").join(" | ")}>"
    return
}

def command = args[0]
def commandFile = command.contains("/") ? new File(command) : new File("../commands/$command")
def mainMethods = []
def moduleFiles = []
commandFile.text.eachLine {
    if(it.startsWith("#")){
        // ignore comment
    }else if(it.startsWith("!")){
        mainMethods << it.substring(1).trim()
    }else{
        moduleFiles << (it.contains("/") ? new File(it) : new File("../modules/$it"))
    }
}
Kernel kernel = new Kernel(moduleFiles)
kernel.reload()
mainMethods.each {
    kernel.onAModule."$it"()
}


