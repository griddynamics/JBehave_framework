package com.griddynamics.qa.tools.resources;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.assertTrue;

/**
 * Class containing common methods to work with files
 *
 * @author ybaturina
 */
public class FileCommonTools {
    private static final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * Read file content and return it as string object
     * {@link #getResource(String)} method is used to resolve {@code pathToFile} param
     *
     * @param pathToFile
     * @return
     * @throws IOException
     */
    public static String readFromFile(String pathToFile) throws IOException {
        return IOUtils.toString(getResource(pathToFile));
    }

    /**
     * Try to find resource in 2 places:
     * <ol>
     * <li>Near JAR file or in Maven target dir</li>
     * <li>In classpath</li>
     * </ol>
     *
     * @param path
     * @return resource {@link InputStream} stream
     * @throws IOException
     */
    public static InputStream getResource(String path) throws IOException {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Argument path is empty or null");
        }

        // At the first search resource by path "JAR dir + path"
        File file = new File(ResourceUtil.getLocalFileSystemFullPath(path));
        if (file.exists()) {
            return new FileInputStream(file);
        }

        // If no file found return resource stream from inside JAR file
        Resource resource = resourcePatternResolver.getResource(path);
        if (!resource.exists()) {
            throw new FileNotFoundException("Resource by path " + path + " was not found");
        }
        return resource.getInputStream();
    }

    /**
     * Returns URL of the resource
     * @param path to resource: either on classpath, or near JAR file/ Maven target dir
     * @return
     * @throws IOException
     */
    public static URL getURL(String path) throws IOException {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Argument path is empty or null");
        }

        // At the first search resource by path "JAR dir + path"
        File file = new File(ResourceUtil.getLocalFileSystemFullPath(path));
        if (file.exists()) {
            return file.toURI().toURL();
        }

