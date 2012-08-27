#!/bin/sh -li

scriptpath=$0
case $scriptpath in
 ./*) DIR=$(pwd) ;;
  * ) DIR=$(dirname $scriptpath)
esac

export BIN_DIR=$DIR/../bin
cd $BIN_DIR

PID_FILE=$BIN_DIR/../Linux/pid

for PID in $(cut -f 1 $PID_FILE); do
   echo "killing $PID"
   kill $PID
done

rm $PID_FILE