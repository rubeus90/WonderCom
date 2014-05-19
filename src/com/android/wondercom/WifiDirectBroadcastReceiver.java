package com.android.wondercom;

import java.net.InetAddress;

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

public class WifiDirectBroadcastReceiver extends BroadcastReceiver{
	private WifiP2pManager mManager;
	private Channel mChannel;
	private MainActivity mActivity;
	
	public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, MainActivity activity){
		super();
		mManager = manager;
		mChannel = channel;
		mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println(action);
		
		if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){ //Wifi P2P is enabled or disabled
			//check if Wifi P2P is supported
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				Toast.makeText(mActivity, "Wifi P2P is supported by this device", Toast.LENGTH_SHORT).show();
			} else{
				Toast.makeText(mActivity, "Wifi P2P is not supported by this device", Toast.LENGTH_SHORT).show();
			}
		}
		
		else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){ //available peer list has changed
			if(mManager != null){
				mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
					
					@Override
					public void onPeersAvailable(WifiP2pDeviceList peerList) {
						mActivity.getPeersName().clear();
						for(WifiP2pDevice device : peerList.getDeviceList()){
							mActivity.getPeersName().add(device.deviceName);
							mActivity.getPeers().add(device);
						}
						mActivity.getmAdapter().notifyDataSetChanged();
					}
				});
			}
		}
		
		else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){ //this device's wifi state has changed
			
		}
		
		else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){ //state of connectivity has changed (new connection/disconnection)
			System.out.println("hey");
			if(mManager == null){
				return;
			}
			System.out.println("you");
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if(networkInfo.isConnected()){
				System.out.println("coucou");
				mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
					
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						InetAddress groupOwnerAddress = info.groupOwnerAddress;
						
						if (info.groupFormed && info.isGroupOwner) { //The GO : create a server thread and accept incoming connections
							Toast.makeText(mActivity, "I'm the group owner  " + groupOwnerAddress.getHostAddress(), Toast.LENGTH_SHORT).show();
						}
						else if (info.groupFormed) { //The client : create a client thread that connects to the group owner
							Toast.makeText(mActivity, "I'm the client", Toast.LENGTH_SHORT).show();
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
