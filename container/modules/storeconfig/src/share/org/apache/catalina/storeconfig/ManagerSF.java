/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.apache.catalina.session.StandardManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Store server.xml Manager element
 * 
 * @author Peter Rossbach
 */
public class ManagerSF extends StoreFactoryBase {

    private static Log log = LogFactory.getLog(ManagerSF.class);

    /**
     * Store the only the Manager elements
     * 
     * @see NamingResourcesSF#storeChilds(PrintWriter, int, Object, StoreDescription)
     */
    public void store(PrintWriter aWriter, int indent, Object aElement)
            throws Exception {
        StoreDescription elementDesc = getRegistry().findDescription(
                aElement.getClass());
        if (elementDesc != null && aElement instanceof StandardManager) {
            StandardManager manager = (StandardManager) aElement;
            if (!isDefaultManager(manager)) {
                if (log.isDebugEnabled())
                    log.debug(sm.getString("factory.storeTag", elementDesc
                            .getTag(), aElement));
                getStoreAppender().printIndent(aWriter, indent + 2);
                getStoreAppender().printTag(aWriter, indent + 2, manager,
                        elementDesc);
            }
        } else {
            if (log.isWarnEnabled())
                log.warn(sm.getString("factory.storeNoDescriptor", aElement
                        .getClass()));
        }
    }

    /**
     * Is this an instance of the default <code>Manager</code> configuration,
     * with all-default properties?
     * 
     * @param smanager
     *            Manager to be tested
     */
    protected boolean isDefaultManager(StandardManager smanager) {

        if (!"SESSIONS.ser".equals(smanager.getPathname())
                || !"java.security.SecureRandom".equals(smanager
                        .getRandomClass())
                || (smanager.getMaxActiveSessions() != -1)
                || !"MD5".equals(smanager.getAlgorithm())) {
            return (false);
        }
        return (true);

    }

}
