package com.android.wondercom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.AsyncTask;
import android.widget.Toast;

public class ReceiveMessageServer extends AsyncTask<Void, String, Void>{
	private static final int SERVER_PORT = 4445;
	private ChatActivity mActivity;
	private ServerSocket serverSocket;

	public ReceiveMessageServer(ChatActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		String message="";
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket clientSocket = serverSocket.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				message = br.readLine();
				clientSocket.close();
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
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Toast.makeText(mActivity, values[0], Toast.LENGTH_SHORT).show();
		new SendMessageServer(mActivity).executeOnExecutor(THREAD_POOL_EXECUTOR, values);
	}
	
}
