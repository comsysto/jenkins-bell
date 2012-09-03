#!/bin/sh -li

scriptpath=$0
case $scriptpath in
 ./*) DIR=$(pwd) ;;
  * ) DIR=$(dirname $scriptpath)
esac

export BIN_DIR=$DIR/../bin
export JAVA_OPTS=-Xdock:name="JenkinsBell"
cd $BIN_DIR
env groovy "$BIN_DIR/main.groovy" monitor