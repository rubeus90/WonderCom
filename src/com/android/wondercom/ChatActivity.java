package com.android.wondercom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
	public static final String TAG = "ChatActivity";	
	public static final int PICK_IMAGE = 1;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private EditText edit;
	private ListView listView;
	private List<HashMap<String, Object>> listMessage;
	private ChatAdapter chatAdapter;
	private Uri imageUri;
	
	
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
        listMessage = new ArrayList<HashMap<String, Object>>();
        chatAdapter = new ChatAdapter(this, listMessage);
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
				sendMessage(Message.TEXT_MESSAGE);
			}
		});
        
        //Pick an image
        Log.v(TAG, "Pick an image");
        Button pickImage = (Button) findViewById(R.id.pickImage);
        pickImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, PICK_IMAGE);
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
		android.os.Process.killProcess(android.os.Process.myPid());
	}
    
    //Return the Uri of the picked image
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case PICK_IMAGE:
				if (resultCode == RESULT_OK && data.getData() != null) {
					imageUri = data.getData();
					sendMessage(Message.IMAGE_MESSAGE);
				}
		}
	}

	public void refreshList(Message message){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("type", message.getmType());
		map.put("chatName", message.getChatName());
		
		if(message.getmType() == Message.TEXT_MESSAGE){
			map.put("text", message.getmText());
		}		
		else if(message.getmType() == Message.IMAGE_MESSAGE){
			map.put("image", message.byteArrayToBitmap(message.getByteArray()));
			Log.v(TAG, "Set image to listMessage ok ");
		}
		
		listMessage.add(map);
    	chatAdapter.notifyDataSetChanged();
    	
    	listView.setSelection(listMessage.size() - 1);
    }	
	
	public void sendMessage(int type){
		Message mes = new Message(type, edit.getText().toString(), null);
		
		if(type == Message.IMAGE_MESSAGE){
			Image image = new Image(this, imageUri);
			Log.v(TAG, "Bitmap from url ok");
			mes.setByteArray(mes.bitmapToByteArray(image.getBitmapFromUri()));
			Log.v(TAG, "Set byte array to image ok");
		}
		
		if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
			Log.v(TAG, "SendMessageServer start");
			new SendMessageServer(ChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
		else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
			Log.v(TAG, "SendMessageClient start");
			new SendMessageClient(ChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}		
		
		edit.setText("");
	}
}