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

package org.apache.webapp.admin.context;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.Lists;
import org.apache.webapp.admin.TomcatTreeBuilder;
/**
 * The <code>Action</code> that sets up <em>Add Context</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class AddContextAction extends Action {
    
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
        
        // Fill in the form values for display and editing
        ContextForm contextFm = new ContextForm();
        session.setAttribute("contextForm", contextFm);
        contextFm.setAdminAction("Create");
        contextFm.setObjectName("");
        String parent = request.getParameter("parent");
        contextFm.setParentObjectName(parent);
        int i = parent.indexOf(":");
        String domain = parent.substring(0, i);
        int position = parent.indexOf(",");
        String loader = domain + TomcatTreeBuilder.LOADER_TYPE + 
                parent.substring(position, parent.length());
        String manager = domain + TomcatTreeBuilder.MANAGER_TYPE + 
                parent.substring(position, parent.length());
        contextFm.setLoaderObjectName(loader);
        contextFm.setManagerObjectName(manager); 
        contextFm.setNodeLabel("");
        contextFm.setCookies("");
        contextFm.setCrossContext("false");
        contextFm.setDocBase("");
        contextFm.setOverride("false");
        contextFm.setPrivileged("false");
        contextFm.setPath("");
        contextFm.setReloadable("false");
        contextFm.setSwallowOutput("false");
        contextFm.setUseNaming("false");
        contextFm.setWorkDir("");        
        contextFm.setPath("");
        //loader initialization
        //contextFm.setLdrCheckInterval("15");
        contextFm.setLdrReloadable("false");
        //manager initialization
        //contextFm.setMgrCheckInterval("60");
        contextFm.setMgrMaxSessions("-1");
        contextFm.setMgrSessionIDInit("");
        
        contextFm.setBooleanVals(Lists.getBooleanValues());        
        
        // Forward to the context display page
        return (mapping.findForward("Context"));
        
    }    
}
