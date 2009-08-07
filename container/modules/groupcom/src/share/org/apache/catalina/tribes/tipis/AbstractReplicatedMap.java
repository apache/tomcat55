/*
 * Copyright 1999,2004-2006 The Apache Software Foundation.
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

package org.apache.catalina.tribes.tipis;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.catalina.tribes.group.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class AbstractReplicatedMap extends LinkedHashMap implements RpcCallback, ChannelListener, MembershipListener {
    protected static Log log = LogFactory.getLog(AbstractReplicatedMap.class);

    /**
     * The default initial capacity - MUST be a power of two.
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The load factor used when none specified in constructor.
     **/
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    /**
     * Used to identify the map
     */
    final String chset = "ISO-8859-1";

//------------------------------------------------------------------------------
//              INSTANCE VARIABLES
//------------------------------------------------------------------------------
    private transient long rpcTimeout = 5000;
    private transient Channel channel;
    private transient RpcChannel rpcChannel;
    private transient byte[] mapContextName;
    private transient boolean stateTransferred = false;
    private transient Object stateMutex = new Object();
    private transient ArrayList mapMembers = new ArrayList();
    private transient int channelSendOptions = Channel.SEND_OPTIONS_DEFAULT;
    private transient Object mapOwner;
    private transient ClassLoader[] externalLoaders;
    

//------------------------------------------------------------------------------
//              CONSTRUCTORS
//------------------------------------------------------------------------------

    /**
     * Creates a new map
     * @param channel The channel to use for communication
     * @param timeout long - timeout for RPC messags
     * @param mapContextName String - unique name for this map, to allow multiple maps per channel
     * @param initialCapacity int - the size of this map, see HashMap
     * @param loadFactor float - load factor, see HashMap
     * @param cls - a list of classloaders to be used for deserialization of objects.
     */
    public AbstractReplicatedMap(Object owner,
                                 Channel channel, 
                                 long timeout, 
                                 String mapContextName, 
                                 int initialCapacity,
                                 float loadFactor,
                                 int channelSendOptions,
                                 ClassLoader[] cls) {
        super(initialCapacity, loadFactor);
        init(owner, channel, mapContextName, timeout, channelSendOptions, cls);
        
    }

    protected Member[] wrap(Member m) {
        return new Member[] {m};
    }

    private void init(Object owner, Channel channel, String mapContextName, long timeout, int channelSendOptions,ClassLoader[] cls) {
        this.mapOwner = owner;
        this.externalLoaders = cls;
        this.channelSendOptions = channelSendOptions;
        this.channel = channel;
        this.rpcTimeout = timeout;

        try {
            //unique context is more efficient if it is stored as bytes
            this.mapContextName = mapContextName.getBytes(chset);
        } catch (UnsupportedEncodingException x) {
            log.warn("Unable to encode mapContextName[" + mapContextName + "] using getBytes(" + chset +") using default getBytes()", x);
            this.mapContextName = mapContextName.getBytes();
        }

        //create an rpc channel and add the map as a listener
        this.rpcChannel = new RpcChannel(this.mapContextName, channel, this);
        this.channel.addChannelListener(this);
        this.channel.addMembershipListener(this);
        
        
        try {
            broadcast(MapMessage.MSG_INIT, true);
            //transfer state from another map
            transferState();
            broadcast(MapMessage.MSG_START, true);
        } catch (ChannelException x) {
            log.warn("Unable to send map start message.");
            throw new RuntimeException("Unable to start replicated map.",x);
        }

    }
    
    

    private void broadcast(int msgtype, boolean rpc) throws ChannelException {
        //send out a map membership message, only wait for the first reply
        MapMessage msg = new MapMessage(this.mapContextName, msgtype,
                                        false, null, null, null, wrap(channel.getLocalMember(false)));
        if ( rpc) {
            Response[] resp = rpcChannel.send(channel.getMembers(), msg, rpcChannel.FIRST_REPLY, (channelSendOptions),rpcTimeout);
            for (int i = 0; i < resp.length; i++) {
                mapMemberAdded(resp[i].getSource());
                messageReceived(resp[i].getMessage(), resp[i].getSource());
            }
        } else {
            channel.send(channel.getMembers(),msg,channelSendOptions);
        }
    }

    public void breakdown() {
        finalize();
    }

    public void finalize() {
        try {broadcast(MapMessage.MSG_STOP,false); }catch ( Exception ignore){}
        //cleanup
        if (this.rpcChannel != null) {
            this.rpcChannel.breakdown();
        }
        if (this.channel != null) {
            this.channel.removeChannelListener(this);
            this.channel.removeMembershipListener(this);
        }
        this.rpcChannel = null;
        this.channel = null;
        this.mapMembers.clear();
        super.clear();
        this.stateTransferred = false;
        this.externalLoaders = null;
    }

