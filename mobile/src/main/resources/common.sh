#!/bin/sh


mobilePropertiesPath="./../classes/mobile.properties";

#  Set environment variables
export ANDROID_HOME="/usr/local/adt/sdk"
export ADB_PATH="$ANDROID_HOME/platform-tools"
export APPIUM_HOME="/usr/local/appium"
export BUILD_DIR=`pwd`
export JAVA_HOME=`/usr/libexec/java_home`
export PATH="$PATH:/bin:/usr/bin:/usr/local/bin:$JAVA_HOME/bin:$ANDROID_HOME/platform-tools/:/Applications/Genymotion.app/Contents/MacOS"


#  Run commands in background or in shell
function execute {
  if [ "$1" == "--in_background" ]; then
    echo $2 > $tempRunSh; chmod +x $tempRunSh; sh $tempRunSh &
  else
    echo $1 > $tempRunSh; chmod +x $tempRunSh; sh $tempRunSh
  fi
}


#  Set property in 'mobile.properties' file
function set_property {
     propertyName=`echo $1 | awk -F "=" '{print $1}'`;
     if [ -n "$(grep $propertyName $mobilePropertiesPath)" ]; then
       sed -i' ' -e "s|$propertyName=.*|$1|g" $mobilePropertiesPath;
     else
       echo "$1" >> $mobilePropertiesPath;
     fi;
}


#  Get property value from 'mobile.properties' file
function get_property {
     grep "$1=" $mobilePropertiesPath | awk -F '=' '{print $2}'
}


#  Create a copy of 'mobile.properties' file, in order to override properties
function cp_properties_file {
     oldPath=`find ./../../src -name mobile.properties`;
     mkdir $(dirname $mobilePropertiesPath) 2>/dev/null;
     cp $oldPath $mobilePropertiesPath;
}


function set_mobile_properties {

     cp_properties_file;

     set_property browser="$mobile";
     set_property REMOTE_WEBDRIVER_URL="http://localhost:4723/wd/hub";
     set_property spring.profiles.active="mobile,remote";

     if [ $osType == "android" ] && [ $appType == "app" ]; then
           fullApkPath=`find ./../.. -name $(get_property android.app) -print -quit`;
       set_property android.app="$fullApkPath";
       cp "$fullApkPath" temp.apk; appPackage=`$ANDROID_HOME/build-tools/aapt dump badging temp.apk | grep -o "package: name='[^']*" | awk -F "'" '{print $2}'`; rm temp.apk;
       set_property android.appPackage="$appPackage";

     elif [ $osType == "ios" ] && [ $appType == "app" ]; then
       fullIpaPath=`find ./../.. -name $(get_property ios.app) -print -quit`
       set_property ios.app="$fullIpaPath";
     fi;
}