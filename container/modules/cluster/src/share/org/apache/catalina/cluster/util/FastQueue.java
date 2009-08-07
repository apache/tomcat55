/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
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
 * A fast queue that remover thread lock the adder thread. <br/>Limit the queue
 * length when you have strange producer thread problemes.
 * 
 * FIXME add i18n support to log messages
 * @author Rainer Jung
 * @author Peter Rossbach
 * @version $Revision$ $Date$
 */
public class FastQueue implements IQueue {

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(FastQueue.class);

    /**
     * This is the actual queue
     */
    private SingleRemoveSynchronizedAddLock lock = null;

    /**
     * First Object at queue (consumer message)
     */
    private LinkObject first = null;

    /**
     * Last object in queue (producer Object)
     */
    private LinkObject last = null;

    /**
     * Current Queue elements size
     */
    private int size = 0;

    /**
     * check lock to detect strange threadings things
     */
    private boolean checkLock = false;

    /**
     * protocol the thread wait times
     */
    private boolean timeWait = false;

    /**
     * calc stats data
     */
    private boolean doStats = false;

    /**
     *  
     */
    private boolean inAdd = false;

    /**
     *  
     */
    private boolean inRemove = false;

    /**
     *  
     */
    private boolean inMutex = false;

    /**
     * limit the queue legnth ( default is unlimited)
     */
    private int maxQueueLength = 0;

    /**
     * addWaitTimeout for producer
     */
    private long addWaitTimeout = 10000L;

    
    /**
     * removeWaitTimeout for consumer
     */
    private long removeWaitTimeout = 30000L;

    /**
     * enabled the queue
     */
    private boolean enabled = true;

    /**
     * calc all add objects
     */
    private long addCounter = 0;

    /**
     * calc all add objetcs in error state ( see limit queue length)
     */
    private long addErrorCounter = 0;

    /**
     * calc all remove objects
     */
    private long removeCounter = 0;

    /**
     * calc all remove objects failures (hupps probleme detection)
     */
    private long removeErrorCounter = 0;

    /**
     * Calc wait time thread
     */
    private long addWait = 0;

    /**
     * Calc remove time threads
     */
    private long removeWait = 0;

    /**
     *  max queue size
     */
    private int maxSize = 0;

    /**
     * avg queue size
     */
    private long avgSize = 0;

    /*
     *  
     */
    private int maxSizeSample = 0;

    /*
     *  
     */
    private long avgSizeSample = 0;

    /**
     *  avg size sample interval
     */
    private int sampleInterval = 100;

    /**
     * Generate Queue SingleRemoveSynchronizedAddLock and set add and wait
     * Timeouts
     */
    public FastQueue() {
        lock = new SingleRemoveSynchronizedAddLock();
        lock.setAddWaitTimeout(addWaitTimeout);
        lock.setRemoveWaitTimeout(removeWaitTimeout);
    }

    /**
     * get current add wait timeout
     * 
     * @return current wait timeout
     */
    public long getAddWaitTimeout() {
        addWaitTimeout = lock.getAddWaitTimeout();
        return addWaitTimeout;
    }

    /**
     * Set add wait timeout (default 10000 msec)
     * 
     * @param timeout
     */
    public void setAddWaitTimeout(long timeout) {
        addWaitTimeout = timeout;
        lock.setAddWaitTimeout(addWaitTimeout);
    }

    /**
     * get current remove wait timeout
     * 
     * @return The timeout
     */
    public long getRemoveWaitTimeout() {
        removeWaitTimeout = lock.getRemoveWaitTimeout();
        return removeWaitTimeout;
    }

    /**
     * set remove wait timeout ( default 30000 msec)
     * 
     * @param timeout
     */
    public void setRemoveWaitTimeout(long timeout) {
        removeWaitTimeout = timeout;
        lock.setRemoveWaitTimeout(removeWaitTimeout);
    }

    /*
     * get Max Queue length
     * 
     * @see org.apache.catalina.cluster.util.IQueue#getMaxQueueLength()
     */
    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public void setMaxQueueLength(int length) {
        maxQueueLength = length;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enable) {
        enabled = enable;
        if (!enabled) {
            lock.abortRemove();
        }
    }

    /*
     * @return Returns the checkLock.
     */
    public boolean isCheckLock() {
        return checkLock;
    }

