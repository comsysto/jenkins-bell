class Agent {

    Files files;
    Config config;
    List<Build> builds;
    List<AgentListener> listeners = []
    private Object loopLock = new Object()
    private boolean isStopped = false
    private boolean isLooping = false


    void addListener(AgentListener l) {
        listeners.add(l)
    }

    void removeListener(AgentListener l) {
        listeners.remove(l)
    }

    void configure(Config config) {
        assert files, "Files must be set!"
        this.config = config
        println "--CONFIGURING--"
        builds = config.buildConfigs.collect {
            def build = new Build(name: it.name, job: it.job, server: it.server)
            def stateFile = files.stateFile(build.fileName)
            build.lastBuildState = stateFile.exists() ? BuildState.forName(stateFile.text) : null
            build.buildState = build.lastBuildState
            println "$build.name: $build.job @ $build.server -> $build.buildStateWithColor"
            build
        }
    }

    void loop() {
        synchronized (loopLock) {
            if (isLooping) return;
            println "--LOOPING--"
            isLooping = true
            while (!isStopped) {
                poll()
                loopLock.wait(config.pollIntervalMillis)
            }
            isLooping = false
            loopLock.notifyAll()
        }
    }

    void stopLooping() {
        synchronized (loopLock) {
            if (!isLooping) return
            isStopped = true
            loopLock.notifyAll()
            while (isLooping) {
                loopLock.wait()
            }
            isStopped = false
            println "--STOPPED LOOPING--"
        }
    }

    void startLooping() {
        Thread.start {
            loop()
        }
    }

    void isLooping() {
        synchronized (loopLock) {
            isLooping
        }
    }

    void poll() {
        eachListenerWithCatch {it.onPollBegin()}
        builds.each {
            pollBuild(it)
        }
        eachListenerWithCatch {it.onEndPoll()}
    }

    void eachListenerWithCatch(Closure c){
        listeners.each {
            try{
                c(it)
            }catch(Exception e){
                e.printStackTrace()
            }
        }
    }



    private pollBuild(Build build) {
        println "--POLL @ ${new Date()}--"
        build.fetch()

        if (build.stateChanged) {
            println("STATE CHANGE: $build.name $build.stateDescriptionWithColor")

            files.stateFile(build.fileName).text = build.buildState
            eachListenerWithCatch {it.onBuildChangedState(build)}
        }

    }

    public List<Object> doCommand(Map<String, Object> args, String cmd, Object... varargs){
        listeners.collect {
            it.onCommand(args, cmd, varargs)
        }
    }

    public List<Object> doCommand(String cmd, Object... varargs){
        doCommand([:], cmd, varargs)
    }

    public List<Object> doCommand(String cmd){
        doCommand([:], cmd, [])
    }

    public List<Object> doCommand(Map<String, Object> args, String cmd){
        doCommand(args, cmd, [])
    }


    Map<BuildState, Integer> getBuildStateCount() {
        builds.countBy{it.buildState}
    }

    BuildState getHighestBuildState(){
        builds.max{it.buildState}.buildState
    }
}
