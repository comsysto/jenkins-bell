void onEndPoll() {
    onAModule.getBuilds().toList().each { build ->
        reportFile(build).ifSome {file ->
            file.withPrintWriter {
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
    }

    reportFile("report/overview").ifSome { file ->
        file.withPrintWriter { out ->
            onAModule.getBuilds().toList().each { build ->
                out.println("--  $build.name  ".padRight(50, "-") + ("  $build.stateDescriptionWithColor").padLeft(30, "-"))
                out.println("  $build.job@$build.server")
                out.println()
            }
        }
    }
}

Option<File> reportFile(Build build) {
    onAModule.configFile("report/$build.fileName")
}

Option<File> reportFile(String name) {
    onAModule.configFile("report/$name")
}