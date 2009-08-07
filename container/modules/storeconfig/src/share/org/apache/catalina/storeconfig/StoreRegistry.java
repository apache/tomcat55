/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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

package org.apache.catalina.storeconfig;

import java.util.HashMap;
import java.util.Map;

import javax.naming.directory.DirContext;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.ClusterDeployer;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.MembershipService;
import org.apache.catalina.cluster.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Central StoreRegistry for all server.xml elements
 * 
 * @author Peter Rossbach
 *  
 */
public class StoreRegistry {
    private static Log log = LogFactory.getLog(StoreRegistry.class);

    private Map descriptors = new HashMap();

    private String encoding = "UTF-8";

    private String name;

    private String version;

    // Access Information
    private static Class interfaces[] = { CatalinaCluster.class,
            ClusterSender.class, ClusterReceiver.class,
            MembershipService.class, ClusterDeployer.class, Realm.class,
            Manager.class, DirContext.class, LifecycleListener.class,
            Valve.class, MessageListener.class };

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Find a description for id. Handle interface search when no direct match
     * found.
     * 
     * @param id
     * @return
     */
    public StoreDescription findDescription(String id) {
        if (log.isDebugEnabled())
            log.debug("search descriptor " + id);
        StoreDescription desc = (StoreDescription) descriptors.get(id);
        if (desc == null) {
            Class aClass = null;
            try {
                aClass = Class.forName(id, true, this.getClass()
                        .getClassLoader());
            } catch (ClassNotFoundException e) {
                log.error("ClassName:" + id, e);
            }
            if (aClass != null) {
                desc = (StoreDescription) descriptors.get(aClass.getName());
                for (int i = 0; desc == null && i < interfaces.length; i++) {
                    if (interfaces[i].isAssignableFrom(aClass)) {
                        desc = (StoreDescription) descriptors.get(interfaces[i]
                                .getName());
                    }
                }
            }
        }
        if (log.isDebugEnabled())
            if (desc != null)
                log.debug("find descriptor " + id + "#" + desc.getTag() + "#"
                        + desc.getStoreFactoryClass());
            else
                log.debug(("Can't find descriptor for key " + id));
        return desc;
    }

    /**
     * Find Description by class
     * 
     * @param aClass
     * @return
     */
    public StoreDescription findDescription(Class aClass) {
        return findDescription(aClass.getName());
    }

    /**
     * Find factory from classname
     * 
     * @param aClassName
     * @return
     */
    public IStoreFactory findStoreFactory(String aClassName) {
        StoreDescription desc = findDescription(aClassName);
        if (desc != null)
            return desc.getStoreFactory();
        else
            return null;

    }

    /**
     * find factory from class
     * 
     * @param aClass
     * @return
     */
    public IStoreFactory findStoreFactory(Class aClass) {
        return findStoreFactory(aClass.getName());
    }

    /**
     * Register a new description
     * 
     * @param desc
     */
    public void registerDescription(StoreDescription desc) {
        String key = desc.getId();
        if (key == null || "".equals(key))
            key = desc.getTagClass();
        descriptors.put(key, desc);
        if (log.isDebugEnabled())
            log.debug("register store descriptor " + key + "#" + desc.getTag()
                    + "#" + desc.getTagClass());
    }

    public StoreDescription unregisterDescription(StoreDescription desc) {
        String key = desc.getId();
        if (key == null || "".equals(key))
            key = desc.getTagClass();
        return (StoreDescription) descriptors.remove(key);
    }

    // Attributes

    /**
     * @return
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param string
     */
    public void setEncoding(String string) {
        encoding = string;
    }

}