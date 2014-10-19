package com.example.easydeals.implementation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.easydeals.R;
import com.example.easydeals.db.MongoDBHandler;

public class RegCompleteActivity extends ActionBarActivity implements OnClickListener{
	
	Button home;
	String email;
	MongoDBHandler mongoDB = new MongoDBHandler();
	/*private NotificationManager notificationManager;
	private int notificationId = 100;*/
	AdNotification adNotification;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_complete_activity);
		/*Intent regComplete = getIntent();
		email = regComplete.getExtras().getString("EMAIL");
		home = (Button)findViewById(R.id.homeButton);
		home.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View view) {
	        	 	//adNotification = new AdNotification();
	        	 	//adNotification.pushAdToUser(email);
	        	 	
	        	 	pushAdToUser(email);
	         }
	      });
		
		home.setOnClickListener(this);*/
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		/*case R.id.homeButton:
			Intent locationUpdateIntent = new Intent(this, UserLocationUpdate.class);
			locationUpdateIntent.putExtra("EMAIL", email);
			startActivity(locationUpdateIntent);
			break;
		default:
			break;
		*/}
	}
	
	/*public void pushAdToUser(String email) {
		Log.i("Start", "notification");
		Advertisement ad = mongoDB.getUserInterest(email);
		
		//getting values
		String adName = ad.getAdName();
		String prodName = ad.getProductName();
		Double price = ad.getPrice();
		int age = ad.getAgePreference();
		String adCategory = ad.getAdCategory();
		String storeName = ad.getStoreName();
		
		System.out.println("Ad Name ----> " + adName);
		System.out.println("Store Name ----> " + storeName);
		System.out.println("Category ----> " + adCategory);
		System.out.println("Price ----> " + price);
		System.out.println("Age ----> " + age);
		System.out.println("Product Name ----> " + prodName);
		
		
		
		//System.out.println("APPLICATION CONTEXT IS ===================> " + getApplicationContext());
		System.out.println("=================== Inside reg complete activity class ===================");
		// Invoking the default notification service 
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
		mBuilder.setContentTitle(adName);
		mBuilder.setContentText(prodName + " for $" + price
				+ " in " + storeName + " for ages "
				+ age + " and above !!!!");
		mBuilder.setTicker(adCategory + " Deals!!");
		mBuilder.setSmallIcon(R.drawable.logo295b);

		 //Creates an explicit intent for an Activity in your app 
		Intent resultIntent = new Intent(getApplicationContext(), NotificationView.class);
		//System.out.println("RESULT INTENT IS ================   "  + resultIntent);
		resultIntent.putExtra("AdName", adName);
		resultIntent.putExtra("ProdName", prodName);
		resultIntent.putExtra("AgePref", age);
		resultIntent.putExtra("Price", price);
		resultIntent.putExtra("Store", storeName);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// notificationID allows you to update the notification later on. 
		notificationManager.notify(notificationId, mBuilder.build());

	}*/
}
