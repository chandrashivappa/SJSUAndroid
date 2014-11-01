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

public class EditUser extends ActionBarActivity implements OnClickListener, OnCheckedChangeListener {
	String email;
	
	MongoDBHandler mongoDB;
	Button updateBtn, cancelBtn;
	private CheckBox food, kids, cloth, electronics, drinks, books,
	currentLocation;
	Map<String, String> userInterest  = new HashMap<String, String>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_user);
		Intent userEditIntent = getIntent();
		email = userEditIntent.getExtras().getString("EMAIL");
		System.out.println("The email id inside edit user is ---->" + email);
		
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		actionBar.setTitle(Html.fromHtml("<font color=\"yellow\"><big>" + getString(R.string.editUserTitle) + "</big></font>"));

		food = (CheckBox) findViewById(R.id.food);
		books = (CheckBox) findViewById(R.id.books);
		kids = (CheckBox) findViewById(R.id.kid);
		cloth = (CheckBox) findViewById(R.id.cloth);
		electronics = (CheckBox) findViewById(R.id.electronics);
		drinks = (CheckBox) findViewById(R.id.drinks);
		currentLocation = (CheckBox) findViewById(R.id.location);
		updateBtn = (Button) findViewById(R.id.next);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		
		mongoDB = new MongoDBHandler();
		userInterest = mongoDB.getOnlyUserInterest(email);
		userInterest.put("eMail", email);
		
		food.setChecked((userInterest.get("food").equals("YES")));
		kids.setChecked((userInterest.get("kids").equals("YES")));
		cloth.setChecked((userInterest.get("clothingApparel").equals("YES")));
		books.setChecked((userInterest.get("books").equals("YES")));
		electronics.setChecked((userInterest.get("electronics").equals("YES")));
		drinks.setChecked((userInterest.get("drinks").equals("YES")));
		currentLocation.setChecked((userInterest.get("locationPermission").equals("YES")));
		
		//Setting listeners on the check boxes
		food.setOnCheckedChangeListener(this);
		books.setOnCheckedChangeListener(this);
		kids.setOnCheckedChangeListener(this);
		cloth.setOnCheckedChangeListener(this);
		electronics.setOnCheckedChangeListener(this);
		drinks.setOnCheckedChangeListener(this);
		currentLocation.setOnCheckedChangeListener(this);
		updateBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
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
		switch(v.getId()){
		case R.id.next:
			if(currentLocation.isChecked()) {
				new MongoDBUpdate().execute( userInterest);
			} else{
				//Alerting the user to check the location permission checkbox
				alertLocationUnCheck();
			}
			break;
		case R.id.cancelBtn:
			Intent homeIntent = new Intent(this, UserHomePageActivity.class);
			homeIntent.putExtra("EMAIL", email);
			startActivity(homeIntent);
			break;
		default:
			break;
		}
	}
	//Location permission alert
		public void alertLocationUnCheck() {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					EditUser.this);
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
					mongoDB.updateUserInterestCollections(userInt);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				return email;
			}

			protected void onPostExecute(String st){
				//Actions to be performed, after interest data is inserted into mongo db
				System.out.println("User interest data inserted!!");

				//For testing purposes retrieving data from mongodb
				//mongoDB = new MongoDBHandler();
				//mongoDB.getUserDataFromDB(st);
				Intent successIntent = new Intent(getApplicationContext(), UserHomePageActivity.class);
				successIntent.putExtra("EMAIL", st);
				startActivity(successIntent);
			}
		}
}
