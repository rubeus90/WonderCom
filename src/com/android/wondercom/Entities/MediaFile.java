package com.android.wondercom.Entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class MediaFile {
	private String fileName;
	private File file;
	
	public String getFileName() { return fileName; 	}	
	

	public MediaFile(String fileURL){
		file = new File(fileURL);
		fileName = file.getName();
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
