package com.android.wondercom;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressWarnings("serial")
public class Message implements Serializable{
	public static final int TEXT_MESSAGE = 1;
	public static final int IMAGE_MESSAGE = 2;
	public static final int VIDEO_MESSAGE = 3;
	
	private int mType;
	private String mText;
	private Bitmap mImage = null;
	
	public int getmType() { return mType; }
	public void setmType(int mType) { this.mType = mType; }
	public String getmText() { return mText; }
	public void setmText(String mText) { this.mText = mText; }
	public Bitmap getmImage() { return mImage; }
	public void setmImage(Bitmap mImage) { this.mImage = mImage; }
	
	
	public Message(int type, String text, String url){
		mType = type;
		mText = text;
		if(!url.equals("")){
			mImage = getBitmapFromURL(url);
		}		
	}
	
	public Bitmap getBitmapFromURL(String url) {
		
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			BufferedInputStream buffer = new BufferedInputStream(inputStream, 8192);
			Bitmap bitmap = BitmapFactory.decodeStream(buffer);
			return bitmap;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
