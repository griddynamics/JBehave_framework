package com.griddynamics.qa.framework.properties;

import com.griddynamics.qa.properties.utils.PropertiesUtils;

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

/**
 * Class contains properties names which are required for JBehave configuration
 *
 * @author ybaturina
 */
public class ProjectProperties {

    public static final String JRUNNER_CONFIG_FILENAME = "runner.config.file.location"; /*!< mandatory property */
    public static final String APPLICATION_CONFIG_FILENAME = "execution.config.file.location"; /*!< mandatory property */
    public static final String MOBILE_CONFIG_FILENAME = "mobile.config.file.location"; /*!< optional property */
    public static final String SUITE_ALL_PROPERTY_NAME = "suite.all"; /*!< mandatory property */
    public static final String SUITE_LIST_PROPERTY_NAME = "suite.list"; /*!< optional property */
    public static final String STORY_LIST_PROPERTY_NAME = "story.list"; /*!< optional property */
    public static final String EXCLUDE_STORY_LIST_PROPERTY_NAME = "exclude.story.list"; /*!< optional property */
    public static final String JBEHAVE_STORY_TIMEOUT_IN_SECS = "jbehave.storyTimeoutInSecs"; /*!< mandatory property */
    public static final String JBEHAVE_IGNORE_FAILURES_IN_STORIES = "jbehave.ignoreFailureInStories"; /*!< mandatory property */
    public static final String JBEHAVE_IGNORE_FAILURES_IN_VIEW = "jbehave.ignoreFailureInView"; /*!< mandatory property */
    public static final String JBEHAVE_THREADS = "threads"; /*!< mandatory property */
    public static final String PROPERTY_META_FILTERS = "meta.filters"; /*!< mandatory property */

    private static Properties props = ProjectPropertiesUtils.getAllProperties();

    public static String getSuiteList() {
        return PropertiesUtils.getProperty(props, SUITE_LIST_PROPERTY_NAME, true);
    }

    public static List<String> getMetaFilters() {
        return asList(PropertiesUtils.getProperty(props, PROPERTY_META_FILTERS, true).split(","));
    }

    public static String getSuiteAll() {
        return PropertiesUtils.getProperty(props, SUITE_ALL_PROPERTY_NAME, true);
    }

    public static String getStoryList() {
        return PropertiesUtils.getProperty(props, STORY_LIST_PROPERTY_NAME, true);
    }

    public static String getExcludeStoryList() {
        return PropertiesUtils.getProperty(props, EXCLUDE_STORY_LIST_PROPERTY_NAME, true);
    }

    public static String getJbehaveThreads(){
        return PropertiesUtils.getProperty(props, JBEHAVE_THREADS);
    }

    public static String getJbehaveIgnoreFailuresInStories(){
        return PropertiesUtils.getProperty(props, JBEHAVE_IGNORE_FAILURES_IN_STORIES);
    }

    public static String getJbehaveIgnoreFailuresInView(){
        return PropertiesUtils.getProperty(props, JBEHAVE_IGNORE_FAILURES_IN_VIEW);
    }

    public static String getJbehaveStoryTimeoutInSecs() {
        return PropertiesUtils.getProperty(props, JBEHAVE_STORY_TIMEOUT_IN_SECS);
    }

    public static String getJrunnerConfigFilenameValue() {
        return System.getProperty(JRUNNER_CONFIG_FILENAME);
    }

    public static String getApplicationConfigFilenameValue() {
        return System.getProperty(APPLICATION_CONFIG_FILENAME);
    }

    public static String getMobileConfigFilenameValue() {
        return System.getProperty(MOBILE_CONFIG_FILENAME);
    }

}
