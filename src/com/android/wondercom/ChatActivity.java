package com.android.wondercom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
	public static final String TAG = "ChatActivity";	
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private EditText edit;
	private ArrayAdapter<String> chatAdapter;
	private List<String> messages;
	private ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmActivity(this);
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        //Itilialise the adapter for the chat
        listView = (ListView) findViewById(R.id.messageList);
        messages = new ArrayList<String>();
        chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(chatAdapter);
        
        //Start the AsyncTask for the server to receive messages
        Log.v(TAG, "Start the AsyncTask for the server to receive messages");
        if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
        	new ReceiveMessageServer(ChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
        else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
        	new ReceiveMessageClient(ChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
        
		//Send a message
        Log.v(TAG, "Send message");
        Button button = (Button) findViewById(R.id.sendMessage);
        edit = (EditText) findViewById(R.id.editMessage);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
					Log.v(TAG, "SendMessageServer start");
					new SendMessageServer(ChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, edit.getText().toString());
					edit.setText("");
				}
				else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
					Log.v(TAG, "SendMessageClient start");
					new SendMessageClient(ChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, edit.getText().toString());
					edit.setText("");
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
	public void onBackPressed() {
		super.onBackPressed();
//		Intent intent = new Intent(ChatActivity.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("EXIT", true);
//        startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void refreshList(String newMessage){
    	messages.add(newMessage);
    	chatAdapter.notifyDataSetChanged();
    	listView.setSelection(messages.size() - 1);
    }
}