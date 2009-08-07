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

import java.io.PrintWriter;

import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Store server.xml Element Engine
 * 
 * @author Peter Rossbach
 */
public class StandardEngineSF extends StoreFactoryBase {

    private static Log log = LogFactory.getLog(StandardEngineSF.class);

    /**
     * Store the specified Engine properties.
     * 
     * @param aWriter
     *            PrintWriter to which we are storing
     * @param indent
     *            Number of spaces to indent this element
     * @param aEngine
     *            Object whose properties are being stored
     * 
     * @exception Exception
     *                if an exception occurs while storing
     */
    public void storeChilds(PrintWriter aWriter, int indent, Object aEngine,
            StoreDescription parentDesc) throws Exception {
        if (aEngine instanceof StandardEngine) {
            StandardEngine engine = (StandardEngine) aEngine;
            // Store nested <Listener> elements
            if (engine instanceof Lifecycle) {
                LifecycleListener listeners[] = ((Lifecycle) engine)
                        .findLifecycleListeners();
                storeElementArray(aWriter, indent, listeners);
            }

            // Store nested <Realm> element
            Realm realm = engine.getRealm();
            if (realm != null) {
                Realm parentRealm = null;
                // TODO is this case possible? (see it a old Server 5.0 impl)
                if (engine.getParent() != null) {
                    parentRealm = engine.getParent().getRealm();
                }
                if (realm != parentRealm) {
                    storeElement(aWriter, indent, realm);

                }
            }

            // Store nested <Valve> elements
            if (engine instanceof Pipeline) {
                Valve valves[] = ((Pipeline) engine).getValves();
                storeElementArray(aWriter, indent, valves);

            }
            // store all <Host> elements
            Container children[] = engine.findChildren();
            storeElementArray(aWriter, indent, children);
        }
    }
}