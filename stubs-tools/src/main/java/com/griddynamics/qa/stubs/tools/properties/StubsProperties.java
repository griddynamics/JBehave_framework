package com.griddynamics.qa.stubs.tools.properties;

import com.griddynamics.qa.properties.utils.PropertiesUtils;

/**
 * Class contains injected by Spring properties which specify
 * stub's ip, port and relative path
 *
 * @author ybaturina
 */
public class StubsProperties {

    private static String stubServiceIp;
    private static String stubServicePort;
    private static String stubServiceRelativePath;

    public static String getStubServiceIp() {
        return PropertiesUtils.getSpringProperty(stubServiceIp);
    }

    public static void setStubServiceIp(String stubServiceIp) {
        StubsProperties.stubServiceIp = stubServiceIp;
    }

    public static String getStubServicePort() {
        return PropertiesUtils.getSpringProperty(stubServicePort);
    }

    public static void setStubServicePort(String stubServicePort) {
        StubsProperties.stubServicePort = stubServicePort;
    }

    public static String getStubServiceRelativePath() {
        return PropertiesUtils.getSpringProperty(stubServiceRelativePath);
    }

    public static void setStubServiceRelativePath(String stubServiceRelativePath) {
        StubsProperties.stubServiceRelativePath = stubServiceRelativePath;
    }

}
