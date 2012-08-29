#!/bin/sh -li

scriptpath=$0
case $scriptpath in
 ./*) DIR=$(pwd) ;;
  * ) DIR=$(dirname $scriptpath)
esac

cd "$DIR"

PLIST=com.comsysto.JenkinsBell.plist
DEST=~/Library/LaunchAgents
echo "creating $PLIST to $DEST/$PLIST"
cat "$DIR/$PLIST" | sed "s|LOCATION_AGENT|$DIR/runAsAgent.sh|g" > "$DEST/$PLIST"

echo "loading $DEST/$PLIST"
launchctl load "$DEST/$PLIST"