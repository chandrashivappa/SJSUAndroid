package com.example.easydeals.implementation;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.easydeals.R;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.implementation.facebook.FacebookImpl;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	// private static Button facebookBtn;
	private static Button signInBtn;
	private static TextView signUpLink;
	private UiLifecycleHelper uiHelper;
	private static final String TAG = "MainFragment";
	private Session session = null;
	com.example.easydeals.pojo.Session easyDealSession = null;
	MongoDBHandler mongoDB;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {

			this.session = session;

		} else if (state.isClosed()) {
			this.session = null;
			Log.i(TAG, "Logged out...");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstance) {

		if (session != null) {
			Session.saveSession(session, savedInstance);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		LoginButton authButton = (LoginButton) findViewById(R.id.authButton);

		authButton.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes", "email"));

		signInBtn = (Button) findViewById(R.id.signIn);
		signUpLink = (TextView) findViewById(R.id.signUpLink);

		signInBtn.setOnClickListener(this);
		signUpLink.setOnClickListener(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_CANCELED) {
			super.onActivityResult(requestCode, resultCode, data);
			uiHelper.onActivityResult(requestCode, resultCode, data);

			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
			if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
				FacebookImpl faceBook = new FacebookImpl();
				String str = faceBook.makeMeRequest(Session.getActiveSession());
				easyDealSession = com.example.easydeals.pojo.Session.getInstance();
				Intent i = new Intent(this, UserHomePageActivity.class);
				System.out.println("Inside on activity result : " + easyDealSession.getUserId());
				i.putExtra("EMAIL", easyDealSession.getUserId());
				i.putExtra("TYPE", 1);
				startActivity(i);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		
		if (id == R.id.signUpLink) {
			// On click of sign up button, the page navigates to the sign up
			// page
			Intent signUpIntent = new Intent(this, RegisterActivity.class);
			signUpIntent.putExtra("emailType", 2);
			startActivity(signUpIntent);
		} else if (id == R.id.signIn) {
			// On click of sign in button, the page navigates to the login page
			System.out.println("Signin button is clicked");
			Intent signInIntent = new Intent(this, SignInActivity.class);
			startActivity(signInIntent);
		} else {
		}
	}
}
