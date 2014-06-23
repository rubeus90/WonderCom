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
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.wondercom.util.SystemUiHider;

public class WebViewActivity extends Activity {
	
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
