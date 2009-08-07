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

package org.apache.catalina.tribes.tcp;

import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelException;

/**
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version $Revision: 303993 $ $Date: 2005-07-16 16:05:54 -0500 (Sat, 16 Jul 2005) $
 * @since 5.5.7
 */

public interface SinglePointSender extends DataSender
{
    public void setAddress(java.net.InetAddress address);
    public java.net.InetAddress getAddress();
    public void setPort(int port);
    public int getPort();
    public void sendMessage(ChannelMessage data) throws ChannelException;
    public void setSuspect(boolean suspect);
    public boolean getSuspect();
    public String getDomain() ;
    public void setDomain(String domain) ;

}
