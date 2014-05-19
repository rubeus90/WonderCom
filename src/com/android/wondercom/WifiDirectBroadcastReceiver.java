package com.android.wondercom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
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
		
		else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){ //state of connectivity has changed (new connection/disconnection)
			
		}
		
		else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){ //this device's wifi state has changed
			
		}
	}

}