//------------------------------------------------------------------------------
//              GROUP COM INTERFACES
//------------------------------------------------------------------------------
    public Member[] getMapMembers() {
        synchronized (mapMembers) {
            Member[] result = new Member[mapMembers.size()];
            mapMembers.toArray(result);
            return result;
        }
    }
    
    public Member[] getMapMembersExcl(Member[] exclude) {
        synchronized (mapMembers) {
            ArrayList list = (ArrayList)mapMembers.clone();
            for (int i=0; i<exclude.length;i++) list.remove(exclude[i]);
            Member[] result = new Member[list.size()];
            list.toArray(result);
            return result;
        }
    }


    /**
     * Replicates any changes to the object since the last time
     * The object has to be primary, ie, if the object is a proxy or a backup, it will not be replicated<br>
     * @param complete - if set to true, the object is replicated to its backup
     * if set to false, only objects that implement ReplicatedMapEntry and the isDirty() returns true will
     * be replicated
     */
    public void replicate(Object key, boolean complete) {
        MapEntry entry = (MapEntry)super.get(key);
        if (entry != null && entry.isPrimary()) {
            Object value = entry.getValue();
            //check to see if we need to replicate this object isDirty()||complete
            boolean repl = complete || ( (value instanceof ReplicatedMapEntry) && ( (ReplicatedMapEntry) value).isDirty());
            if (!repl)return;

            //check to see if the message is diffable
            boolean diff = ( (value instanceof ReplicatedMapEntry) && ( (ReplicatedMapEntry) value).isDiffable());
            MapMessage msg = null;
            if (diff) {
                ReplicatedMapEntry rentry = (ReplicatedMapEntry)entry.getValue();
                try {
                    rentry.lock();
                    //construct a diff message
                    msg = new MapMessage(mapContextName, MapMessage.MSG_BACKUP,
                                         true, (Serializable) entry.getKey(), null,
                                         rentry.getDiff(),
                                         entry.getBackupNodes());
                } catch (IOException x) {
                    log.error("Unable to diff object. Will replicate the entire object instead.", x);
                } finally {
                    rentry.unlock();
                }
                
            }
            if (msg == null) {
                //construct a complete
                msg = new MapMessage(mapContextName, MapMessage.MSG_BACKUP,
                                     false, (Serializable) entry.getKey(),
                                     (Serializable) entry.getValue(),
                                     null, entry.getBackupNodes());

            }
            try {
                if ( channel!=null && entry.getBackupNodes()!= null && entry.getBackupNodes().length > 0 ) {
                    channel.send(entry.getBackupNodes(), msg, channelSendOptions);
                }
            } catch (ChannelException x) {
                log.error("Unable to replicate data.", x);
            }
        } //end if

    }

    /**
     * This can be invoked by a periodic thread to replicate out any changes.
     * For maps that don't store objects that implement ReplicatedMapEntry, this
     * method should be used infrequently to avoid large amounts of data transfer
     * @param complete boolean
     */
    public void replicate(boolean complete) {
        Iterator i = super.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            replicate(e.getKey(), complete);
        } //while

    }

    public void transferState() {
        try {
            Member[] members = getMapMembers();
            Member backup = members.length > 0 ? (Member) members[0] : null;
            if (backup != null) {
                MapMessage msg = new MapMessage(mapContextName, MapMessage.MSG_STATE, false,
                                                null, null, null, null);
                Response[] resp = rpcChannel.send(new Member[] {backup}, msg, rpcChannel.FIRST_REPLY, channelSendOptions, rpcTimeout);
                if (resp.length > 0) {
                    synchronized (stateMutex) {
                        msg = (MapMessage) resp[0].getMessage();
                        msg.deserialize(getExternalLoaders());
                        ArrayList list = (ArrayList) msg.getValue();
                        for (int i = 0; i < list.size(); i++) {
                            messageReceived( (Serializable) list.get(i), resp[0].getSource());
                        } //for
                    }
                } else {
                    log.warn("Transfer state, 0 replies, probably a timeout.");
                }
            }
        } catch (ChannelException x) {
            log.error("Unable to transfer LazyReplicatedMap state.", x);
        } catch (IOException x) {
            log.error("Unable to transfer LazyReplicatedMap state.", x);
        } catch (ClassNotFoundException x) {
            log.error("Unable to transfer LazyReplicatedMap state.", x);
        }
        stateTransferred = true;
    }

    /**
     * @todo implement state transfer
     * @param msg Serializable
     * @return Serializable - null if no reply should be sent
     */
    public Serializable replyRequest(Serializable msg, final Member sender) {
        if (! (msg instanceof MapMessage))return null;
        MapMessage mapmsg = (MapMessage) msg;

        //map init request
        if (mapmsg.getMsgType() == mapmsg.MSG_INIT) {
            mapmsg.setBackUpNodes(wrap(channel.getLocalMember(false)));
            return mapmsg;
        }
        
        //map start request
        if (mapmsg.getMsgType() == mapmsg.MSG_START) {
            mapmsg.setBackUpNodes(wrap(channel.getLocalMember(false)));
            mapMemberAdded(sender);
            return mapmsg;
        }

        //backup request
        if (mapmsg.getMsgType() == mapmsg.MSG_RETRIEVE_BACKUP) {
            MapEntry entry = (MapEntry)super.get(mapmsg.getKey());
            if (entry == null)return null;
            mapmsg.setValue( (Serializable) entry.getValue());
            return mapmsg;
        }

        //state transfer request
        if (mapmsg.getMsgType() == mapmsg.MSG_STATE) {
            synchronized (stateMutex) { //make sure we dont do two things at the same time
                ArrayList list = new ArrayList();
                Iterator i = super.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry e = (Map.Entry) i.next();
                    MapEntry entry = (MapEntry) e.getValue();
                    MapMessage me = new MapMessage(mapContextName, MapMessage.MSG_PROXY,
                        false, (Serializable) entry.getKey(), null,null, entry.getBackupNodes());
                    list.add(me);
                }
                mapmsg.setValue(list);
                return mapmsg;
                
            } //synchronized
        }

        return null;

    }

    /**
     * If the reply has already been sent to the requesting thread,
     * the rpc callback can handle any data that comes in after the fact.
     * @param msg Serializable
     * @param sender Member
     */
    public void leftOver(Serializable msg, Member sender) {
        //left over membership messages
        if (! (msg instanceof MapMessage))return;

        MapMessage mapmsg = (MapMessage) msg;
        try {
            mapmsg.deserialize(getExternalLoaders());
            if (mapmsg.getMsgType() == MapMessage.MSG_START) {
                mapMemberAdded(mapmsg.getBackupNodes()[0]);
            }
        } catch (IOException x ) {
            log.error("Unable to deserialize MapMessage.",x);
        } catch (ClassNotFoundException x ) {
            log.error("Unable to deserialize MapMessage.",x);
        }
    }

    public void messageReceived(Serializable msg, Member sender) {
        if (! (msg instanceof MapMessage)) return;

        MapMessage mapmsg = (MapMessage) msg;
        try {
            mapmsg.deserialize(getExternalLoaders());
        } catch (IOException x) {
            log.error("Unable to deserialize MapMessage.", x);
            return;
        } catch (ClassNotFoundException x) {
            log.error("Unable to deserialize MapMessage.", x);
            return;
        }
        if (mapmsg.getMsgType() == MapMessage.MSG_START) {
            mapMemberAdded(mapmsg.getBackupNodes()[0]);
        }

        if (mapmsg.getMsgType() == MapMessage.MSG_STOP) {
            memberDisappeared(mapmsg.getBackupNodes()[0]);
        }

        if (mapmsg.getMsgType() == MapMessage.MSG_PROXY) {
            MapEntry entry = new MapEntry(mapmsg.getKey(), mapmsg.getValue());
            entry.setBackup(false);
            entry.setProxy(true);
            entry.setBackupNodes(mapmsg.getBackupNodes());
            super.put(entry.getKey(), entry);
        }

        if (mapmsg.getMsgType() == MapMessage.MSG_REMOVE) {
            super.remove(mapmsg.getKey());
        }

        if (mapmsg.getMsgType() == MapMessage.MSG_BACKUP) {
            MapEntry entry = (MapEntry)super.get(mapmsg.getKey());
            if (entry == null) {
                entry = new MapEntry(mapmsg.getKey(), mapmsg.getValue());
                entry.setBackup(true);
                entry.setProxy(false);
                entry.setBackupNodes(mapmsg.getBackupNodes());
                if (mapmsg.getValue()!=null && mapmsg.getValue() instanceof ReplicatedMapEntry ) {
                    ((ReplicatedMapEntry)mapmsg.getValue()).setOwner(getMapOwner());
                }
            } else {
                entry.setBackup(true);
                entry.setProxy(false);
                entry.setBackupNodes(mapmsg.getBackupNodes());
                if (entry.getValue() instanceof ReplicatedMapEntry) {
                    ReplicatedMapEntry diff = (ReplicatedMapEntry) entry.getValue();
                    if (mapmsg.isDiff()) {
                        try {
                            diff.lock();
                            diff.applyDiff(mapmsg.getDiffValue(), 0, mapmsg.getDiffValue().length);
                        } catch (Exception x) {
                            log.error("Unable to apply diff to key:" + entry.getKey(), x);
                        } finally {
                            diff.unlock();
                        }
                    } else {
                        if ( mapmsg.getValue()!=null ) entry.setValue(mapmsg.getValue());
                        ((ReplicatedMapEntry)entry.getValue()).setOwner(getMapOwner());
                    } //end if
                } else if  (mapmsg.getValue() instanceof ReplicatedMapEntry) {
                    ReplicatedMapEntry re = (ReplicatedMapEntry)mapmsg.getValue();
                    re.setOwner(getMapOwner());
                    entry.setValue(re);
                } else {
                    if ( mapmsg.getValue()!=null ) entry.setValue(mapmsg.getValue());
                } //end if
            } //end if
            super.put(entry.getKey(), entry);
        } //end if
    }

    public boolean accept(Serializable msg, Member sender) {
        if (msg instanceof MapMessage) {
            return Arrays.equals(mapContextName, ( (MapMessage) msg).getMapId());
        }
        return false;
    }

    public void mapMemberAdded(Member member) {
        if ( member.equals(getChannel().getLocalMember(false)) ) return;
        boolean memberAdded = false;
        //select a backup node if we don't have one
        synchronized (mapMembers) {
            if (!mapMembers.contains(member) ) {
                mapMembers.add(member);
                memberAdded = true;
            }
        }
        if ( memberAdded ) {
            synchronized (stateMutex) {
                Iterator i = super.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry e = (Map.Entry) i.next();
                    MapEntry entry = (MapEntry) e.getValue();
                    if ( entry == null ) continue;
                    if (entry.isPrimary() && (entry.getBackupNodes() == null || entry.getBackupNodes().length == 0)) {
                        try {
                            Member[] backup = publishEntryInfo(entry.getKey(), entry.getValue());
                            entry.setBackupNodes(backup);
                        } catch (ChannelException x) {
                            log.error("Unable to select backup node.", x);
                        } //catch
                    } //end if
                } //while
            } //synchronized
        }//end if
    }
    
    public boolean inSet(Member m, Member[] set) {
        if ( set == null ) return false;
        boolean result = false;
        for (int i=0; i<set.length && (!result); i++ )
            if ( m.equals(set[i]) ) result = true;
        return result;
    }

    public Member[] excludeFromSet(Member[] mbrs, Member[] set) {
        ArrayList result = new ArrayList();
        for (int i=0; i<set.length; i++ ) {
            boolean include = true;
            for (int j=0; j<mbrs.length; j++ ) 
                if ( mbrs[j].equals(set[i]) ) include = false;
            if ( include ) result.add(set[i]);
        }
        return (Member[])result.toArray(new Member[result.size()]);
    }

    public void memberAdded(Member member) {
        //do nothing
    }

    public void memberDisappeared(Member member) {
        boolean removed = false;
        synchronized (mapMembers) {
            removed = mapMembers.remove(member);
        }
        Iterator i = super.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            MapEntry entry = (MapEntry) e.getValue();
            if (entry.isPrimary() && inSet(member,entry.getBackupNodes())) {
                try {
                    Member[] backup = publishEntryInfo(entry.getKey(), entry.getValue());
                    entry.setBackupNodes(backup);
                } catch (ChannelException x) {
                    log.error("Unable to relocate[" + entry.getKey() + "] to a new backup node", x);
                }
            } //end if
        } //while
    }

    int currentNode = 0;
    public Member getNextBackupNode() {
        Member[] members = getMapMembers();
        if (members.length == 0)return null;
        int node = currentNode++;
        if (node >= members.length) {
            node = 0;
            currentNode = 0;
        }
        return members[node];
    }

    protected abstract Member[] publishEntryInfo(Object key, Object value) throws ChannelException;

