package com.griddynamics.qa.properties.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class FilePropertiesUtils {

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
