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

package org.apache.webapp.admin.connector;

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
 * The <code>Action</code> that sets up <em>Edit Connector</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class EditConnectorAction extends Action {
    

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
        ObjectName cname = null;
        StringBuffer sb = null;
        try {
            cname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage(locale, "error.connectorName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        // Fill in the form values for display and editing
        ConnectorForm connectorFm = new ConnectorForm();
        session.setAttribute("connectorForm", connectorFm);
        connectorFm.setAdminAction("Edit");
        connectorFm.setObjectName(cname.toString());
        sb = new StringBuffer();
        sb.append(resources.getMessage(locale, "server.service.treeBuilder.connector"));
        sb.append(" (");
        sb.append(cname.getKeyProperty("port"));
        sb.append(")");
        connectorFm.setNodeLabel(sb.toString());
        connectorFm.setBooleanVals(Lists.getBooleanValues());        
        connectorFm.setClientAuthVals(Lists.getClientAuthValues());
        
        String attribute = null;
        try {

            // Copy scalar properties
            // General properties
            attribute = "scheme";
            String scheme = (String) mBServer.getAttribute(cname, attribute);
            connectorFm.setScheme(scheme);

            attribute = "protocolHandlerClassName";
            String handlerClassName = 
                (String) mBServer.getAttribute(cname, attribute);
            int period = handlerClassName.lastIndexOf('.');
            String connType = handlerClassName.substring(period + 1);
            String connectorType = "HTTPS";
            if ("JkCoyoteHandler".equalsIgnoreCase(connType)) {
                connectorType = "AJP";
            } else if ("Http11Protocol".equalsIgnoreCase(connType) && 
                      ("http".equalsIgnoreCase(scheme))) {
                connectorType = "HTTP";
            }             
            connectorFm.setConnectorType(connectorType);            
            
            attribute = "acceptCount";
            connectorFm.setAcceptCountText
                (String.valueOf(mBServer.getAttribute(cname, attribute)));          
            attribute = "compression";
            connectorFm.setCompression
                ((String) mBServer.getAttribute(cname, attribute));          
            attribute = "connectionLinger";
            connectorFm.setConnLingerText
                (String.valueOf(mBServer.getAttribute(cname, attribute)));            
            attribute = "connectionTimeout";
            connectorFm.setConnTimeOutText
                (String.valueOf(mBServer.getAttribute(cname, attribute)));             
            attribute = "connectionUploadTimeout";
            connectorFm.setConnUploadTimeOutText
                (String.valueOf(mBServer.getAttribute(cname, attribute)));              
            attribute = "disableUploadTimeout";
            connectorFm.setDisableUploadTimeout
                (String.valueOf(mBServer.getAttribute(cname, attribute)));       
            attribute = "bufferSize";
            connectorFm.setBufferSizeText
                (String.valueOf(mBServer.getAttribute(cname, attribute)));            
            attribute = "enableLookups";
            connectorFm.setEnableLookups
                (String.valueOf(mBServer.getAttribute(cname, attribute)));            
            attribute = "address";
            connectorFm.setAddress
                ((String) mBServer.getAttribute(cname, attribute));          
            attribute = "maxKeepAliveRequests";
            connectorFm.setMaxKeepAliveText
                (String.valueOf(mBServer.getAttribute(cname, attribute)));       
            attribute = "maxSpareThreads";
            connectorFm.setMaxSpare
                (String.valueOf(mBServer.getAttribute(cname, attribute)));         
            attribute = "maxThreads";
            connectorFm.setMaxThreads
                (String.valueOf(mBServer.getAttribute(cname, attribute)));       
            attribute = "minSpareThreads";
            connectorFm.setMinSpare
                (String.valueOf(mBServer.getAttribute(cname, attribute)));        
            attribute = "threadPriority";
            connectorFm.setThreadPriority
                (String.valueOf(mBServer.getAttribute(cname, attribute)));
            attribute = "secure";
            connectorFm.setSecure
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "tcpNoDelay";
            connectorFm.setTcpNoDelay
                (String.valueOf(mBServer.getAttribute(cname, attribute)));
            attribute = "xpoweredBy";
            connectorFm.setXpoweredBy
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "URIEncoding";
            connectorFm.setURIEncodingText
                ((String) mBServer.getAttribute(cname, attribute));
            attribute = "useBodyEncodingForURI";
            connectorFm.setUseBodyEncodingForURIText
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
            attribute = "allowTrace";
            connectorFm.setAllowTraceText
                (((Boolean) mBServer.getAttribute(cname, attribute)).toString());
          
            // Ports
            attribute = "port";
            connectorFm.setPortText
                (((Integer) mBServer.getAttribute(cname, attribute)).toString());            
            attribute = "redirectPort";
            connectorFm.setRedirectPortText
                (((Integer) mBServer.getAttribute(cname, attribute)).toString());            
            
            // Supported by HTTP and HTTPS only
            if (!("AJP".equalsIgnoreCase(connectorType))) {
                attribute = "proxyName";
                connectorFm.setProxyName
                    ((String) mBServer.getAttribute(cname, attribute));
                attribute = "proxyPort";
                connectorFm.setProxyPortText
                    (((Integer) mBServer.getAttribute(cname, attribute)).toString());            
            }
            
            if ("HTTPS".equalsIgnoreCase(connectorType)) {
                // Initialize rest of variables. 
                // These are set only for SSL connectors.
                attribute = "algorithm";
                connectorFm.setAlgorithm
                    ((String) mBServer.getAttribute(cname, attribute));
                attribute = "clientAuth";
                connectorFm.setClientAuthentication
                    (((String) mBServer.getAttribute(cname, attribute)));
                attribute = "ciphers";
                connectorFm.setCiphers
                    ((String) mBServer.getAttribute(cname, attribute));   
                attribute = "keystoreFile";
                connectorFm.setKeyStoreFileName
                    ((String) mBServer.getAttribute(cname, attribute));
                attribute = "keystorePass";
                connectorFm.setKeyStorePassword
                    ((String) mBServer.getAttribute(cname, attribute));     
                attribute = "keystoreType";
                connectorFm.setKeyStoreType
                    ((String) mBServer.getAttribute(cname, attribute));   
                attribute = "sslProtocol";
                connectorFm.setSslProtocol
                    ((String) mBServer.getAttribute(cname, attribute));          
            }     
                
                        
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
        
        // Forward to the connector display page
        return (mapping.findForward("Connector"));
        
    }


}
