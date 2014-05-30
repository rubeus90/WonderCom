package com.android.wondercom.AsyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.Entities.Message;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class ReceiveMessageClient extends AsyncTask<Void, Message, Void> {
	private static final int SERVER_PORT = 4446;
	private ChatActivity mActivity;
	private ServerSocket socket;

	public ReceiveMessageClient(ChatActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket destinationSocket = socket.accept();
				
				InputStream inputStream = destinationSocket.getInputStream();				
				ObjectInputStream objectIS = new ObjectInputStream(inputStream);
				Message message = (Message) objectIS.readObject();
				
				destinationSocket.close();
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
			socket.close();
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
		
		mActivity.refreshList(values[0], false);	
	}
	
	public void playNotificationSound(){
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mp = MediaPlayer.create(mActivity, notification);
		mp.start();
	}
}
