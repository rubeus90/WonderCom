package com.android.wondercom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

import org.span.manager.ManetManagerApp;
import org.span.service.ManetObserver;
import org.span.service.core.ManetService.AdhocStateEnum;
import org.span.service.routing.Node;
import org.span.service.system.ManetConfig;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements ManetObserver {
	public static final String TAG = "MainActivity";
	
	public static final String CONFS= "confs";
	private static ManetManagerApp app = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
//        gainSuperuserRight();
//        if(isFirstLaunch()){
//        	Log.i("First configuration", "First launch!");
//            memorizeFirstLaunch();
            app = (ManetManagerApp)getApplication();
            app.manet.registerObserver(this);
            
            app.manet.sendStartAdhocCommand();
//        }
//        else{
//        	Log.i("First configuration", "Not first launch!");
//        }
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
    	File sdCard = Environment.getExternalStorageDirectory();
    	File dir = new File (sdCard.getAbsolutePath() + "/WonderCom");
    	dir.mkdirs();
    	
    	try {
			Runtime.getRuntime().exec( "mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system" );
			Runtime.getRuntime().exec( "cp /system/bin/wpa_supplicant " + sdCard.getAbsolutePath() + "/WonderCom/wpa_supplicant.original" );
			Runtime.getRuntime().exec( "cp /storage/sdcard0/wpa_supplicant /system/bin/" );
			Runtime.getRuntime().exec( "chmod 755 /system/bin/wpa_supplicant" );
			Runtime.getRuntime().exec( "chown system.wifi /data/misc/wifi/wpa_supplicant.conf" );		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void reboot(){
    	try{
    		Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
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


	@Override
	public void onServiceConnected() {
		Log.d(TAG, "onServiceConnected()"); 
 		app.manet.sendManetConfigQuery();
 		app.manet.sendAdhocStatusQuery();
	}


	@Override
	public void onServiceDisconnected() {
 		Log.d(TAG, "onServiceDisconnected()"); 
	}


	@Override
	public void onServiceStarted() {
		Log.d(TAG, "onServiceStarted()");
	}


	@Override
	public void onServiceStopped() {
		Log.d(TAG, "onServiceStopped()");
	}


	@Override
	public void onAdhocStateUpdated(AdhocStateEnum state, String info) {
		Log.d(TAG, "onAdhocStateUpdated()"); // DEBUG
//		showAdhocMode(state);
		app.displayToastMessage(info);
	}


	@Override
	public void onConfigUpdated(ManetConfig manetcfg) {
		Log.d(TAG, "onConfigUpdated()"); // DEBUG
//		showRadioMode(manetcfg.isUsingBluetooth());
//		displayIPandSSID(manetcfg);
	}


	@Override
	public void onPeersUpdated(HashSet<Node> peers) {
		Log.d(TAG, "onPeersUpdated()");
	}


	@Override
	public void onRoutingInfoUpdated(String info) {
	}


	@Override
	public void onError(String error) {
		Log.d(TAG, "onError()");
	}
}
