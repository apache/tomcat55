/*
 * Copyright 1999, 2000, 2001 ,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tester;


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Application event listener for session events.  All events that occur
 * are logged appropriately to the static logger.  In addition, session
 * creation and destruction events are logged to the servlet context log.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class SessionListener01
    implements HttpSessionListener, HttpSessionAttributeListener {


    public void attributeAdded(HttpSessionBindingEvent event) {
        StaticLogger.write("SessionListener01: attributeAdded(" +
                           event.getName() + "," + event.getValue() + ")");
        event.getSession().getServletContext().log
            ("SessionListener01: attributeAdded(" + event.getSession().getId()
             + "," + event.getName() + ")");
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        StaticLogger.write("SessionListener01: attributeRemoved(" +
                           event.getName() + "," + event.getValue() + ")");
        event.getSession().getServletContext().log
            ("SessionListener01: attributeRemoved(" +
             event.getSession().getId() + "," + event.getName() + ")");
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        StaticLogger.write("SessionListener01: attributeReplaced(" +
                           event.getName() + "," + event.getValue() + ")");
        event.getSession().getServletContext().log
            ("SessionListener01: attributeReplaced(" +
             event.getSession().getId() + "," + event.getName() + ")");
    }

    public void sessionCreated(HttpSessionEvent event) {
        StaticLogger.write("SessionListener01: sessionCreated()");
        HttpSession session = event.getSession();
        session.getServletContext().log("SessionListener01: sessionCreated(" +
                                        session.getId() + ")");
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        StaticLogger.write("SessionListener01: sessionDestroyed()");
        HttpSession session = event.getSession();
        session.getServletContext().log("SessionListener01: sessionDestroyed("
                                        + session.getId() + ")");
    }


}
