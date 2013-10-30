package com.griddynamics.qa.stubs.soapcommon.logger;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ybaturina
 * Date: 7/16/13
 * Time: 11:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomHtmlLayout extends HTMLLayout {
    private StringBuffer sbuf = new StringBuffer(BUF_SIZE);
    static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";

    @Override
    public String format(LoggingEvent event) {

        if(sbuf.capacity() > MAX_CAPACITY) {
            sbuf = new StringBuffer(BUF_SIZE);
        } else {
            sbuf.setLength(0);
        }

        sbuf.append(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);

        sbuf.append("<td>");
        sbuf.append(calcDate());
        sbuf.append("</td>" + Layout.LINE_SEP);

        String escapedThread = Transform.escapeTags(event.getThreadName());
        sbuf.append("<td title=\"" + escapedThread + " thread\">");
        sbuf.append(escapedThread);
        sbuf.append("</td>" + Layout.LINE_SEP);

        sbuf.append("<td title=\"Level\">");
        if (event.getLevel().equals(Level.DEBUG)) {
            sbuf.append("<font color=\"#339933\">");
            sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
            sbuf.append("</font>");
        }
        else if(event.getLevel().isGreaterOrEqual(Level.WARN)) {
            sbuf.append("<font color=\"#993300\"><strong>");
            sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
            sbuf.append("</strong></font>");
        } else {
            sbuf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
        }
        sbuf.append("</td>" + Layout.LINE_SEP);

        String escapedLogger = Transform.escapeTags(event.getLoggerName());
        sbuf.append("<td title=\"" + escapedLogger + " category\">");
        sbuf.append(escapedLogger);
        sbuf.append("</td>" + Layout.LINE_SEP);

        if(getLocationInfo()) {
            LocationInfo locInfo = event.getLocationInformation();
            sbuf.append("<td>");
            sbuf.append(Transform.escapeTags(locInfo.getFileName()));
            sbuf.append(':');
            sbuf.append(locInfo.getLineNumber());
            sbuf.append("</td>" + Layout.LINE_SEP);
        }

        sbuf.append("<td title=\"Message\">");
        sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
        sbuf.append("</td>" + Layout.LINE_SEP);
        sbuf.append("</tr>" + Layout.LINE_SEP);

        if (event.getNDC() != null) {
            sbuf.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
            sbuf.append("NDC: " + Transform.escapeTags(event.getNDC()));
            sbuf.append("</td></tr>" + Layout.LINE_SEP);
        }

        String[] s = event.getThrowableStrRep();
        if(s != null) {
            sbuf.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : xx-small;\" colspan=\"6\">");
            appendThrowableAsHTML(s, sbuf);
            sbuf.append("</td></tr>" + Layout.LINE_SEP);
        }

        return sbuf.toString();
    }

    void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
        if(s != null) {
            int len = s.length;
            if(len == 0)
                return;
            sbuf.append(Transform.escapeTags(s[0]));
            sbuf.append(Layout.LINE_SEP);
            for(int i = 1; i < len; i++) {
                sbuf.append(TRACE_PREFIX);
                sbuf.append(Transform.escapeTags(s[i]));
                sbuf.append(Layout.LINE_SEP);
            }
        }
    }

    private String calcDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss");
        Date resultdate = new Date();
        return dateFormat.format(resultdate.getTime());
    }

}
