package com.android.wondercom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerInit extends Thread{
	private static final int SERVER_PORT = 4444;
	public static ArrayList<InetAddress> clients;
	
	public ServerInit(){
		clients = new ArrayList<InetAddress>();
	}

	@Override
	public void run() {
		clients.clear();
	    
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			// Collect client ip's
		    while(true) {
		       Socket clientSocket = serverSocket.accept();
		       clients.add(clientSocket.getInetAddress());
		       clientSocket.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
