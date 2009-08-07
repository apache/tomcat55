/**
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

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.tomcat.util.IntrospectionUtils;

/**
 * Store the ReplicationTransmitter attributes. 
 * 
 * @author Peter Rossbach
 *  
 */
public class ReplicationTransmitterStoreAppender extends StoreAppender {

    /**
     * Store the relevant attributes of the specified JavaBean.
     * 
     * @param writer
     *            PrintWriter to which we are storing
     * @param include
     *            Should we include a <code>className</code> attribute?
     * @param bean
     *            Bean whose properties are to be rendered as attributes,
     * @param desc
     *            RegistryDescrpitor from this bean
     * 
     * @exception Exception
     *                if an exception occurs while storing
     */
    public void printAttributes(PrintWriter writer, int indent,
            boolean include, Object bean, StoreDescription desc)
            throws Exception {

        // Render the relevant properties of this bean
        String className = bean.getClass().getName();

        // Render a className attribute if requested
        if (include && desc != null && !desc.isStandard()) {
            writer.print(" className=\"");
            writer.print(bean.getClass().getName());
            writer.print("\"");
        }

        List propertyKeys = getPropertyKeys((ReplicationTransmitter) bean);
        // Create blank instance
        Object bean2 = defaultInstance(bean);
        for (Iterator propertyIterator = propertyKeys.iterator(); propertyIterator
                .hasNext();) {
            String key = (String) propertyIterator.next();
            Object value = (Object) IntrospectionUtils.getProperty(bean, key);

            if (desc.isTransientAttribute(key)) {
                continue; // Skip the specified exceptions
            }
            if (value == null) {
                continue; // Null values are not persisted
            }
            if (!isPersistable(value.getClass())) {
                continue;
            }
            Object value2 = IntrospectionUtils.getProperty(bean2, key);
            if (value.equals(value2)) {
                // The property has its default value
                continue;
            }
            if (isPrintValue(bean, bean2, key, desc))
                printValue(writer, indent, key, value);
        }
    }

    /**
     * Get all properties from ReplicationTransmitter (also dynamic properties)
     * 
     * @param bean
     * @return List of Connector Properties
     * @throws IntrospectionException
     */
    protected List getPropertyKeys(ReplicationTransmitter bean)
            throws IntrospectionException {
        ArrayList propertyKeys = new ArrayList();
        // Acquire the list of properties for this bean
        PropertyDescriptor descriptors[] = Introspector.getBeanInfo(
                bean.getClass()).getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[0];
        }
        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i] instanceof IndexedPropertyDescriptor) {
                continue; // Indexed properties are not persisted
            }
            if (!isPersistable(descriptors[i].getPropertyType())
                    || (descriptors[i].getReadMethod() == null)
                    || (descriptors[i].getWriteMethod() == null)) {
                continue; // Must be a read-write primitive or String
            }
            propertyKeys.add(descriptors[i].getName());
        }
        for (Iterator propertyIterator = bean.getPropertyNames(); propertyIterator
                .hasNext();) {
            Object key = propertyIterator.next();
            if (propertyKeys.contains(key))
                continue;
            if ("className".equals(key))
                continue;
            propertyKeys.add(key);
        }
        return propertyKeys;
    }

}