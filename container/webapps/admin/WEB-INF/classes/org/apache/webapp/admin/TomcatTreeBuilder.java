/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

package org.apache.webapp.admin;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.QueryExp;
import javax.management.Query;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

/**
 * <p> Implementation of TreeBuilder interface for Tomcat Tree Controller
 *     to build plugin components into the tree
 *
 * @author Jazmin Jonson
 * @author Manveen Kaur
 * @author Amy Roh
 * @version $Revision$ $Date$
 */


public class TomcatTreeBuilder implements TreeBuilder{
    
    // This SERVER_LABEL needs to be localized
    private final static String SERVER_LABEL = "Tomcat Server";
    
    public final static String DEFAULT_DOMAIN = "Catalina";
    public final static String SERVER_TYPE = ":type=Server";
    public final static String FACTORY_TYPE = 
                        DEFAULT_DOMAIN + ":type=MBeanFactory";
    public final static String SERVICE_TYPE = ":type=Service";
    public final static String ENGINE_TYPE = ":type=Engine";
    public final static String CONNECTOR_TYPE = ":type=Connector";
    public final static String HOST_TYPE = ":type=Host";
    public final static String CONTEXT_TYPE = ":type=Context";
    public final static String LOADER_TYPE = ":type=Loader";
    public final static String MANAGER_TYPE = ":type=Manager";
    public final static String LOGGER_TYPE = ":type=Logger";
    public final static String REALM_TYPE = ":type=Realm";
    public final static String VALVE_TYPE = ":type=Valve";

    public final static String WILDCARD = ",*";

    public final static String URL_ENCODING="UTF-8";
    
    private static MBeanServer mBServer = null;
    private MessageResources resources = null;
    private Locale locale = null;

    public void buildTree(TreeControl treeControl,
                          ApplicationServlet servlet,
                          HttpServletRequest request) {

        try {
            HttpSession session = request.getSession();
            locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
            mBServer = servlet.getServer();
            TreeControlNode root = treeControl.getRoot();
            resources = (MessageResources)
                servlet.getServletContext().getAttribute(Globals.MESSAGES_KEY);
            getServers(root);
        } catch(Throwable t){
            t.printStackTrace(System.out);
        }

    }
    
    public static ObjectName getMBeanFactory() 
            throws MalformedObjectNameException {
        
        return new ObjectName(FACTORY_TYPE);
    }
    

