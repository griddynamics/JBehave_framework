package com.griddynamics.qa.framework;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jbehave.web.selenium.RemoteWebDriverProvider;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Locale;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;


/**
 * The class initializes remote instance of Selenium WebDriver
 *
 * @author lzakharova
 * @author ybaturina
 * @author dcheremushkin
 */
public class CustomRemoteWebDriverProvider extends RemoteWebDriverProvider {
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String TRUE = "true";

    private final static String PROJECT_BUILD_DIRECTORY = "project.build.directory";

    private final static String BROWSER = "browser";
    private final static String BROWSER_DEFAULT = "Firefox";

    private final static String BROWSER_PROXY_IP = "browser.proxy.ip";
    private final static String BROWSER_PROXY_PORT = "browser.proxy.port";
    private final static String BROWSER_PROXY_USER = "browser.proxy.user";
    private final static String BROWSER_PROXY_PASS = "browser.proxy.password";

    private final static String MOBILE = "mobile";
    private final static String MOBILE_BUILD_DIRECTORY = System.getProperty(PROJECT_BUILD_DIRECTORY) + "/mobile/";
    private final static String ANDROID_APPLICATION = "android.app";
    private final static String ANDROID_APPLICATION_WEBDRIVER = "android/android-driver-app-0.12.0.apk";
    //private final static String ANDROID_APPLICATION_WEBDRIVER = "android/android-server-2.21.0.apk";
    private final static String ANDROID_ACTIVITY_MAIN = "android.mainActivity";
    private final static String ANDROID_ACTIVITY_WAIT = "android.waitActivity";
    private final static String ANDROID_PACKAGE = "android.appPackage";
    private final static String IOS_APPLICATION = "ios.app";
    private final static String IOS_SIMULATOR = "ios.simulatorType";
    private final static String IOS_VERSION = "ios.version";

    private static DesiredCapabilities capabilities = getCapabilityFromProperty();
    static Logger logger = Logger.getLogger("Firefox profile settings.");

    /**
     * Enumeration of supported browsers
     */
    private enum CustomBrowser {
        CHROME, FIREFOX, HTMLUNIT, IE, SAFARI, ANDROID_APP, ANDROID_BROWSER, IOS_APP, IOS_SAFARI
    }

    public CustomRemoteWebDriverProvider() {
        super(getCapabilities(capabilities));
    }


    public CustomRemoteWebDriverProvider(DesiredCapabilities capabilities) {
        super(getCapabilities(capabilities));
    }

    /**
     * Returns Selenium's default browser-specific capabilities, depending on
     * — {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#BROWSER} property for desktop browsers;
     * – or {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#MOBILE} property for mobile browsers & apps.
     *
     * Note: By default {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#BROWSER_DEFAULT} browser is used
     * (if {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#BROWSER} property is not set)
     *
     * @return DesiredCapabilities object
     */
    public static DesiredCapabilities getCapabilityFromProperty() {
        CustomBrowser browser = CustomBrowser.valueOf(CustomBrowser.class, System.getProperty(BROWSER, BROWSER_DEFAULT).toUpperCase(Locale.getDefault()));
        switch (browser) {
            case CHROME:
                return DesiredCapabilities.chrome();
            case FIREFOX:
                return DesiredCapabilities.firefox();
            case HTMLUNIT:
            default:
                return DesiredCapabilities.htmlUnit();
            case IE:
                return DesiredCapabilities.internetExplorer();
            case SAFARI:
                return DesiredCapabilities.safari();
            case ANDROID_APP:
                return DesiredCapabilities.android();
            case ANDROID_BROWSER:
                return DesiredCapabilities.android();
            case IOS_APP:
                return DesiredCapabilities.ipad();
            case IOS_SAFARI:
                return DesiredCapabilities.ipad();
        }
    }

