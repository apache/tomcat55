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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.startup.SetAllPropertiesRule;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.SAXException;

/**
 * @author Peter Rossbach
 */
public class StoreAppenderTest extends TestCase {

    /**
     * Create the digester which will be used to parse context config files.
     */
    protected Digester createDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("Resource",
                "org.apache.catalina.deploy.ContextResource");
        digester.addRule("Resource", new SetAllPropertiesRule());
        return digester;
    }

    public void testNormalResource() throws IOException, SAXException {
        Digester digester = createDigester();
        String example = "<Resource auth=\"Container\" name=\"jdbc/Emp\" type=\"javax.sql.DataSource\"/>";
        StringReader reader = new StringReader(example);
        ContextResource resource = (ContextResource) digester.parse(reader);
        assertNotNull(resource);
        assertEquals("javax.sql.DataSource", resource.getType());
    }

    public void testPropertyResouce() throws IOException, SAXException {
        Digester digester = createDigester();
        String example = "<Resource auth=\"Container\" name=\"mail/MailSession\" type=\"javax.mail.session\" mail.host=\"localhost\"/>";
        StringReader reader = new StringReader(example);
        ContextResource resource = (ContextResource) digester.parse(reader);
        assertNotNull(resource);
        assertEquals("localhost", resource.getProperty("mail.host"));
    }

    public void testStoreStandard() throws Exception {
        StoreDescription desc = new StoreDescription();
        desc.setStandard(true);
        PrintWriter writer = new PrintWriter(new StringWriter());
        StandardServer bean = new StandardServer();
        new StoreAppender().printAttributes(writer, 0, true, bean, desc);
    }

    public void testStoreReplicationTransmitter() throws Exception {
        StoreDescription desc = new StoreDescription();
        desc.setStandard(true);
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        ReplicationTransmitter bean = new ReplicationTransmitter();
        bean.setReplicationMode("asynchronous");
        bean.setProperty("keepAliveTimeout","80000");
        new ReplicationTransmitterStoreAppender().printAttributes(writer, 0, true, bean, desc);
        String aspectedResult =LF.LINE_SEPARATOR           
           + "    replicationMode=\"asynchronous\"" + LF.LINE_SEPARATOR 
           + "    keepAliveTimeout=\"80000\"" ;
        assertEquals(aspectedResult, swriter.getBuffer().toString());

    }

}