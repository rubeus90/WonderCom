package com.android.wondercom;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.widget.Toast;

/*
 * This class implements the Singleton pattern
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver{
	private WifiP2pManager mManager;
	private Channel mChannel;
	private Activity mActivity;
	private List<String> peersName = new ArrayList<String>();
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private boolean isGroupeOwner = false;
	private InetAddress ownerAddr;
	private ServerInit server;
	private ClientInit client;
	
	private static WifiDirectBroadcastReceiver instance;
	
	private WifiDirectBroadcastReceiver(){
		super();
	}
	
	public static WifiDirectBroadcastReceiver createInstance(){
		if(instance == null){
			instance = new WifiDirectBroadcastReceiver();
		}
		return instance;
	}
	
	public List<String> getPeersName() { return peersName; }
	public List<WifiP2pDevice> getPeers() { return peers; }
	public boolean isGroupeOwner() { return isGroupeOwner; }
	public InetAddress getOwnerAddr() { return ownerAddr; }
	public void setmManager(WifiP2pManager mManager) { this.mManager = mManager; }
	public void setmChannel(Channel mChannel) { this.mChannel = mChannel; }
	public void setmActivity(Activity mActivity) { this.mActivity = mActivity; }

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		/**********************************
		 Wifi P2P is enabled or disabled 
		**********************************/
		if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){ 
			//check if Wifi P2P is supported
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				Toast.makeText(mActivity, "Wifi P2P is supported by this device", Toast.LENGTH_SHORT).show();
			} else{
				Toast.makeText(mActivity, "Wifi P2P is not supported by this device", Toast.LENGTH_SHORT).show();
			}
		}
		
		/**********************************
		 Available peer list has changed
		**********************************/
		else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){ 
			if(mManager != null){
				mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
					
					@Override
					public void onPeersAvailable(WifiP2pDeviceList peerList) {
						peersName.clear();
						for(WifiP2pDevice device : peerList.getDeviceList()){
							peersName.add(device.deviceName);
							peers.add(device);
						}
						MainActivity.mAdapter.notifyDataSetChanged();
					}
				});
			}
		}
		
		/***************************************
		 This device's wifi state has changed 
		***************************************/
		else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){ //
			
		}
		
		/******************************************************************
		 State of connectivity has changed (new connection/disconnection) 
		******************************************************************/
		else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
			if(mManager == null){
				return;
			}
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if(networkInfo.isConnected()){
				mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
					
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						InetAddress groupOwnerAddress = info.groupOwnerAddress;
						ownerAddr= groupOwnerAddress;
						
						/******************************************************************
						 The GO : create a server thread and accept incoming connections 
						******************************************************************/
						if (info.groupFormed && info.isGroupOwner) { 
							isGroupeOwner = true;
							Toast.makeText(mActivity, "I'm the group owner  " + groupOwnerAddress.getHostAddress(), Toast.LENGTH_SHORT).show();
							
							if(server==null){
								server = new ServerInit();
								server.start();
							}							
						}
						
						/******************************************************************
						 The client : create a client thread that connects to the group owner 
						******************************************************************/
						else if (info.groupFormed) { 
							isGroupeOwner = false;
							Toast.makeText(mActivity, "I'm the client", Toast.LENGTH_SHORT).show();
							
							if(client==null){
								client = new ClientInit(groupOwnerAddress);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								client.start();
							}							
						}		
						else{
							Toast.makeText(mActivity, "Error: The group is not formed", Toast.LENGTH_SHORT).show();
						}
					}
				});				
			}
		}
	}

}
