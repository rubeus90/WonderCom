package com.android.wondercom;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

@SuppressWarnings("serial")
public class Message implements Serializable{
	public static final int TEXT_MESSAGE = 1;
	public static final int IMAGE_MESSAGE = 2;
	public static final int VIDEO_MESSAGE = 3;
	
	private int mType;
	private String mText;
//	private Bitmap mImage = null;
	private String chatName = "Pseudo";
	private Context mContext;
	private byte[] byteArray;
	
	public int getmType() { return mType; }
	public void setmType(int mType) { this.mType = mType; }
	public String getmText() { return mText; }
	public void setmText(String mText) { this.mText = mText; }
	public String getChatName() { return chatName; }
	public void setChatName(String chatName) { this.chatName = chatName; }
	
	
	public Message(Context context, int type, String text, Uri uri){
		mType = type;
		mText = text;
		mContext = context;
		if(uri != null){
			Bitmap bitmap = getBitmapFromURL(uri);
			byteArray = bitmapToByteArray(bitmap);
		}						
	}
	
	public Bitmap getBitmapFromURL(Uri uri) {
		InputStream input;
	    Bitmap bmp;
	    try {
	        input = mContext.getContentResolver().openInputStream(uri);
	        bmp = BitmapFactory.decodeStream(input);
	        return bmp;
	    } catch (FileNotFoundException e) {
	    	e.getStackTrace();
	    	return null;
	    }
		
//		try {
//			URLConnection connection = new URL("file://"+url).openConnection();
//			connection.connect();
//			InputStream inputStream = connection.getInputStream();
//			BufferedInputStream buffer = new BufferedInputStream(inputStream, 8192);
//			Bitmap bitmap = BitmapFactory.decodeStream(buffer);
//			return bitmap;
//			
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
	}
	
	public byte[] bitmapToByteArray(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
		byte[] b = baos.toByteArray();
		return b;
	}
	
	public Bitmap byteArrayToBitmap(byte[] byteArray){
		return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
	}
}
