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

package org.apache.catalina.cluster.mcast;


import java.util.HashMap;
/**
 * A <b>membership</b> implementation using simple multicast.
 * This is the representation of a multicast membership.
 * This class is responsible for maintaining a list of active cluster nodes in the cluster.
 * If a node fails to send out a heartbeat, the node will be dismissed.
 *
 * @author Filip Hanik
 * @version $Revision$, $Date$
 */


public class McastMembership
{
    /**
     * The name of this membership, has to be the same as the name for the local
     * member
     */
    protected String name;
    /**
     * A map of all the members in the cluster.
     */
    protected HashMap map = new java.util.HashMap();

    /**
     * Constructs a new membership
     * @param myName - has to be the name of the local member. Used to filter the local member from the cluster membership
     */
    public McastMembership(String myName) {
        name = myName;
    }

    /**
     * Reset the membership and start over fresh.
     * Ie, delete all the members and wait for them to ping again and join this membership
     */
    public synchronized void reset() {
        map.clear();
    }

    /**
     * Notify the membership that this member has announced itself.
     *
     * @param m - the member that just pinged us
     * @return - true if this member is new to the cluster, false otherwise.
     * @return - false if this member is the local member.
     */
    public synchronized boolean memberAlive(McastMember m) {
        boolean result = false;
        //ignore ourselves
        if ( m.getName().equals(name) ) return result;

        //return true if the membership has changed
        MbrEntry entry = (MbrEntry)map.get(m.getName());
        if ( entry == null ) {
            entry = new MbrEntry(m);
            map.put(m.getName(),entry);
            result = true;
        } else {
            //update the member alive time
            entry.getMember().setMemberAliveTime(m.getMemberAliveTime());
        }//end if
        entry.accessed();
        return result;
    }

    /**
     * Runs a refresh cycle and returns a list of members that has expired.
     * This also removes the members from the membership, in such a way that
     * getMembers() = getMembers() - expire()
     * @param maxtime - the max time a member can remain unannounced before it is considered dead.
     * @return the list of expired members
     */
    public synchronized McastMember[] expire(long maxtime) {
        MbrEntry[] members = getMemberEntries();
        java.util.ArrayList list = new java.util.ArrayList();
        for (int i=0; i<members.length; i++) {
            MbrEntry entry = members[i];
            if ( entry.hasExpired(maxtime) ) {
                list.add(entry.getMember());
            }//end if
        }//while
        McastMember[] result = new McastMember[list.size()];
        list.toArray(result);
        for ( int j=0; j<result.length; j++) map.remove(result[j].getName());
        return result;

    }//expire

    /**
     * Returning a list of all the members in the membership
     */
    public synchronized McastMember[] getMembers() {
        McastMember[] result = new McastMember[map.size()];
        java.util.Iterator i = map.entrySet().iterator();
        int pos = 0;
        while ( i.hasNext() )
            result[pos++] = ((MbrEntry)((java.util.Map.Entry)i.next()).getValue()).getMember();
        return result;
    }

    protected synchronized MbrEntry[] getMemberEntries()
    {
        MbrEntry[] result = new MbrEntry[map.size()];
        java.util.Iterator i = map.entrySet().iterator();
        int pos = 0;
        while ( i.hasNext() )
            result[pos++] = ((MbrEntry)((java.util.Map.Entry)i.next()).getValue());
        return result;
    }


    /**
     * Inner class that represents a member entry
     */
    protected static class MbrEntry
    {

        protected McastMember mbr;
        protected long lastHeardFrom;
        public MbrEntry(McastMember mbr) {
            this.mbr = mbr;
        }
        /**
         * Indicate that this member has been accessed.
         */
        public void accessed(){
            lastHeardFrom = System.currentTimeMillis();
        }
        /**
         * Return the actual McastMember object
         */
        public McastMember getMember() {
            return mbr;
        }

        /**
         * Check if this dude has expired
         * @param maxtime The time threshold
         */
        public boolean hasExpired(long maxtime) {
            long delta = System.currentTimeMillis() - lastHeardFrom;
            return delta > maxtime;
        }
    }//MbrEntry
}
