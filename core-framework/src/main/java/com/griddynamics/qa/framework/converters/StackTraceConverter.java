package com.griddynamics.qa.framework.converters;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class containing one method to get the stacktrace from exception as String object
 *
 * @author ybaturina
 */
public class StackTraceConverter {

    public static String getStringFromStackTrace(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
