package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.example.easydeals.R;
import com.example.easydeals.constants.EasyDealsDisplay;
import com.example.easydeals.constants.Validation;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.db.SqlDbHandler;
import com.example.easydeals.pojo.EasyDealsSession;
import com.example.easydeals.pojo.User;

public class RegisterActivity extends ActionBarActivity implements OnClickListener {
	EditText fName, lName, eMail, pWord, dob;
	Button register, regBack;
	ImageButton calImb;
	String gender = null;
	User userInfo;
	List<RadioButton> radioGroup = new ArrayList<RadioButton>();
	SqlDbHandler sdh = null;
	EasyDealsSession session;

	MongoDBHandler mongoDB;

	private int currentYear = 2014;
	private int day = 1;
	private int month = 11;
	private int year = 2000;
	int emailType;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		actionBar.setTitle(Html.fromHtml("<font face=\"serif\" color=\"#FFFF66\"><big>" + getString(R.string.registration) + "</big></font>"));
		
		Intent eType = getIntent();
		emailType = eType.getExtras().getInt("emailType");

		fName = (EditText) findViewById(R.id.firstName);
		lName = (EditText) findViewById(R.id.lastName);
		eMail = (EditText) findViewById(R.id.eMail);
		pWord = (EditText) findViewById(R.id.password);
		calImb = (ImageButton) findViewById(R.id.imageButton1);
		dob = (EditText) findViewById(R.id.dobText);

		radioGroup.add((RadioButton) findViewById(R.id.female));
		radioGroup.add((RadioButton) findViewById(R.id.male));

		calImb.setOnClickListener(this);
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(this);
		regBack = (Button) findViewById(R.id.registerBackBtn);
		regBack.setOnClickListener(this);

		// The below code is for toggling between the radio buttons
		for (final RadioButton rb : radioGroup) {
			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					System.out.println("gender ===> " + gender);
					if (isChecked) {
						gender = rb.getText().toString();
						processRadioButtonClick(buttonView);
					}
				}
			});
		}

	}

	// To uncheck the other radio button
	private void processRadioButtonClick(CompoundButton buttonView) {
		for (RadioButton rb : radioGroup) {
			if (rb != buttonView) {
				rb.setChecked(false);
			}
		}
	}

	// For creating the date dialog box
	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, datePicker, year, month, day);

	}

	// To set the date selected from the date dialog into the dob text box
	private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			int yearDiff = currentYear - selectedYear;
			if (yearDiff < 18) {
				ageLessThanRequiredAlert();
			}
			dob.setText((selectedMonth + 1) + " / " + selectedDay + " / "
					+ selectedYear);
		}
	};

	// Setting user details in User object
	private User setUserDetails() {
		userInfo = new User();
		userInfo.setfName(fName.getText().toString());
		userInfo.setlName(lName.getText().toString());
		userInfo.seteMail(eMail.getText().toString());
		userInfo.setPwd(pWord.getText().toString());
		userInfo.setDob(dob.getText().toString());
		userInfo.setGender(gender);
		userInfo.setEmailType(emailType);
		return userInfo;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.register:
			// set user details in user object
			Validation validData = new Validation();
			userInfo = setUserDetails();
			String flag = validData.validate(userInfo);
			if (flag == "GENDER") {
				radioButtonUnCheckAlert();
			} else if (flag == "INVALID CHARACTER"){
				callInvalidCharacterAlert();
			} else if(flag == "INVALID EMAIL"){
				callInvalidEmailAlert();
			} else if(flag == "EMPTY FIELDS"){
				fieldEmptyAlert();
			} else {
				// for testing purposes - display user info
				EasyDealsDisplay.display(userInfo);

				// instantiating the SQLite db handler class
				sdh = new SqlDbHandler(this);

				// inserting record in SQLite db
				sdh.insertUserRecord(userInfo);

				// Calling async task to insert the user info in mongo db
				new MongoDBInsert().execute(userInfo);
			}
			break;
		case R.id.registerBackBtn:
			// instantiating the SQLite db handler class
			sdh = new SqlDbHandler(this);
			Intent regBackIntent = new Intent(this, MainActivity.class);
			startActivity(regBackIntent);
			break;
		case R.id.imageButton1:
			// This is for the date dialog
			showDialog(0);
			break;
		default:
			break;
				
		}
		
	}

	// Async task, to insert data into mongo db, so that the main thread will
	// not be used.
	class MongoDBInsert extends AsyncTask<User, Void, String> {

		@Override
		protected String doInBackground(User... params) {
			System.out.println("Inside async task of register activity");
			// Instantiating mongo db handler class
			String uniqueUserEmail = null;
			mongoDB = new MongoDBHandler();
			User user = (User) params[0];
			try {
				// calling the insert method to insert user data into mongo db
				boolean temp = mongoDB.insertUserDataCollection(user);
				if(temp){
					uniqueUserEmail =  "true";
				} else {
					uniqueUserEmail = "false";
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return uniqueUserEmail;
		}

		protected void onPostExecute(String st) {
			System.out.println("User data inserted in mongo db!!");

			// Moving to the user interest activity page, once the user info is
			// inserted
			if(st.equalsIgnoreCase("true")){
				session = EasyDealsSession.getInstance();
				session.setUserId(userInfo.geteMail());
				Intent userIntent = new Intent(getApplicationContext(),UserInterestActivity.class);
				userIntent.putExtra("EMAIL", userInfo.geteMail());
				startActivity(userIntent);
			 } else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
								RegisterActivity.this);
						builder.setTitle("Email exist!!!");
						builder.setPositiveButton("Ok", null);
						builder.setMessage("User email already exist Please enter another id!!!");
						AlertDialog theAlert = builder.create();
						theAlert.show();
						eMail.setText("");
						pWord.setText("");
			 }
			
		}

	}

	// ****************************************VALIDATION****************************************//
	// Empty fields alert validation
	private void fieldEmptyAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegisterActivity.this);
		builder.setTitle("Empty Fields!!!");
		builder.setPositiveButton("Ok", null);
		builder.setMessage("Please fill empty fields!!!");
		AlertDialog theAlert = builder.create();
		theAlert.show();
	}

	// Under age not allowed alert
	private void ageLessThanRequiredAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegisterActivity.this);
		builder.setTitle("Not a legal age for sign up!!");
		builder.setPositiveButton("Ok", null);
		builder.setMessage("Sorry, you should be atleast 18 years or above to signup.");
		AlertDialog alert = builder.create();
		alert.show();
	}

	// To alert if the names have invalid character
	private void callInvalidCharacterAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegisterActivity.this);
		builder.setTitle("Invalid Character alert!!!");
		builder.setPositiveButton("Ok", null);
		builder.setMessage("Please enter valid characters!!!");

		AlertDialog theAlert = builder.create();
		theAlert.show();
	}
	
	//To check whether email pattern is valid or not
		private void callInvalidEmailAlert(){
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle("Not a vaid email pattern");
			builder.setPositiveButton("Ok", null);
			builder.setMessage("Please enter valid email pattern!!!");
			AlertDialog theAlert = builder.create();
			theAlert.show();
		}
	
	//To alert if gender is not checked
	private void radioButtonUnCheckAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setTitle("Gender Check");
		builder.setPositiveButton("Ok", null);
		builder.setMessage("Please select a gender!!!");
		AlertDialog theAlert = builder.create();
		theAlert.show();

	}

}
