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

package org.apache.webapp.admin.defaultcontext;

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
import org.apache.webapp.admin.TomcatTreeBuilder;
/**
 * The <code>Action</code> that sets up <em>Add DefaultContext</em> transactions.
 *
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public class AddDefaultContextAction extends Action {
    
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
    public ActionForward perform(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException, ServletException {
        
        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        
        // Fill in the form values for display and editing
        DefaultContextForm defaultContextFm = new DefaultContextForm();
        session.setAttribute("defaultContextForm", defaultContextFm);
        defaultContextFm.setAdminAction("Create");
        defaultContextFm.setObjectName("");
        String service = request.getParameter("serviceName");
        String parent = request.getParameter("parent");
        String defaultContext = null;
        String domain = null;
        if (service != null) {
            domain = service.substring(0,service.indexOf(":"));
            defaultContext = domain + TomcatTreeBuilder.DEFAULTCONTEXT_TYPE;
            defaultContextFm.setParentObjectName(service);
        } else if (parent != null) {
            domain = parent.substring(0,parent.indexOf(":"));
            defaultContextFm.setParentObjectName(parent);
            int position = parent.indexOf(",");
            defaultContext = domain + TomcatTreeBuilder.DEFAULTCONTEXT_TYPE +
                            parent.substring(position, parent.length());
        }
        defaultContextFm.setObjectName(defaultContext);                        
        int position = defaultContext.indexOf(",");
        String loader = domain + TomcatTreeBuilder.LOADER_TYPE;
        String manager = domain + TomcatTreeBuilder.MANAGER_TYPE;
        if (position > 0) {
            loader += defaultContext.substring(position, defaultContext.length());
            manager += defaultContext.substring(position, defaultContext.length());
        }
        defaultContextFm.setLoaderObjectName(loader);
        defaultContextFm.setManagerObjectName(manager); 
        defaultContextFm.setNodeLabel("");
        defaultContextFm.setCookies("true");
        defaultContextFm.setCrossContext("true");
        defaultContextFm.setReloadable("false");
        defaultContextFm.setSwallowOutput("false");
        defaultContextFm.setUseNaming("true");
        //loader initialization
        defaultContextFm.setLdrCheckInterval("15");
        defaultContextFm.setLdrReloadable("false");
        //manager initialization
        defaultContextFm.setMgrCheckInterval("60");
        defaultContextFm.setMgrMaxSessions("-1");
        defaultContextFm.setMgrSessionIDInit("");
        
        defaultContextFm.setBooleanVals(Lists.getBooleanValues());        
        
        // Forward to the context display page
        return (mapping.findForward("DefaultContext"));
        
    }    
}
