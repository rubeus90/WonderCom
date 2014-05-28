package com.android.wondercom;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
	private List<HashMap<String,Object>> listMessage;
	private LayoutInflater inflater;

	public ChatAdapter(Context context, List<HashMap<String, Object>> listMessage){
		this.listMessage = listMessage;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	            
			view.setTag(cache);
		}
		
		//Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();
        cache.chatName.setText((String)listMessage.get(position).get("chatName"));
        
        if(type == Message.TEXT_MESSAGE){        	
        	cache.image.setVisibility(View.GONE);
        	cache.text.setVisibility(View.VISIBLE);
            cache.text.setText((String)listMessage.get(position).get("text"));
		}
		else if(type == Message.IMAGE_MESSAGE){
			cache.text.setVisibility(View.GONE);
			cache.image.setVisibility(View.VISIBLE);
			cache.image.setImageBitmap((Bitmap) listMessage.get(position).get("image"));
		}       
        
		return view;
	}

	//Cache
	private static class CacheView{
		public TextView chatName;
		public TextView text;
		public ImageView image;
	}
}
