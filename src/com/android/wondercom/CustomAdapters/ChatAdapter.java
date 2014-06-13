package com.android.wondercom.CustomAdapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.wondercom.R;
import com.android.wondercom.ViewImageActivity;
import com.android.wondercom.Entities.Message;

public class ChatAdapter extends BaseAdapter {
	private List<HashMap<String,Object>> listMessage;
	private LayoutInflater inflater;
	public static Bitmap bitmap;
	private Context mContext;

	public ChatAdapter(Context context, List<HashMap<String, Object>> listMessage){
		this.listMessage = listMessage;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
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
		
		int type = (Integer) listMessage.get(position).get("type");
		
		if(view == null){
			CacheView cache = new CacheView();            
            
			view = inflater.inflate(R.layout.chat_row, null);
			cache.chatName = (TextView) view.findViewById(R.id.chatName);
            cache.text = (TextView) view.findViewById(R.id.text);
            cache.image = (ImageView) view.findViewById(R.id.image);
            cache.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
            cache.audioPlayer = (Button) view.findViewById(R.id.playAudio);
	            
			view.setTag(cache);
		}
		
		//Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();
        cache.chatName.setText((String)listMessage.get(position).get("chatName"));
        
        //Colourise differently own message
        if((Boolean) listMessage.get(position).get("isMine")){
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble_mine));
        }   
        else{
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble));
        }
        
        if(type == Message.TEXT_MESSAGE){        	
        	cache.image.setVisibility(View.GONE);
        	cache.audioPlayer.setVisibility(View.GONE);
        	cache.text.setVisibility(View.VISIBLE);
            cache.text.setText((String)listMessage.get(position).get("text"));
            Linkify.addLinks(cache.text, Linkify.ALL);
		}
		else if(type == Message.IMAGE_MESSAGE){
			if(listMessage.get(position).get("text").equals("")){
				cache.text.setVisibility(View.GONE);
			}
			else{
				cache.text.setText((String)listMessage.get(position).get("text"));
			}
			cache.audioPlayer.setVisibility(View.GONE);
			cache.image.setVisibility(View.VISIBLE);
			cache.image.setImageBitmap((Bitmap) listMessage.get(position).get("image"));
			cache.image.setTag(position);
			
			cache.image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					bitmap = (Bitmap) listMessage.get((Integer) v.getTag()).get("image");
					
					Intent intent = new Intent(mContext, ViewImageActivity.class);
					//Send file name and file size
					String fileName = (String) listMessage.get((Integer) v.getTag()).get("fileName");
					intent.putExtra("fileName", fileName);
					
					mContext.startActivity(intent);
				}
			});
		}      
		else if(type == Message.AUDIO_MESSAGE){
			if(listMessage.get(position).get("text").equals("")){
				cache.text.setVisibility(View.GONE);
			}
			else{
				cache.text.setText((String)listMessage.get(position).get("text"));
			}
			cache.image.setVisibility(View.GONE);
			cache.audioPlayer.setVisibility(View.VISIBLE);
			cache.audioPlayer.setTag(position);
			cache.audioPlayer.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MediaPlayer mPlayer = new MediaPlayer();
			        try {
			            mPlayer.setDataSource((String) listMessage.get((Integer) v.getTag()).get("filePath"));
			            mPlayer.prepare();
			            mPlayer.start();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
					
				}
			});
		}
        
		return view;
	}

	//Cache
	private static class CacheView{
		public TextView chatName;
		public TextView text;
		public ImageView image;
		public RelativeLayout relativeLayout;
		public Button audioPlayer;
	}
}
