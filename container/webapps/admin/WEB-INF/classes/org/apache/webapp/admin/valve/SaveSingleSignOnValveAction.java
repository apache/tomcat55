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

package org.apache.webapp.admin.valve;

import java.util.Locale;
import java.io.IOException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;

/**
 * The <code>Action</code> that completes <em>Add Valve</em> and
 * <em>Edit Valve</em> transactions for Single Sign On valve.
 *
 * @author Manveen Kaur
 * @version $Id$
 */

public final class SaveSingleSignOnValveAction extends Action {


    // ----------------------------------------------------- Instance Variables

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
        
        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        
        // Acquire a reference to the MBeanServer containing our MBeans
        try {
            mBServer = ((ApplicationServlet) getServlet()).getServer();
        } catch (Throwable t) {
            throw new ServletException
            ("Cannot acquire MBeanServer reference", t);
        }
        
        // Identify the requested action
        SingleSignOnValveForm vform = (SingleSignOnValveForm) form;
        String adminAction = vform.getAdminAction();
        String parent = vform.getParentObjectName();
        String valveType = vform.getValveType();
               
        // Perform a "Create Valve" transaction (if requested)
        if ("Create".equals(adminAction)) {

            try {
                // Ensure that only one single sign on valve exists
                ObjectName pname = new ObjectName(parent); 
                ObjectName oname = 
                        new ObjectName(pname.getDomain() + 
                                    ":type=Valve,name=SingleSignOn");               
                
                if (mBServer.isRegistered(oname)) {
                    ActionMessages errors = new ActionMessages();
                    errors.add("singleSignOnValve",
                               new ActionMessage("error.singleSignOn.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                } 
            } catch (Exception e) {
                getServlet().log
                    (resources.getMessage(locale, "users.error.invoke",
                                          adminAction), e);
                response.sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                     resources.getMessage(locale, "users.error.invoke",
                                          adminAction));
                return (null);

            }
            
            ValveUtil.createValve(parent, valveType, response, request, mapping,
                    (ApplicationServlet) getServlet());
                      
        }

        // Forward to the success reporting page
        session.removeAttribute(mapping.getAttribute());
        return (mapping.findForward("Save Successful"));
    }
}
