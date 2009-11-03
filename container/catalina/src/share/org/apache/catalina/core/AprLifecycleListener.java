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

package org.apache.catalina.core;


import java.lang.reflect.Method;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Implementation of <code>LifecycleListener</code> that will init and
 * and destroy APR.
 *
 * @author Remy Maucherat
 * @version $Revision$ $Date$
 * @since 4.1
 */

public class AprLifecycleListener
    implements LifecycleListener {

    private static Log log = LogFactory.getLog(AprLifecycleListener.class);

    /**
     * The string manager for this package.
     */
    protected StringManager sm =
        StringManager.getManager(Constants.Package);

    protected static String SSLRandomSeed = "builtin";
    
    // -------------------------------------------------------------- Constants


    protected static final int REQUIRED_MAJOR = 1;
    protected static final int REQUIRED_MINOR = 1;
    protected static final int REQUIRED_PATCH = 3;
    protected static final int RECOMMENDED_PV = 4;


    // ---------------------------------------------- LifecycleListener Methods


    /**
     * Primary entry point for startup and shutdown events.
     *
     * @param event The event that has occurred
     */
    public void lifecycleEvent(LifecycleEvent event) {

        if (Lifecycle.INIT_EVENT.equals(event.getType())) {
            int major = 0;
            int minor = 0;
            int patch = 0;
            try {
                String methodName = "initialize";
                Class paramTypes[] = new Class[1];
                paramTypes[0] = String.class;
                Object paramValues[] = new Object[1];
                paramValues[0] = null;
                Class clazz = Class.forName("org.apache.tomcat.jni.Library");
                Method method = clazz.getMethod(methodName, paramTypes);
                method.invoke(null, paramValues);

                major = clazz.getField("TCN_MAJOR_VERSION").getInt(null);
                minor = clazz.getField("TCN_MINOR_VERSION").getInt(null);
                patch = clazz.getField("TCN_PATCH_VERSION").getInt(null);
                
                methodName = "randSet";
                paramValues[0] = SSLRandomSeed;
                clazz = Class.forName("org.apache.tomcat.jni.SSL");
                method = clazz.getMethod(methodName, paramTypes);
                method.invoke(null, paramValues);
            } catch (Throwable t) {
                if (!log.isDebugEnabled()) {
                    log.info(sm.getString("aprListener.aprInit", 
                            System.getProperty("java.library.path")));
                } else {
                    log.debug(sm.getString("aprListener.aprInit", 
                            System.getProperty("java.library.path")), t);
                }
                return;
            }
            if ((major != REQUIRED_MAJOR) || (minor != REQUIRED_MINOR)
                    || (patch < REQUIRED_PATCH)) {
                log.error(sm.getString("aprListener.tcnInvalid", major + "." 
                        + minor + "." + patch, REQUIRED_MAJOR + "." 
                        + REQUIRED_MINOR + "." + REQUIRED_PATCH));
            }
            if (patch < RECOMMENDED_PV) {
                /* The version is lower then recommended.
                 * Instruct the user to consider upgrading the native
                 * to the current stable version.
                 */
                if (!log.isDebugEnabled()) {
                    log.info(sm.getString("aprListener.tcnVersion", major + "." 
                            + minor + "." + patch, REQUIRED_MAJOR + "." 
                            + REQUIRED_MINOR + "." + RECOMMENDED_PV));
                } else {
                    log.debug(sm.getString("aprListener.tcnVersion", major + "." 
                            + minor + "." + patch, REQUIRED_MAJOR + "." 
                            + REQUIRED_MINOR + "." + RECOMMENDED_PV));
                }                
            }
        } else if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
            try {
                String methodName = "terminate";
                Method method = Class.forName("org.apache.tomcat.jni.Library")
                    .getMethod(methodName, (Class [])null);
                method.invoke(null, (Object []) null);
            } catch (Throwable t) {
                if (!log.isDebugEnabled()) {
                    log.info(sm.getString("aprListener.aprDestroy"));
                } else {
                    log.debug(sm.getString("aprListener.aprDestroy"), t);
                }
            }
        }

    }

    public String getSSLRandomSeed() {
        return SSLRandomSeed;
    }

    public void setSSLRandomSeed(String SSLRandomSeed) {
        AprLifecycleListener.SSLRandomSeed = SSLRandomSeed;
    }
}
