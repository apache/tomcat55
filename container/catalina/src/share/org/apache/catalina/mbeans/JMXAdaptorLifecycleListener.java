/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.mbeans;

import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardServer;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.rmi.RMIConnectorServer;

/**
 * Start JSR160 JMX Adapter with naming and jmx port! Add only as Server Listner
 * to your tomcat <i>server.xml</i> <br/><pre>
 * &lt;Server ...&gt;
 * ...
 *   &lt;Listener className="org.apache.catalina.mbeans.JMXAdaptorLifecycleListener"<br/>
 *             namingPort="8083" port="8084" host="myhost" />  
 * ...
 * &lt;/Server&gt;
 * </pre>
 * 
 * You can use normal jmx system properties from command line or jmx config file: 
 * <ul>
 * <li><code>-Dcom.sun.management.jmxremote.authenticate=true</code></li>
 * <li><code>-Dcom.sun.management.jmxremote.ssl=false</code></li>
 * <li><code>-Dcom.sun.management.jmxremote.access.file=$CATALINA_BASE/conf/access.file</code></li>
 * <li><code>-Dcom.sun.management.jmxremote.password.file=$CATALINA_BASE/conf/password.file</code></li>
 * <li><code>-Dcom.sun.management.config.file=$CATALINA_BASE/conf/jmx.properties</code></li>
 * </ul>
 * <br/>
 * Then run jconsole with:
 * <code>
 * jconsole service:jmx:rmi://myhost:8084/jndi/rmi://myhost:8083/server
 * </code>
 * 
 * <p>
 * It would be be better if this was built into Tomcat as a configuration
 * option, rather than having to do it as part of every Tomcat instance.
 * </p>
 * <p>Origanal code idea comes from George Lindholm read
 * <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=39055">Tomcat Bug 39055<a/>
 * and other helpful links are:<br/>
 * <ul>
 * <li><a href="http://today.java.net/pub/a/today/2005/11/15/using-jmx-to-manage-web-applications.html">Using Web Apps</a></li>
 * <li><a href="http://java.sun.com/j2se/1.5.0/docs/guide/management/agent.html#SSL_enabled">JVM 1.5 JMX Guide</a></li>
 * </p>
 * @author Peter Rossbach
 * @author Juergen Herrmann
 */
public class JMXAdaptorLifecycleListener implements LifecycleListener {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(JMXAdaptorLifecycleListener.class);

    /**
     * The descriptive information string for this implementation.
     */
    private static final String info = "org.apache.catalina.mbeans.JMXAdaptorLifecycleListener/1.0";

    private boolean enabled = true;

    private int namingPort = 0;

    private int port = 0;

    private String host = null;

    private String jmxUrl = null;

    private JMXConnectorServer connectorServer = null;

    private Properties managementProperties = null;

    /**
     * create new jmx adaptor and read properties from file.
     * Use jvm property <code>-Dcom.sun.management.config.file=xxx</code> as file.
     *
     */
    public JMXAdaptorLifecycleListener() {
        final String configProperty = "com.sun.management.config.file";
        String configFileName = System.getProperty(configProperty);

        if (configFileName != null) {
            try {
                FileInputStream configFile = new FileInputStream(configFileName);
                try {
                    managementProperties = new Properties();
                    managementProperties.load(configFile);
                } finally {
                    configFile.close();
                }
            } catch (FileNotFoundException ex) {
                log.error("Cannot open " + configFileName, ex);
            } catch (IOException ex) {
                log.error("Error while reading " + configFileName, ex);
            }
        }
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host The host to set.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return Returns the port.
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        if (port < 1 || 65535 < port) {
            log.warn("Illegal port value " + port);
            port = 0;
        }
        this.port = port;
    }

    /**
     * @return Returns the namingPort.
     */
    public int getNamingPort() {
        return namingPort;
    }

