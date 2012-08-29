jenkins-bell
============

JenkinsBell is a groovy script to monitor the build result of certain jobs of multiple Jenkins instances.

There are multiple ways that a change in the state of a job can be announced:
    * Use a text-to-speech tool to read the name of the job and the build result, The command that is used can be configured dependent on the OS.
    * Open a message window
    * Create report files that can be used in command line scripts or MacOS GeekTools.
      This reports are stored in the ~/.jenkins-bell/report folder

The script creates a hidden directory in your user home folder with the name '.jenkins-bell'.
This directory has the following layout:
    .jenkins-bell
    ├── config.xml    # this file contains the settings of JenkinsBell
    ├── log           # this file contains summary of the configured builds and the last state changes
    ├── report        # in this directory a report of the current state of a job is located
    │   ├── Test
    │   └── overview  # this file contains a overview of the state of all configured builds
    └── state         # in this directory the last seen state of a job is persisted
        └── Test

# Install and Run#

## Install on MacOS ##
* install git (for example via home brew)
* install groovy (for example via home brew)
* git clone git://github.com/comsysto/jenkins-bell.git

## Run on MacOS as Application ##
* double click the application
* open the configuration window by selecting "Open Configuration" in the context menu of the dock icon

## Run on MacOS as LaunchAgent ##
* open the configuration window with the script
    /Applications/JenkinsBell.app/Contents/MacOS/configure.sh
* run the script to install JenkinsBell as MacOS LaunchAgent
    /Applications/JenkinsBell.app/Contents/MacOS/installLaunchAgent.sh

## Use on MacOS with geektools ##
* install geektools from (http://itunes.apple.com/de/app/geektool/id456877552?mt=12 "App Store")
* create a script geeklet by dragging the script element to the desktop
* enter the as script command for the geeklet
    cat ~/.jenkins-bell/report/overview
* configure a refresh interval of 60s
* create more geeklets by displaying the other files in the ~/.jenkins-bell/report directory

## Install on Linux ##
* install git (for example via apt-get)
* install groovy (for example via apt-get)
* git clone git://github.com/comsysto/jenkins-bell.git

# Run on Linux ##
* configure the app via $INSTALL_DIR/Contents/Linux/configure.sh
* start the application via
    $INSTALL_DIR/Contents/Linux/start.sh
* stop the application via
    $INSTALL_DIR/Contents/Linux/stop.sh

### Install as startup item on Ubuntu ###
* see this (http://www.howtogeek.com/howto/ubuntu/how-to-add-a-program-to-the-ubuntu-startup-list-after-login "howto")



