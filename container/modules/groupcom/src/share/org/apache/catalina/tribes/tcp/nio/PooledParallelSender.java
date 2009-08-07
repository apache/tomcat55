package org.apache.catalina.tribes.tcp.nio;

import org.apache.catalina.tribes.tcp.PooledSender;
import org.apache.catalina.tribes.tcp.DataSender;
import org.apache.catalina.tribes.tcp.MultiPointSender;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import java.io.IOException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PooledParallelSender extends PooledSender implements MultiPointSender{
    private boolean suspect;
    private boolean useDirectBuffer;
    private int maxRetryAttempts;

    public PooledParallelSender() {
        super(25);
    }
    public void sendMessage(Member[] destination, ChannelMessage message) throws ChannelException {
        ParallelNioSender sender = (ParallelNioSender)getSender();
        try {
            sender.sendMessage(destination, message);
        }finally {
            returnSender(sender);
        }
    }

    public DataSender getNewDataSender() {
        try {
            ParallelNioSender sender = 
                new ParallelNioSender(getTimeout(), 
                                      getWaitForAck(), 
                                      getMaxRetryAttempts(), 
                                      useDirectBuffer,
                                      getRxBufSize(), 
                                      getTxBufSize());
            return sender;
        } catch ( IOException x ) {
            throw new IllegalStateException("Unable to open NIO selector.",x);
        }
    }

    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }

    public void setUseDirectBuffer(boolean useDirectBuffer) {
        this.useDirectBuffer = useDirectBuffer;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public boolean getSuspect() {
        return suspect;
    }

    public boolean getUseDirectBuffer() {
        return useDirectBuffer;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }
}