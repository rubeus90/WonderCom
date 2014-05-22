package com.android.wondercom;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * This activity is the launcher activity. It shows a list of available peers.
 * When the user choose one, the connection will be established.
 * Once the connection established, the ChatActivity is launched.
 */
public class MainActivity extends ActionBarActivity{
	public static final String TAG = "MainActivity";	
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
//	public static ArrayAdapter<String> mAdapter; //peer list adapter
	private Button goToChat;
	private Button goToSettings;

	//Getters and Setters
    public WifiP2pManager getmManager() { return mManager; }
	public Channel getmChannel() { return mChannel; }
	public WifiDirectBroadcastReceiver getmReceiver() { return mReceiver; }
	public IntentFilter getmIntentFilter() { return mIntentFilter; }
//	public Button getGoToChat() { return goToChat; }	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
//        if (getIntent().getBooleanExtra("EXIT", false)) {
//            finish();
//            return;
//        }
        
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmManager(mManager);
        mReceiver.setmChannel(mChannel);
        mReceiver.setmActivity(this);
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        
        
//        //Initialise the list view which contains peer list
//        ListView listView = (ListView) findViewById(R.id.listView);        
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mReceiver.getPeersName());
//        listView.setAdapter(mAdapter);
//        
//        //Connect to the device when we click on the list
//        listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//				WifiP2pDevice device = mReceiver.getPeers().get(position);
//				WifiP2pConfig config = new WifiP2pConfig();
//				config.deviceAddress = device.deviceAddress;
//				config.wps.setup = WpsInfo.PBC;
//				
//				mManager.connect(mChannel, config, new WifiP2pManager.ActionListener(){
//
//					@Override
//					public void onSuccess() {
//						Toast.makeText(MainActivity.this, "Waiting for peer accept", Toast.LENGTH_SHORT).show();
//					}
//					
//					@Override
//					public void onFailure(int arg0) {
//						Toast.makeText(MainActivity.this, "Connect failed, please retry", Toast.LENGTH_SHORT).show();						
//					}
//				});
//			}
//		});
//        
//        //Disconnect from the wifi direct group when long click on the connected device
//        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
////				mManager.removeGroup(mChannel, null);
////				Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
//				if(mReceiver.isGroupeOwner()){
//					ServerInit server = new ServerInit();
//					server.start();
//				}
//				else{
//					ClientInit client = new ClientInit(mReceiver.getOwnerAddr());
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					client.start();
//				}
//				return true;
//			}
//		});  
        
        //Button Go to Settings
        goToSettings = (Button) findViewById(R.id.goToSettings);
        goToSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				goToSettings();
			}
		});
        
        //Button Go to Chat
        goToChat = (Button) findViewById(R.id.goToChat);
        goToChat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.IS_OWNER){
					Toast.makeText(MainActivity.this, "I'm the group owner  " + mReceiver.getOwnerAddr().getHostAddress(), Toast.LENGTH_SHORT).show();
					ServerInit server = new ServerInit();
					server.start();
				}
				else if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.IS_CLIENT){
					Toast.makeText(MainActivity.this, "I'm the client", Toast.LENGTH_SHORT).show();
					ClientInit client = new ClientInit(mReceiver.getOwnerAddr());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					client.start();
				}
				else if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.GROUP_NOT_FORMED){
					Toast.makeText(MainActivity.this, "Error: The group is not formed", Toast.LENGTH_SHORT).show();
				}
				
				goToChat();		
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
    
    public void goToChat(){
    	Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
		startActivity(intent);
    }
    
    public void goToSettings(){
    	//Open Wifi direct settings
        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
    }
    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    }
}