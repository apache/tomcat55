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

import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * StoreFactory saves spezial elements.
 * Output was generate with StoreAppenders.
 * 
 * @author Peter Rossbach
 * @version 1.0
 *  
 */
public class StoreFactoryBase implements IStoreFactory {
    private static Log log = LogFactory.getLog(StoreFactoryBase.class);

    private StoreRegistry registry;

    private StoreAppender storeAppender = new StoreAppender();

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager
            .getManager(Constants.Package);

    /**
     * The descriptive information string for this implementation.
     */
    private static final String info = "org.apache.catalina.config.StoreFactoryBase/1.0";

    /**
     * Return descriptive information about this Facotry implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }

    /**
     * @return Returns the storeAppender.
     */
    public StoreAppender getStoreAppender() {
        return storeAppender;
    }

    /**
     * @param storeAppender
     *            The storeAppender to set.
     */
    public void setStoreAppender(StoreAppender storeAppender) {
        this.storeAppender = storeAppender;
    }

    /*
     * set Registry
     * 
     * @see org.apache.catalina.config.IStoreFactory#setRegistry(org.apache.catalina.config.ConfigurationRegistry)
     */
    public void setRegistry(StoreRegistry aRegistry) {
        registry = aRegistry;

    }

    /*
     * get Registry
     * 
     * @see org.apache.catalina.config.IStoreFactory#getRegistry()
     */
    public StoreRegistry getRegistry() {

        return registry;
    }

    public void storeXMLHead(PrintWriter aWriter) {
        // Store the beginning of this element
        aWriter.print("<?xml version=\"1.0\" encoding=\"");
        aWriter.print(getRegistry().getEncoding());
        aWriter.println("\"?>");
    }

    /*
     * Store a server.xml element with attributes and childs
     * 
     * @see org.apache.catalina.storeconfig.IStoreFactory#store(java.io.PrintWriter,
     *      int, java.lang.Object)
     */
    public void store(PrintWriter aWriter, int indent, Object aElement)
            throws Exception {

        StoreDescription elementDesc = getRegistry().findDescription(
                aElement.getClass());

        if (elementDesc != null) {
            if (log.isDebugEnabled())
                log.debug(sm.getString("factory.storeTag",
                        elementDesc.getTag(), aElement));
            getStoreAppender().printIndent(aWriter, indent + 2);
            if (!elementDesc.isChilds()) {
                getStoreAppender().printTag(aWriter, indent, aElement,
                        elementDesc);
            } else {
                getStoreAppender().printOpenTag(aWriter, indent + 2, aElement,
                        elementDesc);
                storeChilds(aWriter, indent + 2, aElement, elementDesc);
                getStoreAppender().printIndent(aWriter, indent + 2);
                getStoreAppender().printCloseTag(aWriter, elementDesc);
            }
        } else
            log.warn(sm.getString("factory.storeNoDescriptor", aElement
                    .getClass()));
    }

    /**
     * Must Implement at subclass for sepzial store childs handling
     * 
     * @param aWriter
     * @param indent
     * @param aElement
     * @param elementDesc
     */
    public void storeChilds(PrintWriter aWriter, int indent, Object aElement,
            StoreDescription elementDesc) throws Exception {
    }

    /**
     * Store only elements from storeChilds methods that are not a transient
     * child.
     * 
     * @param aWriter
     * @param indent
     * @param aTagElement
     * @throws Exception
     */
    protected void storeElement(PrintWriter aWriter, int indent,
            Object aTagElement) throws Exception {
        if (aTagElement != null) {
            IStoreFactory elementFactory = getRegistry().findStoreFactory(
                    aTagElement.getClass());

            if (elementFactory != null) {
                StoreDescription desc = getRegistry().findDescription(
                        aTagElement.getClass());
                if (!desc.isTransientChild(aTagElement.getClass().getName()))
                    elementFactory.store(aWriter, indent, aTagElement);
            } else {
                log.warn(sm.getString("factory.storeNoDescriptor", aTagElement
                        .getClass()));
            }
        }
    }

    /*
     * Save a array of elements @param aWriter @param indent @param elements
     */
    protected void storeElementArray(PrintWriter aWriter, int indent,
            Object[] elements) throws Exception {
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                storeElement(aWriter, indent, elements[i]);
            }
        }
    }
}