@Override
void onBuildChangedState(Build build) {

    onAModule.getConfig().ifSome{ config ->
        if (build.anyStateFetchError || !config.speechEnabled) return
        String message = "Build - $build.name - changed state from $build.lastBuildState to $build.buildState"
        [config.speechCmd, message].execute()
        Thread.sleep(5000)
    }


}

