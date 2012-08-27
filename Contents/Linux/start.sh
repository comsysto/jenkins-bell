#!/bin/sh -li

scriptpath=$0
case $scriptpath in
 ./*) DIR=$(pwd) ;;
  * ) DIR=$(dirname $scriptpath)
esac

export BIN_DIR=$DIR/../bin
cd $BIN_DIR

env groovy $DIR/../bin/main.groovy run > /dev/null &

JB_PID=$!
PID_FILE=$BIN_DIR/../Linux/pid

echo "JenkinsBell is running"
echo "Storing PID: $PID into $PID_FILE"

echo "$JB_PID\n" >> $PID_FILE