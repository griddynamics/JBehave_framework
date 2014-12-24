#!/bin/sh


mobilePropertiesPath="./../classes/mobile.properties";

export BUILD_DIR=`pwd`


#  Run commands in background or in shell
execute () {
  if [ "$1" == "--in_background" ]; then
    echo $2 > $tempRunSh; chmod +x $tempRunSh; sh $tempRunSh &
  else
    echo $1 > $tempRunSh; chmod +x $tempRunSh; sh $tempRunSh
  fi
}


#  Set property in 'mobile.properties' file
set_property () {
     propertyName=`echo $1 | awk -F "=" '{print $1}'`;
     if [ -n "$(grep $propertyName $mobilePropertiesPath)" ]; then
       sed -i' ' -e "s|$propertyName=.*|$1|g" $mobilePropertiesPath;
     else
       echo "$1" >> $mobilePropertiesPath
     fi;
}


#  Get property value from 'mobile.properties' file
get_property () {
     grep "$1=" $mobilePropertiesPath | awk -F '=' '{print $2}'
}


#  Create a copy of 'mobile.properties' file, in order to override properties
cp_properties_file () {
echo "[INFO] executing cp_properties_file function...";
     oldPath=`find ./../../src -name mobile.properties`;
     mkdir $(dirname $mobilePropertiesPath) 2>/dev/null;
     platform=`echo $(uname -s)`;
     platformProps=`find ./../../src -name $platform.properties`;
     cat $oldPath $platformProps > $mobilePropertiesPath;

    export ANDROID_HOME=$(get_property ANDROID_HOME);
    export ADB_PATH=$(get_property ADB_PATH);
    export APPIUM_HOME=$(get_property APPIUM_HOME);
    export GENY_HOME=$(get_property GENY_HOME);
    export PATH="$PATH:/bin:/usr/bin:/usr/local/bin:$JAVA_HOME/bin:$ANDROID_HOME/platform-tools/:$GENY_HOME";
}


set_mobile_properties () {
echo "[INFO] executing set_mobile_properties function...";
     cp_properties_file;

     set_property browser="$mobile";
     set_property REMOTE_WEBDRIVER_URL="http://localhost:4723/wd/hub";
     set_property spring.profiles.active="mobile,remote";

     if [ $osType == "android" ] && [ $appType == "app" ]; then
           fullApkPath=`find ./../.. -name $(get_property android.app) -print -quit`;
       set_property android.app="$fullApkPath";
       cp "$fullApkPath" temp.apk; appPackage=`$ANDROID_HOME/build-tools/21.1.2/aapt dump badging temp.apk | grep -o "package: name='[^']*" | awk -F "'" '{print $2}'`; rm temp.apk;
       set_property android.appPackage="$appPackage";

     elif [ $osType == "ios" ] && [ $appType == "app" ]; then
       fullIpaPath=`find ./../.. -name $(get_property ios.app) -print -quit`
       set_property ios.app="$fullIpaPath";
     fi;
}