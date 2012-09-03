#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin
cd "$BIN_DIR"

env groovy "$BIN_DIR/main.groovy" agent > /dev/null &

JB_PID=$!
PID_FILE=$DIR/pid

echo "JenkinsBell is running"
echo "Storing PID: $PID into $PID_FILE"

echo "$JB_PID\n" >> "$PID_FILE"