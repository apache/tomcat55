/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.catalina.cluster.tcp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.io.XByteBuffer;
import org.apache.catalina.util.StringManager;
import org.apache.tomcat.util.IntrospectionUtils;

/**
 * Transmit message to ohter cluster members create sender from replicationMode
 * type 
 * FIXME i18n log messages
 * FIXME compress data depends on message type and size 
 * FIXME send very big messages at some block see FarmWarDeployer!
 * TODO pause and resume senders
 * 
 * @author Peter Rossbach
 * @author Filip Hanik
 * @version $Revision$ $Date$
 */
public class ReplicationTransmitter implements ClusterSender {
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(ReplicationTransmitter.class);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "ReplicationTransmitter/1.3";

    /**
     * The string manager for this package.
     */
    protected StringManager sm = StringManager.getManager(Constants.Package);

    private Map map = new HashMap();

    public ReplicationTransmitter() {
    }

    /**
     * number of transmitted messages>
     */
    private long nrOfRequests = 0;

    /**
     * number of transmitted bytes
     */
    private long totalBytes = 0;

    private long failureCounter = 0;

    /**
     * current sender replication mode
     */
    private String replicationMode;

    /**
     * sender default ackTimeout
     */
    private long ackTimeout = 15000; //15 seconds by default

    /**
     * enabled wait for ack
     */
    private boolean waitForAck = true;

    /**
     * autoConnect sender when next message send
     */
    private boolean autoConnect = true;

    /**
     * Compress message data bytes
     */
    private boolean compress = true;

    /**
     * dynamic sender <code>properties</code>
     */
    private Map properties = new HashMap();

    /**
     * my cluster
     */
    private SimpleTcpCluster cluster;

    /**
     * Transmitter Mbean name
     */
    private ObjectName objectName;

    // ------------------------------------------------------------- Properties

    /**
     * Return descriptive information about this implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }

    /**
     * @return Returns the nrOfRequests.
     */
    public long getNrOfRequests() {
        return nrOfRequests;
    }

    /**
     * @return Returns the totalBytes.
     */
    public long getTotalBytes() {
        return totalBytes;
    }

    /**
     * @return Returns the failureCounter.
     */
    public long getFailureCounter() {
        return failureCounter;
    }

    /**
     * current replication mode
     * 
     * @return
     */
    public String getReplicationMode() {
        return replicationMode;
    }

    /**
     * set replication Mode (pooled, synchonous, asynchonous, fastasyncqueue)
     * 
     * @see IDataSenderFactory#validateMode(String)
     * @param mode
     */
    public void setReplicationMode(String mode) {
        String msg = IDataSenderFactory.validateMode(mode);
        if (msg == null) {
            if (log.isDebugEnabled())
                log.debug("Setting replcation mode to " + mode);
            this.replicationMode = mode;
        } else
            throw new IllegalArgumentException(msg);

    }

    /**
     * Transmitter ObjectName
     * 
     * @param name
     */
    public void setObjectName(ObjectName name) {
        objectName = name;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    /**
     * @return Returns the compress.
     */
    public boolean isCompress() {
        return compress;
    }

    /**
     * @param compress
     *            The compress to set.
     */
    public void setCompress(boolean compressMessageData) {
        this.compress = compressMessageData;
    }

    /**
     * @return Returns the autoConnect.
     */
    public boolean isAutoConnect() {
        return autoConnect;
    }

    /**
     * @param autoConnect
     *            The autoConnect to set.
     */
    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
        setProperty("autoConnect", String.valueOf(autoConnect));

    }

    /**
     * @return
     */
    public long getAckTimeout() {
        return ackTimeout;
    }

    /**
     * @param ackTimeout
     */
    public void setAckTimeout(long ackTimeout) {
        this.ackTimeout = ackTimeout;
        setProperty("ackTimeout", String.valueOf(ackTimeout));
    }

    /**
     * @return Returns the waitForAck.
     */
    public boolean isWaitForAck() {
        return waitForAck;
    }

    /**
     * @param waitForAck
     *            The waitForAck to set.
     */
    public void setWaitForAck(boolean waitForAck) {
        this.waitForAck = waitForAck;
        setProperty("waitForAck", String.valueOf(waitForAck));
    }

    /*
     * configured in cluster
     * 
     * @see org.apache.catalina.cluster.ClusterSender#setCatalinaCluster(org.apache.catalina.cluster.tcp.SimpleTcpCluster)
     */
    public void setCatalinaCluster(SimpleTcpCluster cluster) {
        this.cluster = cluster;

    }

    /**
     * @return
     * @deprecated since version 5.5.7
     */
    public boolean getIsSenderSynchronized() {
        return IDataSenderFactory.SYNC_MODE.equals(replicationMode)
                || IDataSenderFactory.POOLED_SYNC_MODE.equals(replicationMode);
    }

    // ------------------------------------------------------------- dynamic
    // sender property handling

    /**
     * set config attributes with reflect
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, Object value) {
        if (log.isTraceEnabled())
            log.trace(sm.getString("ReplicationTransmitter.setProperty", name,
                    value));

        properties.put(name, value);
    }

    /**
     * get current config
     * 
     * @param key
     * @return
     */
    public Object getProperty(String key) {
        if (log.isTraceEnabled())
            log.trace(sm.getString("ReplicationTransmitter.getProperty", key));
        return properties.get(key);
    }

