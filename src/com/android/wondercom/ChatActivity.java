package com.android.wondercom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.wondercom.AsyncTasks.SendMessageClient;
import com.android.wondercom.AsyncTasks.SendMessageServer;
import com.android.wondercom.CustomAdapters.ChatAdapter;
import com.android.wondercom.Entities.Image;
import com.android.wondercom.Entities.Message;
import com.android.wondercom.Receivers.WifiDirectBroadcastReceiver;

public class ChatActivity extends Activity {
	public static final String TAG = "ChatActivity";	
	public static final int PICK_IMAGE = 1;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private EditText edit;
	private static ListView listView;
	private static List<HashMap<String, Object>> listMessage;
	private static ChatAdapter chatAdapter;
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
        
        //Start the service to receive message
        startService(new Intent(this, MessageService.class));
        
        //Itilialise the adapter for the chat
        listView = (ListView) findViewById(R.id.messageList);
        listMessage = new ArrayList<HashMap<String, Object>>();
        chatAdapter = new ChatAdapter(this, listMessage);
        listView.setAdapter(chatAdapter);
        
		//Send a message
        Button button = (Button) findViewById(R.id.sendMessage);
        edit = (EditText) findViewById(R.id.editMessage);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!edit.getText().toString().equals("")){
					Log.v(TAG, "Send message");
					sendMessage(Message.TEXT_MESSAGE);
				}				
				else{
					Toast.makeText(ChatActivity.this, "Please enter a not empty message", Toast.LENGTH_SHORT).show();
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
		saveStateForeground(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        saveStateForeground(false);
    }    
    
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(MainActivity.server!=null){
			MainActivity.server.interrupt();
		}		
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
				break;
		}
	}

	public static void refreshList(Message message, boolean isMine){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("type", message.getmType());
		map.put("chatName", message.getChatName());
		map.put("isMine", isMine);
		
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
		Message mes = new Message(type, edit.getText().toString(), null, MainActivity.chatName);
		
		if(type == Message.IMAGE_MESSAGE){
			Image image = new Image(this, imageUri);
			Log.v(TAG, "Bitmap from url ok");
			mes.setByteArray(mes.bitmapToByteArray(image.getBitmapFromUri()));
			Log.v(TAG, "Set byte array to image ok");
		}
		
		if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
			Log.v(TAG, "SendMessageServer start");
			new SendMessageServer(ChatActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}
		else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
			Log.v(TAG, "SendMessageClient start");
			new SendMessageClient(ChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
		}		
		
		edit.setText("");
	}
	
	public void saveStateForeground(boolean isForeground){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
  		Editor edit = prefs.edit();
  		edit.putBoolean("isForeground", isForeground);
  		edit.commit();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        if (idItem == R.id.send_image) {
        	Log.v(TAG, "Pick an image");
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PICK_IMAGE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }	
}