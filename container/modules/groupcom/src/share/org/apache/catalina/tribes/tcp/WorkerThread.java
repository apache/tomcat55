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

package org.apache.catalina.tribes.tcp;


/**
 * @author Filip Hanik
 * @version $Revision: 366253 $ $Date: 2006-01-05 13:30:42 -0600 (Thu, 05 Jan 2006) $
 */
public class WorkerThread extends Thread
{
    protected ThreadPool pool;
    protected boolean doRun = true;
    private int options;

    public void setPool(ThreadPool pool) {
        this.pool = pool;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public ThreadPool getPool() {
        return pool;
    }

    public int getOptions() {
        return options;
    }

    public void close()
    {
        doRun = false;
        notify();

    }
}
