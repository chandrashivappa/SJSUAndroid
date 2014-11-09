package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.example.easydeals.R;
import com.example.easydeals.constants.Validation;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.implementation.MainActivity.CardPresentDetails;
import com.example.easydeals.pojo.EasyDealsSession;
import com.example.easydeals.pojo.User;

public class CardDetailsCollectionActivity extends ActionBarActivity implements OnClickListener{

	EditText creditCard, costcoCard;
	Button cardSubmitBtn;
	TextView cardChange;
	String creditCardNum, costcoCardNum;
	String email;
	int type;
	MongoDBHandler mongoDB;
	EasyDealsSession session;
	String creditFlag, costcoFlag;


	//async task class for inserting into mongo db
	public class CardPresentDetails extends AsyncTask<User, Void, Map<String,String>>{
		User user;
		MongoDBHandler mongoDB;
		String email;
		Context context;
		boolean cardPresent = false;
				
		@Override
		protected Map<String,String> doInBackground(User...params){
			user = params[0];
			
			Map<String,String> cardDetails = null;
			
			if(user != null){
				
				try {
					mongoDB = new MongoDBHandler();
					cardDetails= mongoDB.checkCardPresent(user.geteMail(), user.getEmailType());
	
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
			
			return cardDetails;
		}
			    
//		public void onPostExecute(Boolean cardPresent){
//			//Actions to be performed, after interest data is inserted into mongo db
//			
//			if (cardPresent == true) {
//				Intent userHome = new Intent(CardDetailsCollectionActivity.this, UserHomePageActivity.class);
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

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_details);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		actionBar.setTitle(Html.fromHtml("<font color=\"yellow\"><big><b>" + getString(R.string.cardDetails) + "</b></big></font>"));
		Intent regComplete = getIntent();
		email = regComplete.getExtras().getString("EMAIL");
		session = EasyDealsSession.getInstance();
		type = regComplete.getExtras().getInt("TYPE");
		String sessionemail = session.getUserId();
		System.out.println("email using intent inside card details activity -----> " + email);
		creditCard = (EditText)findViewById(R.id.creditEditText);
		costcoCard = (EditText)findViewById(R.id.costcoEditText);
		cardSubmitBtn = (Button)findViewById(R.id.cardSubmit);
		cardChange = (TextView)findViewById(R.id.changeCard);
		cardSubmitBtn.setOnClickListener(this);
		
		try {
//			mongoDB = new MongoDBHandler();
//			Map<String,String> cardDetails= mongoDB.checkCardPresent(sessionemail, 1);
			
			User user = new User();
			user.seteMail(sessionemail);
			user.setEmailType(1);
			
			
//			try {
//				mongoDB = new MongoDBHandler();
//				Map<String,String> cardDetails= mongoDB.checkCardPresent(email, 1);
			
			//Boolean cardPresent = new CardPresentDetails().execute(user).get();
			Map<String,String> cardDetails = new CardPresentDetails().execute(user).get();

			
			if(cardDetails.get("cardType") != null && cardDetails.get("cardNumber") != null){
				System.out.println("The flag value is true and so going to go to user home page ===========>");
				cardChange.setText("If you wish to change your card details, please update. Else you can proceed");
				if((cardDetails.get("cardType")).equalsIgnoreCase("Credit")){
					creditCard.setText(cardDetails.get("cardNumber"));
				} else {
					costcoCard.setText(cardDetails.get("cardNumber"));
				}
				
				Intent userHome = new Intent(this, UserHomePageActivity.class);
				userHome.putExtra("EMAIL",sessionemail );
				userHome.putExtra("TYPE",1);
				startActivity(userHome);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		System.out.println("Inside on card details  --------> " + sessionemail);
		
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
