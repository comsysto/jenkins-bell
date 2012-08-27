import java.awt.event.ActionListener
import java.awt.*

class SpeechModule extends AbstractModule{


    @Override
    void onBuildChangedState(Build build) {
        String message = "Build - $build.name - changed state from $build.lastBuildState to $build.buildState"
        [agent.config.speechCmd, message].execute()
        Thread.sleep(5000)
    }


}