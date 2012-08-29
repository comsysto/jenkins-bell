#!/bin/sh -li

scriptpath=$0
case $scriptpath in
 ./*) DIR=$(pwd) ;;
  * ) DIR=$(dirname $scriptpath)
esac

export BIN_DIR=$DIR/../bin
cd $BIN_DIR
env groovy $DIR/../bin/agentMain.groovy run