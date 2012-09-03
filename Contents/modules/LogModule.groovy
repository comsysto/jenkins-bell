import groovy.transform.Field

@Field Map<Date, String> pollLog = [:]

void onStartMonitoring() {
    logState()
}

void onEndPoll() {
    def messages = onAModule.getBuilds().toList().collect {
        "$it.name ($it.job@$it.server) [$it.fetchMessageWithColor] [$it.stateDescriptionWithColor]"
    }
    pollLog[new Date()] = messages

    while (pollLog.size() > 20) {
        pollLog.remove(pollLog.keySet().iterator().next())
    }

    logState()
}

private logState() {
    onAModule.configFile("log").ifSome { file ->

        file.withPrintWriter { out ->
            out.println("LOG MODIFIED:    ${new Date()}")
            onAModule.getConfig().ifSome{
                out.println("CONFIG MODIFIED: ${it.lastModified}")
            }
            out.println("-----------------------------------------")
            out.println("BUILDS:")
            out.println()
            onAModule.getBuilds().toList().each { build ->

                String stateLine = onAModule.stateFile(build).defaultOrMap("") {"state:  ${it.canonicalPath}"}
                String reportLine = onAModule.reportFile(build).defaultOrMap("") {"report:  ${it.canonicalPath}"}

                out.println """
            |    $build.job @ $build.server
            |        $stateLine
            |        $reportLine
            |
            """.trim().stripMargin()
            }

            out.println("-----------------------------------------")
            out.println("MESSAGES:")
            out.println()
            pollLog.each { time, logs ->
                out.println "    $time:"
                logs.each {
                    out.println "        ${it}"
                }
                out.println()
            }
            out.println("-----------------------------------------")
        }
    }
}
