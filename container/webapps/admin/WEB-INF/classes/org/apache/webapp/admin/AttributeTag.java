/*
 * Copyright 2002,2004 The Apache Software Foundation.
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


package org.apache.webapp.admin;


import java.io.IOException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.beanutils.PropertyUtils;



/**
 * Custom tag that retrieves a JMX MBean attribute value, and writes it
 * out to the current output stream.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class AttributeTag extends TagSupport {


    // ------------------------------------------------------------- Properties


    /**
     * The attribute name on the JMX MBean to be retrieved.
     */
    protected String attribute = null;

    public String getAttribute() {
        return (this.attribute);
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }


    /**
     * The bean name to be retrieved.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * The property name to be retrieved.
     */
    protected String property = null;

    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }


    /**
     * The scope in which the bean should be searched.
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Render the JMX MBean attribute identified by this tag
     *
     * @exception JspException if a processing exception occurs
     */
    public int doEndTag() throws JspException {

        // Retrieve the object name identified by our attributes
        Object bean = null;
        if (scope == null) {
            bean = pageContext.findAttribute(name);
        } else if ("page".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
        } else if ("request".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
        } else if ("session".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
        } else if ("application".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name,
                                            PageContext.APPLICATION_SCOPE);
        } else {
            throw new JspException("Invalid scope value '" + scope + "'");
        }
        if (bean == null) {
            throw new JspException("No bean '" + name + "' found");
        }
        if (property != null) {
            try {
                bean = PropertyUtils.getProperty(bean, property);
            } catch (Throwable t) {
                throw new JspException
                    ("Exception retrieving property '" + property + "': " + t);
            }
            if (bean == null) {
                throw new JspException("No property '" + property + "' found");
            }
        }

        // Convert to an object name as necessary
        ObjectName oname = null;
        try {
            if (bean instanceof ObjectName) {
                oname = (ObjectName) bean;
            } else if (bean instanceof String) {
                oname = new ObjectName((String) bean);
            } else {
                oname = new ObjectName(bean.toString());
            }
        } catch (Throwable t) {
            throw new JspException("Exception creating object name for '" +
                                   bean + "': " + t);
        }

        // Acquire a reference to our MBeanServer
        MBeanServer mserver =
            (MBeanServer) pageContext.getAttribute
            ("org.apache.catalina.MBeanServer", PageContext.APPLICATION_SCOPE);
        if (mserver == null)
            throw new JspException("MBeanServer is not available");

        // Retrieve the specified attribute from the specified MBean
        Object value = null;
        try {
            value = mserver.getAttribute(oname, attribute);
        } catch (Throwable t) {
            throw new JspException("Exception retrieving attribute '" +
                                   attribute + "' from mbean '" +
                                   oname.toString() + "'");
        }

        // Render this value to our current output writer
        if (value != null) {
            JspWriter out = pageContext.getOut();
            try {
                out.print(value);
            } catch (IOException e) {
                throw new JspException("IOException: " + e);
            }
        }

        // Evaluate the remainder of this page
        return (EVAL_PAGE);

    }


    /**
     * Release all current state.
     */
    public void release() {

        attribute = null;
        name = null;
        property = null;
        scope = null;

    }


}
