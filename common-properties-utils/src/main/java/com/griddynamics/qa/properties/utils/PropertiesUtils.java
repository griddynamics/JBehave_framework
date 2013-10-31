package com.griddynamics.qa.properties.utils;

import org.codehaus.plexus.util.StringUtils;

import java.util.Properties;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * The class contains methods for processing properties
 *
 * @author ybaturina
 */
public class PropertiesUtils {

    public static final String ERROR_MESSAGE = "Variable %s has invalid value";

    /**
     * Checks if property exists
     * @param props - Properties object
     * @param propertyName
     * @return
     */
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

    /**
     * Checks if the property was injected by Spring
     * @param propertyValue
     * @return
     */
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

    /**
     * Processes injected by Spring property value in case if it is empty, and the property
     * should have default value
     * @param propertyValue
     * @param defaultValue
     * @return final property value as String
     */
    public static String getSpringProperty (String propertyValue, String defaultValue) {
        String property = propertyValue;
        if (StringUtils.isEmpty(property)){
            property = (StringUtils.isEmpty(defaultValue))? property : defaultValue;
        }

        return property;
    }

    /**
     * Returns the property value when the default value is optional
     * @param propertyValue
     * @return
     */
    public static String getSpringProperty(String propertyValue) {
        return getSpringProperty(propertyValue, "");
    }

    /**
     * Processes property from Properties object and/or system properties
     * @param props - Properties object
     * @param propertyName
     * @param defaultValue
     * @param allowEmpty - flag indicating if the property value can be empty
     * @return final property value as String
     */
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

    /**
     * Processes the property from Properties object and/or system properties in case when default value is absent
     * @param props - Properties object
     * @param propertyName
     * @param allowEmpty - flag indicating if the property value can be empty
     * @return final property value as String
     */
    public static String getProperty(Properties props, String propertyName, boolean allowEmpty) {
        return getProperty(props, propertyName, "", allowEmpty);
    }

    /**
     * Processes the property from Properties object and/or system properties in case when default value is absent
     * and the property is mandatory
     * @param props - Properties object
     * @param propertyName
     * @return final property value as String
     */
    public static String getProperty(Properties props, String propertyName) {
        return getProperty(props, propertyName, false);
    }

    /**
     * Processes the property from Properties object and/or system properties in case when the property is mandatory
     * @param props - Properties object
     * @param propertyName
     * @param defaultValue
     * @return final property value as String
     */
    public static String getProperty(Properties props, String propertyName, String defaultValue) {
        return getProperty(props, propertyName, defaultValue, false);
    }

}