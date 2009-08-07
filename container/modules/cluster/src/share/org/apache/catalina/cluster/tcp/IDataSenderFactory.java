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
import org.apache.catalina.cluster.Member;
import java.net.InetAddress;

/**
 * @author Peter Rossbach
 * @version 1.0
 * @since 5.5.7
 */
public class IDataSenderFactory {
    private IDataSenderFactory() {
    }
    public static final String SYNC_MODE="synchronous";
    public static final String ASYNC_MODE="asynchronous";
    public static final String POOLED_SYNC_MODE="pooled";
    public static final String FAST_ASYNC_QUEUE_MODE="fastasyncqueue";

    public synchronized static IDataSender getIDataSender(String mode, Member mbr)
    throws java.io.IOException {
        if (SYNC_MODE.equals(mode) )
            return new SocketSender(InetAddress.getByName(mbr.getHost()),mbr.getPort());
        else if ( ASYNC_MODE.equals(mode) )
            return new AsyncSocketSender(InetAddress.getByName(mbr.getHost()),mbr.getPort());
        else if ( FAST_ASYNC_QUEUE_MODE.equals(mode) )
            return new FastAsyncSocketSender(InetAddress.getByName(mbr.getHost()),mbr.getPort());
        else if (POOLED_SYNC_MODE.equals(mode) )
            return new PooledSocketSender(InetAddress.getByName(mbr.getHost()),mbr.getPort());
        else
            throw new java.io.IOException("Invalid replication mode="+mode);
    }

    public static String validateMode(String mode) {
        if (SYNC_MODE.equals(mode) ||
            ASYNC_MODE.equals(mode) ||
            FAST_ASYNC_QUEUE_MODE.equals(mode) ||
            POOLED_SYNC_MODE.equals(mode) ) {
            return null;
        } else {
            return "Replication mode has to be '"+SYNC_MODE+"', '" + FAST_ASYNC_QUEUE_MODE +"', '"+ASYNC_MODE+"' or '"+POOLED_SYNC_MODE+"'";
        }
    }


}
