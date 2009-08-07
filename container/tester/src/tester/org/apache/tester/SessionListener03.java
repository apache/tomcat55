/*
 * Copyright 1999, 2000, 2001, 2002 ,2004 The Apache Software Foundation.
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
 * Session attribute that listens to passivation and activation events.
 * All events that occur are logged to the servlet context log.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class SessionListener03
    implements HttpSessionActivationListener, HttpSessionBindingListener,
               Serializable {

    public void sessionDidActivate(HttpSessionEvent event) {
        event.getSession().getServletContext().log
            ("SessionListener03: sessionDidActivate(" +
             event.getSession().getId() + ")");
    }

    public void sessionWillPassivate(HttpSessionEvent event) {
        event.getSession().getServletContext().log
            ("SessionListener03: sessionWillPassivate(" +
             event.getSession().getId() + ")");
    }

    public void valueBound(HttpSessionBindingEvent event) {
        event.getSession().getServletContext().log
            ("SessionListener03: valueBound(" +
             event.getSession().getId() + "," +
             event.getName() + ")");
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        event.getSession().getServletContext().log
            ("SessionListener03: valueUnbound(" +
             event.getName() + ")");
    }


}
