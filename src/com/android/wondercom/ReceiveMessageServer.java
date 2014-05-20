package com.android.wondercom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ReceiveMessageServer extends AsyncTask<Void, Void, String>{
	private static final int SERVER_PORT = 4445;
	private Context mContext;

	public ReceiveMessageServer(Context context){
		mContext = context;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		System.out.println("coucou");
		String message="";
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			Socket clientSocket = serverSocket.accept();
			System.out.println("je passe");
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			message = br.readLine();
			serverSocket.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return message;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	}

}
