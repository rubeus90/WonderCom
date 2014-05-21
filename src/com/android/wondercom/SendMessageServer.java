package com.android.wondercom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SendMessageServer extends AsyncTask<String, Void, String>{
	private static final String TAG = "SendMessageServer";
	private Context mContext;
	private static final int SERVER_PORT = 4446;
	
	public SendMessageServer(Context context){
		mContext = context;
	}
	
	@Override
	protected String doInBackground(String... msg) {
		Log.v(TAG, "doInBackground");
		Socket socket = new Socket();
		try {
			socket.setReuseAddress(true);
			socket.bind(null);
			
			ArrayList<InetAddress> listClients = ServerInit.clients;
			for(InetAddress addr : listClients){
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));
				Log.v(TAG, "doInBackground: connect to "+ addr.getHostAddress() +" succeeded");
				
				PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
				pw.write(msg[0]+"\n"); 
			    pw.flush(); 
			    Log.v(TAG, "doInBackground: write to "+ addr.getHostAddress() +" succeeded");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		            	e.printStackTrace();
		            }
		        }
		    }
		}
		
		return msg[0];
	}

	@Override
	protected void onPostExecute(String result) {
		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
		Toast.makeText(mContext, "Message sent", Toast.LENGTH_SHORT).show();
		((ChatActivity) mContext).getMessages().add(result);
		((ChatActivity) mContext).getChatAdapter().notifyDataSetChanged();
	}
	

}