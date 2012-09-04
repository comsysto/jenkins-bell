#!/bin/sh -li

echo "runAsApplication" > ~/test_app_run

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin

cd $BIN_DIR
export JAVA_OPTS=-Xdock:name="JenkinsBell"

env groovy main.groovy monitor