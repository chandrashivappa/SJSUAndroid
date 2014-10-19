package com.example.easydeals.implementation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.easydeals.R;

public class FbPageActivity extends ActionBarActivity implements OnClickListener{
	Button goBackBtn;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fb_activity);
		setTitle("Login with Facebook");
		goBackBtn = (Button)findViewById(R.id.fbBack);
		goBackBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.fbBack) {
			Intent backIntent = new Intent(this, MainActivity.class);
			startActivity(backIntent);
		} else {
		}
		
	}

}
