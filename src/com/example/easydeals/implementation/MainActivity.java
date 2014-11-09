package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
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
import com.example.easydeals.pojo.User;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	// private static Button facebookBtn;
	private static Button signInBtn;
	private static TextView signUpLink;
	private UiLifecycleHelper uiHelper;
	private static final String TAG = "MainFragment";
	private Session session = null;
	com.example.easydeals.pojo.EasyDealsSession easyDealSession = null;
	MongoDBHandler mongoDB;
	String email;

	//async task class for inserting into mongo db
	public class CardPresentDetails extends AsyncTask<User, Void, Boolean>{
		User user;
		MongoDBHandler mongoDB;
		String email;
		Context context;
		boolean cardPresent = false;
		
//		private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		
		@Override
		protected Boolean doInBackground(User...params){
			user = params[0];
			
			if(user != null){
				
				try {
					mongoDB = new MongoDBHandler();
					Map<String,String> cardDetails= mongoDB.checkCardPresent(user.geteMail(), user.getEmailType());
	
					if(cardDetails.get("cardType") != null && cardDetails.get("cardNumber") != null){
						System.out.println("The flag value is true and so going to go to user home page ===========>");
						
						cardPresent = true;
					}  else {
						System.out.println("The flag value is false and so going to go to card page ===========>");
						cardPresent = false;
					} 
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} 
			}
			
			return cardPresent;
		}
		
//	    protected void onPreExecute() {
//	    	this.dialog.setMessage("Processing..."); 
//	    	this.dialog.show();
//	    }
	    
//		public void onPostExecute(Boolean cardPresent){
//			//Actions to be performed, after interest data is inserted into mongo db
//
////			this.dialog.cancel();
//			
//			if (cardPresent == true) {
//				Intent userHome = new Intent(MainActivity.this, UserHomePageActivity.class);
//				userHome.putExtra("EMAIL",email );
//				userHome.putExtra("TYPE",1);
//				startActivity(userHome);
//				
//			}
//			else {
//				Intent userHome = new Intent(MainActivity.this, CardDetailsCollectionActivity.class);
//				userHome.putExtra("EMAIL",email );
//				userHome.putExtra("TYPE",1);
//				startActivity(userHome);
//				
//			}
//			
//			
//		}
	}

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
			
			User user = new User();
			user.seteMail(email);
			user.setEmailType(1);
			
			
			try {
//				mongoDB = new MongoDBHandler();
//				Map<String,String> cardDetails= mongoDB.checkCardPresent(email, 1);
			
			//Boolean cardPresent = new CardPresentDetails().execute(user).get();
			Boolean cardPresent = new CardPresentDetails().execute(user).get();
//				if(cardDetails.get("cardType") != null && cardDetails.get("cardNumber") != null){
				if (cardPresent == true) {
					System.out.println("The flag value is true and so going to go to user home page ===========>");
					Intent userHome = new Intent(this, UserHomePageActivity.class);
					userHome.putExtra("EMAIL",email );
					userHome.putExtra("TYPE",1);
					startActivity(userHome);
				}  else {
					System.out.println("The flag value is false and so going to go to card page ===========>");
					Intent userHome = new Intent(this, CardDetailsCollectionActivity.class);
					userHome.putExtra("EMAIL",email );
					userHome.putExtra("TYPE",1);
					startActivity(userHome);
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			System.out.println("Session is open !! inside onsessionstate change \\\\\\\\\\\\\\\\\\" + session);
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

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		actionBar.setTitle(Html.fromHtml("<font color=\"yellow\"><big><b>" + getString(R.string.app_name) + "</b></big></font>"));

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		LoginButton authButton = (LoginButton) findViewById(R.id.authButton);

		authButton.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes", "email"));
		authButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					System.out.println("the user name is ---> " + user.getName());
					email = (String) user.asMap().get("email");
					System.out.println("the email is ---> " + email);
					easyDealSession = com.example.easydeals.pojo.EasyDealsSession.getInstance();
					easyDealSession.setUserId(email);
					easyDealSession.setEmailType(1);
				} else {
					System.out.println("You are not logged");
				}
			}
		});

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
				System.out.println("the email is " + str);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
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