    /**
     * @param namingPort The namingPort to set.
     */
    public void setNamingPort(int namingPort) {
        this.namingPort = namingPort;
        if (namingPort < 1 || 65535 < namingPort) {
            log.warn("Illegal namingPort value " + namingPort);
            namingPort = 0;
        }
        this.namingPort = namingPort;
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the jmxUrl
     */
    public String getJmxUrl() {
        return jmxUrl;
    }

    /**
     * Return descriptive information about this Listener implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }

    /*
     * Start/Stop JSR 160 Adaptor
     * 
     * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
     */
    public void lifecycleEvent(LifecycleEvent event) {
        Object source = event.getSource();
        if (!(source instanceof StandardServer))
            return;

        if (Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
            start();
        }
        if (Lifecycle.BEFORE_STOP_EVENT.equals(event.getType())) {
            stop();
        }
    }

    /**
     * stop jmx connector at tomcat normale shutdown 
     *
     */
    protected void stop() {
        if (connectorServer != null) {
            if (log.isInfoEnabled())
                log.info("JMXConnectorServer stopping on " + jmxUrl);

            try {
                // Stop connector, else shutdown takes a lot longer
                connectorServer.stop();
                jmxUrl = null;
            } catch (IOException ex) {
                // We always get this (why?), so just trace it
                log.error("Error while stopping remote JMX connector", ex);
            }
            connectorServer = null;
        }
    }

    /**
     * get jmx config parameter from jmx config file or system property.
     * @param name
     * @return config parameter value
     */
    protected String getConfigProperty(String name) {
        String result = null;
        if (managementProperties != null) {
            result = managementProperties.getProperty(name);
        }
        if (result == null) {
            result = System.getProperty(name);
        }
        return result;
    }

    /**
     * Start JMX connector and local name registry
     *
     */
    public void start() {

        if (enabled) {
            if (namingPort == 0 || port == 0)
                return;
 
            try {
                if (host == null || "".equals(host)) {
                    final InetAddress address = InetAddress.getLocalHost();
                    host = address.getHostName();
                }

                // naming
                LocateRegistry.createRegistry(namingPort);
                // tomcat normal mbeanserver
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

                // read ssl config
                HashMap env = new HashMap();
                final String sslProperty = "com.sun.management.jmxremote.ssl";
                String value = getConfigProperty(sslProperty);
                if (Boolean.valueOf(value).booleanValue()) {
                    if (log.isInfoEnabled())
                        log.info("Activated SSL communication");
                    SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
                    SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();
                    env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE,csf);
                    env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE,ssf);
                }

                // read auth config
                final String authenticateProperty = "com.sun.management.jmxremote.authenticate";
                value = getConfigProperty(authenticateProperty);
                if (log.isTraceEnabled())
                    log.trace(authenticateProperty + " is " + value);

                if (Boolean.valueOf(value).booleanValue()) {
                    final String accessFileProperty = "com.sun.management.jmxremote.access.file";
                    value = getConfigProperty(accessFileProperty);
                    if (log.isTraceEnabled())
                        log.trace(accessFileProperty + " is " + value);
                    if (value != null) {
                        env.put("jmx.remote.x.access.file", value);
                    }

                    final String passwordFileProperty = "com.sun.management.jmxremote.password.file";
                    value = getConfigProperty(passwordFileProperty);
                    if (log.isTraceEnabled())
                        log.trace(passwordFileProperty + " is " + value);
                    if (value != null) {
                        env.put("jmx.remote.x.password.file", value);
                    }
                } else {
                    log.warn("Unsafe JMX remote access!");
                }

                // create jmx adaptor
                jmxUrl = "service:jmx:rmi://" + host + ":" + port
                        + "/jndi/rmi://" + host + ":" + namingPort + "/server";
                final JMXServiceURL url = new JMXServiceURL(jmxUrl);
                connectorServer = JMXConnectorServerFactory
                        .newJMXConnectorServer(url, env, mbs);

                // start jmx adaptor
                try {
                    connectorServer.start();
                    if (log.isInfoEnabled())
                        log.info("JMXConnectorServer started on " + jmxUrl);
                } catch (IOException ex) {
                    log.warn("Cannot start JMXConnectorServer on " + jmxUrl, ex);
                }
            } catch (Exception ex) {
                log.error("Error while setting up remote JMX connector", ex);
            }
        }
    }

}
