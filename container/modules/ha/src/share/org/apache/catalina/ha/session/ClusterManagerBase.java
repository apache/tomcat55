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

package org.apache.catalina.ha.session;

import org.apache.catalina.ha.ClusterManager;
import java.beans.PropertyChangeListener;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.Loader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.catalina.tribes.io.ReplicationStream;

/**
 * 
 * @author Filip Hanik
 * @version $Revision: 380100 $ $Date: 2006-02-23 06:08:14 -0600 (Thu, 23 Feb 2006) $
 */

public abstract class ClusterManagerBase extends ManagerBase implements Lifecycle, PropertyChangeListener, ClusterManager{
    /**
     * Open Stream and use correct ClassLoader (Container) Switch
     * ThreadClassLoader
     * 
     * @param data
     * @return The object input stream
     * @throws IOException
     */
    public ReplicationStream getReplicationStream(byte[] data) throws IOException {
        ByteArrayInputStream fis =null;
        ReplicationStream ois = null;
        Loader loader = null;
        ClassLoader classLoader = null;
        //fix to be able to run the DeltaManager
        //stand alone without a container.
        //use the Threads context class loader
        if (container != null)
            loader = container.getLoader();
        if (loader != null)
            classLoader = loader.getClassLoader();
        else
            classLoader = Thread.currentThread().getContextClassLoader();
        //end fix
        fis = new ByteArrayInputStream(data);
        if ( classLoader == Thread.currentThread().getContextClassLoader() ) {
            ois = new ReplicationStream(fis, new ClassLoader[] {classLoader});
        } else {
            ois = new ReplicationStream(fis, new ClassLoader[] {classLoader,Thread.currentThread().getContextClassLoader()});
        }
        return ois;
    }    

}