    /**
     * Append nodes for all defined servers.
     *
     * @param rootNode Root node for the tree control 
     * @param resources The MessageResources for our localized messages
     *  messages
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getServers(TreeControlNode rootNode) throws Exception {
        
        String domain = rootNode.getDomain();
        Iterator serverNames =
            Lists.getServers(mBServer,domain).iterator();
        while (serverNames.hasNext()) {
            String serverName = (String) serverNames.next();
            ObjectName objectName = new ObjectName(serverName);
            String nodeLabel = SERVER_LABEL;
            TreeControlNode serverNode =
                new TreeControlNode(serverName,
                                    "Server.gif",
                                    nodeLabel,
                                    "EditServer.do?select=" +
                                    URLEncoder.encode(serverName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING),
                                    "content",
                                    true, domain);
            rootNode.addChild(serverNode);
            getServices(serverNode, serverName);
        }
        
    }
    

    /**
     * Append nodes for all defined services for the specified server.
     *
     * @param serverNode Server node for the tree control
     * @param serverName Object name of the parent server
     * @param resources The MessageResources for our localized messages
     *  messages
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getServices(TreeControlNode serverNode, String serverName) 
        throws Exception {

        String domain = serverNode.getDomain();
        Iterator serviceNames =
            Lists.getServices(mBServer, serverName).iterator();
        while (serviceNames.hasNext()) {
            String serviceName = (String) serviceNames.next();
            ObjectName objectName = new ObjectName(serviceName);
            String nodeLabel =
                resources.getMessage(locale, 
                    "server.service.treeBuilder.subtreeNode") + " (" +
                    objectName.getKeyProperty("serviceName") + ")";
            TreeControlNode serviceNode =
                new TreeControlNode(serviceName,
                                    "Service.gif",
                                    nodeLabel,
                                    "EditService.do?select=" +
                                    URLEncoder.encode(serviceName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING),
                                    "content",
                                    false, domain);
            serverNode.addChild(serviceNode);
            getConnectors(serviceNode, serviceName);
            getHosts(serviceNode, serviceName);
            getRealms(serviceNode, serviceName);
            getValves(serviceNode, serviceName);
        }

    }
    

    /**
     * Append nodes for all defined connectors for the specified service.
     *
     * @param serviceNode Service node for the tree control
     * @param serviceName Object name of the parent service
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getConnectors(TreeControlNode serviceNode, String serviceName)
                        throws Exception{
        
        String domain = serviceNode.getDomain();
        Iterator connectorNames =
            Lists.getConnectors(mBServer, serviceName).iterator();
        while (connectorNames.hasNext()) {
            String connectorName = (String) connectorNames.next();
            ObjectName objectName = new ObjectName(connectorName);
            String nodeLabel =
                resources.getMessage(locale, 
                    "server.service.treeBuilder.connector") + " (" +  
                    objectName.getKeyProperty("port") + ")";
            TreeControlNode connectorNode =
                new TreeControlNode(connectorName,
                                    "Connector.gif",
                                    nodeLabel,
                                    "EditConnector.do?select=" +
                                    URLEncoder.encode(connectorName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING),
                                    "content",
                                    false, domain);
            serviceNode.addChild(connectorNode);
        }
    }
    

    /**
     * Append nodes for all defined hosts for the specified service.
     *
     * @param serviceNode Service node for the tree control
     * @param serviceName Object name of the parent service
     * @param resources The MessageResources for our localized messages
     *  messages
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getHosts(TreeControlNode serviceNode, String serviceName) 
        throws Exception {
        
        String domain = serviceNode.getDomain();
        Iterator hostNames =
            Lists.getHosts(mBServer, serviceName).iterator();
        while (hostNames.hasNext()) {
            String hostName = (String) hostNames.next();
            ObjectName objectName = new ObjectName(hostName);
            String nodeLabel =
                resources.getMessage(locale, 
                    "server.service.treeBuilder.host") + " (" +
                    objectName.getKeyProperty("host") + ")";
            TreeControlNode hostNode =
                new TreeControlNode(hostName,
                                    "Host.gif",
                                    nodeLabel,
                                    "EditHost.do?select=" +
                                    URLEncoder.encode(hostName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING),
                                    "content",
                                    false, domain);
            serviceNode.addChild(hostNode);
            getContexts(hostNode, hostName);            
            getRealms(hostNode, hostName);
            getValves(hostNode, hostName);
        }

    }    

    
    /**
     * Append nodes for all defined contexts for the specified host.
     *
     * @param hostNode Host node for the tree control
     * @param hostName Object name of the parent host
     * @param resources The MessageResources for our localized messages
     *  messages
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getContexts(TreeControlNode hostNode, String hostName) 
        throws Exception {
        
        String domain = hostNode.getDomain();
        Iterator contextNames =
            Lists.getContexts(mBServer, hostName).iterator();
        while (contextNames.hasNext()) {
            String contextName = (String) contextNames.next();
            ObjectName objectName = new ObjectName(contextName);
            String name = objectName.getKeyProperty("name");
            name = name.substring(2);
            int i = name.indexOf("/");
            String path = name.substring(i);
            String nodeLabel =
                resources.getMessage(locale, 
                    "server.service.treeBuilder.context") + " (" + path + ")";
            TreeControlNode contextNode =
                new TreeControlNode(contextName,
                                    "Context.gif",
                                    nodeLabel,
                                    "EditContext.do?select=" +
                                    URLEncoder.encode(contextName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING),
                                    "content",
                                    false, domain);
            hostNode.addChild(contextNode);
            getResources(contextNode, contextName);
            getRealms(contextNode, contextName);
            getValves(contextNode, contextName);
        }
    }
    
    /**
     * Append nodes for any defined realms for the specified container.
     *
     * @param containerNode Container node for the tree control
     * @param containerName Object name of the parent container
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getRealms(TreeControlNode containerNode,
                          String containerName) throws Exception {

        String domain = containerNode.getDomain();
        Iterator realmNames =
            Lists.getRealms(mBServer, containerName).iterator();
        while (realmNames.hasNext()) {
            String realmName = (String) realmNames.next();
	    ObjectName objectName = new ObjectName(realmName);
            // Create tree nodes for non JAASRealm only
            try {
                mBServer.getAttribute(objectName, "validate");
            } catch (AttributeNotFoundException e) {
                String nodeLabel = resources.getMessage(locale, 
                    "server.service.treeBuilder.realmFor", 
                    containerNode.getLabel());
	        TreeControlNode realmNode =
		    new TreeControlNode(realmName,
                                    "Realm.gif",
                                    nodeLabel,
                                    "EditRealm.do?select=" +
                                    URLEncoder.encode(realmName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING),
                                    "content",
                                    false, domain);
                containerNode.addChild(realmNode);
            }
        }
        
    }   
        
    
    /**
     * Append nodes for any define resources for the specified Context.
     *
     * @param containerNode Container node for the tree control
     * @param containerName Object name of the parent container
     * @param resources The MessageResources for our localized messages
     *  messages
     */
    public void getResources(TreeControlNode containerNode, String containerName) 
        throws Exception {

        String domain = containerNode.getDomain();
        ObjectName oname = new ObjectName(containerName);
        String type = oname.getKeyProperty("type");
        if (type == null) {
            type = oname.getKeyProperty("j2eeType");
            if (type.equals("WebModule")) {
                type = "Context";
            } else {
                type = "";
            }
        }
        String path = "";
        String host = "";
        String name = oname.getKeyProperty("name");
        if ((name != null) && (name.length() > 0)) {
            // context resource
            name = name.substring(2);
            int i = name.indexOf("/");
            host = name.substring(0,i);
            path = name.substring(i);
        }     
        TreeControlNode subtree = new TreeControlNode
            ("Context Resource Administration " + containerName,
             "folder_16_pad.gif",
             resources.getMessage(locale, "resources.treeBuilder.subtreeNode"),
             null,
             "content",
             true, domain);        
        containerNode.addChild(subtree);
        TreeControlNode datasources = new TreeControlNode
            ("Context Data Sources " + containerName,
            "Datasource.gif",
            resources.getMessage(locale, "resources.treeBuilder.datasources"),
            "resources/listDataSources.do?resourcetype=" + 
                URLEncoder.encode(type,URL_ENCODING) + "&path=" +
                URLEncoder.encode(path,URL_ENCODING) + "&host=" + 
                URLEncoder.encode(host,URL_ENCODING) + "&domain=" + 
                URLEncoder.encode(domain,URL_ENCODING) + "&forward=" +
                URLEncoder.encode("DataSources List Setup",URL_ENCODING),
            "content",
            false, domain);
        TreeControlNode mailsessions = new TreeControlNode
            ("Context Mail Sessions " + containerName,
            "Mailsession.gif",
            resources.getMessage(locale, "resources.treeBuilder.mailsessions"),
            "resources/listMailSessions.do?resourcetype=" + 
                URLEncoder.encode(type,URL_ENCODING) + "&path=" +
                URLEncoder.encode(path,URL_ENCODING) + "&host=" + 
                URLEncoder.encode(host,URL_ENCODING) + "&domain=" + 
                URLEncoder.encode(domain,URL_ENCODING) + "&forward=" +
                URLEncoder.encode("MailSessions List Setup",URL_ENCODING),
            "content",
            false, domain);
        TreeControlNode resourcelinks = new TreeControlNode
            ("Resource Links " + containerName,
            "ResourceLink.gif",
            resources.getMessage(locale, "resources.treeBuilder.resourcelinks"),
            "resources/listResourceLinks.do?resourcetype=" + 
                URLEncoder.encode(type,URL_ENCODING) + "&path=" +
                URLEncoder.encode(path,URL_ENCODING) + "&host=" + 
                URLEncoder.encode(host,URL_ENCODING) + "&domain=" + 
                URLEncoder.encode(domain,URL_ENCODING) + "&forward=" +
                URLEncoder.encode("ResourceLinks List Setup",URL_ENCODING),
            "content",
            false, domain);
        TreeControlNode envs = new TreeControlNode
            ("Context Environment Entries "+ containerName,
            "EnvironmentEntries.gif",
            resources.getMessage(locale, "resources.env.entries"),
            "resources/listEnvEntries.do?resourcetype=" + 
                URLEncoder.encode(type,URL_ENCODING) + "&path=" +
                URLEncoder.encode(path,URL_ENCODING) + "&host=" + 
                URLEncoder.encode(host,URL_ENCODING) + "&domain=" + 
                URLEncoder.encode(domain,URL_ENCODING) + "&forward=" +
                URLEncoder.encode("EnvEntries List Setup",URL_ENCODING),
            "content",
            false, domain);
        subtree.addChild(datasources);
        subtree.addChild(mailsessions);
        subtree.addChild(resourcelinks);
        subtree.addChild(envs);
    }
    
    
   /**
     * Append nodes for any defined valves for the specified container.
     *
     * @param containerNode Container node for the tree control
     * @param containerName Object name of the parent container
     *
     * @exception Exception if an exception occurs building the tree
     */
    public void getValves(TreeControlNode containerNode,
                          String containerName) throws Exception {

        String domain = containerNode.getDomain();
        Iterator valveNames =
                Lists.getValves(mBServer, containerName).iterator();        
        while (valveNames.hasNext()) {
            String valveName = (String) valveNames.next();
            ObjectName objectName = new ObjectName(valveName);
            String nodeLabel = "Valve for " + containerNode.getLabel();
            TreeControlNode valveNode =
                new TreeControlNode(valveName,
                                    "Valve.gif",
                                    nodeLabel,
                                    "EditValve.do?select=" +
                                    URLEncoder.encode(valveName,URL_ENCODING) +
                                    "&nodeLabel=" +
                                    URLEncoder.encode(nodeLabel,URL_ENCODING) +
                                    "&parent=" +
                                    URLEncoder.encode(containerName,URL_ENCODING),
                                    "content",
                                    false, domain);
            containerNode.addChild(valveNode);
        }
    }
}
