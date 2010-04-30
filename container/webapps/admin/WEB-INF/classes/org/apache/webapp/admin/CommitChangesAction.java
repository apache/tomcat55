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

package org.apache.webapp.admin;


import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.struts.util.MessageResources;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Implementation of <strong>Action</strong> that saves the current settings
 * and writes them out to server.xml
 *
 * @author Manveen Kaur
 * @version $Id$
 */

public final class CommitChangesAction extends Action {

    /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mBServer = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {

        // Acquire a reference to the MBeanServer containing our MBeans
        try {
            mBServer = ((ApplicationServlet) getServlet()).getServer();
        } catch (Throwable t) {
            throw new ServletException
            ("Cannot acquire MBeanServer reference", t);
        }

       // Acquire the resources that we need
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        
       ObjectName sname = null;    
        try {
           sname = new ObjectName(TomcatTreeBuilder.DEFAULT_DOMAIN +
                                    TomcatTreeBuilder.SERVER_TYPE);
        } catch (Exception e) {
            String message = "Could not get Server Object";
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }
        
       String operation = "storeConfig";
       try {           
            mBServer.invoke(sname, operation, null, null);            
        } catch (Throwable t) {
            getServlet().log
            (resources.getMessage(locale, "users.error.invoke",
                                  operation), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                resources.getMessage(locale, "users.error.invoke",
                                     operation));
            return (null);            
        }
 

        getServlet().log("Debugging -- changes saved to conf/server.xml");
        // Forward control back to the banner
        return (mapping.findForward("Banner"));

    }


}
