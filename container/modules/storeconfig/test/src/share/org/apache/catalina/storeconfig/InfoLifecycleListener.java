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

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Peter Rossbach
 *  
 */
public class InfoLifecycleListener implements LifecycleListener {

    private static Log log = LogFactory.getLog(InfoLifecycleListener.class);

    /**
     * The descriptive information string for this implementation.
     */
    private static final String info = "org.apache.catalina.listener.InfoLifecycleListener/1.0";

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
     */
    public void lifecycleEvent(LifecycleEvent arg0) {
        if (log.isInfoEnabled())
            log.info(arg0.getSource().toString() + ": " + arg0.getType());
    }

}