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
import java.util.Iterator;
import java.util.List;
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

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;

import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.LabelValueBean;
import org.apache.webapp.admin.Lists;
import org.apache.webapp.admin.TomcatTreeBuilder;

/**
 * The <code>Action</code> that sets up <em>Edit Service</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class EditServiceAction extends Action {
    

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
        
        // Set up the object names of the MBeans we are manipulating
        ObjectName sname = null;
        ObjectName ename = null;
        StringBuffer sb = null;
        try {
            sname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.serviceName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }
        try {
            sb = new StringBuffer(sname.getDomain());
            sb.append(":type=Engine");
            ename = new ObjectName(sb.toString());
        } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.engineName.bad",
                                     sb.toString());
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        String adminService = null;
        // Get the service name the admin app runs on
        // this service cannot be deleted from the admin tool
        try {
            adminService = Lists.getAdminAppService(
                                  mBServer, sname.getDomain(),request);
         } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.serviceName.bad",
                                 adminService);
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        
        // Fill in the form values for display and editing
        ServiceForm serviceFm = new ServiceForm();
        session.setAttribute("serviceForm", serviceFm);
        serviceFm.setAdminAction("Edit");
        serviceFm.setObjectName(sname.toString());
        serviceFm.setEngineObjectName(ename.toString());
        sb = new StringBuffer();
        sb.append(resources.getMessage(locale, "server.service.treeBuilder.subtreeNode"));
        sb.append(" (");
        sb.append(sname.getKeyProperty("serviceName"));
        sb.append(")");
        serviceFm.setNodeLabel(sb.toString());
        serviceFm.setAdminServiceName(adminService);
        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "name";
            serviceFm.setServiceName
                ((String) mBServer.getAttribute(sname, attribute));
            attribute = "name";
            serviceFm.setEngineName
                ((String) mBServer.getAttribute(ename, attribute));
            attribute = "defaultHost";
            serviceFm.setDefaultHost
                ((String) mBServer.getAttribute(ename, attribute));

            // Build the list of available hosts
            attribute = "hosts";
            ArrayList hosts = new ArrayList();
            hosts.add(new LabelValueBean
                      (resources.getMessage(locale, "list.none"), ""));
            Iterator items = Lists.getHosts(mBServer, sname).iterator();
            while (items.hasNext()) {
                ObjectName hname = new ObjectName((String) items.next());
                String name = hname.getKeyProperty("host");
                if (name!=null) {
                    hosts.add(new LabelValueBean(name, name));
                }
            }
            serviceFm.setHostNameVals(hosts);

        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
            return (null);
        }
        
        // Forward to the service display page
        return (mapping.findForward("Service"));
        
    }


}
