package com.android.wondercom;

import com.android.wondercom.AsyncTasks.ReceiveMessageClient;
import com.android.wondercom.AsyncTasks.ReceiveMessageServer;
import com.android.wondercom.Receivers.WifiDirectBroadcastReceiver;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class MessageService extends Service {
	private static final String TAG = "MessageService";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		WifiDirectBroadcastReceiver mReceiver = WifiDirectBroadcastReceiver.createInstance();
		
		//Start the AsyncTask for the server to receive messages
        if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
        	Log.v(TAG, "Start the AsyncTask for the server to receive messages");
        	new ReceiveMessageServer(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
        else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
        	Log.v(TAG, "Start the AsyncTask for the client to receive messages");
        	new ReceiveMessageClient(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
		return START_STICKY;
	}
}
