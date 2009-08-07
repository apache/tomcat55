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
 * The <code>Action</code> that sets up <em>Edit DefaultContext</em> transactions.
 *
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public class EditDefaultContextAction extends Action {
    

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
    public ActionForward perform(ActionMapping mapping,
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
        // DefaultContext mBean
        ObjectName cname = null;
        // Loader mBean
        ObjectName lname = null;
        // Manager mBean 
        ObjectName mname = null;
        
        StringBuffer sb = null;
        try {
            cname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.contextName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }
        
        // Get the corresponding loader
        try {
            sb = new StringBuffer(cname.getDomain());
            sb.append(":type=DefaultLoader");
            String host = cname.getKeyProperty("host");
            if (host != null) {
                sb.append(",host=");
                sb.append(host);
            }
            lname = new ObjectName(sb.toString());
        } catch (Exception e) {
            String message =
                resources.getMessage("error.managerName.bad",
                                 sb.toString());
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        // Session manager properties
        // Get the corresponding Session Manager mBean
        try {
            sb = new StringBuffer(cname.getDomain());
            sb.append(":type=DefaultManager");
            String host = cname.getKeyProperty("host");
            if (host != null) {
                sb.append(",host=");
                sb.append(host);
            }
            mname = new ObjectName(sb.toString());
        } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.managerName.bad",
                                 sb.toString());
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        // Fill in the form values for display and editing
        DefaultContextForm defaultContextFm = new DefaultContextForm();
        session.setAttribute("defaultContextForm", defaultContextFm);
        defaultContextFm.setAdminAction("Edit");
        defaultContextFm.setObjectName(cname.toString());
        defaultContextFm.setLoaderObjectName(lname.toString());
        defaultContextFm.setManagerObjectName(mname.toString());
        sb = new StringBuffer("DefaultContext");
        defaultContextFm.setNodeLabel(sb.toString());
        defaultContextFm.setBooleanVals(Lists.getBooleanValues());
       
        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "cookies";
            defaultContextFm.setCookies
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "crossContext";
            defaultContextFm.setCrossContext
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "useNaming";
            defaultContextFm.setUseNaming
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "reloadable";
            defaultContextFm.setReloadable
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "swallowOutput";
            defaultContextFm.setSwallowOutput
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());

            // loader properties
            attribute = "checkInterval";
            defaultContextFm.setLdrCheckInterval
                (((Integer) mBServer.getAttribute(lname, attribute)).toString());
            attribute = "reloadable";
            defaultContextFm.setLdrReloadable
                (((Boolean) mBServer.getAttribute(lname, attribute)).toString());

            // manager properties
            attribute = "entropy";
            defaultContextFm.setMgrSessionIDInit
                ((String) mBServer.getAttribute(mname, attribute));
            attribute = "maxActiveSessions";
            defaultContextFm.setMgrMaxSessions
                (((Integer) mBServer.getAttribute(mname, attribute)).toString());
            attribute = "checkInterval";
            defaultContextFm.setMgrCheckInterval
                (((Integer) mBServer.getAttribute(mname, attribute)).toString());

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
        
        // Forward to the default context display page
        return (mapping.findForward("DefaultContext"));
        
    }


}
