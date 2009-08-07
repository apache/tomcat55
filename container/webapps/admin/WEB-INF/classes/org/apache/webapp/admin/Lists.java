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

package org.apache.webapp.admin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 * General purpose utility methods to create lists of objects that are
 * commonly required in building the user interface.  In all cases, if there
 * are no matching elements, a zero-length list (rather than <code>null</code>)
 * is returned.
 *
 * @author Craig R. McClanahan
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public class Lists {


    // ----------------------------------------------------------- Constructors


    /**
     * Protected constructor to prevent instantiation.
     */
    protected Lists() { }


    // ------------------------------------------------------- Static Variables


    /**
     * Precomputed list of verbosity level labels and values.
     */
    private static List verbosityLevels = new ArrayList();

    static {
        verbosityLevels.add(new LabelValueBean("0", "0"));
        verbosityLevels.add(new LabelValueBean("1", "1"));
        verbosityLevels.add(new LabelValueBean("2", "2"));
        verbosityLevels.add(new LabelValueBean("3", "3"));
        verbosityLevels.add(new LabelValueBean("4", "4"));
    }

    /**
     * Precomputed list of (true,false) labels and values.
     */
    private static List booleanValues = new ArrayList();

    static {
            booleanValues.add(new LabelValueBean("True", "true"));
            booleanValues.add(new LabelValueBean("False", "false"));
    }

    /**
     * Precomputed list of clientAuth lables and values.
     */
    private static List clientAuthValues = new ArrayList();

    static {
            clientAuthValues.add(new LabelValueBean("True","true"));
            clientAuthValues.add(new LabelValueBean("False","false"));
            clientAuthValues.add(new LabelValueBean("Want","want"));
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Return a <code>List</code> of {@link LabelValueBean}s for the legal
     * settings for <code>verbosity</code> properties.
     */
    public static List getVerbosityLevels() {

        return (verbosityLevels);

    }

    /**
     * Return a <code>List</code> of {@link LabelValueBean}s for the legal
     * settings for <code>boolean</code> properties.
     */
    public static List getBooleanValues() {

        return (booleanValues);

    }
    /**
     * Return a <code>List</code> of {@link LabelValueBean}s for the legal
     * settings for <code>clientAuth</code> properties.
     */
    public static List getClientAuthValues() {

        return (clientAuthValues);

    }

    /**
     * Return a list of <code>Connector</code> object name strings
     * for the specified <code>Service</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param service Object name of the service for which to select connectors
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getConnectors(MBeanServer mbserver, ObjectName service)
        throws Exception {

        StringBuffer sb = new StringBuffer(service.getDomain());
        sb.append(":type=Connector,*");
        ObjectName search = new ObjectName(sb.toString());
        ArrayList connectors = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            connectors.add(names.next().toString());
        }
        Collections.sort(connectors);
        return (connectors);

    }


    /**
     * Return a list of <code>Connector</code> object name strings
     * for the specified <code>Service</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param service Object name of the service for which to select connectors
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getConnectors(MBeanServer mbserver, String service)
        throws Exception {

        return (getConnectors(mbserver, new ObjectName(service)));

    }


    /**
     * Return a list of <code>Context</code> object name strings
     * for the specified <code>Host</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param host Object name of the host for which to select contexts
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getContexts(MBeanServer mbserver, ObjectName host)
        throws Exception {

        StringBuffer sb = new StringBuffer(host.getDomain());
        sb.append(":j2eeType=WebModule,*");
        ObjectName search = new ObjectName(sb.toString());
        ArrayList contexts = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        String name = null;
        ObjectName oname = null;
        String hostPrefix = "//"+host.getKeyProperty("host");
        String hostAttr = null;
        while (names.hasNext()) {
            name = names.next().toString();
            oname = new ObjectName(name);
            hostAttr = oname.getKeyProperty("name");
            if (hostAttr.startsWith(hostPrefix)) {
                contexts.add(name);
            }
        }
        Collections.sort(contexts);
        return (contexts);

    }


    /**
     * Return a list of <code>DefaultContext</code> object name strings
     * for the specified <code>Host</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param container Object name of the container for which to select default
     * contexts
     * @param containerType The type of the container for which to select 
     * default contexts
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getDefaultContexts(MBeanServer mbserver, String 
        container) throws Exception {

        return (getDefaultContexts(mbserver, new ObjectName(container)));

    }
    
    
    /**
     * Return a list of <code>DefaultContext</code> object name strings
     * for the specified <code>Host</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param container Object name of the container for which to select default
     * contexts
     * @param containerType The type of the container for which to select 
     * default contexts
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getDefaultContexts(MBeanServer mbserver, ObjectName 
        container) throws Exception {

        // FIXME
        StringBuffer sb = new StringBuffer(container.getDomain());
        sb.append(":type=DefaultContext");
        String type = container.getKeyProperty("type");
        String host = container.getKeyProperty("host");
        if ("Host".equals(type)) {
            host = container.getKeyProperty("host");
        }
        if (host != null) {
            sb.append(",host=");
            sb.append(host);
        }
        ObjectName search = new ObjectName(sb.toString());
        ArrayList defaultContexts = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            String name = names.next().toString();
            defaultContexts.add(name);
        }
        Collections.sort(defaultContexts);
        return (defaultContexts);

    }


    /**
     * Return a list of <code>Context</code> object name strings
     * for the specified <code>Host</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param host Object name of the host for which to select contexts
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getContexts(MBeanServer mbserver, String host)
        throws Exception {

        return (getContexts(mbserver, new ObjectName(host)));

    }

    /**
     * Return a list of <code>Host</code> object name strings
     * for the specified <code>Service</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param service Object name of the service for which to select hosts
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getHosts(MBeanServer mbserver, ObjectName service)
        throws Exception {

        StringBuffer sb = new StringBuffer(service.getDomain());
        sb.append(":type=Host,*");
        ObjectName search = new ObjectName(sb.toString());
        ArrayList hosts = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            hosts.add(names.next().toString());
        }
        Collections.sort(hosts);
        return (hosts);

    }


    /**
     * Return a list of <code>Host</code> object name strings
     * for the specified <code>Service</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param service Object name of the service for which to select hosts
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getHosts(MBeanServer mbserver, String service)
        throws Exception {

        return (getHosts(mbserver, new ObjectName(service)));

    }


    /**
     * Return a list of <code>Realm</code> object name strings
     * for the specified container (service, host, or context) object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param container Object name of the container for which to select
     *                  realms
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getRealms(MBeanServer mbserver, ObjectName container)
        throws Exception {

        ObjectName search = getSearchObject(container, "Realm");
        ArrayList realms = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            realms.add(names.next().toString());
        }
        Collections.sort(realms);
        return (realms);

    }


    /**
     * Return a list of <code>Realm</code> object name strings
     * for the specified container (service, host, or context) object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param container Object name of the container for which to select
     *                  realms
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getRealms(MBeanServer mbserver, String container)
        throws Exception {

        return (getRealms(mbserver, new ObjectName(container)));

    }

    /**
     * Return a list of <code>Valve</code> object name strings
     * for the specified container (service, host, or context) object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param container Object name of the container for which to select
     *                  Valves
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getValves(MBeanServer mbserver, ObjectName container)
        throws Exception {

        StringBuffer sb = new StringBuffer(container.getDomain());
        sb.append(":type=Valve");
        String type = container.getKeyProperty("type");
        String j2eeType = container.getKeyProperty("j2eeType");
        sb.append(TomcatTreeBuilder.WILDCARD);
        String host = "";
        String path = "";
        String name = container.getKeyProperty("name");
        if ((name != null) && (name.length() > 0)) {
            // parent is context
            name = name.substring(2);
            int i = name.indexOf("/");
            host = name.substring(0,i);
            path = name.substring(i);
        } else if ("Host".equals(type)) {
            // parent is host
            host = container.getKeyProperty("host");
        }    
        
        ObjectName search = new ObjectName(sb.toString());        
        ArrayList valves = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            ObjectName valve = (ObjectName) names.next();
            String vpath = valve.getKeyProperty("path");            
            String vhost = valve.getKeyProperty("host");
            
            String valveType = null;
            String className = (String) 
                    mbserver.getAttribute(valve, "className");
            int period = className.lastIndexOf(".");
            if (period >= 0)
                valveType = className.substring(period + 1);

           // Return only user-configurable valves.
           if ("AccessLogValve".equalsIgnoreCase(valveType) ||
               "RemoteAddrValve".equalsIgnoreCase(valveType) ||
               "RemoteHostValve".equalsIgnoreCase(valveType) || 
               "RequestDumperValve".equalsIgnoreCase(valveType) ||
               "SingleSignOn".equalsIgnoreCase(valveType)) {
            // if service is the container, then the valve name
            // should not contain path or host                   
            if ("Service".equalsIgnoreCase(type)) {
                if ((vpath == null) && (vhost == null)) {
                    valves.add(valve.toString());
                }
            } 
            
            if ("Host".equalsIgnoreCase(type)) {
                if ((vpath == null) && (host.equalsIgnoreCase(vhost))) { 
                    valves.add(valve.toString());      
                }
            }
            
            if ("WebModule".equalsIgnoreCase(j2eeType)) {
                if ((path.equalsIgnoreCase(vpath)) && (host.equalsIgnoreCase(vhost))) {
                    valves.add(valve.toString());      
                }
            }
           }
        }        
        Collections.sort(valves);
        return (valves);
    }

    
    /**
     * Return a list of <code>Valve</code> object name strings
     * for the specified container (service, host, or context) object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param container Object name of the container for which to select
     *                  valves
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getValves(MBeanServer mbserver, String container)
        throws Exception {

        return (getValves(mbserver, new ObjectName(container)));

    }
    
    /**
     * Return a list of <code>Server</code> object name strings.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getServers(MBeanServer mbserver, String domain)
        throws Exception {

        ObjectName search = new ObjectName(domain+":type=Server,*");
        ArrayList servers = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            servers.add(names.next().toString());
        }
        Collections.sort(servers);
        return (servers);

    }


    /**
     * Return a list of <code>Service</code> object name strings
     * for the specified <code>Server</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param server Object name of the server for which to select services
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getServices(MBeanServer mbserver, ObjectName server)
        throws Exception {

        //StringBuffer sb = new StringBuffer(server.getDomain());
        StringBuffer sb = new StringBuffer("*:type=Service,*");
        //sb.append(":type=Service,*");
        ObjectName search = new ObjectName(sb.toString());
        ArrayList services = new ArrayList();
        Iterator names = mbserver.queryNames(search, null).iterator();
        while (names.hasNext()) {
            services.add(names.next().toString());
        }
        Collections.sort(services);
        return (services);

    }


    /**
     * Return a list of <code>Service</code> object name strings
     * for the specified <code>Server</code> object name.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param server Object name of the server for which to select services
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static List getServices(MBeanServer mbserver, String server)
        throws Exception {

        return (getServices(mbserver, new ObjectName(server)));

    }


    /**
     * Return the  <code>Service</code> object name string
     * that the admin app belongs to.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param request Http request
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static String getAdminAppService
        (MBeanServer mbserver, String domain, HttpServletRequest request)
        throws Exception {

        String adminDomain = TomcatTreeBuilder.DEFAULT_DOMAIN;
        // Get the admin app's service name
        StringBuffer sb = new StringBuffer(adminDomain);
        sb.append(":type=Service,*");
        ObjectName search = new ObjectName(sb.toString());
        Iterator names = mbserver.queryNames(search, null).iterator();
        String service = null;
        while (names.hasNext()) {
            service = ((ObjectName)names.next()).getKeyProperty("serviceName");
        }
        return service;

    }


    /**
     * Return the  <code>Host</code> object name string
     * that the admin app belongs to.
     *
     * @param mbserver MBeanServer from which to retrieve the list
     * @param request Http request
     *
     * @exception Exception if thrown while retrieving the list
     */
    public static String getAdminAppHost
        (MBeanServer mbserver, String domain, HttpServletRequest request)
        throws Exception {
        
        // Get the admin app's host name
        String adminDomain = TomcatTreeBuilder.DEFAULT_DOMAIN;
        StringBuffer sb = new StringBuffer(adminDomain);
        sb.append(":j2eeType=WebModule,*"); 
        ObjectName search = new ObjectName(sb.toString());
        Iterator names = mbserver.queryNames(search, null).iterator();
        String contextPath = request.getContextPath();
        String host = null;
        String name = null;
        ObjectName oname = null;
        while (names.hasNext()) {       
            name = names.next().toString();
            oname = new ObjectName(name);
            host = oname.getKeyProperty("name");
            host = host.substring(2);
            int i = host.indexOf("/");
            if (contextPath.equals(host.substring(i))) {
                host = host.substring(0,i);
                return host;
            }
        }
        return host;

    }

    
    /**
     * Return search object name to be used to query.
     *
     * @param container object name to query
     * @param type type of the component
     *
     * @exception MalformedObjectNameException if thrown while retrieving the list
     */
    public static ObjectName getSearchObject(ObjectName container, String type)  
            throws Exception {
        
        StringBuffer sb = new StringBuffer(container.getDomain());
        sb.append(":type="+type);
        String containerType = container.getKeyProperty("type");
        String name = container.getKeyProperty("name");
        if ((name != null) && (name.length() > 0)) {
            // parent is context
            name = name.substring(2);
            int i = name.indexOf("/");
            String host = name.substring(0,i);
            String path = name.substring(i);
            sb.append(",path=");
            sb.append(path);
            sb.append(",host=");
            sb.append(host);
        } else if ("Host".equals(containerType)) {
            // parent is host
            String host = container.getKeyProperty("host");
            sb.append(",host=");
            sb.append(host);
        }    
        
        return new ObjectName(sb.toString());
        
    }
    
}
