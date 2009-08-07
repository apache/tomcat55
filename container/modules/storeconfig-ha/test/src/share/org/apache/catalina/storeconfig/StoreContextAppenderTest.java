/*
 * Created on 24.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.catalina.storeconfig;

import junit.framework.TestCase;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;
import java.io.File ;

/**
 * @author Peter Rossbach
 *  
 */
public class StoreContextAppenderTest extends TestCase {

    StoreContextAppender appender = new StoreContextAppender();

    StandardContext context = new StandardContext();

    StandardHost host = new StandardHost();

    /*
     * setup default Engine, Host and Context
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        host.setName("localhost");
        context.setParent(host);
        StandardEngine engine = new StandardEngine();
        engine.setName("Catalina");
        host.setParent(engine);
        super.setUp();
    }

    public void testWorkDirManager() {
        context.setPath("/manager");
        String defaultDir = appender.getDefaultWorkDir(context);
        assertEquals("work\\Catalina\\localhost\\manager", defaultDir);

    }

    public void testWorkDirRoot() {
        context.setPath("");
        String defaultDir = appender.getDefaultWorkDir(context);
        assertEquals("work\\Catalina\\localhost\\_", defaultDir);
    }

    public void testHostWorkDirRoot() {
        context.setPath("");
        host.setWorkDir("hostwork");
        String defaultDir = appender.getDefaultWorkDir(context);
        assertEquals("hostwork\\_", defaultDir);
    }

    public void testIsPrintValueDefault() {
        StandardContext context2 = new StandardContext();
        context.setPath("");
        context.setWorkDir("work\\Catalina\\localhost\\_");
        assertFalse(appender.isPrintValue(context, context2, "workDir", null));
    }

    public void testIsPrintValue() {
        StandardContext context2 = new StandardContext();
        context.setPath("");
        context.setWorkDir("C:\\work\\Catalina\\localhost\\_");
        assertTrue(appender.isPrintValue(context, context2, "workDir", null));
    }

    public void testHostIsPrintValuedefault() {
        StandardContext context2 = new StandardContext();
        context.setPath("");
        host.setWorkDir("hostwork");
        context.setWorkDir("hostwork\\_");
        assertFalse(appender.isPrintValue(context, context2, "workDir", null));
    }

    public void _testDefaultInstance() throws Exception {
        assertTrue(context.getCookies());
        assertFalse(context.getReloadable());
        StandardContext defaultContext = (StandardContext) appender
                .defaultInstance(context);
        assertFalse(defaultContext.getCookies());
        assertTrue(defaultContext.getReloadable());
        assertEquals(2, defaultContext.findLifecycleListeners().length);
        assertTrue(defaultContext.findLifecycleListeners()[0] instanceof ContextConfig);
        assertTrue(defaultContext.findLifecycleListeners()[1] instanceof InfoLifecycleListener);
    }

    public void _testDefaultInstanceWithoutOverride() throws Exception {
        context.setOverride(true);
        StandardContext defaultContext = (StandardContext) appender
                .defaultInstance(context);
        assertEquals(0, defaultContext.findLifecycleListeners().length);

    }
    
    public void testPath() throws Exception {
        StandardContext defaultContext = (StandardContext) appender
        .defaultInstance(context);
        context.setPath("/myapps");
        assertNull(context.getConfigFile());
        StoreDescription desc = new StoreDescription();
        desc.setExternalAllowed(true);
        desc.setStoreSeparate(true);
        assertTrue(appender.isPrintValue(context, defaultContext, "path", desc));
        context.setConfigFile("conf/Catalina/locahost/myapps.xml");
        assertFalse(appender.isPrintValue(context, defaultContext, "path", desc));
        desc.setExternalAllowed(false);
        assertFalse(appender.isPrintValue(context, defaultContext, "path", desc));
        desc.setExternalAllowed(true);
        desc.setStoreSeparate(false);
        assertFalse(appender.isPrintValue(context, defaultContext, "path", desc));
    }
    
    public void testDocBase() throws Exception {
        StandardContext defaultContext = (StandardContext) appender
        .defaultInstance(context);
        context.setPath("/myapps");
        context.setDocBase("myapps");
        host.setAppBase("webapps");
        assertFalse(appender.isPrintValue(context, defaultContext, "docBase", null));
        context.setDocBase(System.getProperty("java.io.tmpdir") + "/myapps");
        assertTrue(appender.isPrintValue(context, defaultContext, "docBase", null));
        
    }
}