    /**
     * Returns additional browser capabilities, depending on
     * — {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#BROWSER} property for desktop browsers;
     * – or {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#MOBILE} property for mobile browsers & apps.
     *
     * Note: By default {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#BROWSER_DEFAULT} browser is used
     * (if {@link com.griddynamics.qa.framework.CustomRemoteWebDriverProvider#BROWSER} property is not set)
     *
     * @param oldCapabilities - pre-defined capabilities (see previous method)
     * @return DesiredCapabilities object
     */
    public static DesiredCapabilities getCapabilities(DesiredCapabilities oldCapabilities) {
        CustomBrowser browser = CustomBrowser.valueOf(CustomBrowser.class, System.getProperty(BROWSER, BROWSER_DEFAULT).toUpperCase(Locale.getDefault()));
        switch (browser) {

            case FIREFOX:
                FirefoxProfile profile = new FirefoxProfile();

                profile.setPreference("network.http.phishy-userpass-length", 255);
                profile.setPreference("network.automatic-ntlm-auth.allow-non-fqdn", "true");
                profile.setPreference("network.ntlm.send-lm-response", "true");
                profile.setPreference("dom.max_script_run_time", 30);
                profile.setPreference("capability.policy.default.Window.QueryInterface", "allAccess");
                profile.setPreference("capability.policy.default.Window.frameElement.get", "allAccess");

                if (!StringUtils.isEmpty(System.getProperty(BROWSER_PROXY_IP))) {
                    String proxy_ip = System.getProperty(BROWSER_PROXY_IP);
                    int proxy_port = Integer.valueOf(System.getProperty(BROWSER_PROXY_PORT));
                    String[] protocols = {"http", "ssl", "socks"};
                    for (String protocol : protocols) {
                        profile.setPreference("network.proxy." + protocol, proxy_ip);
                        profile.setPreference("network.proxy." + protocol + "_port", proxy_port);
                    }
                    profile.setPreference("network.proxy.type", 1);
                }

                oldCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
                break;

            case ANDROID_BROWSER:
                //oldCapabilities.setCapability("app", MOBILE_BUILD_DIRECTORY + ANDROID_APPLICATION_WEBDRIVER);
                oldCapabilities.setCapability("app-package", "io.selendroid.androiddriver");
                oldCapabilities.setCapability("app-activity", ".WebViewActivity");
                //oldCapabilities.setCapability("app", MOBILE_BUILD_DIRECTORY + ANDROID_APPLICATION_WEBDRIVER);
                oldCapabilities.setCapability("automationName", "Selendroid");
                oldCapabilities.setCapability("platformName", "Android");
                oldCapabilities.setCapability("browserName", "android");
                oldCapabilities.setCapability("deviceName", "Android Emulator");
                oldCapabilities.setCapability(ACCEPT_SSL_CERTS, "true");
                oldCapabilities.setJavascriptEnabled(true);
                break;

            case ANDROID_APP:
                oldCapabilities.setCapability("app", MOBILE_BUILD_DIRECTORY + System.getProperty(ANDROID_APPLICATION));
                oldCapabilities.setCapability("app-package", System.getProperty(ANDROID_PACKAGE));
                oldCapabilities.setCapability("app-activity", System.getProperty(ANDROID_ACTIVITY_MAIN));
                oldCapabilities.setCapability("app-wait-activity", System.getProperty(ANDROID_ACTIVITY_WAIT));
                oldCapabilities.setCapability("device", "Selendroid");
                break;

            case IOS_SAFARI:
                oldCapabilities.setCapability("app", "safari");
                oldCapabilities.setCapability("platformName", "iOS");
                oldCapabilities.setCapability("platformVersion", System.getProperty(IOS_VERSION));
                oldCapabilities.setCapability("deviceName", System.getProperty(IOS_SIMULATOR));
                oldCapabilities.setCapability(ACCEPT_SSL_CERTS, "true");
                oldCapabilities.setJavascriptEnabled(true);
                break;

            case IOS_APP:
                oldCapabilities.setCapability("app", MOBILE_BUILD_DIRECTORY + System.getProperty(IOS_APPLICATION));
                oldCapabilities.setCapability("deviceName", System.getProperty(IOS_SIMULATOR));
                oldCapabilities.setCapability("version", System.getProperty(IOS_VERSION));
                break;
        }
        return oldCapabilities;
    }

}