    /*
     * @param checkLock The checkLock to set.
     */
    public void setCheckLock(boolean checkLock) {
        this.checkLock = checkLock;
    }

    /*
     * @return Returns the doStats.
     */
    public boolean isDoStats() {
        return doStats;
    }

    /*
     * @param doStats The doStats to set.
     */
    public void setDoStats(boolean doStats) {
        this.doStats = doStats;
    }

    /*
     * @return Returns the timeWait.
     */
    public boolean isTimeWait() {
        return timeWait;
    }

    /*
     * @param timeWait The timeWait to set.
     */
    public void setTimeWait(boolean timeWait) {
        this.timeWait = timeWait;
    }

    public int getSampleInterval() {
        return sampleInterval;
    }

    public void setSampleInterval(int interval) {
        sampleInterval = interval;
    }

    public long getAddCounter() {
        return addCounter;
    }

    public void setAddCounter(long counter) {
        addCounter = counter;
    }

    public long getAddErrorCounter() {
        return addErrorCounter;
    }

    public void setAddErrorCounter(long counter) {
        addErrorCounter = counter;
    }

    public long getRemoveCounter() {
        return removeCounter;
    }

    public void setRemoveCounter(long counter) {
        removeCounter = counter;
    }

    public long getRemoveErrorCounter() {
        return removeErrorCounter;
    }

    public void setRemoveErrorCounter(long counter) {
        removeErrorCounter = counter;
    }

    public long getAddWait() {
        return addWait;
    }

    public void setAddWait(long wait) {
        addWait = wait;
    }

    public long getRemoveWait() {
        return removeWait;
    }

    public void setRemoveWait(long wait) {
        removeWait = wait;
    }

    /**
     * @return The max size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @param size
     */
    public void setMaxSize(int size) {
        maxSize = size;
    }

    
    /**
     * Avg queue size
     * @return The average queue size
     */
    public long getAvgSize() {
        if (addCounter > 0) {
            return avgSize / addCounter;
        } else {
            return 0;
        }
    }

    /**
     * reset all stats data 
     */
    public void resetStatistics() {
        addCounter = 0;
        addErrorCounter = 0;
        removeCounter = 0;
        removeErrorCounter = 0;
        avgSize = 0;
        maxSize = 0;
        addWait = 0;
        removeWait = 0;
    }

    /**
     * unlock queue for next add 
     */
    public void unlockAdd() {
        lock.unlockAdd(size > 0 ? true : false);
    }

    /**
     * unlock queue for next remove 
     */
    public void unlockRemove() {
        lock.unlockRemove();
    }

    /**
     * start queuing
     */
    public void start() {
        setEnabled(true);
    }

    /**
     * start queuing
     */
    public void stop() {
        setEnabled(false);
    }

    public long getSample() {
        return addCounter % sampleInterval;
    }

    public int getMaxSizeSample() {
        return maxSizeSample;
    }

    public void setMaxSizeSample(int size) {
        maxSizeSample = size;
    }

    public long getAvgSizeSample() {
        long sample = addCounter % sampleInterval;
        if (sample > 0) {
            return avgSizeSample / sample;
        } else if (addCounter > 0) {
            return avgSizeSample / sampleInterval;
        } else {
            return 0;
        }
    }

    public int getSize() {
        int sz;
        sz = size;
        return sz;
    }