//------------------------------------------------------------------------------    
//              METHODS TO OVERRIDE    
//------------------------------------------------------------------------------
  
    protected void printMap(String header) {
        try {
            System.out.println("\nDEBUG MAP:"+header);
            System.out.println("Map["+ new String(mapContextName, chset) + ", Map Size:" + super.size());
            Member[] mbrs = getMapMembers();
            for ( int i=0; i<mbrs.length;i++ ) {
                System.out.println("Mbr["+(i+1)+"="+mbrs[i].getName());
            }
            Iterator i = super.entrySet().iterator();
            int cnt = 0;

            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                System.out.println( (++cnt) + ". " + e.getValue());
            }
            System.out.println("EndMap]\n\n");
        }catch ( Exception ignore) {
            ignore.printStackTrace();
        }
    }

//------------------------------------------------------------------------------
//                Map Entry class
//------------------------------------------------------------------------------
    public static class MapEntry implements Map.Entry {
        private boolean backup;
        private boolean proxy;
        private Member[] backupNodes;

        private Serializable key;
        private Serializable value;

        public MapEntry(Serializable key, Serializable value) {
            setKey(key);
            setValue(value);
            
        }

        public boolean isBackup() {
            return backup;
        }

        public void setBackup(boolean backup) {
            this.backup = backup;
        }

        public boolean isProxy() {
            return proxy;
        }

        public boolean isPrimary() {
            return ( (!proxy) && (!backup));
        }

        public void setProxy(boolean proxy) {
            this.proxy = proxy;
        }

        public boolean isDiffable() {
            return (value instanceof ReplicatedMapEntry) &&
                   ((ReplicatedMapEntry)value).isDiffable();
        }

        public void setBackupNodes(Member[] nodes) {
            this.backupNodes = nodes;
        }

        public Member[] getBackupNodes() {
            return backupNodes;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            Object old = this.value;
            this.value = (Serializable) value;
            return old;
        }

        public Object getKey() {
            return key;
        }
        
        public Object setKey(Object key) {
            Object old = this.key;
            this.key = (Serializable)key;
            return old;
        }

        public int hashCode() {
            return key.hashCode();
        }

        public boolean equals(Object o) {
            return key.equals(o);
        }

        /**
         * apply a diff, or an entire object
         * @param data byte[]
         * @param offset int
         * @param length int
         * @param diff boolean
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public void apply(byte[] data, int offset, int length, boolean diff) throws IOException, ClassNotFoundException {
            if (isDiffable() && diff) {
                ReplicatedMapEntry rentry = (ReplicatedMapEntry) value;
                try {
                    rentry.lock();
                    rentry.applyDiff(data, offset, length);
                } finally {
                    rentry.unlock();
                }
            } else if (length == 0) {
                value = null;
                proxy = true;
            } else {
                value = XByteBuffer.deserialize(data, offset, length);
            }
        }
        
        public String toString() {
            StringBuffer buf = new StringBuffer("MapEntry[key:");
            buf.append(getKey()).append("; ");
            buf.append("value:").append(getValue()).append("; ");
            buf.append("primary:").append(isPrimary()).append("; ");
            buf.append("backup:").append(isBackup()).append("; ");
            buf.append("proxy:").append(isProxy()).append(";]");
            return buf.toString();
        }

    }

//------------------------------------------------------------------------------
//                map message to send to and from other maps
//------------------------------------------------------------------------------

    public static class MapMessage implements Serializable {
        public static final int MSG_BACKUP = 1;
        public static final int MSG_RETRIEVE_BACKUP = 2;
        public static final int MSG_PROXY = 3;
        public static final int MSG_REMOVE = 4;
        public static final int MSG_STATE = 5;
        public static final int MSG_START = 6;
        public static final int MSG_STOP = 7;
        public static final int MSG_INIT = 8;

        private byte[] mapId;
        private int msgtype;
        private boolean diff;
        private transient Serializable key;
        private transient Serializable value;
        private byte[] valuedata;
        private byte[] keydata;
        private byte[] diffvalue;
        private Member[] nodes;

        public MapMessage() {}

        public MapMessage(byte[] mapId,int msgtype, boolean diff,
                          Serializable key, Serializable value,
                          byte[] diffvalue, Member[] nodes)  {
            this.mapId = mapId;
            this.msgtype = msgtype;
            this.diff = diff;
            this.key = key;
            this.value = value;
            this.diffvalue = diffvalue;
            this.nodes = nodes;
            setValue(value);
            setKey(key);
        }
        
        public void deserialize(ClassLoader[] cls) throws IOException, ClassNotFoundException {
            key(cls);
            value(cls);
        }

        public int getMsgType() {
            return msgtype;
        }

        public boolean isDiff() {
            return diff;
        }

        public Serializable getKey() {
            try {
                return key(null);
            } catch ( Exception x ) {
                log.error("Deserialization error of the MapMessage.key",x);
                return null;
            }
        }

        public Serializable key(ClassLoader[] cls) throws IOException, ClassNotFoundException {
            if ( key!=null ) return key;
            if ( keydata == null || keydata.length == 0 ) return null;
            key = XByteBuffer.deserialize(keydata,0,keydata.length,cls);
            keydata = null;
            return key;
        }
        
        public byte[] getKeyData() {
            return keydata;
        }
        
        public Serializable getValue() {
            try {
                return value(null);
            } catch ( Exception x ) {
                log.error("Deserialization error of the MapMessage.value",x);
                return null;
            }
        }

        public Serializable value(ClassLoader[] cls) throws IOException, ClassNotFoundException  {
            if ( value!=null ) return value;
            if ( valuedata == null || valuedata.length == 0 ) return null;
            value = XByteBuffer.deserialize(valuedata,0,valuedata.length,cls);
            valuedata = null;;
            return value;
        }
        
        public byte[] getValueData() {
            return valuedata;
        }

        public byte[] getDiffValue() {
            return diffvalue;
        }

        public Member[] getBackupNodes() {
            return nodes;
        }

        private void setBackUpNodes(Member[] nodes) {
            this.nodes = nodes;
        }

        public byte[] getMapId() {
            return mapId;
        }

        public void setValue(Serializable value) {
            try {
                if ( value != null ) valuedata = XByteBuffer.serialize(value);
                this.value = value;
            }catch ( IOException x ) {
                throw new RuntimeException(x);
            }
        }
        
        public void setKey(Serializable key) {
            try {
                if (key != null) keydata = XByteBuffer.serialize(key);
                this.key = key;
            } catch (IOException x) {
                throw new RuntimeException(x);
            }
        }
        
        protected Member[] readMembers(ObjectInput in) throws IOException, ClassNotFoundException {
            int nodecount = in.readInt();
            Member[] members = new Member[nodecount];
            for ( int i=0; i<members.length; i++ ) {
                byte[] d = new byte[in.readInt()];
                in.read(d);
                if (d.length > 0) members[i] = MemberImpl.getMember(d);
            }
            return members;
        }
        
        protected void writeMembers(ObjectOutput out,Member[] members) throws IOException {
            if ( members == null ) members = new Member[0];
            out.writeInt(members.length);
            for (int i=0; i<members.length; i++ ) {
                if ( members[i] != null ) {
                    byte[] d = members[i] != null ? ( (MemberImpl)members[i]).getData(false) : new byte[0];
                    out.writeInt(d.length);
                    out.write(d);
                }
            }
        }
        
        
        /**
         * shallow clone
         * @return Object
         */
        public Object clone() {
            MapMessage msg = new MapMessage(this.mapId, this.msgtype, this.diff, this.key, this.value, this.diffvalue, this.nodes);
            msg.keydata = this.keydata;
            msg.valuedata = this.valuedata;
            return msg;
        }
    } //MapMessage


    public Channel getChannel() {
        return channel;
    }

    public byte[] getMapContextName() {
        return mapContextName;
    }

    public RpcChannel getRpcChannel() {
        return rpcChannel;
    }

    public long getRpcTimeout() {
        return rpcTimeout;
    }

    public Object getStateMutex() {
        return stateMutex;
    }

    public boolean isStateTransferred() {
        return stateTransferred;
    }

    public Object getMapOwner() {
        return mapOwner;
    }

    public ClassLoader[] getExternalLoaders() {
        return externalLoaders;
    }

    public int getChannelSendOptions() {
        return channelSendOptions;
    }

    public void setMapOwner(Object mapOwner) {
        this.mapOwner = mapOwner;
    }

    public void setExternalLoaders(ClassLoader[] externalLoaders) {
        this.externalLoaders = externalLoaders;
    }

    public void setChannelSendOptions(int channelSendOptions) {
        this.channelSendOptions = channelSendOptions;
    }

}