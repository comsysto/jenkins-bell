#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

PID_FILE=$DIR/pid

for PID in $(cut -f 1 $PID_FILE); do
   echo "killing $PID"
   kill $PID
done

rm "$PID_FILE"