/*
 * Copyright 2001-2002,2004 The Apache Software Foundation.
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

package org.apache.webapp.admin.service;

import java.io.IOException;
import java.util.Locale;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.LabelValueBean;
import org.apache.webapp.admin.Lists;

/**
 * The <code>Action</code> that sets up <em>Add Service</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class AddServiceAction extends Action {
        
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
        
        String serverName = request.getParameter("select");
        
        // Fill in the form values for display and editing
        ServiceForm serviceFm = new ServiceForm();
        session.setAttribute("serviceForm", serviceFm);
        serviceFm.setAdminAction("Create");
        serviceFm.setObjectName("");
        serviceFm.setEngineObjectName("");
        serviceFm.setServiceName("");
        serviceFm.setEngineName("");
        serviceFm.setDefaultHost("localhost");        
        serviceFm.setAdminServiceName("");
        serviceFm.setServerObjectName(serverName);
        ArrayList hosts = new ArrayList();
        hosts.add(new LabelValueBean
                  (resources.getMessage(locale, "list.none"), ""));
        serviceFm.setHostNameVals(hosts);
        
        // Forward to the service display page
        return (mapping.findForward("Service"));
        
    }


}
