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

package org.apache.catalina.cluster.tcp;

/**
 * @author Peter Rossbach
 * @version 1.0
 * @since 5.5.7
 */

public interface IDataSender
{
    public java.net.InetAddress getAddress();
    public int getPort();
    public void connect() throws java.io.IOException;
    public void disconnect();
    public void sendMessage(String sessionId, byte[] data) throws java.io.IOException;
    public boolean isConnected();
    public void setSuspect(boolean suspect);
    public boolean getSuspect();
    public void setAckTimeout(long timeout);
    public long getAckTimeout();
    public boolean isWaitForAck();
    public void setWaitForAck(boolean isWaitForAck);

}
