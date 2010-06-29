/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.catalina.valves;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;
import org.apache.coyote.RequestInfo;


/**
 * <p>Implementation of the <b>Valve</b> interface that generates a web server
 * access log with the detailed line contents matching a configurable pattern.
 * The syntax of the available patterns is similar to that supported by the
 * Apache <code>mod_log_config</code> module.  As an additional feature,
 * automatic rollover of log files when the date changes is also supported.</p>
 *
 * <p>Patterns for the logged message may include constant text or any of the
 * following replacement strings, for which the corresponding information
 * from the specified Response is substituted:</p>
 * <ul>
 * <li><b>%a</b> - Remote IP address
 * <li><b>%A</b> - Local IP address
 * <li><b>%b</b> - Bytes sent, excluding HTTP headers, or '-' if no bytes
 *     were sent
 * <li><b>%B</b> - Bytes sent, excluding HTTP headers
 * <li><b>%h</b> - Remote host name
 * <li><b>%H</b> - Request protocol
 * <li><b>%l</b> - Remote logical username from identd (always returns '-')
 * <li><b>%m</b> - Request method
 * <li><b>%p</b> - Local port
 * <li><b>%q</b> - Query string (prepended with a '?' if it exists, otherwise
 *     an empty string
 * <li><b>%r</b> - First line of the request
 * <li><b>%s</b> - HTTP status code of the response
 * <li><b>%S</b> - User session ID
 * <li><b>%t</b> - Date and time, in Common Log Format format
 * <li><b>%u</b> - Remote user that was authenticated
 * <li><b>%U</b> - Requested URL path
 * <li><b>%v</b> - Local server name
 * <li><b>%D</b> - Time taken to process the request, in millis
 * <li><b>%T</b> - Time taken to process the request, in seconds
 * <li><b>%I</b> - current request thread name (can compare later with stacktraces)
 * </ul>
 * <p>In addition, the caller can specify one of the following aliases for
 * commonly utilized patterns:</p>
 * <ul>
 * <li><b>common</b> - <code>%h %l %u %t "%r" %s %b</code>
 * <li><b>combined</b> -
 *   <code>%h %l %u %t "%r" %s %b "%{Referer}i" "%{User-Agent}i"</code>
 * </ul>
 *
 * <p>
 * There is also support to write information from the cookie, incoming
 * header, the Session or something else in the ServletRequest.<br>
 * It is modeled after the apache syntax:
 * <ul>
 * <li><code>%{xxx}i</code> for incoming headers
 * <li><code>%{xxx}o</code> for outgoing headers
 * <li><code>%{xxx}c</code> for a specific cookie
 * <li><code>%{xxx}r</code> xxx is an attribute in the ServletRequest
 * <li><code>%{xxx}s</code> xxx is an attribute in the HttpSession
 * </ul>
 * </p>
 *
 * <p>
 * Conditional logging is also supported. This can be done with the
 * <code>condition</code> property.
 * If the value returned from ServletRequest.getAttribute(condition)
 * yields a non-null value. The logging will be skipped.
 * </p>
 *
 * @author Craig R. McClanahan
 * @author Jason Brittain
 * @author Peter Rossbach
 * @version $Id$
 */

