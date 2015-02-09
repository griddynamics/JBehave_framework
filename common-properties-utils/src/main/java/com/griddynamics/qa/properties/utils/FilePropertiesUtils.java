/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.properties.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

/**
 * The class contains methods for processing properties files
 *
 * @author ybaturina
 */
public class FilePropertiesUtils {

    /**
     * Reads and returns Properties located in the properties files
     * @param fileNames - the names of the properties files
     * @return Properties object
     */
    public static Properties getPropertiesFromFile(String... fileNames) {
        Properties fileProperties = new Properties();
        for (String fileName : fileNames) {
            try {
                fileProperties.load(new BufferedReader(new FileReader(fileName)));
            } catch (Exception e) {
                throw new RuntimeException("Could not import properties from file " + fileName + ": " + e.getCause());
            }
        }
        return fileProperties;
    }
}
