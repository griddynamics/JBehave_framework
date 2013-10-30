package com.griddynamics.qa.framework.converters;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: ybaturina
 * Date: 10/16/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class StackTraceConverter {

    public static String getStringFromStackTrace(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
