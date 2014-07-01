package com.android.wondercom.AsyncTasks;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.MainActivity;
import com.android.wondercom.Entities.Message;

public class SendMessageClient extends AsyncTask<Message, Message, Message>{
	private static final String TAG = "SendMessageClient";
	private Context mContext;
	private static final int SERVER_PORT = 4445;
	private InetAddress mServerAddr;
	
	public SendMessageClient(Context context, InetAddress serverAddr){
		mContext = context;
		mServerAddr = serverAddr;
	}
	
	@Override
	protected Message doInBackground(Message... msg) {
		Log.v(TAG, "doInBackground");
		
		//Display le message on the sender before sending it
		publishProgress(msg);
		
		//Send the message
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
	protected void onProgressUpdate(Message... msg) {
		super.onProgressUpdate(msg);
		
		if(isActivityRunning(MainActivity.class)){
			ChatActivity.refreshList(msg[0], true);
		}
	}

	@Override
	protected void onPostExecute(Message result) {
		Log.v(TAG, "onPostExecute");
		super.onPostExecute(result);
	}
	
	@SuppressWarnings("rawtypes")
	public Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
	}
}
