/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.griddynamics.qa.framework;

import com.griddynamics.qa.framework.converters.SpecialStringValuesConverter;
import com.griddynamics.qa.framework.logger.CustomPrintStreamEmbedderMonitor;
import com.griddynamics.qa.framework.logger.GlobalLoggerOutput;
import com.griddynamics.qa.framework.properties.ProjectProperties;
import com.griddynamics.qa.framework.properties.ProjectPropertiesUtils;
import com.griddynamics.qa.logger.LoggerFactory;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.model.TableTransformers;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static com.griddynamics.qa.mobile.MobileUtils.uninstallMobileApps;
import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.*;

/**
 * Main JBehave tests runner configuration
 *
 * @author ybaturina
 * @author mlykosova
 */
public abstract class BaseStoriesRunner extends JUnitStories {

    public static final String STORY_EXTENSION = ".story";
    public static final String ALL_STORIES_PATTERN = "**/*";

    public static List<String> storiesToIncludeList;
    public static List<String> storiesToExcludeList;

    protected static final String PROPERTY_DO_DRY_RUN = "doDryRun";
    protected static final String PROPERTY_MOBILE = "mobile";
    protected ApplicationContext applicationContext;
    protected Configuration configuration;

    private static final String SCREENSHOT_BEAN = "screenshots";
    private static List<String> suiteList;
    private StoryReporterBuilder storyReporterBuilder;
    private String applicationContextPath;
    private Embedder embedder;

    public BaseStoriesRunner() {
    }

    public BaseStoriesRunner(String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
        setStoryReporterBuilder(new StoryReporterBuilder()
                .withCodeLocation(codeLocationFromClass(this.getClass()))
                .withDefaultFormats()
                .withFailureTrace(true)
                .withFailureTraceCompression(true)
                .withFormats(TXT, XML, HTML, GlobalLoggerOutput.GLOBAL_LOGGER_OUTPUT));
        setEmbedderControls();
    }

    public BaseStoriesRunner(String applicationContextPath, StoryReporterBuilder reporter) {
        this.applicationContextPath = applicationContextPath;
        setStoryReporterBuilder(reporter.withCodeLocation(codeLocationFromClass(this.getClass())));
        setEmbedderControls();
    }

    @Override
    public Configuration configuration() {
        return getConfiguration();
    }

    /**
     * Method gathers JBehave steps from Spring context and adds catching screenshots functionality
     * in case when {@link #SCREENSHOT_BEAN} is present in the context
     *
     * @return SpringStepsFactory
     */
    @Override
    public SpringStepsFactory stepsFactory() {
        SpringStepsFactory stepsFactory = new SpringStepsFactory(configuration(), createContext());
        if (applicationContext.containsBean(SCREENSHOT_BEAN)) {
            CustomWebDriverScreenshotOnFailure scr =
                    (CustomWebDriverScreenshotOnFailure) applicationContext.getBean(SCREENSHOT_BEAN);
            scr.setReporterBuilder(storyReporterBuilder);
        }
        return stepsFactory;
    }

    /**
     * Method for suite tests execution
     *
     * @throws Throwable Throwable
     */
    @Test
    public void run() throws Throwable {
        String suiteName = this.getClass().getSimpleName();

        suiteList = getSuiteList();

        /**
         1. suite.list property is empty or null, that's why we should run all list from the template
         2. suite.list property contains the suite name, need to run stories from this suite
         3. suite.list property value pattern doesn't contain current suite name, so we don't execute the tests from this suite
         */

        if (CollectionUtils.isEmpty(suiteList) || suiteList.contains(suiteName)) {
            beforeSuite();
            List<String> storyPath = storyPaths();
            if (CollectionUtils.isEmpty(storyPath)) {
                getLogger().info(suiteName + ": empty list of stories for running.");
                return;
            }
            getEmbedder().useSystemProperties(ProjectPropertiesUtils.getAllProperties());
            getEmbedder().
                    useEmbedderMonitor(new CustomPrintStreamEmbedderMonitor(LoggerFactory.getGlobalFileStream()));
            getEmbedder()
                    .useMetaFilters(ProjectProperties.getMetaFilters());
            try {
                beforeRun();
                getEmbedder().runStoriesAsPaths(storyPath);
            } finally {
                getEmbedder().generateCrossReference();
                afterRun();
            }
        } else {
            getLogger().info("Suite with name '" + suiteName + "' present in the suites run template," +
                    " but not in the list of running suites '" + suiteList + "'.");
        }
    }

    protected abstract List<String> includeStories();

    protected abstract List<String> excludeStories();

    protected List<String> getSuiteList() {
        if (CollectionUtils.isEmpty(suiteList)) {
            setSuiteList();
        }
        return suiteList;
    }

    protected List<String> getStoriesList() {
        if (CollectionUtils.isEmpty(storiesToIncludeList)) {
            setStoriesList();
        }
        return storiesToIncludeList;
    }

    protected List<String> getExcludeStoriesList() {
        if (CollectionUtils.isEmpty(storiesToExcludeList)) {
            setExcludeStoriesList();
        }
        return storiesToExcludeList;
    }

    protected Embedder getEmbedder() {
        if (embedder == null) {
            embedder = configuredEmbedder();
        }
        return embedder;
    }

    protected StoryReporterBuilder getStoryReporterBuilder() {
        return storyReporterBuilder;
    }

    protected void setStoryReporterBuilder(StoryReporterBuilder reporter) {
        storyReporterBuilder = reporter;
    }

