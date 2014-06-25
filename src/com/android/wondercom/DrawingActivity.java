package com.android.wondercom;

import com.android.wondercom.CustomViews.DrawingView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class DrawingActivity extends Activity {	
	private DrawingView drawView;
	private ImageButton currentPaint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		
		drawView = (DrawingView)findViewById(R.id.drawing);
		LinearLayout layout = (LinearLayout) findViewById(R.id.colorPalette);
		
		currentPaint = (ImageButton) layout.getChildAt(1);
//		currentPaint.setBackground(getResources().getDrawable(R.drawable.paint_selected));
	}
	
	public void paintClicked(View view){
	    if(currentPaint != view){
	    	ImageButton button = (ImageButton) view;
	    	String color = view.getTag().toString();
	    	drawView.setColor(color);
	    	
	    	currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
	    	currentPaint=(ImageButton) button;
	    	currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_selected));
	    }
	}
}
