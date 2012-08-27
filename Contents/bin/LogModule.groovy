
/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 27/08/2012
 * Time: 10:02
 * To change this template use File | Settings | File Templates.
 */
class LogModule extends AbstractModule{

    Map<Date, String> pollLog = [:]

    @Override
    void afterInstall() {
        logState()
    }

    @Override
    void onEndPoll() {
        def messages = agent.builds.collect{
            "$it.name ($it.job@$it.server) [$it.fetchMessageWithColor] [$it.stateDescriptionWithColor]"
        }
        pollLog[new Date()] = messages

        while (pollLog.size() > 20) {
            pollLog.remove(pollLog.keySet().iterator().next())
        }

        logState()
    }

    private logState() {
        files.withLogFilePrintWriter { out->
            out.println("LOG MODIFIED:    ${new Date()}")
            out.println("CONFIG MODIFIED: ${agent.config.lastModified}")
            out.println("-----------------------------------------")
            out.println("BUILDS:")
            out.println()
            agent.builds.each {
                out.println """
            |    $it.job @ $it.server
            |        state:  ${files.stateFile(it.fileName).canonicalPath}
            |        report: ${files.reportFile(it.fileName).canonicalPath}
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
