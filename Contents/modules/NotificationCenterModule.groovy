import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.swing.JPanel

void onBuildStateChanged(Build build) {

        if (build.anyStateFetchError || build.building || !build.stateChanged || !build.buildState) return
        [
                "/Applications/terminal-notifier.app/Contents/MacOS/terminal-notifier",
                "-title", "JenkinsBell",
                "-group", "$build.name",
                "-message", "${build.name}: ${build.buildState}",
                "-open", "http://$build.server/job/$build.job/lastBuild"
        ].execute()


}
