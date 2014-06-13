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
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.android.wondercom.AsyncTasks.SendMessageClient;
import com.android.wondercom.AsyncTasks.SendMessageServer;
import com.android.wondercom.CustomAdapters.ChatAdapter;
import com.android.wondercom.Entities.Image;
import com.android.wondercom.Entities.MediaFile;
import com.android.wondercom.Entities.Message;
import com.android.wondercom.Receivers.WifiDirectBroadcastReceiver;

public class ChatActivity extends Activity {
	public static final String TAG = "ChatActivity";	
	public static final int PICK_IMAGE = 1;
	public static final int TAKE_PHOTO = 2;
	public static final int RECORD_AUDIO = 3;
	
	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private EditText edit;
	private static ListView listView;
	private static List<HashMap<String, Object>> listMessage;
	private static ChatAdapter chatAdapter;
	private Uri imageUri;
	private String fileURL;
	
	
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
        
        //Initialize the adapter for the chat
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
        
        //Register the context menu to the list view (for pop up menu)
        registerForContextMenu(listView);
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
			case TAKE_PHOTO:
				if (resultCode == RESULT_OK && data.getData() != null) {
					imageUri = data.getData();
					sendMessage(Message.IMAGE_MESSAGE);
				}
				break;
			case RECORD_AUDIO:
				if (resultCode == RESULT_OK) {
					fileURL = (String) data.getStringExtra("audioPath");
					sendMessage(Message.AUDIO_MESSAGE);
				}
				break;
		}
	}
	
	// Hydrate Message object then launch the AsyncTasks to send it
	public void sendMessage(int type){
		Message mes = new Message(type, edit.getText().toString(), null, MainActivity.chatName);
		
		switch(type){
			case Message.IMAGE_MESSAGE:
				Image image = new Image(this, imageUri);
				Log.v(TAG, "Bitmap from url ok");
				mes.setByteArray(image.bitmapToByteArray(image.getBitmapFromUri()));
				mes.setFileName(image.getFileName());
				mes.setFileSize(image.getFileSize());
				Log.v(TAG, "Set byte array to image ok");
				break;
			case Message.AUDIO_MESSAGE:
				MediaFile mediaFile = new MediaFile(this, fileURL, Message.AUDIO_MESSAGE);
				mes.setByteArray(mediaFile.fileToByteArray());
				mes.setFileName(mediaFile.getFileName());
				mes.setFilePath(mediaFile.getFilePath());
				break;
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
	
	// Refresh the message list
	public static void refreshList(Message message, boolean isMine){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("type", message.getmType());
		map.put("chatName", message.getChatName());
		map.put("isMine", isMine);
		map.put("text", message.getmText());	
		
		if(message.getmType() == Message.IMAGE_MESSAGE){
			map.put("fileName", message.getFileName());
			map.put("fileSize", message.getFileSize());
			map.put("image", message.byteArrayToBitmap(message.getByteArray()));
			Log.v(TAG, "Set image to listMessage ok ");
		}
		else if(message.getmType() == Message.AUDIO_MESSAGE){
			map.put("filePath", message.getFilePath());
			map.put("fileName", message.getFileName());
		}
		
		listMessage.add(map);
    	chatAdapter.notifyDataSetChanged();
    	
    	//Scroll to the last element of the list
    	listView.setSelection(listMessage.size() - 1);
    }	

	// Save the app's state (foreground or background) to a SharedPrefereces
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

	// Handle click on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch(idItem){
	        case R.id.send_image:
	        	showPopup(edit);
	        	return true;
	        	
	        case R.id.send_audio:
	        	Log.v(TAG, "Start activity to record audio");
	        	startActivityForResult(new Intent(this, RecordAudioActivity.class), RECORD_AUDIO);
	        	return true;
	        	
	        case R.id.send_video:
	        	return true;
	        	
	        case R.id.send_file:
	        	return true;
	        	
	        default:
	        	return super.onOptionsItemSelected(item);        	
        }  
    }	
    
    //Show the popup menu
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch(item.getItemId()){
				case R.id.pick_image:
					Log.v(TAG, "Pick an image");
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, PICK_IMAGE);
					break;
				
				case R.id.take_photo:
					Log.v(TAG, "Take a photo");
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				    startActivityForResult(intent2, TAKE_PHOTO);
				    break;
				}
				return true;
			}
		});
        popup.inflate(R.menu.send_image);
        popup.show();
    }
    
    //Create pop up menu for image download, delete message, etc...
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup, menu);
    }
    
    //Handle click event on the pop up menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.download_image:
            	downloadImage(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    //Download image and save it to the app's external directory
    public void downloadImage(long id){
    	HashMap<String,Object> hash = listMessage.get((int) id);
    	MediaStore.Images.Media.insertImage(getContentResolver(), (Bitmap) hash.get("image") ,
    		    (String) hash.get("fileName"), (String) hash.get("fileName"));
    }
}