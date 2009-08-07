/*
 * Copyright 2002,2004 The Apache Software Foundation.
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


package org.apache.webapp.admin.host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import javax.management.modelmbean.ModelMBean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import org.apache.webapp.admin.ApplicationServlet;

/**
 * The <code>Action</code> that completes <em>Delete Aliases</em>
 * transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class DeleteAliasesAction extends Action {


    /**
     * Signature for the <code>removeAlias</code> operation.
     */
    private String removeAliasTypes[] =
    { "java.lang.String",      // Object name
    };


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
        
        
        // Look up the components we will be using as needed
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
        
        AliasesForm aliasesFm = (AliasesForm) form;
        // the host Name is needed to delete the existing aliases from
        String hostName = aliasesFm.getHostName();
        
        // Delete the specified Aliases
        String aliases[]  = aliasesFm.getAliases();
        String values[] = new String[1];
        String operation = "removeAlias";

        try {
            
            ObjectName hname = new ObjectName(hostName);

            // Remove the specified hosts
            for (int i = 0; i < aliases.length; i++) {
                values[0] = aliases[i];         
                mBServer.invoke(hname, operation, values, removeAliasTypes);
            }

        } catch (Exception e) {
            getServlet().log
                (resources.getMessage(locale, "users.error.invoke",
                                      operation), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.invoke",
                                      operation));
            return (null);

        }

        // Report successful completion of this transaction
        return (mapping.findForward("Save Successful"));

    }
    
}