public class AccessLogValve
    extends ValveBase
    implements Lifecycle {


    // ----------------------------------------------------------- Constructors


    private static final char MARK_EMPTY = '-';

    /**
     * Construct a new instance of this class with default property values.
     */
    public AccessLogValve() {
        
        super();
        setPattern("common");


    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The as-of date for the currently open log file, or a zero-length
     * string if there is no open log file.
     */
    private String dateStamp = "";


    /**
     * The directory in which log files are created.
     */
    private String directory = "logs";


    /**
     * The descriptive information about this implementation.
     */
    protected static final String info =
        "org.apache.catalina.valves.AccessLogValve/1.1";


    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);


    /**
     * The set of month abbreviations for log messages.
     */
    protected static final String months[] =
    { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };


    /**
     * If the current log pattern is the same as the common access log
     * format pattern, then we'll set this variable to true and log in
     * a more optimal and hard-coded way.
     */
    private boolean common = false;


    /**
     * For the combined format (common, plus useragent and referer), we do
     * the same
     */
    private boolean combined = false;


    /**
     * The pattern used to format our access log lines.
     */
    private String pattern = null;


    /**
     * The prefix that is added to log file filenames.
     */
    private String prefix = "access_log.";


    /**
     * Should we rotate our log file? Default is true (like old behavior)
     */
    private boolean rotatable = true;


    /**
     * The string manager for this package.
     */
    private StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * Has this component been started yet?
     */
    private boolean started = false;


    /**
     * The suffix that is added to log file filenames.
     */
    private String suffix = "";


    /**
     * The PrintWriter to which we are currently logging, if any.
     */
    private PrintWriter writer = null;


    /**
     * A date formatter to format a Date into a date in the format
     * "yyyy-MM-dd".
     */
    private SimpleDateFormat fileDateFormatter = null;


    /**
     * The system timezone.
     */
    private static final TimeZone timezone;

    
    /**
     * The time zone offset relative to GMT in text form when daylight saving
     * is not in operation.
     */
    private static final String timeZoneNoDST;


    /**
     * The time zone offset relative to GMT in text form when daylight saving
     * is in operation.
     */
    private static final String timeZoneDST;

    static {
        timezone = TimeZone.getDefault();
        timeZoneNoDST = calculateTimeZoneOffset(timezone.getRawOffset());
        int offset = timezone.getDSTSavings();
        timeZoneDST = calculateTimeZoneOffset(timezone.getRawOffset()+offset);
    }

    /**
     * The system time when we last updated the Date that this valve
     * uses for log lines.
     */
    private static class AccessDateStruct {
        private Date currentDate = new Date();
        private String currentDateString = null;
        private SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
        private SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");
        private SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        private DecimalFormat timeTakenFormatter;

        public AccessDateStruct() {
            dayFormatter.setTimeZone(timezone);
            monthFormatter.setTimeZone(timezone);
            yearFormatter.setTimeZone(timezone);
            timeFormatter.setTimeZone(timezone);
        }
        public Date getDate() {
            // Only update the Date once per second, max.
            long systime = System.currentTimeMillis();
            if ((systime - currentDate.getTime()) > 1000) {
                currentDate.setTime(systime);
                currentDateString = null;
            }
            return currentDate;
        }

        /**
         * Format current date and time in Common Log Format format. That is:
         * "[dd/MMM/yyyy:HH:mm:ss Z]"
         */
        public String getCurrentDateString() {
            if (currentDateString == null) {
                StringBuffer current = new StringBuffer(32);
                current.append('[');
                current.append(dayFormatter.format(currentDate));
                current.append('/');
                current.append(lookup(monthFormatter.format(currentDate)));
                current.append('/');
                current.append(yearFormatter.format(currentDate));
                current.append(':');
                current.append(timeFormatter.format(currentDate));
                current.append(' ');
                current.append(getTimeZone(currentDate));
                current.append(']');
                currentDateString = current.toString();
            }
            return currentDateString;
        }

        /**
         * Format the time taken value for 3 decimal places.
         */
        public String formatTimeTaken(long time) {
            if (timeTakenFormatter == null) {
                timeTakenFormatter = new DecimalFormat("0.000");
            }
            return timeTakenFormatter.format(time/1000d);
        }
    }

    private static ThreadLocal currentDateStruct = new ThreadLocal() {
        protected Object initialValue() {
            return new AccessDateStruct();
        }
    };

    /**
     * When formatting log lines, we often use strings like this one (" ").
     */
    private static final char space = ' ';


    /**
     * Resolve hosts.
     */
    private boolean resolveHosts = false;


    /**
     * Instant when the log daily rotation was last checked.
     */
    private volatile long rotationLastChecked = 0L;


    /**
     * Are we doing conditional logging. default false.
     */
    private String condition = null;


    /**
     * Date format to place in log file name. Use at your own risk!
     */
    private String fileDateFormat = null;

    // ------------------------------------------------------------- Properties


    /**
     * Return the directory in which we create log files.
     */
    public String getDirectory() {

        return (directory);

    }


    /**
     * Set the directory in which we create log files.
     *
     * @param directory The new log file directory
     */
    public void setDirectory(String directory) {

        this.directory = directory;

    }


    /**
     * Return descriptive information about this implementation.
     */
    public String getInfo() {

        return (info);

    }


    /**
     * Return the format pattern.
     */
    public String getPattern() {

        return (this.pattern);

    }


    /**
     * Set the format pattern, first translating any recognized alias.
     *
     * @param pattern The new pattern
     */
    public void setPattern(String pattern) {

        if (pattern == null)
            pattern = "";
        if (pattern.equals(Constants.AccessLog.COMMON_ALIAS))
            pattern = Constants.AccessLog.COMMON_PATTERN;
        if (pattern.equals(Constants.AccessLog.COMBINED_ALIAS))
            pattern = Constants.AccessLog.COMBINED_PATTERN;
        this.pattern = pattern;

        if (this.pattern.equals(Constants.AccessLog.COMMON_PATTERN))
            common = true;
        else
            common = false;

        if (this.pattern.equals(Constants.AccessLog.COMBINED_PATTERN))
            combined = true;
        else
            combined = false;

    }


    /**
     * Return the log file prefix.
     */
    public String getPrefix() {

        return (prefix);

    }


    /**
     * Set the log file prefix.
     *
     * @param prefix The new log file prefix
     */
    public void setPrefix(String prefix) {

        this.prefix = prefix;

    }


    /**
     * Should we rotate the logs
     */
    public boolean isRotatable() {

        return rotatable;

    }


    /**
     * Set the value is we should we rotate the logs
     *
     * @param rotatable true is we should rotate.
     */
    public void setRotatable(boolean rotatable) {

        this.rotatable = rotatable;

    }


    /**
     * Return the log file suffix.
     */
    public String getSuffix() {

        return (suffix);

    }


    /**
     * Set the log file suffix.
     *
     * @param suffix The new log file suffix
     */
    public void setSuffix(String suffix) {

        this.suffix = suffix;

    }


    /**
     * Set the resolve hosts flag.
     *
     * @param resolveHosts The new resolve hosts value
     */
    public void setResolveHosts(boolean resolveHosts) {

        this.resolveHosts = resolveHosts;

    }


    /**
     * Get the value of the resolve hosts flag.
     */
    public boolean isResolveHosts() {

        return resolveHosts;

    }


    /**
     * Return whether the attribute name to look for when
     * performing conditional loggging. If null, every
     * request is logged.
     */
    public String getCondition() {

        return condition;

    }


    /**
     * Set the ServletRequest.attribute to look for to perform
     * conditional logging. Set to null to log everything.
     *
     * @param condition Set to null to log everything
     */
    public void setCondition(String condition) {

        this.condition = condition;

    }

    /**
     *  Return the date format date based log rotation.
     */
    public String getFileDateFormat() {
        return fileDateFormat;
    }


    /**
     *  Set the date format date based log rotation.
     */
    public void setFileDateFormat(String fileDateFormat) {
        this.fileDateFormat =  fileDateFormat;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Log a message summarizing the specified request and response, according
     * to the format specified by the <code>pattern</code> property.
     *
     * @param request Request being processed
     * @param response Response being processed
     *
     * @exception IOException if an input/output error has occurred
     * @exception ServletException if a servlet error has occurred
     */
    public void invoke(Request request, Response response)
        throws IOException, ServletException {

        // Pass this request on to the next valve in our pipeline
        long t1=System.currentTimeMillis();

        getNext().invoke(request, response);

        long t2=System.currentTimeMillis();
        long time=t2-t1;

        if (condition!=null &&
                null!=request.getRequest().getAttribute(condition)) {
            return;
        }


        AccessDateStruct struct = (AccessDateStruct) currentDateStruct.get();
        Date date = struct.getDate();
        StringBuffer result = new StringBuffer(128);

        // Check to see if we should log using the "common" access log pattern
        if (common || combined) {
            String value;

            if (isResolveHosts())
                result.append(request.getRemoteHost());
            else
                result.append(request.getRemoteAddr());

            result.append(" - ");

            value = request.getRemoteUser();
            if (value == null)
                result.append("- ");
            else {
                result.append(value);
                result.append(space);
            }

            result.append(struct.getCurrentDateString());

            result.append(" \"");
            result.append(request.getMethod());
            result.append(space);
            result.append(request.getRequestURI());
            if (request.getQueryString() != null) {
                result.append('?');
                result.append(request.getQueryString());
            }
            result.append(space);
            result.append(request.getProtocol());
            result.append("\" ");

            result.append(response.getStatus());

            result.append(space);

            long length = response.getContentCountLong() ;
            if (length <= 0)
                result.append(MARK_EMPTY);
            else
                result.append(length);

            if (combined) {
                result.append(space);
                result.append('\"');
                String referer = request.getHeader("referer");
                if(referer != null)
                    result.append(referer);
                else
                    result.append(MARK_EMPTY);
                result.append('\"');

                result.append(space);
                result.append('\"');
                String ua = request.getHeader("user-agent");
                if(ua != null)
                    result.append(ua);
                else
                    result.append(MARK_EMPTY);
                result.append('\"');
            }

        } else {
            // Generate a message based on the defined pattern
            boolean replace = false;
            for (int i = 0; i < pattern.length(); i++) {
                char ch = pattern.charAt(i);
                if (replace) {
                    /* For code that processes {, the behavior will be ... if I
                     * do not enounter a closing } - then I ignore the {
                     */
                    if ('{' == ch){
                        int j = i + 1;
                        for(;j < pattern.length() && '}' != pattern.charAt(j); j++) {
                            // loop through characters that are part of the name
                        }
                        if (j+1 < pattern.length()) {
                            /* the +1 was to account for } which we increment now */
                            j++;
                            replace(result, pattern.substring(i + 1, j - 1),
                                    pattern.charAt(j), request, response);
                            i=j; /*Since we walked more than one character*/
                        } else {
                            //D'oh - end of string - pretend we never did this
                            //and do processing the "old way"
                            replace(result, ch, struct, request, response, time);
                        }
                    } else {
                        replace(result, ch, struct, request, response,time );
                    }
                    replace = false;
                } else if (ch == '%') {
                    replace = true;
                } else {
                    result.append(ch);
                }
            }
        }
        log(result.toString(), date);

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Close the currently open log file (if any)
     */
    private synchronized void close() {

        if (writer == null)
            return;
        writer.flush();
        writer.close();
        writer = null;
        dateStamp = "";

    }


    /**
     * Log the specified message to the log file, switching files if the date
     * has changed since the previous log call.
     *
     * @param message Message to be logged
     * @param date the current Date object (so this method doesn't need to
     *        create a new one)
     */
    public void log(String message, Date date) {

        if (rotatable){
            // Only do a logfile switch check once a second, max.
            long systime = System.currentTimeMillis();
            if ((systime - rotationLastChecked) > 1000) {
                synchronized(this) {
                    if ((systime - rotationLastChecked) > 1000) {
                        rotationLastChecked = systime;
        
                        // Check for a change of date
                        String tsDate =
                            fileDateFormatter.format(new Date(systime));
        
                        // If the date has changed, switch log files
                        if (!dateStamp.equals(tsDate)) {
                            close();
                            dateStamp = tsDate;
                            open();
                        }
                    }
                }
            }
        }

        // Log this message
        synchronized(this) {
            if (writer != null) {
                writer.println(message);
            }
        }

    }


    /**
     * Return the month abbreviation for the specified month, which must
     * be a two-digit String.
     *
     * @param month Month number ("01" .. "12").
     */
    private static String lookup(String month) {

        int index;
        try {
            index = Integer.parseInt(month) - 1;
        } catch (Throwable t) {
            index = 0;  // Can not happen, in theory
        }
        return (months[index]);

    }


    /**
     * Open the new log file for the date specified by <code>dateStamp</code>.
     */
    private synchronized void open() {

        // Create the directory if necessary
        File dir = new File(directory);
        if (!dir.isAbsolute())
            dir = new File(System.getProperty("catalina.base"), directory);
        dir.mkdirs();

        // Open the current log file
        try {
            String pathname;
            // If no rotate - no need for dateStamp in fileName
            if (rotatable){
                pathname = dir.getAbsolutePath() + File.separator +
                            prefix + dateStamp + suffix;
            } else {
                pathname = dir.getAbsolutePath() + File.separator +
                            prefix + suffix;
            }
            writer = new PrintWriter(new FileWriter(pathname, true), true);
        } catch (IOException e) {
            writer = null;
        }

    }


    /**
     * Print the replacement text for the specified pattern character.
     *
     * @param result StringBuffer that accumulates the log message text
     * @param pattern Pattern character identifying the desired text
     * @param struct the object containing current Date so that this method
     *        doesn't need to create one
     * @param request Request being processed
     * @param response Response being processed
     */
    private void replace(StringBuffer result, char pattern,
            AccessDateStruct struct, Request request, Response response,
            long time) {

        String value;

        if (pattern == 'a') {
            value = request.getRemoteAddr();
        } else if (pattern == 'A') {
            try {
                value = InetAddress.getLocalHost().getHostAddress();
            } catch(Throwable e){
                value = "127.0.0.1";
            }
        } else if (pattern == 'b') {
            long length = response.getContentCountLong() ;
            if (length <= 0)
                result.append(MARK_EMPTY);
            else
                result.append(length);
            return;
        } else if (pattern == 'B') {
            result.append(response.getContentCountLong());
            return;
        } else if (pattern == 'h') {
            value = request.getRemoteHost();
        } else if (pattern == 'H') {
            value = request.getProtocol();
        } else if (pattern == 'l') {
            result.append(MARK_EMPTY);
            return;
        } else if (pattern == 'm') {
            if (request != null)
                value = request.getMethod();
            else
                value = "";
        } else if (pattern == 'p') {
            result.append(request.getServerPort());
            return;
        } else if (pattern == 'D') {
            result.append(time);
            return;
        } else if (pattern == 'q') {
            String query = null;
            if (request != null)
                query = request.getQueryString();
            if (query != null)
                result.append('?').append(query);
            return;
        } else if (pattern == 'r') {
            if (request != null) {
                result.append(request.getMethod());
                result.append(space);
                result.append(request.getRequestURI());
                if (request.getQueryString() != null) {
                    result.append('?');
                    result.append(request.getQueryString());
                }
                result.append(space);
                result.append(request.getProtocol());
            } else {
                result.append("- - -");
            }
            return;
        } else if (pattern == 'S') {
            if (request != null) {
                if (request.getSession(false) != null) {
                    result.append(request.getSessionInternal(false)
                            .getIdInternal());
                    return;
                }
            }
            result.append(MARK_EMPTY);
            return;
        } else if (pattern == 's') {
            if (response != null)
                result.append(response.getStatus());
            else
                result.append(MARK_EMPTY);
            return;
        } else if (pattern == 't') {
            result.append(struct.getCurrentDateString());
            return;
        } else if (pattern == 'T') {
            result.append(struct.formatTimeTaken(time));
            return;
        } else if (pattern == 'u') {
            if (request != null) {
                value = request.getRemoteUser();
                if (value != null) {
                    result.append(value);
                    return;
                }
            }
            result.append(MARK_EMPTY);
            return;
        } else if (pattern == 'U') {
            if (request != null)
                value = request.getRequestURI();
            else {
                result.append(MARK_EMPTY);
                return;
            }
        } else if (pattern == 'v') {
            value = request.getServerName();
        } else if (pattern == 'I' ) {
            RequestInfo info = request.getCoyoteRequest().getRequestProcessor();
            if(info != null) {
                value = info.getWorkerThreadName();
            } else {
                result.append(MARK_EMPTY);
                return;
            }
        } else {
            result.append("???").append(pattern).append("???");
            return;
        }

        if (value != null)
            result.append(value);
    }


    /**
     * Print the replacement text for the specified "header/parameter".
     *
     * @param result StringBuffer that accumulates the log message text
     * @param header The header/parameter to get
     * @param type Where to get it from i=input,c=cookie,r=ServletRequest,s=Session
     * @param request Request being processed
     * @param response Response being processed
     */
    private void replace(StringBuffer result, String header, char type,
            Request request, Response response) {

        Object value;

        switch (type) {
            case 'i':
                if (null != request)
                    value = request.getHeader(header);
                else
                    value = "??";
                break;
            case 'o':
                if (null != response) {
                    String[] values = response.getHeaderValues(header);
                    if(values.length > 0) {
                        for (int i = 0; i < values.length; i++) {
                            String string = values[i];
                            result.append(string);
                            if(i+1<values.length)
                                result.append(',');
                        }
                        return;
                    }
                    value = null;
                } else
                    value = "??";
                break;
            case 'c':
                value = null;
                 Cookie[] c = request.getCookies();
                 for (int i=0; c != null && i < c.length; i++){
                     if (header.equals(c[i].getName())){
                         value = c[i].getValue();
                         break;
                     }
                 }
                break;
            case 'r':
                if (null != request)
                    value = request.getAttribute(header);
                else
                    value= "??";
                break;
            case 's':
                value = null;
                if (null != request) {
                    HttpSession sess = request.getSession(false);
                    if (null != sess)
                        value = sess.getAttribute(header);
                }
               break;
            default:
                value = "???";
        }

        /* try catch in case toString() barfs */
        try {
            if (value!=null)
                result.append(value.toString());
            else
                result.append(MARK_EMPTY);
        } catch(Throwable e) {
            result.append(MARK_EMPTY);
        }
    }


    private static String getTimeZone(Date date) {
        if (timezone.inDaylightTime(date)) {
            return timeZoneDST;
        } else {
            return timeZoneNoDST;
        }
    }


    private static String calculateTimeZoneOffset(long offset) {
        StringBuffer tz = new StringBuffer();
        if ((offset<0))  {
            tz.append('-');
            offset = -offset;
        } else {
            tz.append('+');
        }

        long hourOffset = offset/(1000*60*60);
        long minuteOffset = (offset/(1000*60)) % 60;

        if (hourOffset<10)
            tz.append('0');
        tz.append(hourOffset);

        if (minuteOffset<10)
            tz.append('0');
        tz.append(minuteOffset);

        return tz.toString();
    }


    // ------------------------------------------------------ Lifecycle Methods


    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {

        lifecycle.addLifecycleListener(listener);

    }


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this
     * Lifecycle has no listeners registered, a zero-length array is returned.
     */
    public LifecycleListener[] findLifecycleListeners() {

        return lifecycle.findLifecycleListeners();

    }


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to add
     */
    public void removeLifecycleListener(LifecycleListener listener) {

        lifecycle.removeLifecycleListener(listener);

    }


    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {

        // Validate and update our current component state
        if (started)
            throw new LifecycleException
                (sm.getString("accessLogValve.alreadyStarted"));
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;

        // Initialize formatters, and dateStamp
        if (fileDateFormat==null || fileDateFormat.length()==0)
            fileDateFormat = "yyyy-MM-dd";
        synchronized (this) {
            fileDateFormatter = new SimpleDateFormat(fileDateFormat);
            fileDateFormatter.setTimeZone(timezone);
            dateStamp = fileDateFormatter.format(new Date());
        }

        open();

    }


    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException {

        // Validate and update our current component state
        if (!started)
            throw new LifecycleException
                (sm.getString("accessLogValve.notStarted"));
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        close();

    }
}
