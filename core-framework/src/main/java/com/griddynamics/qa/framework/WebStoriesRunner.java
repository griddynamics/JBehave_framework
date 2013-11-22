package com.griddynamics.qa.framework;

import com.griddynamics.qa.framework.converters.SpecialStringValuesConverter;
import com.griddynamics.qa.framework.logger.GlobalLoggerOutput;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.WebDriverHtmlOutput;

import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML;


/**
 * Main JBehave UI tests runner configuration
 *
 * @author ybaturina
 * @author mlykosova
 */
public abstract class WebStoriesRunner extends BaseStoriesRunner {

    private SeleniumContext seleniumContext = new SeleniumContext();

    public WebStoriesRunner(String contextPath) {
        super(contextPath,new StoryReporterBuilder()
                .withDefaultFormats()
                .withFailureTrace(true)
                .withFailureTraceCompression(true)
                .withFormats( TXT, XML, GlobalLoggerOutput.GLOBAL_LOGGER_OUTPUT, WebDriverHtmlOutput.WEB_DRIVER_HTML));

    }

    public WebStoriesRunner() {
    }

    @Override
    protected Configuration getConfiguration() {
        if (configuration == null) {
            ParameterConverters parameterConverters = new ParameterConverters();
            parameterConverters.addConverters(new SpecialStringValuesConverter());
            parameterConverters.addConverters(new ParameterConverters.StringListConverter());
            setConfiguration(new SeleniumConfiguration()
                    .useSeleniumContext(seleniumContext)
                    .useStoryReporterBuilder(getStoryReporterBuilder())
                    .useParameterControls(new ParameterControls().useDelimiterNamedParameters(true))
                    .usePendingStepStrategy(new FailingUponPendingStep())
                    .useParameterConverters(parameterConverters)
                    .doDryRun(Boolean.getBoolean(PROPERTY_DO_DRY_RUN)));
        }
        return configuration;
    }

}