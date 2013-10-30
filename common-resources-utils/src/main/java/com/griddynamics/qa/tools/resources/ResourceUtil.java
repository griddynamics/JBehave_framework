package com.griddynamics.qa.tools.resources;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.CodeSource;

import static org.apache.commons.io.FilenameUtils.separatorsToUnix;


public class ResourceUtil {

    public static String getResourceFullPath(String resourcePath) {

        return getResourceUrl(resourcePath).getPath();
    }

    public static String getLocalFileSystemFullPath(String relativePath) throws UnsupportedEncodingException {
        return getBasePath() + File.separatorChar + relativePath;
    }

    public static String getResourceAsString(String resourcePath)
            throws IOException {

        return IOUtils.toString(getResourceAsStream(resourcePath));
    }

    public static InputStream getResourceAsStream(String resourcePath)
            throws IOException {

        return getResourceUrl(resourcePath).openStream();
    }

    public static URL getResourceUrl(String relativeResourcePath) {

        Validate.notEmpty(relativeResourcePath);
        relativeResourcePath = separatorsToUnix(relativeResourcePath);

        URL resource = null;
        try {
            resource = Thread.currentThread().getContextClassLoader().getResource(relativeResourcePath);
        } catch (Exception ignored) {
        }
        if (resource == null)
            resource = ClassUtils.class.getResource(relativeResourcePath);

        if (resource == null)
            throw new RuntimeException("Resource '" + relativeResourcePath + "' is not found in the classpath.");

        return resource;
    }


    public static boolean isResource(String relativeResourcePath) {

        if (StringUtils.isEmpty(relativeResourcePath)) {
            return false;
        }

        relativeResourcePath = separatorsToUnix(relativeResourcePath);

        URL resource = null;
        try {
            resource = Thread.currentThread().getContextClassLoader().getResource(relativeResourcePath);
        } catch (Exception ignored) {
        }

        if (resource == null)
            resource = ClassUtils.class.getResource(relativeResourcePath);

        if (resource == null) {
            return false;
        }


        return true;
    }

    public static String getBasePath() throws UnsupportedEncodingException {
        CodeSource codeSource = ResourceUtil.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().getPath());
        return jarFile.getParentFile().getPath();
    }
}