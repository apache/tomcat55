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

package org.apache.webapp.admin.valve;

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
 * A generic <code>Action</code> that sets up <em>Edit 
 * Valve </em> transactions, based on the type of Valve.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class EditValveAction extends Action {
    

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
        ObjectName vname = null;
        StringBuffer sb = null;
        try {
            vname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.valveName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }
        
       String parent = request.getParameter("parent");
       String valveType = null;
       String attribute = null;
       
       // Find what type of Valve this is
       try {    
            attribute = "className";
            String className = (String) 
                mBServer.getAttribute(vname, attribute);
            int period = className.lastIndexOf(".");
            if (period >= 0)
                valveType = className.substring(period + 1);
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

        // Forward to the appropriate valve display page        
        if ("AccessLogValve".equalsIgnoreCase(valveType)) {
               setUpAccessLogValve(vname, request, response);
        } else if ("RemoteAddrValve".equalsIgnoreCase(valveType)) {
               setUpRemoteAddrValve(vname, request, response);
        } else if ("RemoteHostValve".equalsIgnoreCase(valveType)) {
                setUpRemoteHostValve(vname, request, response);
        } else if ("RequestDumperValve".equalsIgnoreCase(valveType)) {
               setUpRequestDumperValve(vname, request, response);
        } else if ("SingleSignOn".equalsIgnoreCase(valveType)) {
               setUpSingleSignOnValve(vname, request, response);
        }
       
        
        return (mapping.findForward(valveType));
                
    }

    private void setUpAccessLogValve(ObjectName vname, HttpServletRequest request,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        String parent = request.getParameter("parent");
        AccessLogValveForm valveFm = new AccessLogValveForm();
        session.setAttribute("accessLogValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "AccessLogValve";
        StringBuffer sb = new StringBuffer("");
        String host = vname.getKeyProperty("host");
        String context = vname.getKeyProperty("path");        
        if (host!=null) {
            sb.append("Host (" + host + ") > ");
        }
        if (context!=null) {
            sb.append("Context (" + context + ") > ");
        }
        sb.append("Valve");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        valveFm.setBooleanVals(Lists.getBooleanValues());
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "directory";
            valveFm.setDirectory
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "pattern";
            valveFm.setPattern
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "prefix";
            valveFm.setPrefix
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "suffix";
            valveFm.setSuffix
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "resolveHosts";
            valveFm.setResolveHosts
                (((Boolean) mBServer.getAttribute(vname, attribute)).toString());
            attribute = "rotatable";
            valveFm.setRotatable
                (((Boolean) mBServer.getAttribute(vname, attribute)).toString());

        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }

    private void setUpRequestDumperValve(ObjectName vname, HttpServletRequest request,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        String parent = request.getParameter("parent");
        RequestDumperValveForm valveFm = new RequestDumperValveForm();
        session.setAttribute("requestDumperValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "RequestDumperValve";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        String attribute = null;
    }

    private void setUpSingleSignOnValve(ObjectName vname, HttpServletRequest request,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        String parent = request.getParameter("parent");
        SingleSignOnValveForm valveFm = new SingleSignOnValveForm();
        session.setAttribute("singleSignOnValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "SingleSignOn";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        String attribute = null;
    }


    private void setUpRemoteAddrValve(ObjectName vname, HttpServletRequest request,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        String parent = request.getParameter("parent");
        RemoteAddrValveForm valveFm = new RemoteAddrValveForm();
        session.setAttribute("remoteAddrValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteAddrValve";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "allow";
            valveFm.setAllow
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "deny";
            valveFm.setDeny
                ((String) mBServer.getAttribute(vname, attribute));
                        
        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }

    private void setUpRemoteHostValve(ObjectName vname, HttpServletRequest request,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        String parent = request.getParameter("parent");
        RemoteHostValveForm valveFm = new RemoteHostValveForm();
        session.setAttribute("remoteHostValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteHostValve";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "allow";
            valveFm.setAllow
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "deny";
            valveFm.setDeny
                ((String) mBServer.getAttribute(vname, attribute));
                        
        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }
    
}
