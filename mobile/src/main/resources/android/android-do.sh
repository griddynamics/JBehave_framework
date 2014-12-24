#!/bin/sh


# name of GenyMotion virtual machine
  vmName=`echo $vmName`

# temporary script to run commands from
  tempRunSh=$BUILD_DIR/run.sh



# Pre-requisites function:  | takes boolean argument, if it's false then emulator needs to be started
#  1) Closes Appium server and all emulators
#  2) Starts emulator if set (if argument is false)
#  3) Starts Appium server
android_prerequisites () {
  echo "[INFO] executing android_prerequisites function...";
  : '(1)' &&   killall player 2>/dev/null;
               kill $(ps -e | grep node | grep "[0-9]. node \./lib/server/main\.js") 2>/dev/null;
               $ADB_PATH/adb kill-server >/dev/null;
               $ADB_PATH/adb start-server >/dev/null
  : '(2)' &&   test "$1" == "true" || execute --in_background "player --vm-name \"$vmName\" 1>/dev/null 2>/dev/null &"
  : '(3)' &&   cd $APPIUM_HOME;
               test -z `cat $APPIUM_HOME/.appiumconfig* | grep -o ndroid | head -1` && ./reset.sh --android --selendroid --ios;
               screen -dmS Appium nohup node ./lib/server/main.js --device-ready-timeout 120 --log-no-colors -g $BUILD_DIR/../../appium.log;
}


#  Waits until device is connected  |  no arguments
android_connect () {
        echo "[INFO] executing android_connect function...";
        printf "[INFO] Waiting until device is connected / emulator is started...";
        $ADB_PATH/adb start-server 1>/dev/null 2>/dev/null;
        count=0;
        until [ "$($ADB_PATH/adb wait-for-device && $ADB_PATH/adb shell dumpsys window | grep mCurrentFocus | grep Keyguard | wc -l)" == "1" ]; do
          printf ".";
          sleep 1; let count+=1;
          #[ $(expr $count % 40) == "0" ] && android_prerequisites "false";
        done; echo "";
}

#  Unlocks device & installs tested app  |  no arguments
android_prepare () {
echo "[INFO] executing android_prepare function...";
: '(0)' &&     if [ "$1" == "browser" ]; then
                 android_prepare_browser;
               fi;
}


#  Prepares application for testing:  |  required argument is application's package
#  0) Verifies app package is supplied as an argument
#  1) Gets application's path and downloads application from device
#  2) Enables debug mode for the application
#  3) Re-installs application to the device
android_prepare_app () {
echo "[INFO] executing android_prepare_app function...";
: '(0)' &&     if [ -z $1 ]; then
                 echo "[ERROR] Android app package must be supplied as argument to 'prepare_app' function (in 'pom.xml' file).";
                 exit 1;
               fi;
               set_property REMOTE_WEBDRIVER_URL="http://localhost:4723/wd/hub";
               cd $BUILD_DIR;
: '(1)' &&     appSystemPath=$($ADB_PATH/adb shell pm path $1 2>&1 | grep -o /.*apk);
               $ADB_PATH/adb pull \$appSystemPath ./$1.apk >/dev/null 2>/dev/null &&
: '(2)' &&     java -jar $APPIUM_HOME/lib/devices/android/helpers/sign.jar ./$1.apk --override >/dev/null &&
: '(3)' &&     $ADB_PATH/adb uninstall $1 >/dev/null &&
               $ADB_PATH/adb install ./$1.apk >/dev/null 2>/dev/null \
               || echo "[ERROR] The app should be re-installed manually, i.e. from Google Play Store." >&2;
}


#  Prepares native browser for testing:  |  no arguments
#  1) (Re-)Starts selendroid server
#  2) Unlocks screen if necessary
android_prepare_browser () {
echo "[INFO] executing android_prepare_browser function...";
               cd $BUILD_DIR;
: '(1)' &&     kill $(jps -m | grep selendroid-standalone | awk -F " " '{print $1}') 2>/dev/null;
               execute --in_background "java -jar $APPIUM_HOME/submodules/selendroid/selendroid-standalone/target/selendroid-standalone-0.12.0-with-dependencies.jar" 1>/dev/null 2>/dev/null;
: '(2)' &&     $ADB_PATH/adb start-server 1>/dev/null 2>/dev/null;
               until [ "$($ADB_PATH/adb connect 192.168.56.101 1>/dev/null 2>/dev/null && $ADB_PATH/adb shell dumpsys window | grep mCurrentFocus | grep Keyguard | wc -l)" == "0" ]; do
                 $ADB_PATH/adb install $APPIUM_HOME/submodules/unlock_apk/bin/unlock_apk-debug.apk 1>/dev/null 2>/dev/null;
                 $ADB_PATH/adb shell am start -n io.appium.unlock/.Unlock 1>/dev/null 2>/dev/null;
               done;
               set_property REMOTE_WEBDRIVER_URL="http://localhost:4444/wd/hub";
}