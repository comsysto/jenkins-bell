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
cd $BIN_DIR
env groovy "$BIN_DIR/main.groovy" agent