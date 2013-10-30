package com.griddynamics.qa.tools;

import org.junit.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {

    /**
     * @param source
     * @param regexp
     * @return
     */
    public static String getMatchString(String source, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(source);
        Assert.assertTrue("No matches were found for regexp <" + regexp + "> for string = " + source, matcher.find());
        return matcher.group();
    }
}

