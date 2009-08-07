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

import java.io.File;
import java.io.IOException;

import javax.print.DocPrintJob;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;

/**
 * store StandardContext Attributes ... TODO DefaultContext Handling
 * 
 * @author Peter Rossbach
 *  
 */
public class StoreContextAppender extends StoreAppender {

    /*
     * Print Context Values. <ul><li> Spezial handling to default workDir.
     * </li><li> Don't save path at external context.xml </li><li> Don't
     * generate docBase for host.appBase webapps <LI></ul>
     * 
     * @see org.apache.catalina.config.StoreAppender#isPrintValue(java.lang.Object,
     *      java.lang.Object, java.lang.String,
     *      org.apache.catalina.config.StoreDescription)
     */
    public boolean isPrintValue(Object bean, Object bean2, String attrName,
            StoreDescription desc) {
        boolean isPrint = super.isPrintValue(bean, bean2, attrName, desc);
        if (isPrint) {
            StandardContext context = ((StandardContext) bean);
            if ("workDir".equals(attrName)) {
                String defaultWorkDir = getDefaultWorkDir(context);
                isPrint = !defaultWorkDir.equals(context.getWorkDir());
            } else if ("path".equals(attrName)) {
                isPrint = desc.isStoreSeparate() 
                            && desc.isExternalAllowed()
                            && context.getConfigFile() == null;
            } else if ("docBase".equals(attrName)) {
                Container host = context.getParent();
                if (host instanceof StandardHost) {
                    File appBase = getAppBase(((StandardHost) host));
                    File docBase = getDocBase(context,appBase);
                    isPrint = !appBase.equals(docBase.getParentFile());
                }
            }
        }
        return isPrint;
    }

    protected File getAppBase(StandardHost host) {

        File appBase;
        File file = new File(host.getAppBase());
        if (!file.isAbsolute())
            file = new File(System.getProperty("catalina.base"), host
                    .getAppBase());
        try {
            appBase = file.getCanonicalFile();
        } catch (IOException e) {
            appBase = file;
        }
        return (appBase);

    }

    protected File getDocBase(StandardContext context, File appBase) {

        File docBase;
        File file = new File(context.getDocBase());
        if (!file.isAbsolute())
            file = new File(appBase, context.getDocBase());
        try {
            docBase = file.getCanonicalFile();
        } catch (IOException e) {
            docBase = file;
        }
        return (docBase);

    }

    /**
     * Make default Work Dir
     * 
     * @param context
     * @return
     */
    protected String getDefaultWorkDir(StandardContext context) {
        String defaultWorkDir = null;
        String contextPath = context.getPath().length() == 0 ? "_" : context
                .getPath().substring(1);
        Container host = context.getParent();
        if (host instanceof StandardHost) {
            String hostWorkDir = ((StandardHost) host).getWorkDir();
            if (hostWorkDir != null) {
                defaultWorkDir = hostWorkDir + File.separator + contextPath;
            } else {
                String engineName = context.getParent().getParent().getName();
                String hostName = context.getParent().getName();
                defaultWorkDir = "work" + File.separator + engineName
                        + File.separator + hostName + File.separator
                        + contextPath;
            }
        }
        return defaultWorkDir;
    }

    /*
     * Generate a real default StandardContext TODO read and interpret the
     * default context.xml and context.xml.default TODO Cache a Default
     * StandardContext ( with reloading strategy) TODO remove really all
     * elements, but detection is hard... To Listener or Valve from same class?>
     * 
     * @see org.apache.catalina.storeconfig.StoreAppender#defaultInstance(java.lang.Object)
     */
    public Object defaultInstance(Object bean) throws InstantiationException,
            IllegalAccessException {
        if (bean instanceof StandardContext) {
            StandardContext defaultContext = new StandardContext();
            /*
             * if (!((StandardContext) bean).getOverride()) {
             * defaultContext.setParent(((StandardContext)bean).getParent());
             * ContextConfig contextConfig = new ContextConfig();
             * defaultContext.addLifecycleListener(contextConfig);
             * contextConfig.setContext(defaultContext);
             * contextConfig.processContextConfig(new File(contextConfig
             * .getBaseDir(), "conf/context.xml"));
             * contextConfig.processContextConfig(new File(contextConfig
             * .getConfigBase(), "context.xml.default")); }
             */
            return defaultContext;
        } else
            return super.defaultInstance(bean);
    }
}