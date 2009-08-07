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
 * Application event listener for context events.  All events that occur
 * are logged appropriately to the static logger..
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ContextListener01
    implements ServletContextAttributeListener, ServletContextListener {


    public void attributeAdded(ServletContextAttributeEvent event) {
        StaticLogger.write("ContextListener01: attributeAdded(" +
                           event.getName() + "," + event.getValue() + ")");
        ServletContext context = (ServletContext) event.getSource();
        context.log("ContextListener01: attributeAdded(" +
                    event.getName() + "," + event.getValue() + ")");
        if (event.getValue() instanceof ContextBean) {
            ContextBean bean = (ContextBean) event.getValue();
            bean.setLifecycle(bean.getLifecycle() + "/add");
        }
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
        StaticLogger.write("ContextListener01: attributeRemoved(" +
                           event.getName() + "," + event.getValue() + ")");
        ServletContext context = (ServletContext) event.getSource();
        context.log("ContextListener01: attributeRemoved(" +
                    event.getName() + "," + event.getValue() + ")");
        if (event.getValue() instanceof ContextBean) {
            ContextBean bean = (ContextBean) event.getValue();
            bean.setLifecycle(bean.getLifecycle() + "/rem");
        }
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
        StaticLogger.write("ContextListener01: attributeReplaced(" +
                           event.getName() + "," + event.getValue() + ")");
        ServletContext context = (ServletContext) event.getSource();
        context.log("ContextListener01: attributeReplaced(" +
                    event.getName() + "," + event.getValue() + ")");
        if (event.getValue() instanceof ContextBean) {
            ContextBean bean = (ContextBean) event.getValue();
            bean.setLifecycle(bean.getLifecycle() + "/rep");
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        StaticLogger.write("ContextListener01: contextDestroyed()");
        ServletContext context = (ServletContext) event.getSource();
        context.log("ContextListener01: contextDestroyed()");
        context.removeAttribute("contextListener01");
    }

    public void contextInitialized(ServletContextEvent event) {
        StaticLogger.write("ContextListener01: contextInitialized()");
        ServletContext context = (ServletContext) event.getSource();
        context.log("ContextListener01: contextInitialized()");
        ContextBean bean = new ContextBean();
        bean.setStringProperty("ContextListener01");
        context.setAttribute("contextListener01", bean);
    }


}
