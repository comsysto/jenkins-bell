class ReportModule extends AbstractModule {


    @Override
    void onBuildChangedState(Build build) {

    }

    @Override
    void onEndPoll() {
        agent.builds.each { build ->
            files.reportFile(build.fileName).withPrintWriter {
                it.println(build.name)
                it.println("----------------------------")
                it.println "$build.job@$build.server"
                it.println build.date
                it.println("----------------------------")
                if (build.authors) {
                    it.println "authors:"
                    it.println build.authors.collect {"  * " + it}.join("\n")
                    it.println("----------------------------")
                }
                if (build.changes) {
                    it.println "changes:"
                    it.println build.changes.collect {"  * " + it}.join("\n")
                    it.println("----------------------------")
                }

                it.println "***${build.stateDescriptionWithColor}***"
            }
        }

        files.reportFile("overview").withPrintWriter { out ->
            agent.builds.each { build ->
                out.println("--  $build.name  ".padRight(50, "-") + ("  $build.stateDescriptionWithColor").padLeft(30, "-"))
                out.println("  $build.job@$build.server")
                out.println()
            }
        }


    }
}