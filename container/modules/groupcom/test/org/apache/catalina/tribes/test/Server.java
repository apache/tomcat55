package org.apache.catalina.tribes.test;
import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("Usage: Server [port]");
        int port = 4444;
        if ( args.length > 0 ) port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Echo server is listening on port "+port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+port+".");
            System.exit(-1);
        }

        while (listening)
	    new ServerThread(serverSocket.accept()).start();

        serverSocket.close();
    }
}
