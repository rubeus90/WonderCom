package com.android.wondercom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SendMessageClient extends AsyncTask<String, Void, Void>{
	private static final String TAG = "SendMessageClient";
	private Context mContext;
	private static final int SERVER_PORT = 4445;
	private InetAddress mServerAddr;
	
	public SendMessageClient(Context context, InetAddress serverAddr){
		mContext = context;
		mServerAddr = serverAddr;
	}
	
	@Override
	protected Void doInBackground(String... msg) {
		Log.v(TAG, "doInBackground");
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			Log.v(TAG, "doInBackground: connect succeeded");
			
			PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
			pw.write(msg[0]+"\n"); 
		    pw.flush(); 
		    Log.v(TAG, "doInBackground: connect succeeded");
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
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
		Toast.makeText(mContext, "Message sent", Toast.LENGTH_SHORT).show();
	}
}
