#!/bin/sh


# version of AndroidBrowser server component
  aBrowserVersion=2.21.0

# name of GenyMotion virtual machine
  vmName="Nexus One - 4.2.2 - API 17 - 480x800"

# command to verify if screen is locked
  screenLockedCheck="[ \$(/usr/local/adt/sdk/platform-tools/adb shell dumpsys window | grep -o mShowingLockscreen=true | wc -l) -ge 1 ]"

# temporary script to run commands from
  tempRunSh=$BUILD_DIR/run.sh



# Pre-requisites function:  | takes boolean argument, if it's false then emulator needs to be started
#  1) Closes Appium server and all emulators
#  2) Starts emulator if set (if argument is false)
#  3) Starts Appium server
function android_prerequisites {
  : '(1)' &&   killall player 2>/dev/null;
               kill $(ps -e | grep node | grep "[0-9]. node \./lib/server/main\.js") 2>/dev/null;
  : '(2)' &&   test "$1" == "true" || execute --in_background "player --vm-name \"$vmName\" 1>/dev/null 2>/dev/null &"
  : '(3)' &&   cd $APPIUM_HOME;
               test -z `cat $APPIUM_HOME/.appiumconfig | grep -o ndroid | head -1` && ./reset.sh --android --selendroid --ios;
               screen -dmS Appium nohup node ./lib/server/main.js --device-ready-timeout 120 --log-no-colors -g $BUILD_DIR/appium.log;
}


#  Waits until device is connected  |  no arguments
#   currently install and uninstalls Unlock.apk, as default install command waits for device
function android_connect {
        echo "[INFO] Waiting until device is connected / emulator is started...";
        $ADB_PATH/adb install -r /usr/local/appium/build/unlock_apk/unlock_apk-debug.apk >/dev/null;
        $ADB_PATH/adb $ADB_PATH/adb uninstall -l io.appium.unlock >/dev/null 2>/dev/null;
}

#  Unlocks device & installs tested app  |  no arguments
function android_prepare {
: '(0)' &&     if [ "$1" == "browser" ]; then
                 android_prepare_browser;
               fi;
}


#  Prepares application for testing:  |  required argument is application's package
#  0) Verifies app package is supplied as an argument
#  1) Gets application's path and downloads application from device
#  2) Enables debug mode for the application
#  3) Re-installs application to the device
function android_prepare_app {
: '(0)' &&     if [ -z $1 ]; then
                 echo "[ERROR] Android app package must be supplied as argument to 'prepare_app' function (in 'pom.xml' file).";
                 exit 1;
               fi;
               cd $BUILD_DIR;
: '(1)' &&     appSystemPath=$($ADB_PATH/adb shell pm path $1 2>&1 | grep -o /.*apk);
               $ADB_PATH/adb pull \$appSystemPath ./$1.apk >/dev/null 2>/dev/null &&
: '(2)' &&     java -jar $APPIUM_HOME/lib/devices/android/helpers/sign.jar ./$1.apk --override >/dev/null &&
: '(3)' &&     $ADB_PATH/adb uninstall $1 >/dev/null &&
               $ADB_PATH/adb install ./$1.apk >/dev/null 2>/dev/null \
               || echo "[ERROR] The app should be re-installed manually, i.e. from Google Play Store." >&2;
}


#  Prepares native browser for testing:  |  no arguments
#  1) (Re-)installs WebDriver server application  (required to test native browser)
#  2) Installs Unlock application, to unlock screen automatically
#  3) Unlocks screen if necessary
#  4) Starts WebDriver application
#  5) Sets port redirection rules for WebDriver application, to communicate with it
function android_prepare_browser {
               cd $BUILD_DIR;
: '(1)' &&     $ADB_PATH/adb uninstall org.openqa.selenium.android.app >/dev/null;
               $ADB_PATH/adb install ../src/main/resources/com/kohls/mobile/android/android-server-$aBrowserVersion.apk >/dev/null;
: '(2)' &&     $ADB_PATH/adb install $APPIUM_HOME/build/unlock_apk/unlock_apk-debug.apk >/dev/null;
: '(3)' &&     $screenLockedCheck && $ADB_PATH/adb shell am start -n io.appium.unlock/.Unlock >/dev/null;
: '(4)' &&     $ADB_PATH/adb shell am start -a android.intent.action.MAIN -n org.openqa.selenium.android.app/.MainActivity >/dev/null;
: '(5)' &&     $ADB_PATH/adb forward tcp:8080 tcp:8080;
               $ADB_PATH/adb forward tcp:6080 tcp:8080
}