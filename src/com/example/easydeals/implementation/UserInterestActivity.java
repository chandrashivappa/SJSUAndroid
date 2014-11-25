package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.easydeals.R;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.db.SqlDbHandler;
import com.example.easydeals.pojo.EasyDealsSession;

public class UserInterestActivity extends ActionBarActivity implements OnClickListener, OnCheckedChangeListener {

	String userEmail = null;
	private CheckBox food, kids, cloth, electronics, drinks, books,
			currentLocation;
	private Button submit, back;
	
	Map<String, String> userInterest = null;
	SqlDbHandler sdh = null;
	MongoDBHandler mongoDB;
	EasyDealsSession session;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interest_activity);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		actionBar.setTitle(Html.fromHtml("<font face=\"serif\" color=\"#FFFF66\"><big>" + getString(R.string.interestCheck) + "</big></font>"));
		Intent userIntent = getIntent();
		userEmail = userIntent.getExtras().getString("EMAIL");
		session = EasyDealsSession.getInstance();
		System.out.println("user Email using session in user interest activity===> " + session.getUserId() );

		food = (CheckBox) findViewById(R.id.food);
		books = (CheckBox) findViewById(R.id.books);
		kids = (CheckBox) findViewById(R.id.kid);
		cloth = (CheckBox) findViewById(R.id.cloth);
		electronics = (CheckBox) findViewById(R.id.electronics);
		drinks = (CheckBox) findViewById(R.id.drinks);
		currentLocation = (CheckBox) findViewById(R.id.location);
		submit = (Button) findViewById(R.id.next);
		back = (Button)findViewById(R.id.backBtn);
		userInterest = new HashMap<String, String>();
		
		//Initially setting the option choices to NO, so that in the db it will not store as null. So if the
		//user checks these options, the respective options value will store as YES in db.
		userInterest.put("eMail", userEmail);
		userInterest.put("food", "NO");
		userInterest.put("books", "NO");
		userInterest.put("kids", "NO");
		userInterest.put("clothingApparel", "NO");
		userInterest.put("electronics", "NO");
		userInterest.put("drinks", "NO");
		userInterest.put("locationPermission", "NO");
		//Setting listeners on the check boxes
		food.setOnCheckedChangeListener(this);
		books.setOnCheckedChangeListener(this);
		kids.setOnCheckedChangeListener(this);
		cloth.setOnCheckedChangeListener(this);
		electronics.setOnCheckedChangeListener(this);
		drinks.setOnCheckedChangeListener(this);
		currentLocation.setOnCheckedChangeListener(this);
		submit.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == food){
			if(isChecked){
				userInterest.put("food", "YES");
			} else {
				userInterest.put("food", "NO");
			}
		}
		if(buttonView == books){
			if(isChecked){
				userInterest.put("books", "YES");
			} else {
				userInterest.put("books", "NO");
			}
		}
		if(buttonView == kids){
			if(isChecked){
				userInterest.put("kids", "YES");
			} else {
				userInterest.put("kids", "NO");
			}
		}
		if(buttonView == cloth){
			if(isChecked){
				userInterest.put("clothingApparel", "YES");
			} else {
				userInterest.put("clothingApparel", "NO");
			}
		}
		
		if(buttonView == electronics){
			if(isChecked){
				userInterest.put("electronics", "YES");
			} else{
				userInterest.put("electronics", "NO");
			}
		}
		if(buttonView == drinks){
			if(isChecked){
				userInterest.put("drinks", "YES");
			} else {
				userInterest.put("drinks", "NO");
			}
		}
		if(buttonView == currentLocation){
			if(isChecked){
				userInterest.put("locationPermission", "YES");
			} else {
				userInterest.put("locationPermission", "NO");
			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		if (id == R.id.next) {
			if(currentLocation.isChecked()) {
				try {
					
					sdh = new SqlDbHandler(this);
					//inserting user interest info in SQLite db
					sdh.insertUserInterest(userInterest);
					
					//For testing purposes.
					sdh.getUserInterest(userEmail);
					
					//calling async task to insert user interest in mongo db
					new MongoDBUpdate().execute(userInterest);
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			else{
				//Alerting the user to check the location permission checkbox
				alertLocationUnCheck();
			}
		} else if (id == R.id.backBtn) {
			Intent backIntent = new Intent(this,RegisterActivity.class);
			backIntent.putExtra("EMAIL", userEmail);
			startActivity(backIntent);
		} else {
		}
	}

	
	//Location permission alert
	public void alertLocationUnCheck() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				UserInterestActivity.this);
		builder.setTitle("Location Check");
		builder.setPositiveButton("Ok", null);
		builder.setMessage("Please check the location tracking, inorder to recieve easy deals!!");
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	//async task class for inserting into mongo db
	class MongoDBUpdate extends AsyncTask<Map<String, String>, String, String>{

		@Override
		protected String doInBackground(Map<String, String>... params) {
			System.out.println("Inside async task of user interest activity");
			mongoDB = new MongoDBHandler();
			Map<String, String> userInt = params[0];
			String email = userInt.get("eMail");
			try {
				mongoDB.insertUserInterestCollection(userInt);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return email;
		}

		protected void onPostExecute(String st){
			//Actions to be performed, after interest data is inserted into mongo db
			System.out.println("User interest data inserted!!");

			//For testing purposes retrieving data from mongodb
			mongoDB = new MongoDBHandler();
			mongoDB.getUserDataFromDB(st);
			Intent successIntent = new Intent(getApplicationContext(), CardDetailsCollectionActivity.class);
			successIntent.putExtra("EMAIL", st);
			startActivity(successIntent);
		}
	}

	

}
