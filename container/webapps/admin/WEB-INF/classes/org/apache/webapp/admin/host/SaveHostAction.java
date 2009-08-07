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

package org.apache.webapp.admin.host;


import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
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
import org.apache.webapp.admin.TomcatTreeBuilder;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;



/**
 * The <code>Action</code> that completes <em>Add Host</em> and
 * <em>Edit Host</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class SaveHostAction extends Action {


    // ----------------------------------------------------- Instance Variables

    /**
     * Signature for the <code>createStandardHost</code> operation.
     */
    private String createStandardHostTypes[] =
    { "java.lang.String",     // parent
      "java.lang.String",     // name
      "java.lang.String",     // appBase
      "boolean",              // autoDeploy
      "boolean",              // deployOnStartup
      "boolean",              // deployXML
      "boolean",              // unpackWARs
      "boolean",              // xmlNamespaceAware
      "boolean",              // xmlValidation
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
        HostForm hform = (HostForm) form;
        String adminAction = hform.getAdminAction();
        String hObjectName = hform.getObjectName();
        ObjectName honame = null;

        // Perform a "Create Host" transaction (if requested)
        if ("Create".equals(adminAction)) {

            String operation = null;
            Object values[] = null;

            try {
                String serviceName = hform.getServiceName();
                ObjectName soname = new ObjectName(serviceName);
                String domain = soname.getDomain();
                // Ensure that the requested host name is unique
                ObjectName oname =
                    new ObjectName(domain + 
                                   TomcatTreeBuilder.HOST_TYPE +
                                   ",host=" + hform.getHostName());
                if (mBServer.isRegistered(oname)) {
                    ActionErrors errors = new ActionErrors();
                    errors.add("hostName",
                               new ActionError("error.hostName.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                }

                // Look up our MBeanFactory MBean
                ObjectName fname = TomcatTreeBuilder.getMBeanFactory();

                // Create a new StandardHost object
                values = new Object[9];
                values[0] = domain + TomcatTreeBuilder.ENGINE_TYPE;
                values[1] = hform.getHostName();
                values[2] = hform.getAppBase();
                values[3] = new Boolean(hform.getAutoDeploy());
                values[4] = new Boolean(hform.getDeployOnStartup());
                values[5] = new Boolean(hform.getDeployXML());
                values[6] = new Boolean(hform.getUnpackWARs());
                values[7] = new Boolean(hform.getXmlNamespaceAware());
                values[8] = new Boolean(hform.getXmlValidation());


                operation = "createStandardHost";
                hObjectName = (String)
                    mBServer.invoke(fname, operation,
                                    values, createStandardHostTypes);

                // Add the new Host to our tree control node
                TreeControl control = (TreeControl)
                    session.getAttribute("treeControlTest");
                if (control != null) {
                    String parentName = serviceName;
                    TreeControlNode parentNode = control.findNode(parentName);
                    if (parentNode != null) {
                        String nodeLabel =
                            resources.getMessage(locale, "server.service.treeBuilder.host") +
                            " (" + hform.getHostName() + ")";
                        String encodedName =
                            URLEncoder.encode(hObjectName,TomcatTreeBuilder.URL_ENCODING);
                        TreeControlNode childNode =
                            new TreeControlNode(hObjectName,
                                                "Host.gif",
                                                nodeLabel,
                                                "EditHost.do?select=" +
                                                encodedName,
                                                "content",
                                                true, domain);
                        parentNode.addChild(childNode);
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

            honame = new ObjectName(hObjectName);

            attribute = "appBase";
            String appBase = "";
            try {
                appBase = hform.getAppBase();
            } catch (Throwable t) {
                appBase = "";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("appBase", appBase));

            attribute = "autoDeploy";
            String autoDeploy = "true";
            try {
                autoDeploy = hform.getAutoDeploy();
            } catch (Throwable t) {
                autoDeploy = "true";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("autoDeploy", new Boolean(autoDeploy)));

            attribute = "deployXML";
            String deployXML = "true";
            try {
                deployXML = hform.getDeployXML();
            } catch (Throwable t) {
                deployXML = "true";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("deployXML", new Boolean(deployXML)));

            attribute = "deployOnStartup";
            String deployOnStartup = "true";
            try {
                deployOnStartup = hform.getDeployOnStartup();
            } catch (Throwable t) {
                deployOnStartup = "true";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("deployOnStartup", new Boolean(deployOnStartup)));
                                  
            attribute = "unpackWARs";
            String unpackWARs = "false";
            try {
                unpackWARs = hform.getUnpackWARs();
            } catch (Throwable t) {
                unpackWARs = "false";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("unpackWARs", new Boolean(unpackWARs)));

            attribute = "xmlNamespaceAware";
            String xmlNamespaceAware = "false";
            try {
                xmlNamespaceAware = hform.getXmlNamespaceAware();
            } catch (Throwable t) {
                xmlNamespaceAware = "false";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("xmlNamespaceAware", new Boolean(xmlNamespaceAware)));

            attribute = "xmlValidation";
            String xmlValidation = "false";
            try {
                xmlValidation = hform.getXmlValidation();
            } catch (Throwable t) {
                xmlValidation = "false";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("xmlValidation", new Boolean(xmlValidation)));

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
