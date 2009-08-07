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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.*;

/**
 * A smart implementation of a stateful replicated map. uses primary/secondary backup strategy. 
 * One node is always the primary and one node is always the backup.
 * This map is synchronized across a cluster, and only has one backup member.<br/>
 * A perfect usage for this map would be a session map for a session manager in a clustered environment.<br/>
 * The only way to modify this list is to use the <code>put, putAll, remove</code> methods.
 * entrySet, entrySetFull, keySet, keySetFull, returns all non modifiable sets.<br><br>
 * If objects (values) in the map change without invoking <code>put()</code> or <code>remove()</code>
 * the data can be distributed using two different methods:<br>
 * <code>replicate(boolean)</code> and <code>replicate(Object, boolean)</code><br>
 * These two methods are very important two understand. The map can work with two set of value objects:<br>
 * 1. Serializable - the entire object gets serialized each time it is replicated<br>
 * 2. ReplicatedMapEntry - this interface allows for a isDirty() flag and to replicate diffs if desired.<br>
 * Implementing the <code>ReplicatedMapEntry</code> interface allows you to decide what objects 
 * get replicated and how much data gets replicated each time.<br>
 * If you implement a smart AOP mechanism to detect changes in underlying objects, you can replicate
 * only those changes by implementing the ReplicatedMapEntry interface, and return true when isDiffable()
 * is invoked.<br><br>
 * 
 * This map implementation doesn't have a background thread running to replicate changes.
 * If you do have changes without invoking put/remove then you need to invoke one of the following methods:
 * <ul>
 * <li><code>replicate(Object,boolean)</code> - replicates only the object that belongs to the key</li>
 * <li><code>replicate(boolean)</code> - Scans the entire map for changes and replicates data</li>
 *  </ul>
 * the <code>boolean</code> value in the <code>replicate</code> method used to decide 
 * whether to only replicate objects that implement the <code>ReplicatedMapEntry</code> interface
 * or to replicate all objects. If an object doesn't implement the <code>ReplicatedMapEntry</code> interface
 * each time the object gets replicated the entire object gets serialized, hence a call to <code>replicate(true)</code>
 * will replicate all objects in this map that are using this node as primary.
 * 
 * <br><br><b>REMBER TO CALL <code>breakdown()</code> or <code>finalize()</code> when you are done with the map to 
 * avoid memory leaks.<br><br>
 * @todo implement periodic sync/transfer thread
 * @author Filip Hanik
 * @version 1.0
 */
public class LazyReplicatedMap extends AbstractReplicatedMap 
    implements RpcCallback, ChannelListener, MembershipListener {
    protected static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LazyReplicatedMap.class);
    
    
    
//------------------------------------------------------------------------------    
//              CONSTRUCTORS / DESTRUCTORS
//------------------------------------------------------------------------------   
    /**
         * Creates a new map
         * @param channel The channel to use for communication
         * @param timeout long - timeout for RPC messags
         * @param mapContextName String - unique name for this map, to allow multiple maps per channel
         * @param initialCapacity int - the size of this map, see HashMap
         * @param loadFactor float - load factor, see HashMap
         */
        public LazyReplicatedMap(Object owner, Channel channel, long timeout, String mapContextName, int initialCapacity, float loadFactor, ClassLoader[] cls) {
            super(owner,channel,timeout,mapContextName,initialCapacity,loadFactor, Channel.SEND_OPTIONS_DEFAULT,cls);
        }

        /**
         * Creates a new map
         * @param channel The channel to use for communication
         * @param timeout long - timeout for RPC messags
         * @param mapContextName String - unique name for this map, to allow multiple maps per channel
         * @param initialCapacity int - the size of this map, see HashMap
         */
        public LazyReplicatedMap(Object owner, Channel channel, long timeout, String mapContextName, int initialCapacity, ClassLoader[] cls) {
            super(owner, channel,timeout,mapContextName,initialCapacity, LazyReplicatedMap.DEFAULT_LOAD_FACTOR, Channel.SEND_OPTIONS_DEFAULT, cls);
        }

        /**
         * Creates a new map
         * @param channel The channel to use for communication
         * @param timeout long - timeout for RPC messags
         * @param mapContextName String - unique name for this map, to allow multiple maps per channel
         */
        public LazyReplicatedMap(Object owner, Channel channel, long timeout, String mapContextName, ClassLoader[] cls) {
            super(owner, channel,timeout,mapContextName, LazyReplicatedMap.DEFAULT_INITIAL_CAPACITY,LazyReplicatedMap.DEFAULT_LOAD_FACTOR,Channel.SEND_OPTIONS_DEFAULT, cls);
        }




    
