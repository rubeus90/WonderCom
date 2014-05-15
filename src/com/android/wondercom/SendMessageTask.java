package com.android.wondercom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SendMessageTask extends AsyncTask<String, Void, Void> {
	private static final String TAG = "SendMessageTask";
	private Context context;

	public SendMessageTask(Context context){
		context = context;
	}
	@Override
	protected Void doInBackground(String... params) {
		String address = params[0];
		String message = params[1];
		sendMessage(address, message);
		return null;
	}
	
	public void sendMessage(String address, String message){
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte buffer[] = message.getBytes();
			int messageLength = buffer.length;
			
			DatagramPacket packet = new DatagramPacket(buffer, messageLength, InetAddress.getByName(address), 9000);
			socket.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
			Log.e(TAG, "Problem creating socket");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.e(TAG, "Unknown host");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		Log.v(TAG, "Message sent");
	}
}
