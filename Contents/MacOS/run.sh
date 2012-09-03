#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin
export JAVA_OPTS=-Xdock:name="JenkinsBell"
cd $BIN_DIR
env groovy "main.groovy" monitor