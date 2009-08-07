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
 * Application event listener for request events.  All events that occur
 * are logged appropriately to the static logger..
 *
 * @author Justyna Horwat
 * @version $Revision$ $Date$
 */

public class RequestListener01
    implements ServletRequestAttributeListener, ServletRequestListener {


    public void attributeAdded(ServletRequestAttributeEvent event) {
        StaticLogger.write("RequestListener01: attributeAdded(" +
                           event.getName() + "," + event.getValue() + ")");
        ServletContext context = (ServletContext) event.getSource();
        context.log("RequestListener01: attributeAdded(" +
                    event.getName() + "," + event.getValue() + ")");
        if (event.getValue() instanceof ContextBean) {
            ContextBean bean = (ContextBean) event.getValue();
            bean.setLifecycle(bean.getLifecycle() + "/add");
        }
    }

    public void attributeRemoved(ServletRequestAttributeEvent event) {
        StaticLogger.write("RequestListener01: attributeRemoved(" +
                           event.getName() + "," + event.getValue() + ")");
        ServletContext context = (ServletContext) event.getSource();
        context.log("RequestListener01: attributeRemoved(" +
                    event.getName() + "," + event.getValue() + ")");
        if (event.getValue() instanceof ContextBean) {
            ContextBean bean = (ContextBean) event.getValue();
            bean.setLifecycle(bean.getLifecycle() + "/rem");
        }
    }

    public void attributeReplaced(ServletRequestAttributeEvent event) {
        StaticLogger.write("RequestListener01: attributeReplaced(" +
                           event.getName() + "," + event.getValue() + ")");
        ServletContext context = (ServletContext) event.getSource();
        context.log("RequestListener01: attributeReplaced(" +
                    event.getName() + "," + event.getValue() + ")");
        if (event.getValue() instanceof ContextBean) {
            ContextBean bean = (ContextBean) event.getValue();
            bean.setLifecycle(bean.getLifecycle() + "/rep");
        }
    }

    public void requestDestroyed(ServletRequestEvent event) {
        StaticLogger.write("RequestListener01: requestDestroyed() -- probably cached from previous request");
        ServletContext context = (ServletContext) event.getSource();
        context.log("RequestListener01: requestDestroyed()");
    }

    public void requestInitialized(ServletRequestEvent event) {
        StaticLogger.write("RequestListener01: requestInitialized()");
        ServletContext context = (ServletContext) event.getSource();
        context.log("RequestListener01: requestInitialized()");
    }


}
