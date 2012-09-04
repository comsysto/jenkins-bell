#!/bin/sh -li

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=`readlink -f $0`
# Absolute path this script is in, thus /home/user/bin
DIR=`dirname $SCRIPT`

export BIN_DIR=$DIR/../bin
cd "$BIN_DIR"

COMMAND=$1
if [ -z "$COMMAND" ]; then
    COMMAND=monitor
fi

env groovy "$BIN_DIR/main.groovy" "$COMMAND" > /dev/null &

JB_PID=$!
PID_FILE=$DIR/pid

echo "JenkinsBell is running"
echo "Storing PID: $PID into $PID_FILE"

echo "$JB_PID\n" >> "$PID_FILE"