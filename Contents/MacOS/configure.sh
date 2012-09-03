#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin
cd $BIN_DIR

env groovy $DIR/../bin/main.groovy configure > /dev/null &