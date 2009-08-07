package org.apache.catalina.tribes.test;

import java.net.*;
import java.io.*;
import org.apache.catalina.tribes.io.XByteBuffer;

public class ServerThread
    extends Thread {
    private Socket socket = null;
    private static int incounter = 0;
    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
    }
    
    public synchronized int incounter() {
        return ++incounter;
    }

    public void run() {

        try {
            this.socket.setSoLinger(false,0);
            System.out.println("Accepted:\n\tThread:"+Thread.currentThread().getName());
            OutputStream out = (socket.getOutputStream());
            InputStream in = socket.getInputStream();
            byte[] input = new byte[43800];
            byte[] outputLine;
            XByteBuffer buf = new XByteBuffer(input.length, true);
            AckProtocol ack = new AckProtocol();
            int length = 0;
            
            while ( (length = in.read(input)) >= 0) {
                buf.append(input, 0, length);
                if (buf.countPackages() > 0) {
                    outputLine = ack.processInput(buf,incounter());
                    out.write(outputLine);
                }
            }
            System.out.println("Finished:\n\tThread:"+Thread.currentThread().getName());
            out.close();
            in.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    
    public void printBytes(byte[] d, int offset, int length) {
        for (int i=offset; i<length; i++ ) {
            System.out.println("["+(i-offset)+"]="+d[i]);
        }
    }

}
