package com.android.wondercom.AsyncTasks;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.Entities.Message;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SendMessageClient extends AsyncTask<Message, Void, Message>{
	private static final String TAG = "SendMessageClient";
	private ChatActivity mActivity;
	private static final int SERVER_PORT = 4445;
	private InetAddress mServerAddr;
	
	public SendMessageClient(ChatActivity activity, InetAddress serverAddr){
		mActivity = activity;
		mServerAddr = serverAddr;
	}
	
	@Override
	protected Message doInBackground(Message... msg) {
		Log.v(TAG, "doInBackground");
		Socket socket = new Socket();
		try {
			socket.setReuseAddress(true);
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			Log.v(TAG, "doInBackground: connect succeeded");
			
			OutputStream outputStream = socket.getOutputStream();
			
			new ObjectOutputStream(outputStream).writeObject(msg[0]);
			
		    Log.v(TAG, "doInBackground: send message succeeded");
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
	protected void onPostExecute(Message result) {
		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
		Toast.makeText(mActivity, "Message sent", Toast.LENGTH_SHORT).show();
		mActivity.refreshList(result);
	}
}
