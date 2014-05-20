package com.android.wondercom;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity{
	public static final String TAG = "MainActivity";	
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private ArrayAdapter<String> mAdapter;
	private List<String> peersName;
	private List<WifiP2pDevice> peers;
	private boolean isGroupeOwner = false;
	private InetAddress ownerAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        //Initialise the list of peers and peers's names
        peersName = new ArrayList<String>();
        peers = new ArrayList<WifiP2pDevice>();
        
        //Initialise the list view which contains peer list
        ListView listView = (ListView) findViewById(R.id.listView);        
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peersName);
        listView.setAdapter(mAdapter);
        
        //Connect to the device when we click on the list
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				WifiP2pDevice device = peers.get(position);
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				
				mManager.connect(mChannel, config, new WifiP2pManager.ActionListener(){

					@Override
					public void onFailure(int arg0) {
						Toast.makeText(MainActivity.this, "Connect failed, please retry", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess() {
						Toast.makeText(MainActivity.this, "Waiting for peer accept", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
		});
        
        //Disconnect from the wifi direct group when long click on the connected device
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mManager.removeGroup(mChannel, null);
				Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
        
        //Send a message
        Button button = (Button) findViewById(R.id.sendMessage);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isGroupeOwner){
					new ReceiveMessageServer(MainActivity.this).execute();
				}
				else{
					new SendMessageClient(MainActivity.this, ownerAddr).execute("Hello world");
				}
			}
		});
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
					
			@Override
			public void onSuccess() {
				Log.v(TAG, "Discovery process succeeded");
			}
			
			@Override
			public void onFailure(int reason) {
				Log.v(TAG, "Discovery process failed");
			}
		});
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
        int idItem = item.getItemId();
        if (idItem == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public ArrayAdapter<String> getmAdapter() { return mAdapter; }

	public List<String> getPeersName() { return peersName; }

	public List<WifiP2pDevice> getPeers() { return peers; }

	public void setGroupeOwner(boolean isGroupeOwner) { this.isGroupeOwner = isGroupeOwner; }

	public void setOwnerAddr(InetAddress ownerAddr) { this.ownerAddr = ownerAddr; }
}