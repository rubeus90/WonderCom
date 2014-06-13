package com.android.wondercom.Entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class MediaFile {
	private static final String TAG = "MediaFile";
	private String fileName;
	private String filePath;
	private File file;
	
	public String getFileName() { return fileName; 	}	
	public String getFilePath() { return filePath; }

	public MediaFile(Context context, String fileURL, int type){
		file = new File(fileURL);
		fileName = file.getName();
		
		switch(type){
			case Message.AUDIO_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() 
								+ "/" + fileName;
				break;
			case Message.FILE_MESSAGE:
				filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() 
								+ "/" + fileName;
				break;
		}		
	}
	
	public byte[] fileToByteArray(){
		Log.v(TAG, "Convert media file to byte array");
		FileInputStream input;
		try {
			input = new FileInputStream(file);
			byte[] array = IOUtils.toByteArray(input);
			return array;
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
