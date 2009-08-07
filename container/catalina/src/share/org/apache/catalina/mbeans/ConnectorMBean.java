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

package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.IntrospectionUtils;


/**
 * <p>A <strong>ModelMBean</strong> implementation for the
 * <code>org.apache.coyote.tomcat5.CoyoteConnector</code> component.</p>
 *
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public class ConnectorMBean extends ClassNameMBean {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a <code>ModelMBean</code> with default
     * <code>ModelMBeanInfo</code> information.
     *
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception RuntimeOperationsException if an IllegalArgumentException
     *  occurs
     */
    public ConnectorMBean()
        throws MBeanException, RuntimeOperationsException {

        super();

    }


    // ------------------------------------------------------------- Attributes


    /**
     * Obtain and return the value of a specific attribute of this MBean.
     *
     * @param name Name of the requested attribute
     *
     * @exception AttributeNotFoundException if this attribute is not
     *  supported by this MBean
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception ReflectionException if a Java reflection exception
     *  occurs when invoking the getter
     */
    public Object getAttribute(String name)
        throws AttributeNotFoundException, MBeanException,
        ReflectionException {
		
 	Object attribute = null;
        // Validate the input parameters
        if (name == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute name is null"),
                 "Attribute name is null");
		 
        Connector connector = null;
	try {
	    connector = (Connector) getManagedResource();
	} catch (InstanceNotFoundException e) {
	    throw new MBeanException(e);
	} catch (InvalidTargetObjectTypeException e) {
	   throw new MBeanException(e);
        } 	    
	
	if (isProtocolProperty(name)) {
                
            name = translateAttributeName(name);
                
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
	    /* check the Protocol first, since JkCoyote has an independent
             * configure method.
             */
            try {
                if( protocolHandler != null ) {
                    attribute = IntrospectionUtils.getAttribute(protocolHandler, name);
                } else {
                    attribute = connector.getProperty(name);
                }
            } catch (Exception e) {
                throw new MBeanException(e);
            }
	} else {
	    attribute = super.getAttribute(name);
	}
	
        return attribute;

    }

    
    /**
     * Set the value of a specific attribute of this MBean.
     *
     * @param attribute The identification of the attribute to be set
     *  and the new value
     *
     * @exception AttributeNotFoundException if this attribute is not
     *  supported by this MBean
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception ReflectionException if a Java reflection exception
     *  occurs when invoking the getter
     */
     public void setAttribute(Attribute attribute)
        throws AttributeNotFoundException, MBeanException,
        ReflectionException {

        // Validate the input parameters
        if (attribute == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute is null"),
                 "Attribute is null");
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute name is null"),
                 "Attribute name is null"); 
		 
        Connector connector = null;
	try {
	    connector = (Connector) getManagedResource();
	} catch (InstanceNotFoundException e) {
	    throw new MBeanException(e);
	} catch (InvalidTargetObjectTypeException e) {
	   throw new MBeanException(e);
        } 	    
	
        if (isProtocolProperty(name)) {

            name = translateAttributeName(name);
            
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
	    /* check the Protocol first, since JkCoyote has an independent
             * configure method.
             */
            try {
                if( protocolHandler != null ) {
                    IntrospectionUtils.setAttribute(protocolHandler, name, value);
                } else {
                    connector.setProperty(name, value);
                }
            } catch (Exception e) {
                throw new MBeanException(e);
            }
  
	} else {
	    super.setAttribute(attribute);
	}
	
    }


    // ------------------------------------------------------------- Operations
    
    // ------------------------------------------------------------- Methods

    /**
     * Test for a property that is really on the Protocol.
     */
    private boolean isProtocolProperty(String name) {
        return ("algorithm").equals(name) || 
            ("keystoreType").equals(name) ||
            ("maxThreads").equals(name) || 
            ("maxSpareThreads").equals(name) ||
            ("minSpareThreads").equals(name);
    }    

    /*
     * Translate the attribute name from the legacy Factory names to their
     * internal protocol names.
     */
    private String translateAttributeName(String name) {
	if ("clientAuth".equals(name)) {
	    return "clientauth";
	} else if ("keystoreFile".equals(name)) {
	    return "keystore";
	} else if ("randomFile".equals(name)) {
	    return "randomfile";
	} else if ("rootFile".equals(name)) {
	    return "rootfile";
	} else if ("keystorePass".equals(name)) {
	    return "keypass";
	} else if ("keystoreType".equals(name)) {
	    return "keytype";
	} else if ("sslProtocol".equals(name)) {
	    return "protocol";
	} else if ("sslProtocols".equals(name)) {
	    return "protocols";
	}
	return name;
    }
}
