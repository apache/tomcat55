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


import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.io.IOException;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.QueryExp;
import javax.management.Query;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.Lists;
import org.apache.webapp.admin.TomcatTreeBuilder;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;
import org.apache.webapp.admin.valve.ValveUtil;



/**
 * The <code>Action</code> that completes <em>Add Service</em> and
 * <em>Edit Service</em> transactions.
 *
 * @author Manveen Kaur
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public final class SaveServiceAction extends Action {


    // ----------------------------------------------------- Instance Variables


    /**
     * Signature for the <code>createStandardEngine</code> operation.
     */
    private String createStandardEngineTypes[] =
    { "java.lang.String",     // parent
      "java.lang.String",     // name
      "java.lang.String",     // defaultHost
    };


    /**
     * Signature for the <code>createStandardService</code> operation.
     */
    private String createStandardServiceTypes[] =
    { "java.lang.String",     // parent
      "java.lang.String",     // name
      "java.lang.String"      // domain
    };


    /**
     * Signature for the <code>createStandardEngineService</code> operation.
     */
    private String createStandardEngineServiceTypes[] =
    { "java.lang.String",     // parent
      "java.lang.String",     // engineName
      "java.lang.String",     // defaultHost
      "java.lang.String"      // serviceName
    };
    
    
    /**
     * Signature for the <code>createUserDatabaseRealm</code> operation.
     */
    private String createUserDatabaseRealmTypes[] =
    { "java.lang.String",     // parent
      "java.lang.String",     // name
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
        ServiceForm sform = (ServiceForm) form;
        String adminAction = sform.getAdminAction();
        String sObjectName = sform.getObjectName();
        String eObjectName = sform.getEngineObjectName();
        String serverObjectName = sform.getServerObjectName();
        ObjectName eoname = null;
        ObjectName soname = null;
        // Perform a "Create Service" transaction (if requested)
        if ("Create".equals(adminAction)) {

            String operation = null;
            String values[] = null;

            try {
                // engine name is domain
                String engineName = sform.getEngineName();
                //String domain = (new ObjectName(serverObjectName)).getDomain();
                // Ensure that the requested service name is unique
                ObjectName oname =
                    new ObjectName("*" + TomcatTreeBuilder.SERVICE_TYPE + 
                                ",serviceName="+sform.getServiceName());
                Iterator names = mBServer.queryNames(oname, null).iterator();
                while (names.hasNext()) {       
                    if (mBServer.isRegistered((ObjectName)names.next())) {
                        ActionErrors errors = new ActionErrors();
                        errors.add("serviceName",
                               new ActionError("error.serviceName.exists"));
                        saveErrors(request, errors);
                        return (new ActionForward(mapping.getInput()));
                    }
                }
                
                oname = new ObjectName(engineName + TomcatTreeBuilder.ENGINE_TYPE);
                if (mBServer.isRegistered(oname)) {
                    ActionErrors errors = new ActionErrors();
                    errors.add("serviceName",
                               new ActionError("error.engineName.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                }
                
                // Look up our MBeanFactory MBean
                ObjectName fname = TomcatTreeBuilder.getMBeanFactory();

                // Create a new StandardService and StandardEngine object
                values = new String[4];
                values[0] = TomcatTreeBuilder.SERVER_TYPE;
                values[1] = engineName;
                values[2] = sform.getDefaultHost();
                values[3] = sform.getServiceName();
                operation = "createStandardEngineService";
                Vector onames = (Vector)
                    mBServer.invoke(fname, operation,
                                    values, createStandardEngineServiceTypes);
                eoname = (ObjectName)onames.get(0);
                soname = (ObjectName)onames.get(1);
                sObjectName = soname.toString();
                eObjectName = eoname.toString();
                
                String realmOName = ValveUtil.getObjectName(
                                    eObjectName, TomcatTreeBuilder.REALM_TYPE);
            
                ObjectName roname = new ObjectName(realmOName);
                if (mBServer.isRegistered(roname)) {
                    mBServer.unregisterMBean(roname); 
                }
                
                // Create a new UserDatabaseRealm object
                values = new String[2];
                values[0] = eObjectName;
                values[1] = "UserDatabase";
                operation = "createUserDatabaseRealm";
                //realmOName = (String)
                //    mBServer.invoke(fname, operation,
                //                    values, createUserDatabaseRealmTypes);
                                    
                //Enumeration enum = onames.elements();
                //while (enum.hasMoreElements()) {
                //    getServlet().log("save service "+enum.nextElement());
                //}
                sObjectName = soname.toString();
                eObjectName = eoname.toString();
                
                // Create a new StandardService object
                //values = new String[3];
                //values[0] = TomcatTreeBuilder.SERVER_TYPE;
                //values[1] = sform.getServiceName();
                //values[2] = engineName;
                //operation = "createStandardService";
                //sObjectName = (String)
                //    mBServer.invoke(fname, operation,
                //                    values, createStandardServiceTypes);

                // Create a new StandardEngine object
                //values = new String[3];
                //values[0] = sObjectName;
                //values[1] = sform.getEngineName();
                //values[2] = sform.getDefaultHost();
                //if ("".equals(values[2])) {
                //    values[2] = null;
                //}
                //operation = "createStandardEngine";
                //eObjectName = (String)
                //    mBServer.invoke(fname, operation,
                //                    values, createStandardEngineTypes);

                // Add the new Service to our tree control node
                TreeControl control = (TreeControl)
                    session.getAttribute("treeControlTest");
                if (control != null) {
                    String parentName = TomcatTreeBuilder.DEFAULT_DOMAIN + 
                                            TomcatTreeBuilder.SERVER_TYPE;
                    TreeControlNode parentNode = control.findNode(parentName);
                    if (parentNode != null) {
                        String nodeLabel = resources.getMessage(locale, 
                            "server.service.treeBuilder.subtreeNode") +" (" +
                            soname.getKeyProperty("serviceName") + ")";
                        String encodedName =
                            URLEncoder.encode(sObjectName,TomcatTreeBuilder.URL_ENCODING);
                        TreeControlNode childNode =
                            new TreeControlNode(sObjectName,
                                                "Service.gif",
                                                nodeLabel,
                                                "EditService.do?select=" +
                                                encodedName,
                                                "content",
                                                true, engineName);
                        parentNode.addChild(childNode);
                        // update tree to display the newly added realm
                        //Iterator realmNames =
                        //    Lists.getRealms(mBServer, sObjectName).iterator();
                        //while (realmNames.hasNext()) {
                        //    String realmName = (String) realmNames.next();
                        //    ObjectName objectName = new ObjectName(realmName);
                        //    nodeLabel = "Realm for service (" + 
                        //                        sform.getServiceName() + ")";
                        //    TreeControlNode realmNode =
                        //        new TreeControlNode(realmName,
                        //                            "Realm.gif",
                        //                            nodeLabel,
                        //                            "EditRealm.do?select=" +
                        //                            URLEncoder.encode(realmName) +
                        //                            "&nodeLabel=" +
                        //                            URLEncoder.encode(nodeLabel),
                        //                            "content",
                        //                            false, engineName);
                        //    childNode.addChild(realmNode);               
                        //}         
                        // FIXME - force a redisplay
                    } else {
                        getServlet().log
                            ("Cannot find parent node '" + parentName + "'");
                    }
                } else {
                    getServlet().log
                        ("Cannot find TreeControlNode!");
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

        }

        // Perform attribute updates as requested
        String attribute = null;
        try {
        
            eoname = new ObjectName(eObjectName);
            soname = new ObjectName(sObjectName);

            attribute = "defaultHost";
            String defaultHost = sform.getDefaultHost();
            if ("".equals(defaultHost)) {
                defaultHost = null;
            }
            mBServer.setAttribute(eoname,
                                  new Attribute("defaultHost", defaultHost));

        } catch (Exception e) {

            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.set",
                                      attribute), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.set",
                                      attribute));
            return (null);
        }
        
        // Forward to the success reporting page
        session.removeAttribute(mapping.getAttribute());
        return (mapping.findForward("Save Successful"));
        
    }
    
}
