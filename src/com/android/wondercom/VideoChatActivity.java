package com.android.wondercom;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.video.VideoQuality;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

public class VideoChatActivity extends Activity {
	private SurfaceView mSurfaceView;
	private RtspServer mRtspServer;
	private Session mSession;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_chat);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surface);
		
		// Sets the port of the RTSP server to 1234
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(RtspServer.KEY_PORT, String.valueOf(1234));
		editor.commit();

		// Configures the SessionBuilder
		SessionBuilder builder = SessionBuilder.getInstance()
				.setSurfaceView(mSurfaceView)
				.setPreviewOrientation(90)
				.setContext(getApplicationContext())
				.setAudioEncoder(SessionBuilder.AUDIO_AMRNB)
				.setVideoEncoder(SessionBuilder.VIDEO_H264)
				.setAudioQuality(new AudioQuality(16000, 32000))
				.setVideoQuality(new VideoQuality(320,240,20,500000));
		
		Session session = builder.build();
		session.start();
		session.configure();
		
		// Starts the RTSP server
		this.startService(new Intent(getApplicationContext(), RtspServer.class));
	}
	
	public void onStart() {
		super.onStart();
		bindService(new Intent(this, RtspServer.class), mRtspServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mRtspServer != null) mRtspServer.removeCallbackListener(mRtspCallbackListener);
    	this.unbindService(mRtspServiceConnection);
	}
	
	@Override
    public void onResume() {
    	super.onResume();
		this.bindService(new Intent(this, RtspServer.class), mRtspServiceConnection, Context.BIND_AUTO_CREATE);
    }
	
	public void update() {
		if ((mRtspServer != null && mRtspServer.isStreaming()))
			System.out.println("Streaming");
		else 
			System.out.println("NOT Streaming");
	}
	
	private ServiceConnection mRtspServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRtspServer = (RtspServer) ((RtspServer.LocalBinder)service).getService();
			mRtspServer.addCallbackListener(mRtspCallbackListener);
			mRtspServer.start();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {}

	};
	
	private RtspServer.CallbackListener mRtspCallbackListener = new RtspServer.CallbackListener() {

		@Override
		public void onError(RtspServer server, Exception e, int error) {
		}

		@Override
		public void onMessage(RtspServer server, int message) {
			update();
		}

	};
}
