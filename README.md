README:
JenkinsBell is a script that loops forever and polls jenkins for the configured job states until its killed.

The script has multiple ways to announce changes of the build states:
    * Use a text-to-speech tool of the OS, the command can be configured in the config file
    * Open a swing window
    * Create report files that can be used in command line scripts or MacOS GeekTools
      This reports are stored in the ~/.jenkins-bell/report folder

The script stores the build state persistent on disk in the ~/.jenkins-bell/state folder.
A short log file is created at ~/.jenkins-bell/log


how to install:
    create the  '~/.jenkins-bell' directory in your home folder
    copy 'config' file in to ~/.jenkins-bell/config

manual start up on *nix and MacOS X:
    install groovy via package manger
    change file permissions "chmod ug+x JenkinsBell.groovy"
    start JenkisBell via "./JenkinsBell.groovy"

install as LaunchAgent on MacOS X:
    fix path to the JenkinsBell.groovy in the file com.comsysto.JenkinsBell.plist
    copy com.comsysto.JenkinsBell.plist into ~/Library/LaunchAgent/
    install the agent, insteed of relogging: "launchctl load ~/Library/LaunchAgent/com.comsysto.JenkinsBell.plist"
    now check with "jps" if a GroovyStarter process is running

install with geektools
    http://itunes.apple.com/de/app/geektool/id456877552?mt=12



install as startup item on Ubuntu
    http://www.howtogeek.com/howto/ubuntu/how-to-add-a-program-to-the-ubuntu-startup-list-after-login/