    /**
     * Get all properties keys
     * 
     * @return
     */
    public Iterator getPropertyNames() {
        return properties.keySet().iterator();
    }

    /**
     * remove a configured property.
     * 
     * @param key
     */
    public void removeProperty(String key) {
        properties.remove(key);
    }

    // ------------------------------------------------------------- public

    /**
     * Send data to one member
     * 
     * @see org.apache.catalina.cluster.ClusterSender#sendMessage(java.lang.String,
     *      byte[], org.apache.catalina.cluster.Member)
     */
    public void sendMessage(String sessionId, byte[] indata, Member member)
            throws java.io.IOException {
        byte[] data = convertSenderData(indata);
        String key = getKey(member);
        IDataSender sender = (IDataSender) map.get(key);
        sendMessageData(sessionId, data, sender);
    }

    /**
     * send message to all senders (broadcast)
     * 
     * @see org.apache.catalina.cluster.ClusterSender#sendMessage(java.lang.String,
     *      byte[])
     */
    public void sendMessage(String sessionId, byte[] indata)
            throws java.io.IOException {
        IDataSender[] senders = getSenders();
        byte[] data = convertSenderData(indata);
        for (int i = 0; i < senders.length; i++) {

            IDataSender sender = senders[i];
            try {
                sendMessageData(sessionId, data, sender);
            } catch (Exception x) {

                if (!sender.getSuspect())
                    log.warn("Unable to send replicated message to " + sender
                            + ", is server down?", x);
                sender.setSuspect(true);
            }
        }
    }

    /**
     * start the sender and register transmitter mbean
     * 
     * @see org.apache.catalina.cluster.ClusterSender#start()
     */
    public void start() throws java.io.IOException {
        if (cluster != null) {
            ObjectName clusterName = cluster.getObjectName();
            try {
                MBeanServer mserver = cluster.getMBeanServer();
                ObjectName transmitterName = new ObjectName(clusterName
                        .getDomain()
                        + ":type=ClusterSender,host="
                        + clusterName.getKeyProperty("host"));
                if (mserver.isRegistered(transmitterName)) {
                    if (log.isWarnEnabled())
                        log.warn(sm.getString(
                                "cluster.mbean.register.allready",
                                transmitterName));
                    return;
                }
                setObjectName(transmitterName);
                mserver.registerMBean(cluster.getManagedBean(this),
                        getObjectName());
            } catch (Exception e) {
                log.warn(e);
            }
        }

    }

