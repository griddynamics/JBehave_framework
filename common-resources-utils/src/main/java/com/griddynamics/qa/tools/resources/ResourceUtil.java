package com.griddynamics.qa.tools.resources;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.CodeSource;

import static org.apache.commons.io.FilenameUtils.separatorsToUnix;

/**
 * Class contains methods for working with resources used in framework
 *
 * @author sbashkyrtsev
 */
public class ResourceUtil {

    /**
     * Method returns full path of the resource
     *
     * @param resourcePath - relative path to the resource located on classpath
     * @return
     */
    public static String getResourceFullPath(String resourcePath) {

        return getResourceUrl(resourcePath).getPath();
    }

    /**
     * Method returns full path of resource in local system
     *
     * @param relativePath - relative path to the resource
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getLocalFileSystemFullPath(String relativePath) throws UnsupportedEncodingException {
        return getBasePath() + File.separatorChar + relativePath;
    }

    /**
     * Method converts content of the resource into String object
     *
     * @param resourcePath - path to the resource
     * @return
     * @throws IOException
     */
    public static String getResourceAsString(String resourcePath)
            throws IOException {

        return IOUtils.toString(getResourceAsStream(resourcePath));
    }

    /**
     * Method returns content of the resource as InputStream object
     *
     * @param resourcePath - path to the resource
     * @return
     * @throws IOException
     */
    public static InputStream getResourceAsStream(String resourcePath)
            throws IOException {

        return getResourceUrl(resourcePath).openStream();
    }

    /**
     * Method returns URL of the resource
     *
     * @param relativeResourcePath - relative path to the resource located on classpath
     * @return
     */
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

    /**
     * Method checks if the resource with specified path exists
     *
     * @param relativeResourcePath - relative path to the resource located on classpath
     * @return
     */
    public static boolean isResource(String relativeResourcePath) {
        try {
            getResourceUrl(relativeResourcePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Method returns base path of the Maven project location
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getBasePath() throws UnsupportedEncodingException {
        CodeSource codeSource = ResourceUtil.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().getPath());
        return jarFile.getParentFile().getPath();
    }
}