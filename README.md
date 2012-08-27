jenkins-bell
============

JenkinsBell is a groovy script that is packaged as a MacOS Application.

The script has multiple ways to announce changes of the build states:
    * Use a text-to-speech tool of the OS, the command can be configured in the config file
    * Open a swing window
    * Create report files that can be used in command line scripts or MacOS GeekTools
      This reports are stored in the ~/.jenkins-bell/report folder

The script stores the build state persistent on disk in the ~/.jenkins-bell/state folder.
A short log file is created at ~/.jenkins-bell/log


how to install:
    on MacOS
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
