#!/bin/sh -li

command -v groovy > /dev/null
GROOVY_EXISTS=$?

if test !$GROOVY_EXISTS; then
    MSG="Command 'groovy' is not on the PATH! Is groovy installed on the system?";
    echo "$MSG";
    syslog -s -k Facility com.apple.console Sender JenkinsBell.app Level 3 Message "$MSG";
    exit -1;
fi

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin

MY_CMD=$1
if [ -z "$MY_CMD" ]; then
    MY_CMD=monitor
fi

cd $BIN_DIR
export JAVA_OPTS=-Xdock:name="JenkinsBell"

env groovy main.groovy "$MY_CMD"