package com.android.wondercom.CustomAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wondercom.R;
import com.android.wondercom.Entities.Item;

public class FileListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Item> listItem;
	private Item item;
	private LayoutInflater inflater;
	
	public FileListAdapter(Context context, List<Item> list){
		mContext = context;
		listItem = list;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		item = listItem.get(position);
		
		if(view == null){
			CacheView cache = new CacheView();
			view = inflater.inflate(R.layout.file_row, null);
			cache.name = (TextView) view.findViewById(R.id.file_name);
			cache.icon = (ImageView) view.findViewById(R.id.file_icon);
			cache.details = (TextView) view.findViewById(R.id.file_details);
			
			view.setTag(cache);
		}
		
		//Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();
        
		switch(item.getTypeItem()){
			case Item.DIRECTORY:
				cache.name.setText(item.getName());
				cache.details.setText(item.getNumItems() + " item(s)");
				if(item.getNumItems() > 0){
					cache.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.folder_full));
				}
				else{
					cache.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.folder_emtpy));
				}
				break;
			case Item.FILE:	
				cache.name.setText(item.getName());
				cache.details.setText(item.getSize() + " bytes");
				cache.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file));
				break;
			case Item.UP:
				cache.name.setText(item.getName());
				break;
		}
		
		return view;
	}

	//Cache
	private static class CacheView{
		public TextView name;
		public ImageView icon;
		public TextView details;
	}
}