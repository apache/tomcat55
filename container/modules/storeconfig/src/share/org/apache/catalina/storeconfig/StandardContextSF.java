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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.NamingResources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.resources.ProxyDirContext;

/**
 * Store server.xml Context element with all childs
 * <ul>
 * <li>Store all context at server.xml</li>
 * <li>Store existing app.xml context a conf/enginename/hostname/app.xml</li>
 * <li>Store with backup</li>
 * </ul>
 * 
 * @author Peter Rossbach
 */
public class StandardContextSF extends StoreFactoryBase {

    private static Log log = LogFactory.getLog(StandardContextSF.class);

    /*
     * Store a Context as Separate file as configFile value from context exists.
     * filename can be relative to catalina.base.
     * 
     * @see org.apache.catalina.config.IStoreFactory#store(java.io.PrintWriter,
     *      int, java.lang.Object)
     */
    public void store(PrintWriter aWriter, int indent, Object aContext)
            throws Exception {

        if (aContext instanceof StandardContext) {
            StoreDescription desc = getRegistry().findDescription(
                    aContext.getClass());
            if (desc.isStoreSeparate()) {
                String configFile = ((StandardContext) aContext)
                        .getConfigFile();
                if (configFile != null) {
                    if (desc.isExternalAllowed()) {
                        if (desc.isBackup())
                            storeWithBackup((StandardContext) aContext);
                        else
                            storeContextSeparate(aWriter, indent,
                                    (StandardContext) aContext);
                        return;
                    }
                }
            }
        }
        super.store(aWriter, indent, aContext);

    }

