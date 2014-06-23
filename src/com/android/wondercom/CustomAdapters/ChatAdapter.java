package com.android.wondercom.CustomAdapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.PlayVideoActivity;
import com.android.wondercom.R;
import com.android.wondercom.ViewImageActivity;
import com.android.wondercom.Entities.Message;

public class ChatAdapter extends BaseAdapter {
	private List<Message> listMessage;
	private LayoutInflater inflater;
	public static Bitmap bitmap;
	private Context mContext;
	private HashMap<String,Bitmap> mapThumb;

	public ChatAdapter(Context context, List<Message> listMessage){
		this.listMessage = listMessage;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mapThumb = new HashMap<String, Bitmap>();
	}
	
	@Override
	public int getCount() {
		return listMessage.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		Message mes = listMessage.get(position);
		int type = mes.getmType();
		
		if(view == null){
			CacheView cache = new CacheView();            
            
			view = inflater.inflate(R.layout.chat_row, null);
			cache.chatName = (TextView) view.findViewById(R.id.chatName);
            cache.text = (TextView) view.findViewById(R.id.text);
            cache.image = (ImageView) view.findViewById(R.id.image);
            cache.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
            cache.audioPlayer = (ImageView) view.findViewById(R.id.playAudio);
            cache.videoPlayer = (ImageView) view.findViewById(R.id.playVideo);
            cache.fileSaved = (TextView) view.findViewById(R.id.fileSaved);
            cache.videoPlayerButton = (ImageView) view.findViewById(R.id.buttonPlayVideo);
            cache.fileSavedIcon = (ImageView) view.findViewById(R.id.file_attached_icon);
	            
			view.setTag(cache);
		}
		
		//Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();
        cache.chatName.setText(listMessage.get(position).getChatName());
        cache.chatName.setTag(cache);
        cache.chatName.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				CacheView cache = (CacheView) v.getTag();
				((ChatActivity)mContext).talkTo((String) cache.chatName.getText());
				return true;
			}
		});
        
        //Colourise differently own message
        if((Boolean) listMessage.get(position).isMine()){
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble_mine));
        }   
        else{
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble));
        }
        
        //We disable all the views and enable certain views depending on the message's type
        disableAllMediaViews(cache);
        
        /***********************************************
          				Text Message
         ***********************************************/
        if(type == Message.TEXT_MESSAGE){           
        	enableTextView(cache, mes.getmText());
		}
        
        /***********************************************
			            Image Message
         ***********************************************/
		else if(type == Message.IMAGE_MESSAGE){
			if(!mes.getmText().equals("")){
				enableTextView(cache, mes.getmText());
			}
			cache.image.setVisibility(View.VISIBLE);
			
			if(!mapThumb.containsKey(mes.getFileName())){
				Bitmap thumb = mes.byteArrayToBitmap(mes.getByteArray());
				mapThumb.put(mes.getFileName(), thumb);				
			}
			cache.image.setImageBitmap(mapThumb.get(mes.getFileName()));
			cache.image.setTag(position);
			
			cache.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message mes = listMessage.get((Integer) v.getTag());
					bitmap = mes.byteArrayToBitmap(mes.getByteArray());
					
					Intent intent = new Intent(mContext, ViewImageActivity.class);
					String fileName = mes.getFileName();
					intent.putExtra("fileName", fileName);
					
					mContext.startActivity(intent);
				}
			});
		}     
        
        /***********************************************
        				Audio Message
         ***********************************************/
		else if(type == Message.AUDIO_MESSAGE){
			if(!mes.getmText().equals("")){
				enableTextView(cache, mes.getmText());
			}
			cache.audioPlayer.setVisibility(View.VISIBLE);
			cache.audioPlayer.setTag(position);
			cache.audioPlayer.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					MediaPlayer mPlayer = new MediaPlayer();
					Message mes = listMessage.get((Integer) v.getTag());
			        try {			        	
			            mPlayer.setDataSource(mes.getFilePath());
			            mPlayer.prepare();
			            mPlayer.start();
			            
			            //Disable the button when the audio is playing
			            v.setEnabled(false);
			            ((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_audio_in_progress));
			            
			            mPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								//Re-enable the button when the audio has finished playing
								v.setEnabled(true);
								((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_audio));
							}
						});
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
					
				}
			});
		}
        
        /***********************************************
        				Video Message
         ***********************************************/
		else if(type == Message.VIDEO_MESSAGE){
			if(!mes.getmText().equals("")){
				enableTextView(cache, mes.getmText());
			}
			cache.videoPlayer.setVisibility(View.VISIBLE);
			cache.videoPlayerButton.setVisibility(View.VISIBLE);
			
			if(!mapThumb.containsKey(mes.getFilePath())){
				Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mes.getFilePath(), Thumbnails.MINI_KIND);
				mapThumb.put(mes.getFilePath(), thumb);				
			}
			cache.videoPlayer.setImageBitmap(mapThumb.get(mes.getFilePath()));
			
			cache.videoPlayer.setTag(position);
			cache.videoPlayer.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message mes = listMessage.get((Integer) v.getTag());
					Intent intent = new Intent(mContext, PlayVideoActivity.class);
					intent.putExtra("filePath", mes.getFilePath());
					mContext.startActivity(intent);
				}
			});
		}
        
        /***********************************************
						File Message
         ***********************************************/
		else if(type == Message.FILE_MESSAGE){
			if(!mes.getmText().equals("")){
				enableTextView(cache, mes.getmText());
			}
			cache.fileSavedIcon.setVisibility(View.VISIBLE);
			cache.fileSaved.setVisibility(View.VISIBLE);
			cache.fileSaved.setText(mes.getFileName());
//			if(mes.isMine())
//				cache.fileSaved.setText("File \"" + mes.getFileName() + "\" is sent succesfully");
//			else
//				cache.fileSaved.setText("File \"" + mes.getFileName() + "\" received");
		}
        
		return view;
	}
	
	private void disableAllMediaViews(CacheView cache){
		cache.text.setVisibility(View.GONE);
		cache.image.setVisibility(View.GONE);
		cache.audioPlayer.setVisibility(View.GONE);
		cache.videoPlayer.setVisibility(View.GONE);
		cache.fileSaved.setVisibility(View.GONE);
		cache.videoPlayerButton.setVisibility(View.GONE);
		cache.fileSavedIcon.setVisibility(View.GONE);
	}
	
	private void enableTextView(CacheView cache, String text){
		cache.text.setVisibility(View.VISIBLE);
		cache.text.setText(text);
		Linkify.addLinks(cache.text, Linkify.PHONE_NUMBERS);
		Linkify.addLinks(cache.text, Patterns.WEB_URL, "myweburl:");
	}

	//Cache
	private static class CacheView{
		public TextView chatName;
		public TextView text;
		public ImageView image;
		public RelativeLayout relativeLayout;
		public ImageView audioPlayer;
		public ImageView videoPlayer;
		public ImageView videoPlayerButton;
		public ImageView fileSavedIcon;
		public TextView fileSaved;
	}
}
