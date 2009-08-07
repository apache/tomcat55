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
 * are logged appropriately to the static logger.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class SessionListener02
    implements HttpSessionListener, HttpSessionAttributeListener {


    public void attributeAdded(HttpSessionBindingEvent event) {
        StaticLogger.write("SessionListener02: attributeAdded(" +
                           event.getName() + "," + event.getValue() + ")");
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        StaticLogger.write("SessionListener02: attributeRemoved(" +
                           event.getName() + "," + event.getValue() + ")");
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        StaticLogger.write("SessionListener02: attributeReplaced(" +
                           event.getName() + "," + event.getValue() + ")");
    }

    public void sessionCreated(HttpSessionEvent event) {
        StaticLogger.write("SessionListener02: sessionCreated()");
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        StaticLogger.write("SessionListener02: sessionDestroyed()");
    }


}
