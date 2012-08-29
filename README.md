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

    state: in this directory the last seen state of a job is persisted
    report: in this directory a report of the current state of a job is located
    config.xml: this file contains the settings of JenkinsBell

how to install:
    on MacOS
        * install git (for example via home brew)
        * install groovy (for example via home brew)
        * git clone git@github.com:comsysto/jenkins-bell.git /Applications/JenkinsBell.app
        * double click the application to start
        * configure the app with SystemTray->Configure Jenkins Bell...

    on Linux
        * install groovy (for example via apt-get)
        * git clone git@github.com:comsysto/jenkins-bell.git
        * configure the app via $INSTALL_DIR/Contents/Linux/configure.sh
        * start the app via  $INSTALL_DIR/Contents/Linux/start.sh
        * stop the app via  $INSTALL_DIR/Contents/Linux/stop.sh

install with geektools
    http://itunes.apple.com/de/app/geektool/id456877552?mt=12

install as startup item on Ubuntu
    http://www.howtogeek.com/howto/ubuntu/how-to-add-a-program-to-the-ubuntu-startup-list-after-login/
