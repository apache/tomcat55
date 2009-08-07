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


package org.apache.catalina.deploy;

import java.io.Serializable;
import java.util.Iterator;
import java.util.HashMap;


/**
 * Representation of an EJB resource reference for a web application, as
 * represented in a <code>&lt;ejb-ref&gt;</code> element in the
 * deployment descriptor.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ContextEjb implements Serializable {


    // ------------------------------------------------------------- Properties


    /**
     * The description of this EJB.
     */
    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * The name of the EJB home implementation class.
     */
    private String home = null;

    public String getHome() {
        return (this.home);
    }

    public void setHome(String home) {
        this.home = home;
    }


    /**
     * The link to a J2EE EJB definition.
     */
    private String link = null;

    public String getLink() {
        return (this.link);
    }

    public void setLink(String link) {
        this.link = link;
    }


    /**
     * The name of this EJB.
     */
    private String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * The name of the EJB remote implementation class.
     */
    private String remote = null;

    public String getRemote() {
        return (this.remote);
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }


    /**
     * The name of the EJB bean implementation class.
     */
    private String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
    }


    /**
     * Holder for our configured properties.
     */
    private HashMap properties = new HashMap();

    /**
     * Return a configured property.
     */
    public Object getProperty(String name) {
        return properties.get(name);
    }

    /**
     * Set a configured property.
     */
    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    /** 
     * remove a configured property.
     */
    public void removeProperty(String name) {
        properties.remove(name);
    }

    /**
     * List properties.
     */
    public Iterator listProperties() {
        return properties.keySet().iterator();
    }
    
    
    // --------------------------------------------------------- Public Methods


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ContextEjb[");
        sb.append("name=");
        sb.append(name);
        if (description != null) {
            sb.append(", description=");
            sb.append(description);
        }
        if (type != null) {
            sb.append(", type=");
            sb.append(type);
        }
        if (home != null) {
            sb.append(", home=");
            sb.append(home);
        }
        if (remote != null) {
            sb.append(", remote=");
            sb.append(remote);
        }
        if (link != null) {
            sb.append(", link=");
            sb.append(link);
        }
        sb.append("]");
        return (sb.toString());

    }


    // -------------------------------------------------------- Package Methods


    /**
     * The NamingResources with which we are associated (if any).
     */
    protected NamingResources resources = null;

    public NamingResources getNamingResources() {
        return (this.resources);
    }

    void setNamingResources(NamingResources resources) {
        this.resources = resources;
    }


}
