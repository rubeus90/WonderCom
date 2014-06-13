package com.android.wondercom.Entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.os.Environment;

public class MediaFile {
	private String fileName;
	private String filePath;
	private File file;
	
	public String getFileName() { return fileName; 	}	
	public String getFilePath() { return filePath; }

	public MediaFile(Context context, String fileURL){
		file = new File(fileURL);
		fileName = file.getName();
		filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName;
	}
	
	public byte[] fileToByteArray(){
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
