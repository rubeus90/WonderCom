package com.android.wondercom;

import java.net.InetAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	public static final String TAG = "MainActivity";	
	public static final String CONFS= "confs";
	
	private WifiManager mWifiManager;
    private WifiManagerNew mWifiManagerNew;
    private IntentFilter mFilter;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        /* Wrap WifiManager to access new methods */
        mWifiManagerNew = new WifiManagerNew(mWifiManager);

        /* Register broadcast receiver to get notified when Wifi has been enabled */
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN);

                    if (state == WifiManager.WIFI_STATE_ENABLED) {
                        log("Wifi state enabled");

                        /* This function only returns valid results in enabled
                         * state and is not part of the standard API (yet?) */
                        if (mWifiManagerNew.isIbssSupported()) {
                            log("Ad-hoc mode is supported");

                            configureAdhocNetwork();

                            log("Successfully configured Adhoc network!");
                        } else {
                            log("Sorry, Ad-hoc mode is not supported by your system or device!");
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
        mWifiManager.setWifiEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void log(String msg) {
        Log.d(TAG, msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    private void configureAdhocNetwork() {
        try {
            /* We use WifiConfigurationNew which provides a way to access 
             * the Ad-hoc mode and static IP configuration options which are 
             * not part of the standard API yet */
            WifiConfigurationNew wifiConfig = new WifiConfigurationNew();

            /* Set the SSID and security as normal */
            wifiConfig.SSID = "\"WonderCom\"";
            wifiConfig.allowedKeyManagement.set(KeyMgmt.NONE);

            /* Use reflection until API is official */
            wifiConfig.setIsIBSS(true);
            wifiConfig.setFrequency(2442);

            /* Use reflection to configure static IP addresses */
            wifiConfig.setIpAssignment("STATIC");
            wifiConfig.setIpAddress(InetAddress.getByName("10.0.0.2"), 24);
            wifiConfig.setGateway(InetAddress.getByName("10.0.0.1"));
            wifiConfig.setDNS(InetAddress.getByName("8.8.8.8"));

            /* Add, enable and save network as normal */
            int id = mWifiManager.addNetwork(wifiConfig);
            if (id < 0) {
                log("Failed to add Ad-hoc network");
            } else {
                mWifiManager.enableNetwork(id, true);
                mWifiManager.saveConfiguration();
            }
        } catch (Exception e) {
            log("Wifi configuration failed!");
            e.printStackTrace();
        }
    }
}