#!/bin/sh -li

scriptpath=$0
case $scriptpath in
 ./*) DIR=$(pwd) ;;
  * ) DIR=$(dirname $scriptpath)
esac

cd "$DIR"

PLIST=com.comsysto.JenkinsBell.plist
DEST=~/Library/LaunchAgents

echo "unloading $DEST/$PLIST"
launchctl unload "$DEST/$PLIST"

echo "removing $DEST/$PLIST"
rm "$DEST/$PLIST"

