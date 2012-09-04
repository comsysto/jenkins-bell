#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin

COMMAND=$1
if [ -z "$COMMAND" ]; then
    COMMAND=monitor
fi

cd $BIN_DIR
export JAVA_OPTS=-Xdock:name="JenkinsBell"
env groovy "main.groovy" "$COMMAND"