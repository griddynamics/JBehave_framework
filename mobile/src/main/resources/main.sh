#!/bin/sh

source ./common.sh
source ./ios/ios-do.sh
source ./android/android-do.sh

osType=`echo $mobile | awk -F "_" '{print $1}'`
appType=`echo $mobile | awk -F "_" '{print $2}'`


function launch_appium {
     set_mobile_properties;
     if [ "$osType" == "android" ]; then
       android_prerequisites $device;
     elif [ "$osType" == "ios" ]; then
       ios_prerequisites $device;
     fi;
}


function install_app {
     set_mobile_properties;
     if [ "$osType" == "android" ]; then
       android_connect;
       android_prepare $appType;
     fi;
}


