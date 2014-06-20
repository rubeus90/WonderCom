package com.android.wondercom.Entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MediaFile {
	private static final String TAG = "MediaFile";
	private String fileName;
	private String filePath;
	private File file;
	
	public String getFileName() { return fileName; 	}	
	public String getFilePath() { return filePath; }

	public MediaFile(Context context, String fileURL){
		file = new File(fileURL);
		fileName = file.getName();
		filePath = fileURL;
	}
	
	public byte[] fileToByteArray(){
		Log.v(TAG, "Convert media file to byte array");
		FileInputStream input;
		try {
			input = new FileInputStream(file);
			byte[] array = IOUtils.toByteArray(input);
			Log.v(TAG, "Convert media file to byte array DONE !");
			return array;
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
