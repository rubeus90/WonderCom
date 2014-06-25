package com.android.wondercom;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.wondercom.CustomViews.DrawingView;

public class DrawingActivity extends Activity {
	private static final String TAG = "DrawingActivity";
	private DrawingView drawView;
	private ImageButton currentPaint;
	private float smallBrush, mediumBrush, largeBrush;
	private Button brushButton, eraserButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		
		drawView = (DrawingView)findViewById(R.id.drawing);
		LinearLayout layout = (LinearLayout) findViewById(R.id.colorPalette);
		brushButton = (Button) findViewById(R.id.chooseBrush);
		eraserButton = (Button) findViewById(R.id.chooseEraser);
		
		//Retrieve brush sizes
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		
		//Default colour is the first color in the color palette
		currentPaint = (ImageButton) layout.getChildAt(0);
		drawView.setColor(currentPaint.getTag().toString());
		
		//Default brush size is medium
		drawView.setBrushSize(smallBrush);	
		drawView.setLastBrushSize(smallBrush);
		
		brushButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Choose brush size");
				final Dialog brushDialog = new Dialog(DrawingActivity.this);
				brushDialog.setTitle("Brush size");
				brushDialog.setContentView(R.layout.brush_chooser);
				brushDialog.show();
				
				ImageView smallBrush = (ImageView) brushDialog.findViewById(R.id.small_brush);
				smallBrush.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						drawView.setBrushSize(DrawingActivity.this.smallBrush);
						drawView.setLastBrushSize(DrawingActivity.this.smallBrush);
						drawView.setErase(false);
						brushDialog.dismiss();
					}
				});
				
				ImageView mediumBrush = (ImageView) brushDialog.findViewById(R.id.medium_brush);
				mediumBrush.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						drawView.setBrushSize(DrawingActivity.this.mediumBrush);
						drawView.setLastBrushSize(DrawingActivity.this.mediumBrush);
						drawView.setErase(false);
						brushDialog.dismiss();
					}
				});
				
				ImageView largeBrush = (ImageView) brushDialog.findViewById(R.id.large_brush);
				largeBrush.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						drawView.setBrushSize(DrawingActivity.this.largeBrush);
						drawView.setLastBrushSize(DrawingActivity.this.largeBrush);
						drawView.setErase(false);
						brushDialog.dismiss();
					}
				});
			}
		});
		
		eraserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Choose eraser size");
				final Dialog eraserDialog = new Dialog(DrawingActivity.this);
				eraserDialog.setTitle("Eraser size");
				eraserDialog.setContentView(R.layout.brush_chooser);
				eraserDialog.show();
				
				ImageView smallBrush = (ImageView) eraserDialog.findViewById(R.id.small_brush);
				smallBrush.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(DrawingActivity.this.smallBrush);
						eraserDialog.dismiss();
					}
				});
				
				ImageView mediumBrush = (ImageView) eraserDialog.findViewById(R.id.medium_brush);
				mediumBrush.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(DrawingActivity.this.mediumBrush);
						eraserDialog.dismiss();
					}
				});
				
				ImageView largeBrush = (ImageView) eraserDialog.findViewById(R.id.large_brush);
				largeBrush.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(DrawingActivity.this.largeBrush);
						eraserDialog.dismiss();
					}
				});
			}
		});
	}
	
	public void paintClicked(View view){
	    if(currentPaint != view){
	    	ImageButton button = (ImageButton) view;
	    	String color = view.getTag().toString();
	    	drawView.setColor(color);
	    	
	    	//Change the background of the old color to normal, and change background of the new color to 'pressed'
	    	currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
	    	currentPaint=(ImageButton) button;
	    	currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_selected));
	    	
	    	drawView.setErase(false);
	    	drawView.setBrushSize(drawView.getLastBrushSize());
	    }
	}
}
