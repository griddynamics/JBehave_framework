package com.griddynamics.qa.framework.properties;

import com.griddynamics.qa.properties.utils.PropertiesUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class ProjectPropertiesUtils extends PropertiesUtils {
    private static Properties allProperties;
    private static final AtomicBoolean isPropertiesLoaded = new AtomicBoolean(false);

    public static Properties getAllProperties() {
        if ( !isPropertiesLoaded.get() )
            try {
                isPropertiesLoaded.getAndSet(initAllProperties());
            }
            catch (Exception e) {
                assertTrue("Could not import properties from execution/runtime properties file: " + e.getMessage(), false);
            }
        return allProperties;
    }

    public static void setAllProperties(Properties newProperties) {
        allProperties = newProperties;
    }

    synchronized static boolean initAllProperties() throws IOException {
        if (System.getProperty(ProjectProperties.JRUNNER_CONFIG_FILENAME) == null) {
            throw new IllegalArgumentException("System property '" + ProjectProperties.JRUNNER_CONFIG_FILENAME +"' was not set");
        }
        if (System.getProperty(ProjectProperties.ATG_CONFIG_FILENAME) == null) {
            throw new IllegalArgumentException("System property '" + ProjectProperties.ATG_CONFIG_FILENAME +"' was not set");
        }

        Properties runnerProperties = new Properties();
        runnerProperties.load(new BufferedReader(new FileReader(System.getProperty(ProjectProperties.JRUNNER_CONFIG_FILENAME))));
        Properties execProperties = new Properties();
        execProperties.load(new BufferedReader(new FileReader(System.getProperty(ProjectProperties.ATG_CONFIG_FILENAME))));
        runnerProperties.putAll(execProperties);
        setAllProperties(runnerProperties);
        return true;
    }

}
