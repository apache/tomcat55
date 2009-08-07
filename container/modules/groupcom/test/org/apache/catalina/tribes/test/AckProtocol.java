package org.apache.catalina.tribes.test;

import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.io.ClusterData;
public class AckProtocol {
    public byte[] processInput(XByteBuffer buf, int counter) throws Exception {
        ClusterData data = buf.extractPackage(true);
        System.out.println("Received:\n\tThread:"+Thread.currentThread().getName()+"\n\tCount:"+counter+"\n\tData:"+new String(data.getMessage().getBytes()));
        return org.apache.catalina.tribes.transport.Constants.ACK_COMMAND;
    }

}
