package com.android.wondercom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class MessageService extends Service{
	private static final int MESSAGE_PORT = 9000;
	private static final int MAX_MESSAGE_LENGTH = 256;
	private MessageListenerThread msgListenerThread;

	@Override    
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	if (msgListenerThread == null) {	
	    	msgListenerThread = new MessageListenerThread();
	    	msgListenerThread.start();
    	}
    	
    	return START_STICKY; // run until explicitly stopped    
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	
	private class MessageListenerThread extends Thread {
		
		public void run(){
			try {
				DatagramSocket socket = new DatagramSocket(MESSAGE_PORT);
				byte[] buff = new byte[MAX_MESSAGE_LENGTH];
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				
				while(true){
					packet.setLength(buff.length); 
					socket.receive(packet);
					String msg = new String(packet.getData(), 0, packet.getLength());
					
					Bundle extras = new Bundle();
					extras.putString("Message", msg);
					
					Intent intent = new Intent(getApplicationContext(), ViewMessageActivity.class);
					if (extras != null) {
			    		intent.putExtras(extras);
			    	}
					startActivity(intent);
					
					socket.close();
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
