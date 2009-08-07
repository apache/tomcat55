/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
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

package org.apache.catalina.tribes;


/**
 * Cluster Receiver Interface
 * @author Filip Hanik
 * @version $Revision: 379904 $, $Date: 2006-02-22 15:16:25 -0600 (Wed, 22 Feb 2006) $
 */
public interface ChannelReceiver {
    /**
     * Start message listing
     * @throws java.io.IOException
     */
    public void start() throws java.io.IOException;

    /**
     * Stop message listing 
     */
    public void stop();

    /**
     * get the listing ip interface
     * @return The host
     */
    public String getHost();
    
    
    /**
     * get the listing ip port
     * @return The port
     */
    public int getPort();
    
    public void setMessageListener(MessageListener listener);
    public MessageListener getMessageListener();

}
