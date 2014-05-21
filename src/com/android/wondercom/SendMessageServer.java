package com.android.wondercom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SendMessageServer extends AsyncTask<String, Void, String>{
	private static final String TAG = "SendMessageServer";
	private ChatActivity mActivity;
	private static final int SERVER_PORT = 4446;
	
	public SendMessageServer(ChatActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected String doInBackground(String... msg) {
		Log.v(TAG, "doInBackground");
		
		try {			
			ArrayList<InetAddress> listClients = ServerInit.clients;
			for(InetAddress addr : listClients){
				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);
				System.out.println("New client " + addr.getHostAddress());
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));
				Log.v(TAG, "doInBackground: connect to "+ addr.getHostAddress() +" succeeded");
				
				PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
				pw.write(msg[0]+"\n"); 
			    pw.flush(); 
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
	protected void onPostExecute(String result) {
		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
		Toast.makeText(mActivity, "Message sent", Toast.LENGTH_SHORT).show();
		mActivity.getMessages().add(result);
		mActivity.getChatAdapter().notifyDataSetChanged();
		mActivity.getListView().setSelection(mActivity.getMessages().size() - 1);
	}
	

}