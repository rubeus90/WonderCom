package com.android.wondercom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SendMessageServer extends AsyncTask<Message, Void, Message>{
	private static final String TAG = "SendMessageServer";
	private ChatActivity mActivity;
	private static final int SERVER_PORT = 4446;	

	public SendMessageServer(ChatActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected Message doInBackground(Message... msg) {
		Log.v(TAG, "doInBackground");
		
		try {			
			ArrayList<InetAddress> listClients = ServerInit.clients;
			for(InetAddress addr : listClients){
				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);
				Log.v(TAG,"Connect to client: " + addr.getHostAddress());
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));
				Log.v(TAG, "doInBackground: connect to "+ addr.getHostAddress() +" succeeded");
				
				OutputStream outputStream = socket.getOutputStream();
				
				new ObjectOutputStream(outputStream).writeObject(msg[0]);
				
			    Log.v(TAG, "doInBackground: write to "+ addr.getHostAddress() +" succeeded");
			    socket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Erreur d'envoie du message");
		}
		
		return msg[0];
	}

	@Override
	protected void onPostExecute(Message result) {
		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
		Toast.makeText(mActivity, "Message sent", Toast.LENGTH_SHORT).show();
		
		mActivity.refreshList(result);	
	}
}