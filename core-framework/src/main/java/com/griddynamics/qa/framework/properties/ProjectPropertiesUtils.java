/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.framework.properties;

import com.griddynamics.qa.properties.utils.FilePropertiesUtils;
import com.griddynamics.qa.properties.utils.PropertiesUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * Class contains methods for setting properties needed by JBehave configs from properties files
 *
 * @author ybaturina
 * @author mlykosova
 */
public class ProjectPropertiesUtils extends PropertiesUtils {
    private static Properties allProperties;
    private static final AtomicBoolean isPropertiesLoaded = new AtomicBoolean(false);

    /**
     * Method initializes properties in case they are not set yet
     * @return Properties object
     */
    public static Properties getAllProperties() {
        if ( !isPropertiesLoaded.get() )
            try {
                isPropertiesLoaded.getAndSet(initAllProperties());
            }
            catch (IOException e) {
                assertTrue("Could not import properties from execution/runtime properties file: " + e.getMessage(), false);
            }
        return allProperties;
    }

    public static void setAllProperties(Properties newProperties) {
        allProperties = newProperties;
    }

    /**
     * By default method gathers properties from file with path provided in {@link com.griddynamics.qa.framework.properties.ProjectProperties#CONFIG_FILENAME}
     * @return
     * @throws IOException
     */
    synchronized static boolean initAllProperties() throws IOException {
        Properties runnerProperties;

        if (ProjectProperties.getConfigFilenameValue() == null) {
            throw new IllegalArgumentException("System property '" + ProjectProperties.CONFIG_FILENAME + "' was not set");
        }

        runnerProperties = FilePropertiesUtils.getPropertiesFromFile(ProjectProperties.getConfigFilenameValue());

        setAllProperties(runnerProperties);
        return true;
    }

}
