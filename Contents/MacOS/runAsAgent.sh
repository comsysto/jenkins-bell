#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin
cd $BIN_DIR
env groovy "$BIN_DIR/agentMain.groovy" agent