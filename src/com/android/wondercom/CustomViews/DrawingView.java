package com.android.wondercom.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;

public class DrawingView extends View {
	private Path drawPath;
	private Paint drawPaint, canvasPaint;
	private int paintColor;
	private Canvas drawCanvas;
	private Bitmap canvasBitmap;
	private float brushSize, lastBrushSize;
	private boolean erase=false;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	public void setupDrawing(){
		drawPath = new Path();
		
		drawPaint = new Paint();		
		drawPaint.setColor(paintColor);		
		drawPaint.setAntiAlias(true);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}
	
	//Method which will be called when the custom View is assigned a size
	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	
	//Each time the user draws using touch interaction, we will invalidate the View, causing the onDraw method to execute.
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	//Handle touch events
	@Override
	public boolean onTouchEvent(MotionEvent event) { 
		float touchX = event.getX();
		float touchY = event.getY();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(erase){
					drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				}
				else{
					drawPaint.setXfermode(null);					
				}		
				drawCanvas.drawPoint(touchX, touchY, drawPaint);
			    drawPath.moveTo(touchX, touchY);
			    break;
			case MotionEvent.ACTION_MOVE:
				if(erase){
					drawPath.lineTo(touchX, touchY);
					drawCanvas.drawPath(drawPath, drawPaint);
					drawPath.reset();
					drawPath.moveTo(touchX, touchY);
				}
				else{
					drawPath.lineTo(touchX, touchY);
				}			    
			    break;
			case MotionEvent.ACTION_UP:
			    drawCanvas.drawPath(drawPath, drawPaint);
			    drawPath.reset();
			    break;
		    default: return false;
		}
		invalidate();
		return true;
	}
	
	public void setColor(String color){
		invalidate();
		paintColor = Color.parseColor(color);
		drawPaint.setColor(paintColor);
	}
	
	public void setBrushSize(float size){
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
		brushSize=pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}
	
	public void setLastBrushSize(float lastSize){
	    lastBrushSize=lastSize;
	}
	
	public float getLastBrushSize(){
	    return lastBrushSize;
	}
	
	public void setErase(boolean isEraser){
		erase = isEraser;
	}
}
