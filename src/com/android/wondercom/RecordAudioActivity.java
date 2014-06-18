package com.android.wondercom;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class RecordAudioActivity extends Activity {
	private static final String TAG = "RecordAudioActivity";
	private ImageView buttonRecord;
	private ImageView buttonPlay;
	private ImageView buttonOk;
	private MediaRecorder mRecorder;
	private String mFileName;
	private MediaPlayer mPlayer;
	private boolean isRecording = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_audio);
		
		mFileName = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        mFileName += "/" + fileName() + ".3gp";
        
        buttonRecord = (ImageView) findViewById(R.id.record_audio);
        buttonPlay = (ImageView) findViewById(R.id.play_audio);
        buttonOk = (ImageView) findViewById(R.id.ok);
        buttonPlay.setVisibility(View.GONE);
        buttonOk.setVisibility(View.GONE);
        
		buttonRecord.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!isRecording){
					isRecording = true;
					buttonRecord.setImageDrawable(getResources().getDrawable(R.drawable.microphone_stop));
					buttonPlay.setVisibility(View.GONE);
			        buttonOk.setVisibility(View.GONE);
					startRecording();					
				}
				else{
					isRecording = false;
					buttonRecord.setImageDrawable(getResources().getDrawable(R.drawable.microphone_start));
					stopRecording();
					buttonPlay.setVisibility(View.VISIBLE);
			        buttonOk.setVisibility(View.VISIBLE);
				}
			}
		});
		
		buttonPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPlaying();
			}
		});
		
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("audioPath", mFileName);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	@Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
	
	// Create file name for the new record: composed of date and time of the record's beginning
	public String fileName(){
		Calendar c = Calendar.getInstance();
		String fileName = c.get(Calendar.YEAR) + ""
				+ c.get(Calendar.MONTH)  + ""
				+ c.get(Calendar.DATE) + "_"
				+ c.get(Calendar.HOUR_OF_DAY)+ ""
				+ c.get(Calendar.MINUTE)+ ""
				+ c.get(Calendar.SECOND);
		return fileName;
	}
	
	public void startRecording(){
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
	}
	
	public void stopRecording(){
		mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
	}
	
	public void startPlaying(){
		mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
	}
	
	public void stopPlaying(){
		mPlayer.release();
        mPlayer = null;
	}
}
