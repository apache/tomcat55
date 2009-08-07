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
 */

package org.apache.catalina.tribes.group.interceptors;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.transport.bio.util.FastQueue;
import org.apache.catalina.tribes.transport.bio.util.LinkObject;

/**
 *
 * The message dispatcher is a way to enable asynchronous communication
 * through a channel. The dispatcher will look for the <code>Channel.SEND_OPTIONS_ASYNCHRONOUS</code>
 * flag to be set, if it is, it will queue the message for delivery and immediately return to the sender.
 * 
 * 
 * 
 * @author Filip Hanik
 * @version 1.0
 */
public class MessageDispatchInterceptor extends ChannelInterceptorBase implements Runnable {
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(MessageDispatchInterceptor.class);

    private long maxQueueSize = 1024*1024*64; //64MB
    private FastQueue queue = new FastQueue();
    private boolean run = false;
    private Thread msgDispatchThread = null;
    private AtomicLong currentSize = new AtomicLong(0);
    private boolean useDeepClone = false;

    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        boolean async = (msg.getOptions() & Channel.SEND_OPTIONS_ASYNCHRONOUS) == Channel.SEND_OPTIONS_ASYNCHRONOUS;
        if ( async && run ) {
            if ( (currentSize.get()+msg.getMessage().getLength()) > maxQueueSize ) throw new ChannelException("Asynchronous queue is full, reached its limit of "+maxQueueSize+" bytes, current:"+currentSize+" bytes.");
            //add to queue
            if ( useDeepClone ) msg = (ChannelMessage)msg.deepclone();
            if (!queue.add(msg, destination, payload) ) {
                throw new ChannelException("Unable to add the message to the async queue, queue bug?");
            }
            currentSize.addAndGet(msg.getMessage().getLength());
        } else {
            super.sendMessage(destination, msg, payload);
        }
    }
    
    public void setMaxQueueSize(long maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public void setUseDeepClone(boolean useDeepClone) {
        this.useDeepClone = useDeepClone;
    }

    public long getMaxQueueSize() {
        return maxQueueSize;
    }

    public boolean getUseDeepClone() {
        return useDeepClone;
    }

    public void start(int svc) throws ChannelException {
        //start the thread
        if (!run ) {
            synchronized (this) {
                if ( !run ) {
                    msgDispatchThread = new Thread(this);
                    msgDispatchThread.setName("MessageDispatchThread");
                    msgDispatchThread.setDaemon(true);
                    msgDispatchThread.setPriority(Thread.MAX_PRIORITY);
                    queue.setEnabled(true);
                    run = true;
                    msgDispatchThread.start();
                }//end if
            }//sync
        }//end if
        super.start(svc);
    }

    
    public void stop(int svc) throws ChannelException {
        //stop the thread
        if ( run ) {
            synchronized (this) {
                if ( run ) {
                    run = false;
                    queue.setEnabled(false);
                    msgDispatchThread.interrupt();
                    currentSize = new AtomicLong(0);
                }//end if
            }//sync
        }//end if

        super.stop(svc);
    }
    
    public void run() {
        while ( run ) {
            LinkObject link = queue.remove();
            if ( link == null ) continue; //should not happen unless we exceed wait time
            while ( link != null && run ) {
                ChannelMessage msg = link.data();
                Member[] destination = link.getDestination();
                try {
                    super.sendMessage(destination,msg,null);
                    try {
                        if ( link.getHandler() != null ) link.getHandler().handleCompletion(destination,msg); 
                    } catch ( Exception ex ) {
                        log.error("Unable to report back completed message.",ex);
                    }
                } catch ( Exception x ) {
                    if ( log.isDebugEnabled() ) log.debug("Error while processing async message.",x);
                    try {
                        if (link.getHandler() != null) link.getHandler().handleError(x, destination, msg);
                    } catch ( Exception ex ) {
                        log.error("Unable to report back error message.",ex);
                    }
                } finally {
                    currentSize.addAndGet(-msg.getMessage().getLength());
                    link = link.next();
                }//try
            }//while
        }//while
    }//run


}
