#!/bin/sh -li

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=`readlink -f $0`
# Absolute path this script is in, thus /home/user/bin
DIR=`dirname $SCRIPT`

export BIN_DIR=$DIR/../bin
cd $BIN_DIR

PID_FILE=$BIN_DIR/../Linux/pid

for PID in $(cut -f 1 $PID_FILE); do
   echo "killing $PID"
   kill $PID
done

rm $PID_FILE