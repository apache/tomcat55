/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.catalina.cluster.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Properties;

import org.apache.catalina.cluster.Member;
import org.apache.catalina.util.StringManager;

/**
 * Create DataSender for different modes. DataSender factory load mode list from 
 * <code>org/apache/catalina/cluster/tcp/DataSenders.properties</code> resource.
 * 
 * @author Peter Rossbach
 * @version $Revision$ $Date$
 * @since 5.5.7
 */
public class IDataSenderFactory {

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(IDataSenderFactory.class);
    
    private static final String DATASENDERS_PROPERTIES = "org/apache/catalina/cluster/tcp/DataSenders.properties";
    public static final String SYNC_MODE = "synchronous";
    public static final String ASYNC_MODE = "asynchronous";
    public static final String POOLED_SYNC_MODE = "pooled";
    public static final String FAST_ASYNC_QUEUE_MODE = "fastasyncqueue";

    /**
     * The string manager for this package.
     */
    protected static StringManager sm = StringManager
            .getManager(Constants.Package);

    // ----------------------------------------------------- Instance Variables

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "IDataSenderFactory/2.0";

    private IDataSenderFactory() {
    }

    private Properties senderModes;

    private static IDataSenderFactory factory ;

    static {
        factory = new IDataSenderFactory();
        factory.loadSenderModes();
    }

    // ------------------------------------------------------------- Properties

    /**
     * Return descriptive information about this implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public static String getInfo() {
        return (info);
    }
    
    // ------------------------------------------------------------- static

    /**
     * Create a new DataSender
     * @param mode replicaton mode 
     * @param mbr sender target
     * @return new sender object
     * @throws java.io.IOException
     */
    public synchronized static IDataSender getIDataSender(String mode,
            Member mbr) throws java.io.IOException {
       // Identify the class name of the DataSender we should configure
       IDataSender sender = factory.getSender(mode,mbr);
       if(sender == null)
           throw new java.io.IOException("Invalid replication mode=" + mode);          
       return sender ;    
    }

    /**
     * Check that mode is valid
     * @param mode
     * @return
     */
    public static String validateMode(String mode) {
        if(factory.isSenderMode(mode))
            return null ;
        else {
            StringBuffer buffer = new StringBuffer("Replication mode has to be '");
            for (Iterator iter = factory.senderModes.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();  
                buffer.append(key);
                if(iter.hasNext())
                    buffer.append("', '");
            }
            return buffer.toString();        
        }
    }
    
    // ------------------------------------------------------------- private

    private boolean isSenderMode(String mode){
        return senderModes != null && senderModes.containsKey(mode) ;           
    }

    private IDataSender getSender(String mode,Member mbr) {
        IDataSender sender = null;
        String senderName = null;
        senderName = senderModes.getProperty(mode);
        if (senderName != null) {

            // Instantiate and install a data replication sender of the requested class
            try {
                Class senderClass = Class.forName(senderName);
                Class paramTypes[] = new Class[3];
                paramTypes[0] = Class.forName("java.lang.String");
                paramTypes[1] = Class.forName("java.net.InetAddress");
                paramTypes[2] = Integer.TYPE ;
                Constructor constructor = senderClass.getConstructor(paramTypes);
                if (constructor != null) {
                    Object paramValues[] = new Object[3];
                    paramValues[0] = mbr.getDomain();
                    paramValues[1] = InetAddress.getByName(mbr.getHost());
                    paramValues[2] = new Integer(mbr.getPort());
                    sender = (IDataSender) constructor.newInstance(paramValues);
                } else {
                    log.error(sm.getString("IDataSender.senderModes.Instantiate",
                            senderName));
                }
            } catch (Throwable t) {
                log.error(sm.getString("IDataSender.senderModes.Instantiate",
                        senderName), t);
            }
        } else {
            log.error(sm.getString("IDataSender.senderModes.Missing", mode));
        }
        return sender;
    }

    private synchronized void loadSenderModes() {
        // Load our mapping properties if necessary
        if (senderModes == null) {
            try {
                InputStream is = IDataSender.class
                        .getClassLoader()
                        .getResourceAsStream(
                                DATASENDERS_PROPERTIES);
                if (is != null) {
                    senderModes = new Properties();
                    senderModes.load(is);
                } else {
                    log.error(sm.getString("IDataSender.senderModes.Resources"));
                    return;
                }
            } catch (IOException e) {
                log.error(sm.getString("IDataSender.senderModes.Resources"), e);
                return;
            }
        }

    }

}
