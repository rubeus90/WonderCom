package com.android.wondercom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	public static final String CONFS= "confs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        if(isFirstLaunch()){
        	Log.i("Copy wpa_supplicant", "First launch, copy the file!");
        	createExternalStoragePrivateFile();
            copyWifiConfigFile();
            memorizeFirstLaunch();
            reboot();
        }
        else{
        	Log.i("Copy wpa_supplicant", "Not first launch, skip copying");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void gainSuperuserRight(){
    	try {
			Runtime.getRuntime().exec("su");			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void createExternalStoragePrivateFile() {
        File file = new File(getExternalFilesDir(null), "wpa_supplicant");

        try {
        	AssetManager assetManager = getAssets();
        	InputStream input = assetManager.open("wpa_supplicant");
            OutputStream output = new FileOutputStream(file);
            byte[] data = new byte[input.available()];
            input.read(data);
            output.write(data);
            input.close();
            output.close();
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }
    
    public void copyWifiConfigFile(){
    	try {
			Runtime.getRuntime().exec( "mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system" );
			Runtime.getRuntime().exec( "cp /system/bin/wpa_supplicant "+ getExternalFilesDir(null)+ "/wpa_supplicant.original" );
			Runtime.getRuntime().exec( "cp "+ getExternalFilesDir(null) +"/wpa_supplicant /system/bin/." );
			Runtime.getRuntime().exec( "chmod 755 /system/bin/wpa_supplicant" );
			Runtime.getRuntime().exec( "chown system.wifi /data/misc/wifi/wpa_supplicant.conf" );
			Runtime.getRuntime().exec( "exit" );			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void reboot(){
    	try{
    		Runtime.getRuntime().exec("reboot");
    	} catch( IOException e){
    		e.printStackTrace();
    	}
    }
    
    public void memorizeFirstLaunch(){
    	SharedPreferences settings = getSharedPreferences(CONFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstLaunch", false);
        editor.commit();
    }
    
    public boolean isFirstLaunch(){
    	SharedPreferences settings = getSharedPreferences(CONFS, 0);
        return settings.getBoolean("firstLaunch", true);
    }
}
