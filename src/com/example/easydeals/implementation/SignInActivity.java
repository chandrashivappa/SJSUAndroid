package com.example.easydeals.implementation;

import java.net.UnknownHostException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easydeals.R;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.Session;

public class SignInActivity extends ActionBarActivity implements OnClickListener{
	EditText userName, password;
	Button login;
	MongoDBHandler mongoDB;
	String userEmail, pwd;
	Session session = null;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	        setContentView(R.layout.signin_activity);
	        userName = (EditText)findViewById(R.id.emailEdit);
	        password = (EditText)findViewById(R.id.pwdEdit);
	        login = (Button)findViewById(R.id.loginBtn);
	        
	        login.setOnClickListener(this);
	    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.loginBtn) {
			userEmail = userName.getText().toString();
			pwd = password.getText().toString();
			new UserAuthentication().execute(userEmail, pwd);
		}
		
	}
	
	class UserAuthentication extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = false;
			System.out.println("Inside asynctask of signin ");
			String email = params[0];
			String password = params[1];
			mongoDB = new MongoDBHandler();
			try {
				flag = mongoDB.authenticateUser(email, password);
				System.out.println("The flag value after authenticating user is ============> " + flag);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return flag;
		}
		
		protected void onPostExecute(Boolean status) {
			System.out.println("Inside onpostexecute of user authentication!!");

			if(status){
				
				session = Session.getInstance();
				session.setUserId(userEmail);
				
				Intent userHomePage = new Intent(getApplicationContext(), UserHomePageActivity.class);
				userHomePage.putExtra("EMAIL",userEmail );
				startActivity(userHomePage);
			} else {
				Toast.makeText(getApplicationContext(), "Username/Password is invalid. Please try again!!", Toast.LENGTH_LONG).show();
				userName.setText("");
				password.setText("");
			}
			
		}
		
	}

}
