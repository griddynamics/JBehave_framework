package com.griddynamics.qa.properties.utils;

import org.codehaus.plexus.util.StringUtils;

import java.util.Properties;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PropertiesUtils {

    public static final String ERROR_MESSAGE = "Variable %s has invalid value";

    public static boolean doesPropertyExist(Properties props, String propertyName) {
        try {
            if (getProperty(props, propertyName, true).equals("")){
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean doesSpringPropertyExist(String propertyValue) {
        try {
            if (StringUtils.isEmpty(propertyValue)){
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getSpringProperty (String propertyValue, String defaultValue) {
        String property = propertyValue;
        if (StringUtils.isEmpty(property)){
            property = (StringUtils.isEmpty(defaultValue))? property : defaultValue;
        }

        return property;
    }

    public static String getSpringProperty(String propertyValue) {
        return getSpringProperty(propertyValue, "");
    }

    public static String getProperty(Properties props, String propertyName, String defaultValue, boolean allowEmpty) {
        String property = "";
        try {
            if (StringUtils.isEmpty(defaultValue)) {
                property = System.getProperty(propertyName);
                property = (property == null) ? props.getProperty(propertyName) : property;
            } else {
                property = System.getProperty(propertyName, defaultValue);
                property = (property == null) ? props.getProperty(propertyName, defaultValue) : property;
            }
        } catch (Exception e) {
            assertTrue("Could not get system property " + propertyName + ": " + e.getMessage(), false);
        }

        if (!allowEmpty)
            assertThat(String.format(ERROR_MESSAGE, propertyName), property, not(""));

        return property;
    }

    public static String getProperty(Properties props, String propertyName, boolean allowEmpty) {
        return getProperty(props, propertyName, "", allowEmpty);
    }

    public static String getProperty(Properties props, String propertyName) {
        return getProperty(props, propertyName, false);
    }

    public static String getProperty(Properties props, String propertyName, String defaultValue) {
        return getProperty(props, propertyName, defaultValue, false);
    }

}