//------------------------------------------------------------------------------    
//              METHODS TO OVERRIDE    
//------------------------------------------------------------------------------
    /**
     * publish info about a map pair (key/value) to other nodes in the cluster
     * @param key Object
     * @param value Object
     * @return Member - the backup node
     * @throws ChannelException
     */
    protected Member[] publishEntryInfo(Object key, Object value) throws ChannelException {
        //select a backup node
        Member next = getNextBackupNode();
        
        if ( next == null ) return null;
        
        Member[] backup = wrap(next);
        MapMessage msg = null;
        //publish the data out to all nodes
        Member[] proxies = excludeFromSet(backup,getMapMembers());
        if ( proxies.length > 0 ) {
            msg = new MapMessage(getMapContextName(), MapMessage.MSG_PROXY, false,
                                            (Serializable) key, null, null, backup);
            getChannel().send(proxies, msg, getChannelSendOptions());
        }
        //publish the backup data to one node
        msg = new MapMessage(getMapContextName(), MapMessage.MSG_BACKUP, false,
                             (Serializable) key, (Serializable) value, null, backup);
        getChannel().send(backup, msg, getChannelSendOptions());
        return backup;
    }
    
    public Object get(Object key) {
        MapEntry entry = (MapEntry)super.get(key);
        if (log.isTraceEnabled()) log.trace("Requesting id:"+key+" entry:"+entry);
        if ( entry == null ) return null;
        if ( !entry.isPrimary() ) {
            //if the message is not primary, we need to retrieve the latest value
            try {
                Member[] backup = null;
                MapMessage msg = null;
                if ( !entry.isBackup() ) {
                    //make sure we don't retrieve from ourselves
                    msg = new MapMessage(getMapContextName(), MapMessage.MSG_RETRIEVE_BACKUP, false,
                                         (Serializable) key, null, null, null);
                    Response[] resp = getRpcChannel().send(entry.getBackupNodes(),msg, this.getRpcChannel().FIRST_REPLY, Channel.SEND_OPTIONS_DEFAULT, getRpcTimeout());
                    if (resp == null || resp.length == 0) {
                        //no responses
                        log.warn("Unable to retrieve remote object for key:" + key);
                        return null;
                    }
                    msg = (MapMessage) resp[0].getMessage();
                    msg.deserialize(getExternalLoaders());
                    backup = entry.getBackupNodes();
                    if ( entry.getValue() instanceof ReplicatedMapEntry ) {
                        ReplicatedMapEntry val = (ReplicatedMapEntry)entry.getValue();
                        val.setOwner(getMapOwner());
                    }
                    if ( msg.getValue()!=null ) entry.setValue(msg.getValue());
                }
                if (entry.isBackup()) {
                    //select a new backup node
                    backup = publishEntryInfo(key, entry.getValue());
                } else if ( entry.isProxy() ) {
                    //invalidate the previous primary
                    msg = new MapMessage(getMapContextName(),MapMessage.MSG_PROXY,false,(Serializable)key,null,null,backup);
                    getChannel().send(getMapMembersExcl(backup),msg,getChannelSendOptions());
                }

                entry.setBackupNodes(backup);
                entry.setBackup(false);
                entry.setProxy(false);

                
            } catch (Exception x) {
                log.error("Unable to replicate out data for a LazyReplicatedMap.get operation", x);
                return null;
            }
        }
        if (log.isTraceEnabled()) log.trace("Requesting id:"+key+" result:"+entry.getValue());
        return entry.getValue();
    }

    /**
     * Returns true if the key has an entry in the map.
     * The entry can be a proxy or a backup entry, invoking <code>get(key)</code>
     * will make this entry primary for the group
     * @param key Object
     * @return boolean
     */
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    
    public Object put(Object key, Object value) {
        if ( !(key instanceof Serializable) ) throw new IllegalArgumentException("Key is not serializable:"+key.getClass().getName());
        if ( value == null ) return remove(key);
        if ( !(value instanceof Serializable) ) throw new IllegalArgumentException("Value is not serializable:"+value.getClass().getName());

        MapEntry entry = new MapEntry((Serializable)key,(Serializable)value);
        entry.setBackup(false);
        entry.setProxy(false);
        
        Object old = null;
        
        //make sure that any old values get removed
        if ( containsKey(key) ) old = remove(key);
        try {
            Member[] backup = publishEntryInfo(key, value);
            entry.setBackupNodes(backup);
        } catch (ChannelException x) {
            log.error("Unable to replicate out data for a LazyReplicatedMap.put operation", x);
        }
        super.put(key,entry);
        return old;
    }

    
    /**
     * Copies all values from one map to this instance
     * @param m Map
     */
    public void putAll(Map m) {
        Iterator i = m.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry entry = (Map.Entry)i.next();
            put(entry.getKey(),entry.getValue());
        }
    }
    
    /**
     * Removes an object from this map, it will also remove it from 
     * 
     * @param key Object
     * @return Object
     */
    public Object remove(Object key) {
        MapEntry entry = (MapEntry)super.remove(key);
        
        try {
            MapMessage msg = new MapMessage(getMapContextName(),MapMessage.MSG_REMOVE,false,(Serializable)key,null,null,null);
            getChannel().send(getMapMembers(), msg,getChannelSendOptions());
        } catch ( ChannelException x ) {
            log.error("Unable to replicate out data for a LazyReplicatedMap.remove operation",x);
        }
        return entry!=null?entry.getValue():null;
    }

    public void clear() {
        //only delete active keys
        Iterator keys = keySet().iterator();
        while ( keys.hasNext() ) remove(keys.next());
    }

    public boolean containsValue(Object value) {
        if ( value == null ) {
            return super.containsValue(value);
        } else {
            Iterator i = super.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                MapEntry entry = (MapEntry) e.getValue();
                if (entry.isPrimary() && value.equals(entry.getValue())) return true;
            }//while
            return false;
        }//end if
    }

    public Object clone() {
        throw new UnsupportedOperationException("This operation is not valid on a replicated map");
    }
    
    /**
     * Returns the entire contents of the map
     * Map.Entry.getValue() will return a LazyReplicatedMap.MapEntry object containing all the information 
     * about the object.
     * @return Set
     */
    public Set entrySetFull() {
        return super.entrySet();
    }
    
    public Set keySetFull() {
        return super.keySet();
    }
    
    public int sizeFull() {
        return super.size();
    }

    public Set entrySet() {
        LinkedHashSet set = new LinkedHashSet(super.size());
        Iterator i = super.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry e = (Map.Entry)i.next();
            MapEntry entry = (MapEntry)e.getValue();
            if ( entry.isPrimary() ) set.add(entry);
        }
        return Collections.unmodifiableSet(set);
    }
    
    public Set keySet() {
        //todo implement
        //should only return keys where this is active.
        LinkedHashSet set = new LinkedHashSet(super.size());
        Iterator i = super.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry e = (Map.Entry)i.next();
            MapEntry entry = (MapEntry)e.getValue();
            if ( entry.isPrimary() ) set.add(entry.getKey());
        }
        return Collections.unmodifiableSet(set);
    }
    
    
    public int size() {
        //todo, implement a counter variable instead
        //only count active members in this node
        int counter = 0;
        Iterator i = super.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry e = (Map.Entry)i.next();
            MapEntry entry = (MapEntry)e.getValue();
            if ( entry.isPrimary() && entry.getValue()!=null ) counter++;
        }
        return counter;
    }
    
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return false;
    }
    
    public boolean isEmpty() {
        return size()==0;
    }
    
    public Collection values() {
        ArrayList values = new ArrayList(super.size());
        Iterator i = super.entrySet().iterator();
        while ( i.hasNext() ) {
            Map.Entry e = (Map.Entry)i.next();
            MapEntry entry = (MapEntry)e.getValue();
            if ( entry.isPrimary() && entry.getValue()!=null) values.add(entry.getValue());
        }
        return Collections.unmodifiableCollection(values);
    }
    



}