    /**
     * Store a Context without backup add separate file or when configFile =
     * null a aWriter.
     * 
     * @param aWriter
     * @param indent
     * @param aContext
     * @throws Exception
     */
    protected void storeContextSeparate(PrintWriter aWriter, int indent,
            StandardContext aContext) throws Exception {
        String configFile = aContext.getConfigFile();
        PrintWriter writer = null;
        if (configFile != null) {
            File config = new File(configFile);
            if (!config.isAbsolute()) {
                config = new File(System.getProperty("catalina.base"),
                        configFile);
            }
            if (log.isInfoEnabled())
                log.info("Store Context " + aContext.getPath()
                        + " separate at file " + config);
            try {
                writer = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(config), getRegistry()
                                .getEncoding()));
                storeXMLHead(writer);
                super.store(writer, -2, aContext);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                    } catch (Exception e) {
                        ;
                    }
                    try {
                        writer.close();
                    } catch (Throwable t) {
                        ;
                    }
                }
            }
        } else {
            super.store(aWriter, indent, aContext);
        }
    }

    /**
     * Store the Context with a Backup
     * 
     * @param aContext
     * @throws Exception
     */
    protected void storeWithBackup(StandardContext aContext) throws Exception {
        StoreFileMover mover = getConfigFileWriter((Context) aContext);
        if (mover != null) {
            if (log.isInfoEnabled())
                log.info("Store Context " + aContext.getPath()
                        + " separate with backup (at file "
                        + mover.getConfigSave() + " )");
            PrintWriter writer = mover.getWriter();
            try {
                storeXMLHead(writer);
                super.store(writer, -2, aContext);
            } finally {
                // Flush and close the output file
                try {
                    writer.flush();
                } catch (Exception e) {
                    log.error(e);
                }
                try {
                    writer.close();
                } catch (Exception e) {
                    throw (e);
                }
            }
            mover.move();
        }
    }

    /**
     * Get explizit writer for context (context.getConfigFile())
     * 
     * @param context
     * @return
     * @throws IOException
     */
    protected StoreFileMover getConfigFileWriter(Context context)
            throws IOException {
        String configFile = context.getConfigFile();
        PrintWriter writer = null;
        StoreFileMover mover = null;
        if (configFile != null) {
            File config = new File(configFile);
            if (!config.isAbsolute()) {
                config = new File(System.getProperty("catalina.base"),
                        configFile);
            }
            // Open an output writer for the new configuration file
            mover = new StoreFileMover("", config.getCanonicalPath(),
                    getRegistry().getEncoding());
        }
        return mover;
    }

    /**
     * Store the specified Host properties.
     * 
     * @param aWriter
     *            PrintWriter to which we are storing
     * @param indent
     *            Number of spaces to indent this element
     * @param aContext
     *            Context whose properties are being stored
     * 
     * @exception Exception
     *                if an exception occurs while storing
     */
    public void storeChilds(PrintWriter aWriter, int indent, Object aContext,
            StoreDescription parentDesc) throws Exception {
        if (aContext instanceof StandardContext) {
            StandardContext context = (StandardContext) aContext;
            // Store nested <Listener> elements
            if (context instanceof Lifecycle) {
                LifecycleListener listeners[] = context
                        .findLifecycleListeners();
                storeElementArray(aWriter, indent, listeners);
            }
            // Store nested <Valve> elements
            if (context instanceof Pipeline) {
                Valve valves[] = ((Pipeline) context).getValves();
                storeElementArray(aWriter, indent, valves);
            }

            // Store nested <Loader> elements
            Loader loader = context.getLoader();
            storeElement(aWriter, indent, loader);

            // Store nested <Manager> elements
            Manager manager = context.getManager();
            storeElement(aWriter, indent, manager);

            // Store nested <Realm> element
            Realm realm = context.getRealm();
            if (realm != null) {
                Realm parentRealm = null;
                // @TODO is this case possible?
                if (context.getParent() != null) {
                    parentRealm = context.getParent().getRealm();
                }
                if (realm != parentRealm) {
                    storeElement(aWriter, indent, realm);

                }
            }
            // Store nested resources
            DirContext resources = context.getResources();
            if (resources instanceof ProxyDirContext)
                resources = ((ProxyDirContext) resources).getDirContext();
            storeElement(aWriter, indent, resources);

            // Store nested <InstanceListener> elements
            String iListeners[] = context.findInstanceListeners();
            getStoreAppender().printTagArray(aWriter, "InstanceListener",
                    indent + 2, iListeners);

            // Store nested <WrapperListener> elements
            String wLifecycles[] = context.findWrapperLifecycles();
            getStoreAppender().printTagArray(aWriter, "WrapperListener",
                    indent + 2, wLifecycles);
            // Store nested <WrapperLifecycle> elements
            String wListeners[] = context.findWrapperListeners();
            getStoreAppender().printTagArray(aWriter, "WrapperLifecycle",
                    indent + 2, wListeners);

            // Store nested <Parameter> elements
            ApplicationParameter[] appParams = context
                    .findApplicationParameters();
            storeElementArray(aWriter, indent, appParams);

            // Store nested naming resources elements (EJB,Resource,...)
            NamingResources nresources = context.getNamingResources();
            storeElement(aWriter, indent, nresources);

            // Store nested watched resources <WatchedResource>
            String[] wresources = context.findWatchedResources();
            wresources = filterWatchedResources(context, wresources);
            getStoreAppender().printTagArray(aWriter, "WatchedResource",
                    indent + 2, wresources);
        }
    }

    /**
     * Return a File object representing the "configuration root" directory for
     * our associated Host.
     */
    protected File configBase(Context context) {

        File file = new File(System.getProperty("catalina.base"), "conf");
        Container host = (Host) context.getParent();

        if ((host != null) && (host instanceof Host)) {
            Container engine = host.getParent();
            if ((engine != null) && (engine instanceof Engine)) {
                file = new File(file, engine.getName());
            }
            file = new File(file, host.getName());
            try {
                file = file.getCanonicalFile();
            } catch (IOException e) {
                log.error(e);
            }
        }
        return (file);

    }

    /**
     * filter out the default watched resources
     * 
     * @param context
     * @param wresources
     * @return
     * @throws IOException
     *             TODO relative watchedresource TODO absolute handling
     *             configFile TODO Filename case handling for Windows? TODO
     *             digester variable subsitution $catalina.base, $catalina.home
     */
    protected String[] filterWatchedResources(StandardContext context,
            String[] wresources) throws IOException {
        File configBase = configBase(context);
        String confContext = new File(System.getProperty("catalina.base"),
                "conf/context.xml").getCanonicalPath();
        String confHostDefault = new File(configBase, "context.xml.default")
                .getCanonicalPath();
        String configFile = context.getConfigFile();

        List resource = new ArrayList();
        for (int i = 0; i < wresources.length; i++) {

            if (wresources[i].equals(confContext))
                continue;
            if (wresources[i].equals(confHostDefault))
                continue;
            if (wresources[i].equals(configFile))
                continue;
            resource.add(wresources[i]);
        }
        return (String[]) resource.toArray(new String[resource.size()]);
    }

}