#!/bin/sh

source ./common.sh
source ./ios/ios-do.sh
source ./android/android-do.sh

osType=`echo $mobile | awk -F "_" '{print $1}'`
appType=`echo $mobile | awk -F "_" '{print $2}'`


launch_appium () {
echo "[INFO] executing launch_appium function...";
     > $BUILD_DIR/../appium.log;
     set_mobile_properties;
     if [ "$osType" == "android" ]; then
       android_prerequisites $device;
     elif [ "$osType" == "ios" ]; then
       ios_prerequisites $device;
     fi;
}


install_app () {
echo "[INFO] executing install_app function...";
     set_mobile_properties;
     if [ "$osType" == "android" ]; then
       android_connect;
       android_prepare $appType;
     fi;
}


