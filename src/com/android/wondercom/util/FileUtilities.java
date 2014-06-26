package com.android.wondercom.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

public class FileUtilities {

	//Save image from bitmap to the given file path
	public static void saveImageFromBitmap(Activity activity, Bitmap bm, String path, String fileName){
    	OutputStream fOut = null;
    	File file = new File(path, fileName);
    	try {
			fOut = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
	    	fOut.flush();
	    	fOut.close();
	    	Toast.makeText(activity, "Image downloaded to "+path+fileName, Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Copy a file from the source path to the given destination
	public static void copyFile(Activity activity, String sourcePath, String destinationPath, String fileName){
		File source = new File(sourcePath);
		File destination = new File(destinationPath + File.separator + fileName);
		
		try 
        {
            FileUtils.copyFile(source, destination);
            Toast.makeText(activity, "File downloaded to "+destinationPath, Toast.LENGTH_SHORT).show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	//Refresh the media library (only for pictures)
	public static void refreshMediaLibrary(Activity activity){
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
    	{
	        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        File f = new File("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
	        Uri contentUri = Uri.fromFile(f);
	        mediaScanIntent.setData(contentUri);
	        activity.sendBroadcast(mediaScanIntent);
    	}
    	else
    	{
    		activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    	} 
    }	
	
	//Create file name for the new record: composed of date and time of the record's beginning
	public static String fileName(){
		Calendar c = Calendar.getInstance();
		String fileName = c.get(Calendar.YEAR) + ""
				+ c.get(Calendar.MONTH)  + ""
				+ c.get(Calendar.DATE) + "_"
				+ c.get(Calendar.HOUR_OF_DAY)+ ""
				+ c.get(Calendar.MINUTE)+ ""
				+ c.get(Calendar.SECOND);
		return fileName;
	}
	
	public static Bitmap getBitmapFromFile(String path){
		File imgFile = new  File(path);
		if(imgFile.exists()){
		    return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}
		return null;
	}
}
