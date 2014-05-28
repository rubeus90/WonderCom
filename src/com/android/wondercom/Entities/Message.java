package com.android.wondercom.Entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressWarnings("serial")
public class Message implements Serializable{
	public static final int TEXT_MESSAGE = 1;
	public static final int IMAGE_MESSAGE = 2;
	public static final int VIDEO_MESSAGE = 3;
	
	private int mType;
	private String mText;
	private String chatName = "Pseudo";
	private byte[] byteArray;
	private InetAddress senderAddress;
	
	public int getmType() { return mType; }
	public void setmType(int mType) { this.mType = mType; }
	public String getmText() { return mText; }
	public void setmText(String mText) { this.mText = mText; }
	public String getChatName() { return chatName; }
	public void setChatName(String chatName) { this.chatName = chatName; }
	public byte[] getByteArray() { return byteArray; }
	public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
	public InetAddress getSenderAddress() { return senderAddress; }
	public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
	
	
	public Message(int type, String text, InetAddress sender){
		mType = type;
		mText = text;	
		senderAddress = sender;
	}
	
	public byte[] bitmapToByteArray(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);    
		byte[] b = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public Bitmap byteArrayToBitmap(byte[] b){
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}
}