    protected void setEmbedderControls() {
        getEmbedder().embedderControls().doGenerateViewAfterStories(true).doIgnoreFailureInStories(Boolean.valueOf(ProjectProperties.getJbehaveIgnoreFailuresInStories()))
                .doIgnoreFailureInView(Boolean.valueOf(ProjectProperties.getJbehaveIgnoreFailuresInView())).useStoryTimeoutInSecs(Long.valueOf(ProjectProperties.getJbehaveStoryTimeoutInSecs())).doVerboseFailures(true)
                .useThreads(Integer.parseInt(ProjectProperties.getJbehaveThreads()))
                .useStoryTimeoutInSecs(Long.parseLong(ProjectProperties.getJbehaveStoryTimeoutInSecs()));
    }

    protected List<String> storyPaths() {
        return storiesForRunning(includeStories(), excludeStories());
    }

    /**
     * Sets list of stories which should be run in suite
     *
     * @param includeStories - stories which must be run
     * @param excludeStories - stories which must not be run
     * @return List of stories
     */
    protected List<String> storiesForRunning(List<String> includeStories, List<String> excludeStories) {
        if (!CollectionUtils.isEmpty(includeStories)) {
            List<String> storiesToRunFromProperties = getStoriesList();
            if (CollectionUtils.isEmpty(storiesToRunFromProperties)) {
                storiesToRunFromProperties.add(ALL_STORIES_PATTERN + STORY_EXTENSION);
            }
            List<String> storiesToExcludeFromProperties = getExcludeStoriesList();
            List<String> storiesFromSuite = new StoryFinder().findPaths(
                    codeLocationFromClass(this.getClass()).getFile(), includeStories, excludeStories);
            List<String> storiesFromProperties = new StoryFinder().findPaths(
                    codeLocationFromClass(this.getClass()).getFile(), storiesToRunFromProperties, storiesToExcludeFromProperties);
            storiesFromProperties.retainAll(storiesFromSuite);
            return storiesFromProperties;
        }
        return Collections.emptyList();
    }

    protected Configuration getConfiguration() {
        if (configuration == null) {
            ParameterConverters parameterConverters = new ParameterConverters();
            ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LoadFromClasspath(this.getClass()),
                    parameterConverters, new ParameterControls(), new TableTransformers());

            parameterConverters.addConverters(new SpecialStringValuesConverter());
            parameterConverters.addConverters(new ParameterConverters.StringListConverter());
            parameterConverters.addConverters(new ParameterConverters.ExamplesTableConverter(examplesTableFactory));

            setConfiguration(new MostUsefulConfiguration()
                    .useStoryReporterBuilder(storyReporterBuilder)
                    .useParameterControls(new ParameterControls().useDelimiterNamedParameters(true))
                    .usePendingStepStrategy(new FailingUponPendingStep())
                    .useParameterConverters(parameterConverters)
                    .doDryRun(Boolean.getBoolean(PROPERTY_DO_DRY_RUN))
                    .useStoryParser(new RegexStoryParser(examplesTableFactory)));
        }
        return configuration;
    }

    protected void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected ApplicationContext createContext() {
        if (applicationContext == null) {
            applicationContext = new SpringApplicationContextFactory(applicationContextPath).createApplicationContext();
        }

        return applicationContext;
    }

    protected void beforeRun() {
        getLogger().info("Suite: " + this.getClass().getSimpleName() + ". Before run.");
    }

    protected void afterRun() {
        getLogger().info("Suite: " + this.getClass().getSimpleName() + ". After run.");
        if (StringUtils.isNotBlank(System.getProperty(PROPERTY_MOBILE))) {
            uninstallMobileApps();
        }
    }

    protected void beforeSuite() {
    }

    /**
     * Sets list of stories suites which should be run
     */
    private void setSuiteList() {
        String suitesFromProperty = ProjectProperties.getSuiteList();
        if (StringUtils.isNotEmpty(suitesFromProperty)) {
            suiteList = asList(suitesFromProperty.split(","));
        } else {
            suiteList = Collections.emptyList();
        }
    }

    /**
     * Sets list of stories which must be run in the suite,
     * the list of stories should be provided in {@link com.griddynamics.qa.framework.properties.ProjectProperties#STORY_LIST_PROPERTY_NAME}
     */
    private void setStoriesList() {
        String storyProperty = ProjectProperties.getStoryList();
        if (StringUtils.isNotEmpty(storyProperty)) {
            storiesToIncludeList = addPatternToStoryPaths(asList(storyProperty.split(",")));
        } else {
            storiesToIncludeList = new ArrayList<String>();
        }
    }

    /**
     * Method constructs story paths pattern from story names
     *
     * @param paths
     * @return
     */
    private List<String> addPatternToStoryPaths(List<String> paths) {
        List<String> newPaths = new ArrayList<String>();
        for (String path : paths) {
            newPaths.add(ALL_STORIES_PATTERN + path + STORY_EXTENSION);
        }
        return newPaths;
    }

    /**
     * Sets list of stories which must be run in the suite,
     * the list of stories should be provided in {@link com.griddynamics.qa.framework.properties.ProjectProperties#EXCLUDE_STORY_LIST_PROPERTY_NAME}
     */
    private void setExcludeStoriesList() {
        String storyProperty = ProjectProperties.getExcludeStoryList();
        if (StringUtils.isNotEmpty(storyProperty)) {
            storiesToExcludeList = addPatternToStoryPaths(asList(storyProperty.split(",")));
        } else {
            storiesToExcludeList = Collections.emptyList();
        }
    }

}