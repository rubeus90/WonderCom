package com.android.wondercom.Entities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

@SuppressWarnings("serial")
public class Message implements Serializable{
	public static final int TEXT_MESSAGE = 1;
	public static final int IMAGE_MESSAGE = 2;
	public static final int VIDEO_MESSAGE = 3;
	public static final int AUDIO_MESSAGE = 4;
	public static final int FILE_MESSAGE = 5;
	
	private int mType;
	private String mText;
	private String chatName;
	private byte[] byteArray;
	private InetAddress senderAddress;
	private String fileName;
	private long fileSize;
	private String filePath;
	
	//Getters and Setters
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
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }
	public long getFileSize() { return fileSize; }
	public void setFileSize(long fileSize) { this.fileSize = fileSize; }
	public String getFilePath() { return filePath; }
	public void setFilePath(String filePath) { this.filePath = filePath; }
	
	
	
	public Message(int type, String text, InetAddress sender, String name){
		mType = type;
		mText = text;	
		senderAddress = sender;
		chatName = name;
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
	
	public void saveByteArrayToFile(Context context){
		filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName;
		File file = new File(filePath);

		if (file.exists()) {
			file.delete();
		}

		try {
			FileOutputStream fos=new FileOutputStream(file.getPath());

			fos.write(byteArray);
			fos.close();
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
