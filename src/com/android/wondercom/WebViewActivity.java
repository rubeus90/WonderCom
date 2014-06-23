package com.android.wondercom;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.wondercom.util.SystemUiHider;

public class WebViewActivity extends Activity {
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	
	private SystemUiHider mSystemUiHider;
	
	private ProgressBar progressBar;

	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Retrieve the link
		Intent intent = getIntent();
		String link = intent.getDataString().replace(this.getIntent().getScheme() + ":", "").trim();
		if(!link.toLowerCase().startsWith("http")){
			link = "http://" + link;
		}
		
		WebView webView = (WebView) findViewById(R.id.webView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		
		mSystemUiHider = SystemUiHider.getInstance(this, webView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
				// Cached values.
				int mControlsHeight;
				int mShortAnimTime;

				@Override
				@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
				public void onVisibilityChange(boolean visible) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
						if (mControlsHeight == 0) {
							mControlsHeight = controlsView.getHeight();
						}
						if (mShortAnimTime == 0) {
							mShortAnimTime = getResources().getInteger(
									android.R.integer.config_shortAnimTime);
						}
						controlsView
								.animate()
								.translationY(visible ? 0 : mControlsHeight)
								.setDuration(mShortAnimTime);
					} else {
						controlsView.setVisibility(visible ? View.VISIBLE
								: View.GONE);
					}

					if (visible && AUTO_HIDE) {
						delayedHide(AUTO_HIDE_DELAY_MILLIS);
					}
				}
			});
		
		webView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true); 
		webView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onProgressChanged(WebView view, int progress){
				if(progress<100){
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(progress);
				}
				else{
					progressBar.setVisibility(View.GONE);
				}
			}
			
			@Override
		    public void onReceivedTitle(WebView view, String title) {
		        super.onReceivedTitle(view, title);
		        if (!TextUtils.isEmpty(title)) {
		            WebViewActivity.this.setTitle(title);
		        }
		    }
			
		});
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(link);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
