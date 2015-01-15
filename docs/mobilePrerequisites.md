Mobile: Prerequisites  {#prerequisites-mobile}
===============================


Common (for both iOS & Android)
---------------------------

1. Install node.js<br>
> `http://nodejs.org`
<br>

2. Get Appium framework<br>
> `cd ~`<br>
> `git clone https://github.com/appium/appium.git -b v0.11.4`
<br>

3. Move Appium framework to `/usr/local/`<br>
> `mv appium /usr/local/`
<br>


iOS-specific
------------

* Update XCode to 5.0.2 or later version & install Command-Line tools <i>(when update is started, you can proceed to further steps)</i><br>
> `xcode-select --install`
<br>


Android-specific
----------------

1. Download Android SDK<br>
> `https://developer.android.com/sdk/index.html`
<br>

2. Extract Android SDK to `/usr/local/adt/`<br>
> `unzip -qo adt-bundle-*.zip -d /usr/local/adt`
<br>

3. Set `ANDROID_HOME` and `JAVA_HOME` environment variables<br>
> `export JAVA_HOME=$(/usr/libexec/java_home)`<br>
> `export ANDROID_HOME=/usr/local/adt/sdk`
<br>

4. Add `ANDROID_HOME` and `JAVA_HOME` to sh profile <i>(in order not to set them every time)</i><br>
> `echo "export JAVA_HOME=$(/usr/libexec/java_home)" >> ~/.profile`<br>
> `echo "export ANDROID_HOME=/usr/local/adt/sdk" >> ~/.profile`
<br>

5. Install Android build and platform tools<br>
> `$ANDROID_HOME/tools/android update sdk --no-ui --obsolete --force`
<br>

6. Configure Appium for Android<br>
> `cd appium`<br>
> `/usr/local/appium/reset.sh --android --selendroid`
<br>


### Android – device setup

* <b>Enable debugging via USB</b><br>
> – On most devices running Android 3.2 or older, you can find the option under <b>`Settings > Applications > Development`</b><br>
> – On Android 4.0 and newer, it's in <b>`Settings > Developer options`</b><br><br>
> <i><b>Note</b>: On Android 4.2 and newer, Developer options is hidden by default.</i><br>
> <i>To make it available, go to Settings > About phone and tap Build number seven times. Return to the previous screen to find Developer options.</i>
<br>


### Android – emulator setup

1. Install VirtualBox Platform and VirtualBox Extension Pack<br>
> `https://www.virtualbox.org/wiki/Downloads`
<br>

2. Install GenyMotion emulator<br>
> `https://cloud.genymotion.com/page/launchpad/download/`
<br>

3. Download Android image<br>
> `https://docs.google.com/a/griddynamics.com/file/d/0B0H0qQBF_S5pOHI3VHBlS0ZxU0E`
<br>

4. Extract Android image to ~/.Genymobile/Genymotion/deployed/<br>
> `unzip -qo "Android 4.3 VD.zip" -d ~/.Genymobile/Genymotion/deployed`
<br>

5. Open Android image with Virtual Box<br>
> `/Applications/VirtualBox.app/Contents/MacOS/VirtualBox "~/.Genymobile/Genymotion/deployed/Nexus One - 4.2.2 - API 17 - 480x800/Nexus One - 4.2.2 - API 17 - 480x800.vbox"`
<br>