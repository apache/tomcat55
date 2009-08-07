/*
 * Created on Jul 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.tomcat.servlets.jmxremote;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Using o.a.tomcat.servlets because o.a.catalina won't be loaded unless trusted app 

/**
 * Experimental servlet allowing connectors to be deployed and managed using the 
 * webapp infrastructure.
 * 
 * Connectors can be packaged in a WAR file, deployed and managed using the 
 * normal tools. Configuration is done using web.xml init-params - while this is 
 * not as simple as <connector> tags in server.xml, it may be easier to support
 * in tools and explain to webapp developers.
 * 
 * Since webapp class loader is used - start/stop as well as reloading can be 
 * controlled from the /manager.
 * 
 * Issues:
 *  - may polute the webapps namespace - solution would be to reserve a prefix
 * or use some invalid/special name.    
 *
 * @author Costin Manolache
 */
public class JmxRemoteServlet extends HttpServlet {
    JMXConnectorServer cntorServer = null; 
    
    public void init(ServletConfig conf) throws ServletException {
        // otherwise log doesn't work
        super.init(conf);
        
        MBeanServer mBeanServer = null;

        Registry reg=null;
        
        // TODO: use config to get the registry port, url, pass, user

        
        try {
            if( reg==null )
                reg=LocateRegistry.createRegistry(1099);
        } catch( Throwable t ) {
            log("Can't start registry - it may be already started: " + t);
        }
        
        try {
            mBeanServer = null;
            if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                mBeanServer =
                    (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            } else {
                mBeanServer = MBeanServerFactory.createMBeanServer();
            }
        } catch( Throwable t ) {
            log("Can't get the mbean server " + t);
            return;
        }
        
        try {
            JMXServiceURL address = new JMXServiceURL("service:jmx:rmi://rmiHost/jndi/rmi://localhost:1099/jndiPath");
            cntorServer = 
                JMXConnectorServerFactory.newJMXConnectorServer(address, null, mBeanServer);
            cntorServer.start();
        } catch (Throwable e) {
            log("Can't register jmx connector ", e);
        }
    }

    /** Stop the connector
     * 
     */
    public void destroy() {
        try {
            if( cntorServer != null ) cntorServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        resp.sendError(404);
    }

    // I don't know why super.log is broken in init 
//    public void log(String s) {
//        System.err.println("JMX rem:" + s);
//    }

}
