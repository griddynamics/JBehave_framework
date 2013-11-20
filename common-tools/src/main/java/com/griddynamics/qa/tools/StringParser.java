package com.griddynamics.qa.tools;

import org.junit.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing utils for working with String objects
 *
 * @author ybaturina
 */
public class StringParser {

    /**
     * Method returns string which is matched for the provided regexp
     * @param source - original string
     * @param regexp - regular expression which should be matched
     * @return
     */
    public static String getMatchString(String source, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(source);
        Assert.assertTrue("No matches were found for regexp <" + regexp + "> for string = " + source, matcher.find());
        return matcher.group();
    }
}

