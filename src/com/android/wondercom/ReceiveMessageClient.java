package com.android.wondercom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ReceiveMessageClient extends AsyncTask<Void, String, Void> {
	private static final int SERVER_PORT = 4446;
	private Context mContext;
	private ServerSocket socket;

	public ReceiveMessageClient(Context context){
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		String message="";
		try {
			socket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket destinationSocket = socket.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(destinationSocket.getInputStream()));
				message = br.readLine();
				destinationSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return null;
	}

	@Override
	protected void onCancelled() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
		ChatActivity activity = (ChatActivity) mContext;
		activity.getMessages().add(values[0]);
		activity.getChatAdapter().notifyDataSetChanged();
		activity.getListView().setSelection(activity.getMessages().size() - 1);
	}
}