    /* Add new data to the queue
     * @see org.apache.catalina.cluster.util.IQueue#add(java.lang.String, java.lang.Object)
     * FIXME extract some method
     */
    public boolean add(String key, Object data) {
        boolean ok = true;
        long time = 0;

        if (!enabled) {
            if (log.isInfoEnabled())
                log.info("FastQueue: queue disabled, add aborted");
            return false;
        }

        if (timeWait) {
            time = System.currentTimeMillis();
        }
        lock.lockAdd();
        try {
            if (timeWait) {
                addWait += (System.currentTimeMillis() - time);
            }

            if (log.isTraceEnabled()) {
                log.trace("FastQueue: add starting with size " + size);
            }
            if (checkLock) {
                if (inAdd)
                    log.warn("FastQueue.add: Detected other add");
                inAdd = true;
                if (inMutex)
                    log.warn("FastQueue.add: Detected other mutex in add");
                inMutex = true;
            }

            if ((maxQueueLength > 0) && (size >= maxQueueLength)) {
                ok = false;
                if (log.isTraceEnabled()) {
                    log.trace("FastQueue: Could not add, since queue is full ("
                            + size + ">=" + maxQueueLength + ")");
                }

            } else {
                LinkObject element = new LinkObject(key, data);
                if (size == 0) {
                    first = last = element;
                    size = 1;
                } else {
                    if (last == null) {
                        ok = false;
                        log
                                .error("FastQueue: Could not add, since last is null although size is "
                                        + size + " (>0)");
                    } else {
                        last.append(element);
                        last = element;
                        size++;
                    }
                }

            }

            if (doStats) {
                if (ok) {
                    if (addCounter % sampleInterval == 0) {
                        maxSizeSample = 0;
                        avgSizeSample = 0;
                    }
                    addCounter++;
                    if (size > maxSize) {
                        maxSize = size;
                    }
                    if (size > maxSizeSample) {
                        maxSizeSample = size;
                    }
                    avgSize += size;
                    avgSizeSample += size;
                } else {
                    addErrorCounter++;
                }
            }

            if (first == null) {
                log.error("FastQueue: first is null, size is " + size
                        + " at end of add");
            }
            if (last == null) {
                log.error("FastQueue: last is null, size is " + size
                        + " at end of add");
            }

            if (checkLock) {
                if (!inMutex)
                    log.warn("FastQueue: Cancelled by other mutex in add");
                inMutex = false;
                if (!inAdd)
                    log.warn("FastQueue: Cancelled by other add");
                inAdd = false;
            }
            if (log.isTraceEnabled()) {
                log.trace("FastQueue: add ending with size " + size);
            }

            if (timeWait) {
                time = System.currentTimeMillis();
            }
        } finally {
            lock.unlockAdd(true);
        }
        if (timeWait) {
            addWait += (System.currentTimeMillis() - time);
        }
        return ok;
    }

    /* remove the complete queued object list
     * @see org.apache.catalina.cluster.util.IQueue#remove()
     * FIXME extract some method
     */
    public LinkObject remove() {
        LinkObject element;
        boolean gotLock;
        long time = 0;

        if (!enabled) {
            if (log.isInfoEnabled())
                log.info("FastQueue: queue disabled, remove aborted");
            return null;
        }

        if (timeWait) {
            time = System.currentTimeMillis();
        }
        gotLock = lock.lockRemove();
        try {

            if (!gotLock) {
                if (enabled) {
                    if (timeWait) {
                        removeWait += (System.currentTimeMillis() - time);
                    }
                    if (doStats) {
                        removeErrorCounter++;
                    }
                    if (log.isInfoEnabled())
                        log
                                .info("FastQueue: Remove aborted although queue enabled");
                } else {
                    if (log.isInfoEnabled())
                        log.info("FastQueue: queue disabled, remove aborted");
                }
                return null;
            }

            if (timeWait) {
                removeWait += (System.currentTimeMillis() - time);
            }

            if (log.isTraceEnabled()) {
                log.trace("FastQueue: remove starting with size " + size);
            }
            if (checkLock) {
                if (inRemove)
                    log.warn("FastQueue: Detected other remove");
                inRemove = true;
                if (inMutex)
                    log.warn("FastQueue: Detected other mutex in remove");
                inMutex = true;
            }

            element = first;

            if (doStats) {
                if (element != null) {
                    removeCounter++;
                } else {
                    removeErrorCounter++;
                    log
                            .error("FastQueue: Could not remove, since first is null although size is "
                                    + size + " (>0)");
                }
            }

            first = last = null;
            size = 0;

            if (checkLock) {
                if (!inMutex)
                    log.warn("FastQueue: Cancelled by other mutex in remove");
                inMutex = false;
                if (!inRemove)
                    log.warn("FastQueue: Cancelled by other remove");
                inRemove = false;
            }
            if (log.isTraceEnabled()) {
                log.trace("FastQueue: remove ending with size " + size);
            }

            if (timeWait) {
                time = System.currentTimeMillis();
            }
        } finally {
            lock.unlockRemove();
        }
        if (timeWait) {
            removeWait += (System.currentTimeMillis() - time);
        }
        return element;
    }

}
