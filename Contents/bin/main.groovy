#! /usr/bin/env groovy

import groovy.transform.Field

@Field pattern =  ~/(\w+)\.command/

def userHome = System.getProperty("user.home")

def globalCommandDir = new File("../commands")
def userCommandDir = new File(userHome, ".jenkins-bell/commands")

def filesForCommands = [:]

filesForCommands.putAll(commandFiles(globalCommandDir))
filesForCommands.putAll(commandFiles(userCommandDir))

if(args.length < 1){
    println "usage groovy main.groovy <${filesForCommands.keySet().join(" | ")}>"
    return
}


def command = args[0]
def commandFile
if(command =~ pattern){
    commandFile = new File(command)
} else {
    commandFile = filesForCommands[command]
    assert commandFile, "command not found available commands: ${filesForCommands.keySet()}"
}


def mainMethods = []
def moduleFiles = []
commandFile.text.eachLine {
    if(it.startsWith("#")){
        // ignore comment
    }else if(it.startsWith("!")){
        mainMethods << it.substring(1).trim()
    }else{
        moduleFiles << (it.endsWith(".groovy") ? new File(it.replaceAll("~", userHome)) : new File("../modules/${it}.groovy"))
    }
}
Kernel kernel = new Kernel(moduleFiles)
kernel.reload()
mainMethods.each {
    if(args.length == 1){
        kernel.onAModule."$it"()
    } else if(args.length == 2){
        def params = args[1 .. -1]
        kernel.onAModule."$it"(* params)
    }
}

def commandFiles(File parentDir){
    Map<String, File> fileMap = [:]
    if(parentDir.exists()){
        parentDir.listFiles().findAll {it.isFile()}.each {

            def matcher = pattern.matcher(it.name)
            if(matcher.matches()){
                fileMap[matcher.group(1)] = it
            }
        }
    }
    fileMap
}