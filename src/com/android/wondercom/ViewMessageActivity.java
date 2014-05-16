package com.android.wondercom;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ViewMessageActivity extends Activity{
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);	
		Bundle extras = getIntent().getExtras();
	    String msg = extras.getString("Message");
	    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
		
}
