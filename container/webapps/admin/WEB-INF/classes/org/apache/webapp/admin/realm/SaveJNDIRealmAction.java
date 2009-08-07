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

package org.apache.webapp.admin.realm;


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
import org.apache.webapp.admin.valve.ValveUtil;

/**
 * The <code>Action</code> that completes <em>Add Realm</em> and
 * <em>Edit Realm</em> transactions for JNDI realm.
 *
 * @author Manveen Kaur
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public final class SaveJNDIRealmAction extends Action {


    // ----------------------------------------------------- Instance Variables

    /**
     * Signature for the <code>createStandardRealm</code> operation.
     */
    private String createStandardRealmTypes[] =
    { "java.lang.String",     // parent
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
        JNDIRealmForm rform = (JNDIRealmForm) form;
        String adminAction = rform.getAdminAction();
        String rObjectName = rform.getObjectName();

        // Perform a "Create JNDI Realm" transaction (if requested)
        if ("Create".equals(adminAction)) {

            String operation = null;
            String values[] = null;

            try {

                String parent = rform.getParentObjectName();
                String objectName = ValveUtil.getObjectName(parent,
                                    TomcatTreeBuilder.REALM_TYPE);

                ObjectName pname = new ObjectName(parent);
                StringBuffer sb = new StringBuffer(pname.getDomain());

                // For service, create the corresponding Engine mBean
                // Parent in this case needs to be the container mBean for the service
                try {
                    if ("Service".equalsIgnoreCase(pname.getKeyProperty("type"))) {
                        sb.append(":type=Engine");
                        parent = sb.toString();
                    }
                } catch (Exception e) {
                    String message =
                        resources.getMessage("error.engineName.bad",
                                         sb.toString());
                    getServlet().log(message);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                    return (null);
                }

                // Ensure that the requested user database name is unique
                ObjectName oname =
                    new ObjectName(objectName);
                if (mBServer.isRegistered(oname)) {
                    ActionErrors errors = new ActionErrors();
                    errors.add("realmName",
                               new ActionError("error.realmName.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                }

                String domain = oname.getDomain();
                // Look up our MBeanFactory MBean
                ObjectName fname = 
                    TomcatTreeBuilder.getMBeanFactory();

                // Create a new StandardRealm object
                values = new String[1];
                values[0] = parent;
                operation = "createJNDIRealm";
                rObjectName = (String)
                    mBServer.invoke(fname, operation,
                                    values, createStandardRealmTypes);

                if (rObjectName==null) {
                    request.setAttribute("warning", "error.jndirealm");
                    return (mapping.findForward("Save Unsuccessful"));
                }
                
                // Add the new Realm to our tree control node
                TreeControl control = (TreeControl)
                    session.getAttribute("treeControlTest");
                if (control != null) {
                    TreeControlNode parentNode = control.findNode(rform.getParentObjectName());
                    if (parentNode != null) {
                        String nodeLabel = rform.getNodeLabel();
                        String encodedName =
                            URLEncoder.encode(rObjectName,TomcatTreeBuilder.URL_ENCODING);
                        TreeControlNode childNode =
                            new TreeControlNode(rObjectName,
                                                "Realm.gif",
                                                nodeLabel,
                                                "EditRealm.do?select=" +
                                                encodedName,
                                                "content",
                                                true, domain);
                        parentNode.addChild(childNode);
                        // FIXME - force a redisplay
                    } else {
                        getServlet().log
                            ("Cannot find parent node '" + parent + "'");
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

            ObjectName roname = new ObjectName(rObjectName);

            attribute = "connectionName";
            String connectionName = rform.getConnectionName();
            if ((connectionName != null) && (connectionName.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("connectionName",  connectionName));
            }

            attribute = "connectionPassword";
            String connectionPassword = rform.getConnectionPassword();
            if ((connectionPassword != null) && (connectionPassword.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("connectionPassword",  connectionPassword));
            }

            attribute = "connectionURL";
            String connectionURL = rform.getConnectionURL();
            if ((connectionURL != null) && (connectionURL.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("connectionURL",  connectionURL));
            }

            attribute = "contextFactory";
            String contextFactory = rform.getContextFactory();
            if ((contextFactory != null) && (contextFactory.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("contextFactory",  contextFactory));
            }

            attribute = "digest";
            String digest = rform.getDigest();
            if ((digest != null) && (digest.length()>0)) {
                mBServer.setAttribute(roname,
                                        new Attribute("digest", digest));
            }

            attribute = "roleBase";
            String roleBase = rform.getRoleBase();
            if ((roleBase != null) && (roleBase.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("roleBase",  roleBase));
            }

            attribute = "roleName";
            String roleName = rform.getRoleName();
            if ((roleName != null) && (roleName.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("roleName",  roleName));
            }

            attribute = "roleSearch";
            String rolePattern = rform.getRolePattern();
            if ((rolePattern != null) && (rolePattern.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("roleSearch",  rolePattern));
            }

            attribute = "roleSubtree";
            String roleSubtree = rform.getRoleSubtree();
            if ((roleSubtree != null) && (roleSubtree.length()>0)) {
                mBServer.setAttribute(roname,
                    new Attribute("roleSubtree",  new Boolean(roleSubtree)));
            }

            attribute = "userBase";
            String userBase = rform.getUserBase();
            if ((userBase != null) && (userBase.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userBase",  userBase));
            }

            attribute = "userPassword";
            String userPassword = rform.getUserPassword();
            if ((userPassword != null) && (userPassword.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userPassword",  userPassword));
            }

            attribute = "userPattern";
            String userPattern = rform.getUserPattern();
            if ((userPattern != null) && (userPattern.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userPattern",  userPattern));
            }

            attribute = "userRoleName";
            String userRoleName = rform.getUserRoleName();
            if ((userRoleName != null) && (userRoleName.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userRoleName",  userRoleName));
            }

            attribute = "userSearch";
            String userSearch = rform.getUserSearch();
            if ((userSearch != null) && (userSearch.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userSearch",  userSearch));
            }

            attribute = "userSubtree";
            String userSubtree = rform.getUserSubtree();
            if ((userSubtree != null) && (userSubtree.length()>0)) {
                mBServer.setAttribute(roname,
                    new Attribute("userSubtree",  new Boolean(userSubtree)));
            }

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
