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

package org.apache.catalina.cluster.deploy;

import org.apache.catalina.cluster.ClusterDeployer;
import org.apache.catalina.cluster.ClusterMessage;
import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.LifecycleException;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import org.apache.catalina.cluster.Member;
import java.util.HashMap;

/**
 * <p>
 * A farm war deployer is a class that is able to
 * deploy/undeploy web applications in WAR form
 * within the cluster.</p>
 * Any host can act as the admin, and will have three directories
 * <ul>
 * <li> deployDir - the directory where we watch for changes</li>
 * <li> applicationDir - the directory where we install applications</li>
 * <li> tempDir - a temporaryDirectory to store binary data when downloading a war
 *      from the cluster </li>
 * </ul>
 * Currently we only support deployment of WAR files since they are easier to send
 * across the wire.
 *
 * @author Filip Hanik
 * @version 1.0
 */
public class FarmWarDeployer implements ClusterDeployer, FileChangeListener {
    /*--Static Variables----------------------------------------*/
    public static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog(FarmWarDeployer.class);
    /*--Instance Variables--------------------------------------*/
    protected CatalinaCluster cluster = null;
    protected boolean started = false; //default 5 seconds
    protected HashMap fileFactories = new HashMap();
    protected String deployDir;
    protected String tempDir;
    protected String watchDir;
    protected boolean watchEnabled = false;
    protected WarWatcher watcher = null;
    /*--Constructor---------------------------------------------*/
    public FarmWarDeployer() {
    }

    /*--Logic---------------------------------------------------*/
    public void start() throws Exception {
        if (started)return;
        getCluster().addClusterListener(this);
        if (watchEnabled) {
            watcher = new WarWatcher(this, new File(getWatchDir()), (long) 5000);
            Thread t = new Thread(watcher);
            t.start();
            log.info("Cluster deployment is watching " + getWatchDir() +
                     " for changes.");
        } //end if
        started = true;
        log.info("Cluster FarmWarDeployer started.");
    }

    public void stop() throws LifecycleException {
        started = false;
        getCluster().removeClusterListener(this);
        if (watcher != null) watcher.stop();
        log.info("Cluster FarmWarDeployer stopped.");
    }

    public void cleanDeployDir() {
        throw new java.lang.UnsupportedOperationException(
            "Method cleanDeployDir() not yet implemented.");
    }

    /**
     * Callback from the cluster, when a message is received,
     * The cluster will broadcast it invoking the messageReceived
     * on the receiver.
     * @param msg ClusterMessage - the message received from the cluster
     */
    public void messageReceived(ClusterMessage msg) {
        try {
            if (msg instanceof FileMessage && msg != null) {
                FileMessage fmsg = (FileMessage) msg;
                FileMessageFactory factory = getFactory(fmsg);
                if (factory.writeMessage(fmsg)) {
                    //last message received
                    String name = factory.getFile().getName();
                    if (!name.endsWith(".war"))
                        name = name + ".war";
                    File deployable = new File(getDeployDir(), name);
                    factory.getFile().renameTo(deployable);
                    // FIXME
                    /*
                    try {
                        if (getDeployer().findDeployedApp(fmsg.getContextPath()) != null)
                            getDeployer().remove(fmsg.getContextPath(), true);
                    } catch (Exception x) {
                        log.info(
                            "Error removing existing context before installing a new one.",
                            x);
                    }
                    getDeployer().install(fmsg.getContextPath(),
                                          deployable.toURL());
                                          */
                    removeFactory(fmsg);
                } //end if
            } else if (msg instanceof UndeployMessage && msg != null) {
                UndeployMessage umsg = (UndeployMessage) msg;
                // FIXME
                /*
                if (getDeployer().findDeployedApp(umsg.getContextPath()) != null)
                    getDeployer().remove(umsg.getContextPath(),
                                         umsg.getUndeploy());
                                         */
            } //end if
        } catch (java.io.IOException x) {
            log.error("Unable to read farm deploy file message.", x);
        }
    }

    public synchronized FileMessageFactory getFactory(FileMessage msg) throws
        java.io.FileNotFoundException, java.io.IOException {
        File tmpFile = new File(msg.getFileName());
        File writeToFile = new File(getTempDir(), tmpFile.getName());
        FileMessageFactory factory = (FileMessageFactory) fileFactories.get(msg.
            getFileName());
        if (factory == null) {
            factory = FileMessageFactory.getInstance(writeToFile, true);
            fileFactories.put(msg.getFileName(), factory);
        }
        return factory;
    }

    public void removeFactory(FileMessage msg) {
        fileFactories.remove(msg.getFileName());
    }

    /**
     * Before the cluster invokes messageReceived the
     * cluster will ask the receiver to accept or decline the message,
     * In the future, when messages get big, the accept method will only take
     * a message header
     * @param msg ClusterMessage
     * @return boolean - returns true to indicate that messageReceived
     * should be invoked. If false is returned, the messageReceived method
     * will not be invoked.
     */
    public boolean accept(ClusterMessage msg) {
        return (msg instanceof FileMessage) ||
            (msg instanceof UndeployMessage);
    }