    /*
     * stop the sender and deregister mbeans (transmitter, senders)
     * 
     * @see org.apache.catalina.cluster.ClusterSender#stop()
     */
    public synchronized void stop() {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            IDataSender sender = (IDataSender) ((java.util.Map.Entry) i.next())
                    .getValue();
            try {
                unregisterSenderMBean(sender);
                sender.disconnect();
            } catch (Exception x) {
            }
            i.remove();
        }
        if (cluster != null && getObjectName() != null) {
            try {
                MBeanServer mserver = cluster.getMBeanServer();
                mserver.unregisterMBean(getObjectName());
            } catch (Exception e) {
                log.error(e);
            }
        }

    }

    /**
     * get all current senders
     * 
     * @return
     */
    public IDataSender[] getSenders() {
        java.util.Iterator iter = map.entrySet().iterator();
        IDataSender[] array = new IDataSender[map.size()];
        int i = 0;
        while (iter.hasNext()) {
            IDataSender sender = (IDataSender) ((java.util.Map.Entry) iter
                    .next()).getValue();
            if (sender != null)
                array[i] = sender;
            i++;
        }
        return array;
    }

    /**
     * get all current senders
     * 
     * @return
     */
    public ObjectName[] getSenderObjectNames() {
        java.util.Iterator iter = map.entrySet().iterator();
        ObjectName array[] = new ObjectName[map.size()];
        int i = 0;
        while (iter.hasNext()) {
            IDataSender sender = (IDataSender) ((java.util.Map.Entry) iter
                    .next()).getValue();
            if (sender != null)
                array[i] = getSenderObjectName(sender);
            i++;
        }
        return array;
    }

    /*
     * Reset sender statistics
     */
    public synchronized void resetStatistics() {
        nrOfRequests = 0;
        totalBytes = 0;
        failureCounter = 0;
    }

    /*
     * add new cluster member and create sender ( s. replicationMode) transfer
     * current properties to sender
     * 
     * @see org.apache.catalina.cluster.ClusterSender#add(org.apache.catalina.cluster.Member)
     */
    public synchronized void add(Member member) {
        try {
            String key = getKey(member);
            if (!map.containsKey(key)) {
                IDataSender sender = IDataSenderFactory.getIDataSender(
                        replicationMode, member);
                transferSenderProperty(sender);
                map.put(key, sender);
                registerSenderMBean(member, sender);
            }
        } catch (java.io.IOException x) {
            log.error("Unable to create and add a IDataSender object.", x);
        }
    }

    /**
     * remove sender from transmitter. ( deregister mbean and disconnect sender )
     * 
     * @see org.apache.catalina.cluster.ClusterSender#remove(org.apache.catalina.cluster.Member)
     */
    public synchronized void remove(Member member) {
        String key = getKey(member);
        IDataSender toberemoved = (IDataSender) map.get(key);
        if (toberemoved == null)
            return;
        unregisterSenderMBean(toberemoved);
        toberemoved.disconnect();
        map.remove(key);

    }

    // ------------------------------------------------------------- protected

    /**
     * calc number of requests and transfered bytes. Log stats all 100 requets
     * 
     * @param length
     */
    protected synchronized void addStats(int length) {
        nrOfRequests++;
        totalBytes += length;
        if (log.isDebugEnabled() && (nrOfRequests % 100) == 0) {
            log.debug("Nr of bytes sent=" + totalBytes + " over "
                    + nrOfRequests + "; avg=" + (totalBytes / nrOfRequests)
                    + " bytes/request; failures=" + failureCounter);
        }

    }

    /**
     * Transfer all properties from transmitter to concrete sender
     * 
     * @param sender
     */
    protected void transferSenderProperty(IDataSender sender) {
        for (Iterator iter = getPropertyNames(); iter.hasNext();) {
            String pkey = (String) iter.next();
            Object value = getProperty(pkey);
            IntrospectionUtils.setProperty(sender, pkey, value.toString());
        }
    }

    /**
     * set unique key to find sender
     * 
     * @param member
     * @return concat member.host:member.port
     */
    protected String getKey(Member member) {
        return member.getHost() + ":" + member.getPort();
    }

    /**
     * unregsister sendern Mbean
     * 
     * @see #getSenderObjectName(IDataSender)
     * @param sender
     */
    protected void unregisterSenderMBean(IDataSender sender) {
        try {
            MBeanServer mserver = cluster.getMBeanServer();
            if (mserver != null) {
                mserver.unregisterMBean(getSenderObjectName(sender));
            }
        } catch (Exception e) {
            log.warn(e);
        }
    }

    /**
     * register MBean and check it exist (big problem!)
     * 
     * @param member
     * @param sender
     */
    protected void registerSenderMBean(Member member, IDataSender sender) {
        if (member != null && cluster != null) {
            try {
                MBeanServer mserver = cluster.getMBeanServer();
                ObjectName senderName = getSenderObjectName(sender);
                if (mserver.isRegistered(senderName)) {
                    if (log.isWarnEnabled())
                        log.warn(sm.getString(
                                "cluster.mbean.register.allready", senderName));
                    return;
                }
                mserver.registerMBean(cluster.getManagedBean(sender),
                        senderName);
            } catch (Exception e) {
                log.warn(e);
            }
        }
    }

    /**
     * build sender ObjectName (
     * engine.domain:type=IDataSender,host="host",senderAddress="receiver.address",senderPort="port" )
     * 
     * @param sender
     * @return
     */
    protected ObjectName getSenderObjectName(IDataSender sender) {
        ObjectName senderName = null;
        try {
            ObjectName clusterName = cluster.getObjectName();
            MBeanServer mserver = cluster.getMBeanServer();
            senderName = new ObjectName(clusterName.getDomain()
                    + ":type=IDataSender,host="
                    + clusterName.getKeyProperty("host") + ",senderAddress="
                    + sender.getAddress().getHostAddress() + ",senderPort="
                    + sender.getPort());
        } catch (Exception e) {
            log.warn(e);
        }
        return senderName;
    }

    /**
     * compress data
     * 
     * @see XByteBuffer#createDataPackage(byte[])
     * @param indata
     * @return
     * @throws IOException
     *             FIXME get CompressMessageDate from cluster instanz
     */
    protected byte[] convertSenderData(byte[] data) throws IOException {
        return XByteBuffer.createDataPackage(data, isCompress());
    }

    /**
     * Send message to concrete sender. If autoConnect is true, check is
     * connection broken and the reconnect the complete sender.
     * <ul>
     * <li>failure the suspect flag is set true. After successfully sending the
     * suspect flag is set to false.</li>
     * <li>Stats is only update after sussesfull sending</li>
     * </ul>
     * 
     * @param sessionId
     *            Unique Message Id
     * @param data
     *            message Data
     * @param sender
     *            concrete message sender
     * @throws java.io.IOException
     */
    protected void sendMessageData(String sessionId, byte[] data,
            IDataSender sender) throws java.io.IOException {
        if (sender == null)
            throw new java.io.IOException(
                    "Sender not available. Make sure sender information is available to the ReplicationTransmitter.");
        try {
            if (autoConnect && !sender.isConnected())
                sender.connect();
            sender.sendMessage(sessionId, data);
            sender.setSuspect(false);
            addStats(data.length);
        } catch (Exception x) {
            if (log.isWarnEnabled()) {
                if (!sender.getSuspect()) {
                    log
                            .warn(
                                    "Unable to send replicated message, is server down?",
                                    x);
                }
            }
            sender.setSuspect(true);
            failureCounter++;
        }

    }

}