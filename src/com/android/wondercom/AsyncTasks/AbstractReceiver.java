package com.android.wondercom.AsyncTasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import com.android.wondercom.ChatActivity;
import com.android.wondercom.R;
import com.android.wondercom.Entities.Message;

public class AbstractReceiver extends AsyncTask<Void, Message, Void>{
	
	protected void playNotification(Context context, Message message){
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		Intent intent = new Intent(context, ChatActivity.class);		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		Notification mNotification = new Notification.Builder(context)
			.setContentTitle(message.getChatName())
			.setContentText(message.getmText())
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(pIntent)
			.setSound(notification)			
			.build();
		
//		.addAction(R.drawable.ic_launcher, "View", pIntent)
//		.addAction(0, "Remind", pIntent)
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		
		mNotificationManager.notify(0, mNotification);

	}

	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}
}
