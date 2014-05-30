package com.android.wondercom.AsyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.Entities.Message;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class ReceiveMessageServer extends AsyncTask<Void, Message, Void>{
	private static final int SERVER_PORT = 4445;
	private ChatActivity mActivity;
	private ServerSocket serverSocket;

	public ReceiveMessageServer(ChatActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket clientSocket = serverSocket.accept();				
				
				InputStream inputStream = clientSocket.getInputStream();				
				ObjectInputStream objectIS = new ObjectInputStream(inputStream);
				Message message = (Message) objectIS.readObject();
				
				//Add the InetAdress of the sender to the message
				InetAddress senderAddr = clientSocket.getInetAddress();
				message.setSenderAddress(senderAddr);
				
				clientSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
		playNotificationSound();
		
		String text = values[0].getmText();
		Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
		
		new SendMessageServer(mActivity, false).executeOnExecutor(THREAD_POOL_EXECUTOR, values);
	}
	
	public void playNotificationSound(){
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mp = MediaPlayer.create(mActivity, notification);
		mp.start();
	}
	
}
