#!/bin/sh -li

cd "$(dirname $0)"
DIR=$(pwd)

cd "$DIR"

PLIST=com.comsysto.JenkinsBell.plist
DEST=~/Library/LaunchAgents
echo "creating $PLIST to $DEST/$PLIST"
cat "$DIR/$PLIST" | sed "s|LOCATION_AGENT|$DIR/runAsAgent.sh|g" > "$DEST/$PLIST"

echo "loading $DEST/$PLIST"
launchctl load "$DEST/$PLIST"