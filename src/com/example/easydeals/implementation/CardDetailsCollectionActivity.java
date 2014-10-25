package com.example.easydeals.implementation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.easydeals.R;
import com.example.easydeals.constants.Validation;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.Session;

public class CardDetailsCollectionActivity extends ActionBarActivity implements OnClickListener{

	EditText creditCard, costcoCard;
	Button cardSubmitBtn;
	String creditCardNum, costcoCardNum;
	String email;
	int type;
	MongoDBHandler mongoDB;
	Session session;
	String creditFlag, costcoFlag;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_details);
		Intent regComplete = getIntent();
		email = regComplete.getExtras().getString("EMAIL");
		type = regComplete.getExtras().getInt("TYPE");
		System.out.println("email using intent inside card details activity -----> " + email);
		creditCard = (EditText)findViewById(R.id.creditEditText);
		costcoCard = (EditText)findViewById(R.id.costcoEditText);
		cardSubmitBtn = (Button)findViewById(R.id.cardSubmit);
		
		cardSubmitBtn.setOnClickListener(this);
		
		session = Session.getInstance();
		System.out.println("email id using session in card details activity ===> " + session.getUserId());
		
		
		
	}
	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.cardSubmit) {
			creditCardNum = creditCard.getText().toString();
			costcoCardNum = costcoCard.getText().toString();
			Validation validate = new Validation();
			creditFlag = validate.validate("Credit", creditCardNum);
			costcoFlag = validate.validate("Costco", costcoCardNum);
			if(creditFlag == "GOOD"){
				//email = mongoDB.insertUserCardDetails(email, creditCardNum);
				new CardDetailsInsert().execute(session.getUserId(), creditCardNum);
			} else if (costcoFlag == "GOOD"){
				//email = mongoDB.insertUserCardDetails(email, costcoCardNum);
				new CardDetailsInsert().execute(session.getUserId(), costcoCardNum);
			} else if(creditFlag == "INVALID CARD" || costcoFlag == "INVALID CARD"){
				callValidAlertBox();
			} else {
				callFieldsEmptyAlert();
			}
		} else {
		}
	
	}
	
	class CardDetailsInsert extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			System.out.println("Inside the asynctask of card details insert");
			mongoDB = new MongoDBHandler();
			String email = params[0];
			String cardNum = params[1];
			
			email = mongoDB.insertUserCardDetails(email, cardNum);
			return email;
		}
		protected void onPostExecute(String eMail) {
			System.out.println("User card data inserted in mongo db!!");

			// Moving to the user interest activity page, once the user info is
			// inserted
			Intent userIntent = new Intent(getApplicationContext(),
					UserHomePageActivity.class);
			userIntent.putExtra("EMAIL", eMail);
			userIntent.putExtra("TYPE", session.getEmailType());
			startActivity(userIntent);
		}
	}
	
	// Empty fields alert validation
		private void callFieldsEmptyAlert() {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CardDetailsCollectionActivity.this);
			builder.setTitle("Empty Fields!!!");
			builder.setPositiveButton("Ok", null);
			builder.setMessage("Please fill one of the field!!!");
			AlertDialog theAlert = builder.create();
			theAlert.show();
		}
		
		private void callValidAlertBox(){
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CardDetailsCollectionActivity.this);
			builder.setTitle("Invalid Card Number!!!");
			builder.setPositiveButton("Ok", null);
			builder.setMessage("Please fill valid card number!!!");
			AlertDialog theAlert = builder.create();
			theAlert.show();
			
		}
}
