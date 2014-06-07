package com.android.wondercom;

import java.io.IOException;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoChatReceiverActivity extends Activity implements SurfaceHolder.Callback {
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private RtspClient mClient;
	private Session mSession;
	private MediaPlayer mMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_chat_receiver);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_receiver);
	    mSurfaceHolder = mSurfaceView.getHolder();
	    mSurfaceHolder.addCallback(this);
		
		
		
//		VideoView mVideoView = (VideoView) findViewById(R.id.videoView);
//		mVideoView.setVideoPath("rtsp://192.168.49.1:1234/");
//		mVideoView.setMediaController(new MediaController(this));
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
	        mMediaPlayer = new MediaPlayer();	        
	        mMediaPlayer.setDataSource("rtsp://192.168.1.109:1234");
	        mMediaPlayer.prepare();
	        mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
				}
			});
	        mMediaPlayer.setDisplay(mSurfaceHolder);
	        mMediaPlayer.setSurface(arg0.getSurface());
	        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        mMediaPlayer.setScreenOnWhilePlaying(true);
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    } catch (SecurityException e) {
	        e.printStackTrace();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
	}
}
