/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.framework;

import com.griddynamics.qa.framework.logger.CustomPrintStreamEmbedderMonitor;
import com.griddynamics.qa.framework.properties.ProjectProperties;
import com.griddynamics.qa.tools.resources.FileCommonTools;
import org.jbehave.core.embedder.*;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.griddynamics.qa.framework.converters.StackTraceConverter.getStringFromStackTrace;
import static com.griddynamics.qa.logger.LoggerFactory.getLogger;

/**
 * Main class for executing tests from JAR file
 *
 * @author akirillov
 * @author ybaturina
 */
public class JBehaveJarRunner {
    private static final String CLASS_EXTENSION = ".class";
    private static final char PACKAGE_DELIMITER_CHAR = '.';
    private static final char PATH_SEPARATOR_CHAR = '/';
    private static final String CSS_REPORT_FOLDER = new StringBuilder("jbehave").append(File.separator)
            .append("view").toString();
    private static final List<String> CSS_STYLE_FOLDERS = Arrays.asList("ftl", "i18n","images","js","style");

    private static final String storyFinderClass = StoryFinder.class.getName();
    private static final String embedderClass = Embedder.class.getName();

    protected EmbedderClassLoader classLoader;
    protected List<String> includes;
    protected List<String> excludes;

    public static void main(String[] args) {
        new JBehaveJarRunner().execute();
    }

    public JBehaveJarRunner() {
        classLoader = new EmbedderClassLoader(Arrays.asList(searchDirectory()));
        includes = getIncludedSuiteList();
        excludes = Collections.emptyList();
    }

    /**
     * Configures which suites should be run (suites path pattern should be provided in
     * {link com.griddynamics.qa.framework.properties.ProjectProperties#STORY_LIST_PROPERTY_NAME})
     * @return List String included Suite list
     */
    protected List<String> getIncludedSuiteList(){
       return Arrays.asList(ProjectProperties.getSuiteAll().split(","));
    }

    /**
     * Extracts JBehave reports CSS folders from the archive
     */
    protected void copyCssReportsStyles() {
        try {
            for (String folderPath : CSS_STYLE_FOLDERS) {
                FileCommonTools.copyFolderToLocalFileSystem(CSS_REPORT_FOLDER, folderPath);
            }
        } catch (Exception e) {
            getLogger().error("Couldn't copy css styles for JBehave reports, reason: " + getStringFromStackTrace(e));
        }
    }

    protected void execute() {
        Embedder embedder = newEmbedder();
        getLogger().info("Running stories as embeddables using embedder " + embedder);
        copyCssReportsStyles();
        embedder.runAsEmbeddables(classNames());
    }

    /**
     * Creates an instance of Embedder using
     * {@link #embedderClass}.
     *
     * @return An Embedder
     */
    protected Embedder newEmbedder() {
        Embedder embedder = classLoader.newInstance(Embedder.class, embedderClass);

        embedder.configuration().storyReporterBuilder().withCodeLocation(CodeLocations.codeLocationFromClass(embedder.getClass()));
        embedder.useClassLoader(classLoader);
        embedder.useEmbedderControls(embedderControls());
        embedder.useEmbedderMonitor(embedderMonitor());

        return embedder;
    }

    protected EmbedderMonitor embedderMonitor() {
        return new CustomPrintStreamEmbedderMonitor(System.out);
    }

    protected EmbedderControls embedderControls() {

        return new UnmodifiableEmbedderControls(new EmbedderControls()
                .doGenerateViewAfterStories(true)
                .doIgnoreFailureInStories(Boolean.valueOf(ProjectProperties.getJbehaveIgnoreFailuresInStories()))
                .doIgnoreFailureInView(Boolean.valueOf(ProjectProperties.getJbehaveIgnoreFailuresInView()))
                .doVerboseFailures(true)
                .doVerboseFiltering(false)
                .useStoryTimeoutInSecs(Long.valueOf(ProjectProperties.getJbehaveStoryTimeoutInSecs()))
                .useThreads(Integer.parseInt(ProjectProperties.getJbehaveThreads())));
    }

    /**
     * Finds class names, using the {@link #newStoryFinder()}, in the
     * {@link #searchDirectory()} given specified {@link #includes} and
     * {@link #excludes}.
     *
     * @return A List of class names found
     */
    protected List<String> classNames() {
        getLogger().debug("Searching for class names including " + includes + " and excluding " + excludes);
        String searchDirectory = searchDirectory();
        List<String> foundClassPaths = newStoryFinder().findClassNames(searchDirectory, includes, excludes);

        List<String> classNames = new ArrayList<String>();
        for (String classPath : foundClassPaths) {
            classNames.add(qualifiedClassFromPath(classPath));
        }

        getLogger().info("Found class names: " + classNames);
        return classNames;
    }

    /**
     * Creates an instance of StoryFinder
     *
     * @return A StoryFinder
     */
    protected StoryFinder newStoryFinder() {
        return classLoader.newInstance(StoryFinder.class, storyFinderClass);
    }

    protected String searchDirectory() {
        return getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    private String qualifiedClassFromPath(String path) {
        return path.replace(PATH_SEPARATOR_CHAR, PACKAGE_DELIMITER_CHAR).substring(0, path.length() - CLASS_EXTENSION.length());
    }
}
