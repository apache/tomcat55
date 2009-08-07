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

package org.apache.catalina.cluster.tcp;
import java.util.List;
import java.util.LinkedList;

/**
 * @author not attributable
 * @version 1.0
 */

public class ThreadPool
{
    /**
     * A very simple thread pool class.  The pool size is set at
     * construction time and remains fixed.  Threads are cycled
     * through a FIFO idle queue.
     */

    List idle = new LinkedList();
    Object mutex = new Object();
    Object interestOpsMutex = null;

    ThreadPool (int poolSize, Class threadClass, Object interestOpsMutex) throws Exception {
        // fill up the pool with worker threads
        this.interestOpsMutex = interestOpsMutex;
        for (int i = 0; i < poolSize; i++) {
            WorkerThread thread = (WorkerThread)threadClass.newInstance();
            thread.setPool(this);

            // set thread name for debugging, start it
            thread.setName (threadClass.getName()+"[" + (i + 1)+"]");
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();

            idle.add (thread);
        }
    }

    /**
     * Find an idle worker thread, if any.  Could return null.
     */
    WorkerThread getWorker()
    {
        WorkerThread worker = null;

        
        synchronized (mutex) {
            while ( worker == null ) {
                if (idle.size() > 0) {
                    try {
                        worker = (WorkerThread) idle.remove(0);
                    } catch (java.util.NoSuchElementException x) {
                        //this means that there are no available workers
                        worker = null;
                    }
                } else {
                    try { mutex.wait(); } catch ( java.lang.InterruptedException x ) {}
                }
            }
        }

        return (worker);
    }

    /**
     * Called by the worker thread to return itself to the
     * idle pool.
     */
    void returnWorker (WorkerThread worker)
    {
        synchronized (mutex) {
            idle.add (worker);
            mutex.notify();
        }
    }
    public Object getInterestOpsMutex() {
        return interestOpsMutex;
    }
}
