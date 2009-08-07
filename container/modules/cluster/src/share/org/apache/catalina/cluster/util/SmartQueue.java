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

package org.apache.catalina.cluster.util;

/**
 * A smart queue, used for async replication<BR>
 * the "smart" part of this queue is that if the session is already queued for replication,
 * and it is updated again, the session will simply be replaced, hence we don't 
 * replicate stuff that is obsolete.
 * Put this into util, since it is quite  generic.
 * 
 * @author Filip Hanik
 * @version 1.0
 */
 

import java.util.LinkedList;
import java.util.HashMap;

public class SmartQueue {
    
    public static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( SmartQueue.class );
    
    /**
     * This is the actual queue
     */
    private LinkedList queue = new LinkedList();
    /**
     * And this is only for performance, fast lookups
     */
    private HashMap queueMap = new HashMap();
    
    private Object mutex = new Object();
    public static int debug = 0;
    
    public SmartQueue() {
    }
    
    /**
     * Add an object to the queue
     * @param entry - the smart entry
     */
    public void add(SmartEntry entry) {
        /*make sure we are within a synchronized block since we are dealing with two
          unsync collections*/
        synchronized (mutex) {
            /*check to see if this object has already been queued*/
            SmartEntry current = (SmartEntry)queueMap.get(entry.getKey());
            if ( current == null ) {
                /*the object has not been queued, at it to the end of the queue*/
                if ( debug != 0 ) log.debug("["+Thread.currentThread().getName()+"][SmartQueue] Adding new object="+entry);
                queue.addLast(entry);
                queueMap.put(entry.getKey(),entry);
            }else {
                /*the object has been queued, replace the value*/
                if ( debug != 0 ) log.debug("["+Thread.currentThread().getName()+"][SmartQueue] Replacing old object="+current);
                current.setValue(entry.getValue());
                if ( debug != 0 ) log.debug("with new object="+current);
            }
            /*wake up all the threads that are waiting for the lock to be released*/
            mutex.notifyAll();
        }
    }
    
    public int size() {
        synchronized (mutex) {
            return queue.size();            
        }
    }
    
    /**
     * Blocks forever until an element has been added to the queue
     */
    public SmartEntry remove() {
        return remove(0);
    }
    public SmartEntry remove(long timeout) {
        SmartEntry result = null; 
        long startEntry = System.currentTimeMillis();
        synchronized (mutex) {
            while ( size() == 0 ) {
                try {
                    if ( debug != 0 ) log.debug("["+Thread.currentThread().getName()+"][SmartQueue] Queue sleeping until object added size="+size()+".");
                    if ( (timeout != 0) && ((System.currentTimeMillis()-startEntry)>timeout) ) {
                        return null;
                    }
                    mutex.wait(timeout);
                    if ( debug != 0 ) log.debug("["+Thread.currentThread().getName()+"][SmartQueue] Queue woke up or interrupted size="+size()+".");
                }
                catch(IllegalMonitorStateException ex) {
                    throw ex;
                }
                catch(InterruptedException ex) {
                }//catch
            }//while
            /*guaranteed that we are not empty by now*/
            result = (SmartEntry)queue.removeFirst();
            queueMap.remove(result.getKey());
            if ( debug != 0 ) log.debug("["+Thread.currentThread().getName()+"][SmartQueue] Returning="+result);
        }
        return result;
    }
    
    
    
    public static class SmartEntry {
        protected Object key;
        protected Object value;
        public SmartEntry(Object key,
                               Object value) {
            if ( key == null ) throw new IllegalArgumentException("SmartEntry key can not be null.");
            if ( value == null ) throw new IllegalArgumentException("SmartEntry value can not be null.");
            this.key = key;
            this.value = value;
        }
        
        public Object getKey() {
            return key;
        }
        
        public Object getValue() {
            return value;
        }
        
        public void setValue(Object value) {
            if ( value == null ) throw new IllegalArgumentException("SmartEntry value can not be null.");
            this.value = value;
        }
        
        public int hashCode() {
            return key.hashCode();
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof SmartEntry)) return false;
            SmartEntry other = (SmartEntry)o;
            return other.getKey().equals(getKey());
        }
        
        public String toString() {
            return "[SmartyEntry key="+key+" value="+value+"]";
        }
    }
    

}
