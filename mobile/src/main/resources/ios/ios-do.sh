#!/bin/sh


# Pre-requisites function:  | takes boolean argument, if it's false then emulator needs to be started
#  1) Closes Appium server and webproxy
#  2) Copies SafariLauncher from project to Appium directory
#  3) Starts Appium server
ios_prerequisites () {
echo "[INFO] executing ios_prerequisites function...";
  : '(1)' &&   kill $(ps -e | grep node | grep "[0-9]. node \./lib/server/main\.js") 2>/dev/null;
               kill $(ps -e | grep ios_webkit | grep "[0-9]. ios_webkit_debug_proxy") 2>/dev/null;
  : '(2)' &&   mkdir -p $APPIUM_HOME/build/SafariLauncher >/dev/null 2>/dev/null;
               cp -f $BUILD_DIR/mobile/ios/SafariLauncher.zip $APPIUM_HOME/build/SafariLauncher/ >/dev/null 2>/dev/null;
               cd $APPIUM_HOME;
               test -z `cat $APPIUM_HOME/.appiumconfig | grep -o ios` && ./reset.sh --android --selendroid --ios;
  : '(3)' &&   if [ "$1" == "true" ]; then
                deviceId=`system_profiler SPUSBDataType | sed -n -e '/iPad/,/Serial/p' -e '/iPhone/,/Serial/p' | grep "Serial Number:" | awk -F ": " '{print $2}'`;
                screen -dmS Appium nohup node ./lib/server/main.js --log-no-colors -U=$deviceId -g $BUILD_DIR/appium.log;
                screen -dmS webProxy ios_webkit_debug_proxy -c $deviceId:27753 -d;
               else
                screen -dmS Appium nohup node ./lib/server/main.js --log-no-colors -g $BUILD_DIR/appium.log;
               fi;
}