        // If no file found return resource stream from inside JAR file
        Resource resource = resourcePatternResolver.getResource(path);
        if (!resource.exists()) {
            throw new FileNotFoundException("Resource by path " + path + " was not found");
        }
        return resource.getURL();
    }

    /**
     * Returns array of Resource objects which have specified extension
     * and are located in the specified folder
     * @param dirName - path to directory on classpath
     * @param ext - files extension
     * @return
     * @throws IOException
     */
    public static Resource[] getFileList(String dirName, final String ext) throws IOException {
        return resourcePatternResolver.getResources("classpath:" + dirName + "/*." + ext);
    }

    /**
     * Returns names of files which are located in the directory
     * @param dirName - path to the directory located on classpath
     * @return
     * @throws IOException
     */
    public static String[] getFileNames(String dirName) throws IOException {
        String normalizedDirName = dirName.endsWith("/") ? dirName.concat("/") : dirName;
        Resource[] resources = resourcePatternResolver.getResources("classpath:" + normalizedDirName + "*");
        String[] paths = new String[resources.length];

        for (int i = 0; i < resources.length; i++) {
            paths[i] = resources[i].getFilename();
        }

        return paths;
    }

    /**
     * Method writes string to file
     *
     * @param file - name of the file on classpath
     * @param content - String which should be stored in the file
     */
    public static void writeToFile(File file, String content) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] Exception occurred while working with file " + file.getPath() + "\n" + e.getMessage());
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException("[ERROR] Exception occurred while working with file " + file.getPath() + "\n" + e.getMessage());
            }
        }
    }

    /**
     * Replaces file content according to the regex pattern
     * @param pathToFile - path to file
     * @param regEx - reqular expression
     * @param stringToReplace - string to replace
     * @throws IOException
     */
    public static void replaceFileContent(String pathToFile, String regEx, String stringToReplace) throws IOException {
        String fileContent = IOUtils.toString(getResource(pathToFile));
        fileContent = fileContent.replaceAll(regEx, stringToReplace);
        FileUtils.write(new File(ResourceUtil.getLocalFileSystemFullPath(pathToFile)), fileContent);
    }

    /**
     * Method returns data from file as list of String objects. Is used for text files with column of some names/search phrases/etc.
     *
     * @param file
     * @return
     */
    public static ArrayList readFromFileAsList(String file) {
        ArrayList<String> fileContent = new ArrayList<String>();
        BufferedReader bufferedReader = null;
        try {
            // Open the file that is the first
            // command line parameter
            InputStream fileInputStream = getResource(file);
            // Get the object of DataInputStream
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            //Read File Line By Line
            while ((strLine = bufferedReader.readLine()) != null) {
                // Print the content on the console
                if (!fileContent.equals("")) {
                    fileContent.add(strLine);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] Exception occurred while working with file " + file + "\n" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(bufferedReader);
        }
        return fileContent;
    }

    /**
     * Copy resource from classpath (from inside Jar) to local filesystem
     * to directory where Jar file is placed plus relative resource path
     *
     * @param pathToFile - relative path inside classpath to resource
     * @return Full path on local file system to copied file
     * @throws IOException
     */
    public static String copyToLocalFileSystem(String pathToFile) throws IOException {
        Resource resource = resourcePatternResolver.getResource(pathToFile);
        String destination = ResourceUtil.getLocalFileSystemFullPath(pathToFile);

        FileUtils.copyInputStreamToFile(resource.getInputStream(), new File(destination));

        return destination;
    }

    /**
     * Copy folder from classpath (from inside Jar) to local filesystem
     * to directory where Jar file is placed plus relative resource path
     *
     * @param pathToDestination - relative resource path
     * @param pathToFolder      - relative path inside classpath to folder
     * @throws Exception
     */
    public static void copyFolderToLocalFileSystem(String pathToDestination, String pathToFolder) throws Exception {
        String destination = ResourceUtil.getLocalFileSystemFullPath(pathToDestination);
        File configHome = new File(destination, pathToFolder);
        configHome.mkdirs();
        copyResourcesRecursively(FileCommonTools.class.getResource("/" + pathToFolder), configHome);
    }

    private static void copyResourcesRecursively(URL originUrl, File destination) throws Exception {
        URLConnection urlConnection = originUrl.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
        } else if (urlConnection instanceof FileURLConnection) {
            copyFilesRecusively(new File(originUrl.getPath()), destination);
        } else {
            throw new Exception("URLConnection[" + urlConnection.getClass().getSimpleName() +
                    "] is not a recognized/implemented connection type.");
        }
    }

    private static void copyJarResourcesRecursively(File destination, JarURLConnection jarConnection) throws IOException {
        JarFile jarFile = jarConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry entry = jarEntries.nextElement();
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                String fileName = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
                if (!entry.isDirectory()) {
                    InputStream entryInputStream = null;
                    try {
                        entryInputStream = jarFile.getInputStream(entry);
                        FileUtils.copyInputStreamToFile(entryInputStream, new File(destination, fileName));
                    } finally {
                        entryInputStream.close();
                    }
                } else {
                    ensureDirectoryExists(new File(destination, fileName));
                }
            }
        }
    }

    private static boolean copyFile(final File toCopy, final File destFile) throws Exception {
        return copyStream(new FileInputStream(toCopy),
                new FileOutputStream(destFile));
    }

    private static boolean copyFilesRecusively(final File toCopy,
                                               final File destDir) throws Exception {
        assertTrue("[ERROR] Resource " + destDir.getAbsolutePath() + " is not a directory!", destDir.isDirectory());

        if (!toCopy.isDirectory()) {
            return copyFile(toCopy, new File(destDir, toCopy.getName()));
        } else {
            final File newDestDir = new File(destDir, toCopy.getName());
            if (!newDestDir.exists() && !newDestDir.mkdir()) {
                return false;
            }
            for (final File child : toCopy.listFiles()) {
                if (!copyFilesRecusively(child, newDestDir)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean copyStream(final InputStream is, final OutputStream os) throws IOException {
        final byte[] buf = new byte[1024];

        int len = 0;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        is.close();
        os.close();
        return true;
    }

    private static boolean ensureDirectoryExists(final File f) {
        return f.exists() || f.mkdir();
    }
}
