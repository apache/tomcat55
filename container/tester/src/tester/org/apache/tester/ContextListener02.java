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


import java.beans.PropertyEditorManager;
import java.sql.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Application event listener for context events.  Ensures that the property
 * editor classes for this web application are appropriately registered.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ContextListener02
    implements ServletContextListener {


    private ServletContext context = null;


    public void contextDestroyed(ServletContextEvent event) {
        context.log("ContextListener02: contextDestroyed()");
        context = null;
    }

    public void contextInitialized(ServletContextEvent event) {
        context = (ServletContext) event.getSource();
        context.log("ContextListener02: contextInitialized()");
        PropertyEditorManager.registerEditor(Date.class,
                                             DatePropertyEditor.class);
        context.log("ContextListener02: getEditorSearchPath() -->");
        String search[] = PropertyEditorManager.getEditorSearchPath();
        if (search == null)
            search = new String[0];
        for (int i = 0; i < search.length; i++)
            context.log("ContextListener02:   " + search[i]);
        context.log("ContextListener02: findEditor() --> " +
                    PropertyEditorManager.findEditor(Date.class));
               
    }


}
