package com.griddynamics.qa.atg.utils.properties;

import com.griddynamics.qa.properties.utils.PropertiesUtils;

/**
 * Class contains properties used for working with ATG dynamo admin via REST
 *
 * @author ybaturina
 */
public class ATGDynAdminProperties {

    private static String dynadminPort;
    private static String dynadminIp;
    private static String dynadminUser;
    private static String dynadminPassw;

    public static String getDynadminIp() {
        return PropertiesUtils.getSpringProperty(dynadminIp);
    }

    public static String getDynadminPort() {
        return PropertiesUtils.getSpringProperty(dynadminPort);
    }

    public static String getDynAdminUser() {
        return PropertiesUtils.getSpringProperty(dynadminUser);
    }

    public static String getDynAdminPassw() {
        return PropertiesUtils.getSpringProperty(dynadminPassw);
    }

    public static void setDynadminPort(String dynadminPort) {
        ATGDynAdminProperties.dynadminPort = dynadminPort;
    }

    public static void setDynadminIp(String dynadminIp) {
        ATGDynAdminProperties.dynadminIp = dynadminIp;
    }

    public static void setDynadminUser(String dynadminUser) {
        ATGDynAdminProperties.dynadminUser = dynadminUser;
    }

    public static void setDynadminPassw(String dynadminPassw) {
        ATGDynAdminProperties.dynadminPassw = dynadminPassw;
    }
}
