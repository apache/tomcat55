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

import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.ClusterDeployer;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.MembershipService;
import org.apache.catalina.deploy.ContextEjb;
import org.apache.catalina.deploy.ContextEnvironment;
import org.apache.catalina.deploy.ContextLocalEjb;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.deploy.ContextResourceEnvRef;
import org.apache.catalina.deploy.ContextResourceLink;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.storeconfig.IStoreFactory;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Peter Rossbach
 *  
 */
public class DescriptorHelper {
    private static Log log = LogFactory.getLog(DescriptorHelper.class);

    public static StoreDescription registerDescriptor(
            StoreDescription parentdesc, StoreRegistry registry, String tag,
            Class aClass) {
        return registerDescriptor(parentdesc, registry, aClass.getName(), tag,
                aClass.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
    }

    public static StoreDescription registerDescriptor(
            StoreDescription parentdesc, StoreRegistry registry, String tag,
            String aClassToken, String factoryClass, boolean fstandard,
            boolean fdefault) {
        return registerDescriptor(parentdesc, registry, aClassToken, tag,
                aClassToken,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
    }

    /**
     * Generate a descriptor and register this to another parent descriptor.
     * 
     * @param parentdesc
     * @param registry
     * @param id
     * @param tag
     * @param aClassToken
     * @param factoryClass
     * @param fstandard
     * @param fdefault
     * @return
     */
    public static StoreDescription registerDescriptor(
            StoreDescription parentdesc, StoreRegistry registry, String id,
            String tag, String aClassToken, String factoryClass,
            boolean fstandard, boolean fdefault) {
        // add Listener
        StoreDescription descChild = new StoreDescription();
        descChild.setId(id);
        descChild.setTag(tag);
        descChild.setTagClass(aClassToken);
        descChild.setStandard(fstandard);
        descChild.setDefault(fdefault);
        descChild.setStoreFactoryClass(factoryClass);
        Object factory = null;
        try {
            Class aFactoryClass = Class.forName(factoryClass);
            factory = aFactoryClass.newInstance();
        } catch (Exception e) {
            log.error(e);
        }
        if (factory != null) {
            ((IStoreFactory) factory).setRegistry(registry);
            descChild.setStoreFactory((IStoreFactory) factory);
        }
        if (parentdesc != null)
            parentdesc.setChilds(true);
        registry.registerDescription(descChild);
        return descChild;
    }

    /**
     * register all Registery descriptors on naming support to context!
     * 
     * @param parent
     * @param registry
     * @return
     * @throws Exception
     */
    public static StoreDescription registerNamingDescriptor(
            StoreDescription parent, StoreRegistry registry) throws Exception {

        StoreDescription nameingDesc = DescriptorHelper.registerDescriptor(
                parent, registry, NamingResources.class.getName(),
                "NamingResources", NamingResources.class.getName(),
                "org.apache.catalina.storeconfig.NamingResourcesSF", true,
                false);
        registerDescriptor(nameingDesc, registry, ContextEjb.class.getName(),
                "EJB", ContextEjb.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        registerDescriptor(nameingDesc, registry, ContextEnvironment.class
                .getName(), "Environment", ContextEnvironment.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        registerDescriptor(nameingDesc, registry, ContextLocalEjb.class
                .getName(), "LocalEjb", ContextLocalEjb.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        registerDescriptor(nameingDesc, registry, ContextResource.class
                .getName(), "Resource", ContextResource.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        registerDescriptor(nameingDesc, registry, ContextResourceLink.class
                .getName(), "ResourceLink",
                ContextResourceLink.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        registerDescriptor(nameingDesc, registry, ContextResourceEnvRef.class
                .getName(), "ResourceEnvRef", ContextResourceEnvRef.class
                .getName(), "org.apache.catalina.storeconfig.StoreFactoryBase",
                true, false);
        return nameingDesc;
    }

    /**
     * register all cluster and subelement descriptors to registery
     * 
     * @param parent
     * @param registry
     * @return
     * @throws Exception
     */
    public static StoreDescription registerClusterDescriptor(
            StoreDescription parent, StoreRegistry registry) throws Exception {

        StoreDescription clusterDesc = DescriptorHelper.registerDescriptor(
                parent, registry, CatalinaCluster.class.getName(), "Cluster",
                CatalinaCluster.class.getName(),
                "org.apache.catalina.storeconfig.CatalinaClusterSF", false,
                false);
        registerDescriptor(clusterDesc, registry,
                ClusterSender.class.getName(), "Sender", ClusterSender.class
                        .getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        registerDescriptor(clusterDesc, registry, ClusterReceiver.class
                .getName(), "Receiver", ClusterReceiver.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        registerDescriptor(clusterDesc, registry, MembershipService.class
                .getName(), "Membership", MembershipService.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        registerDescriptor(clusterDesc, registry, ClusterDeployer.class
                .getName(), "Deployer", ClusterDeployer.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        return clusterDesc;
    }

}