    /**
     * Install a new web application, whose web application archive is at the
     * specified URL, into this container and all the other
     * members of the cluster with the specified context path.
     * A context path of "" (the empty string) should be used for the root
     * application for this container.  Otherwise, the context path must
     * start with a slash.
     * <p>
     * If this application is successfully installed locally,
     * a ContainerEvent of type
     * <code>INSTALL_EVENT</code> will be sent to all registered listeners,
     * with the newly created <code>Context</code> as an argument.
     *
     * @param contextPath The context path to which this application should
     *  be installed (must be unique)
     * @param war A URL of type "jar:" that points to a WAR file, or type
     *  "file:" that points to an unpacked directory structure containing
     *  the web application to be installed
     *
     * @exception IllegalArgumentException if the specified context path
     *  is malformed (it must be "" or start with a slash)
     * @exception IllegalStateException if the specified context path
     *  is already attached to an existing web application
     * @exception IOException if an input/output error was encountered
     *  during installation
     */
    public void install(String contextPath, URL war) throws IOException {
    	// FIXME
    	/*
        if (getDeployer().findDeployedApp(contextPath) != null)
            getDeployer().remove(contextPath, true);
            //step 1. Install it locally
        getDeployer().install(contextPath, war);
        */
        //step 2. Send it to each member in the cluster
        Member[] members = getCluster().getMembers();
        Member localMember = getCluster().getLocalMember();
        FileMessageFactory factory = FileMessageFactory.getInstance(new File(
            war.getFile()), false);
        FileMessage msg = new FileMessage(localMember, war.getFile(),
                                          contextPath);
        msg = factory.readMessage(msg);
        while (msg != null) {
            for (int i = 0; i < members.length; i++) {
                getCluster().send(msg, members[i]);
            } //for
            msg = factory.readMessage(msg);
        } //while
    }

    /**
     * Remove an existing web application, attached to the specified context
     * path.  If this application is successfully removed, a
     * ContainerEvent of type <code>REMOVE_EVENT</code> will be sent to all
     * registered listeners, with the removed <code>Context</code> as
     * an argument. Deletes the web application war file and/or directory
     * if they exist in the Host's appBase.
     *
     * @param contextPath The context path of the application to be removed
     * @param undeploy boolean flag to remove web application from server
     *
     * @exception IllegalArgumentException if the specified context path
     *  is malformed (it must be "" or start with a slash)
     * @exception IllegalArgumentException if the specified context path does
     *  not identify a currently installed web application
     * @exception IOException if an input/output error occurs during
     *  removal
     */
    public void remove(String contextPath, boolean undeploy) throws IOException {
        log.info("Cluster wide remove of web app " + contextPath);
        //step 1. Remove it locally
        
        // FIXME
        /*
        if (getDeployer().findDeployedApp(contextPath) != null)
            getDeployer().remove(contextPath, undeploy);
            */
            //step 2. Send it to each member in the cluster
        Member[] members = getCluster().getMembers();
        Member localMember = getCluster().getLocalMember();
        UndeployMessage msg = new UndeployMessage(localMember,
                                                  System.currentTimeMillis(),
                                                  "Undeploy:" + contextPath +
                                                  ":" +
                                                  System.currentTimeMillis(),
                                                  contextPath,
                                                  undeploy);
        cluster.send(msg);
    }

    public void fileModified(File newWar) {
        try {
            File deployWar = new File(getDeployDir(),newWar.getName());
            copy(newWar,deployWar);
            String contextName = "/" + deployWar.getName().substring(0,
                deployWar.getName().lastIndexOf(".war"));
            log.info("Installing webapp[" + contextName + "] from " + deployWar.getAbsolutePath());
            try {
                remove(contextName, true);
            } catch (Exception x) {
                log.error("No removal", x);
            }
            install(contextName, deployWar.toURL());
        } catch (Exception x) {
            log.error("Unable to install WAR file", x);
        }
    }

    public void fileRemoved(File removeWar) {
        try {
            String contextName = "/" + removeWar.getName().substring(0,
                removeWar.getName().lastIndexOf(".war"));
            log.info("Removing webapp[" + contextName + "]");
            remove(contextName, true);
        } catch (Exception x) {
            log.error("Unable to remove WAR file", x);
        }
    }

    /*--Instance Getters/Setters--------------------------------*/
    public CatalinaCluster getCluster() {
        return cluster;
    }

    public void setCluster(CatalinaCluster cluster) {
        this.cluster = cluster;
    }

    public boolean equals(Object listener) {
        return super.equals(listener);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String getDeployDir() {
        return deployDir;
    }

    public void setDeployDir(String deployDir) {
        this.deployDir = deployDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getWatchDir() {
        return watchDir;
    }

    public void setWatchDir(String watchDir) {
        this.watchDir = watchDir;
    }

    public boolean isWatchEnabled() {
        return watchEnabled;
    }

    public boolean getWatchEnabled() {
        return watchEnabled;
    }

    public void setWatchEnabled(boolean watchEnabled) {
        this.watchEnabled = watchEnabled;
    }
    
    /**



    /**
     * Copy a file to the specified temp directory. This is required only
     * because Jasper depends on it.
     */
    private boolean copy(File from, File to) {
        try {
            if ( !to.exists() ) to.createNewFile();
            java.io.FileInputStream is = new java.io.FileInputStream(from);
            java.io.FileOutputStream os = new java.io.FileOutputStream(to,false);
            byte[] buf = new byte[4096];
            while (true) {
                int len = is.read(buf);
                if (len < 0)
                    break;
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            log.error("Unable to copy file from:"+from+" to:"+to,e);
            return false;
        }
        return true;
    }

}
