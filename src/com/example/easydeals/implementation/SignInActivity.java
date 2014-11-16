package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easydeals.R;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.EasyDealsSession;

public class SignInActivity extends ActionBarActivity implements OnClickListener{
	EditText userName, password;
	TextView forPwd;
	Button login;
	MongoDBHandler mongoDB;
	String userEmail, pwd;
	EasyDealsSession session = null;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.signin_activity);
	        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
			actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
			actionBar.setTitle(Html.fromHtml("<font face=\"serif\" color=\"yellow\"><big>" + getString(R.string.signin) + "</big></font>"));
	        userName = (EditText)findViewById(R.id.emailEdit);
	        password = (EditText)findViewById(R.id.pwdEdit);
	        login = (Button)findViewById(R.id.loginBtn);
	        forPwd = (TextView)findViewById(R.id.forgotPassword);
	        login.setOnClickListener(this);
	        forPwd.setOnClickListener(this);
	    }

	@Override
	public void onClick(View v) {	
		int id = v.getId();
		
		switch(id){
		
		case R.id.loginBtn:
			userEmail = userName.getText().toString();
			pwd = password.getText().toString();
			new UserAuthentication().execute(userEmail, pwd);
			break;
		
		case R.id.forgotPassword:
			Intent forgotIntent = new Intent(this, ForgotPasswordActivity.class);
			startActivity(forgotIntent);
			break;
			
		default:
			break;
		}
		
	}
	
	class UserAuthentication extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String flag = null;
			System.out.println("Inside asynctask of signin ");
			String email = params[0];
			String password = params[1];
			mongoDB = new MongoDBHandler();
			try {
				flag = mongoDB.authenticateUser(email, password);
				System.out.println("The flag value after authenticating user is ============> " + flag);
				if(flag.equalsIgnoreCase("Signup User")){
					Map<String,String> cardDetails= mongoDB.checkCardPresent(email, 2);
					System.out.println("The email id is of signup user after logging in is =======> " + email);
					if(cardDetails.get("cardType") != null && cardDetails.get("cardNumber") != null){
						flag = "Signup User";
					}  else {
						flag = "card details";
					} 
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return flag;
		}
		
		protected void onPostExecute(String status) {
			System.out.println("Inside onpostexecute of user authentication!!");

			if(status.equalsIgnoreCase("Signup User")){
				session = EasyDealsSession.getInstance();
				session.setUserId(userEmail);
				Intent userHomePage = new Intent(getApplicationContext(), UserHomePageActivity.class);
				userHomePage.putExtra("EMAIL",userEmail );
				startActivity(userHomePage);
			} else if (status.equalsIgnoreCase("Fb User")){
				Toast.makeText(getApplicationContext(), "Sorry!!, You have to login using Facebook", Toast.LENGTH_LONG).show();
				Intent loginPage = new Intent(getApplicationContext(),MainActivity.class );
				startActivity(loginPage);
			} else if(status.equalsIgnoreCase("card details")){
				session = EasyDealsSession.getInstance();
				session.setUserId(userEmail);
				System.out.println(" going to go to card page ===========>");
				Intent userHome = new Intent(getApplicationContext(), CardDetailsCollectionActivity.class);
				userHome.putExtra("EMAIL",userEmail );
				userHome.putExtra("TYPE",2);
				startActivity(userHome);
			}
			else {
				Toast.makeText(getApplicationContext(), "Username/Password is invalid. Please try again!!", Toast.LENGTH_LONG).show();
				userName.setText("");
				password.setText("");
			}
		}
	}
}
