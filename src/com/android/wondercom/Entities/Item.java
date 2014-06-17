package com.android.wondercom.Entities;

public class Item {
	
	public static final int DIRECTORY = 1;
	public static final int FILE = 2;
	public static final int UP = 3;

	private int typeItem;
	private String name;
	private long size;
	private String absolutePath;
	private int numItems;
	
	
	public int getTypeItem() {
		return typeItem;
	}
	public void setTypeItem(int typeItem) {
		this.typeItem = typeItem;
	}
	public String getName() {
		return name;
	}
	public long getSize() {
		return size;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public int getNumItems() {
		return numItems;
	}
	
	//Constructor for directory
	public Item(int typeItem, String name, long size, String absolutePath){
		this.typeItem = typeItem;
		this.name = name;
		this.size = size;
		this.absolutePath = absolutePath;
	}
	
	//Constructor for file
	public Item(int typeItem, String name, int numItems, String absolutePath){
		this.typeItem = typeItem;
		this.name = name;
		this.absolutePath = absolutePath;
		this.numItems = numItems;
	}
	
	//Constructor for UP link
	public Item(int typeItem, String display, String parentPath){
		this.typeItem = typeItem;
		this.name = display;
		this.absolutePath = parentPath;
	}
}
