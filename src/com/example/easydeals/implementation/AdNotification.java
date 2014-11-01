package com.example.easydeals.implementation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.easydeals.R;
import com.example.easydeals.pojo.Advertisement;

public class AdNotification extends ActionBarActivity {


	// Context context;
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_detect);
		Intent adIntent = getIntent();
		Advertisement ad = (Advertisement) adIntent.getSerializableExtra("ADVERTISEMENT");
		pushAdToUser(ad);
		// context = AdNotification.this;
	}

	/*public void pushAdToUser(Context context, String email) {
		Log.i("Start", "notification");
		Advertisement ad = mongoDB.getUserInterest(email);
		System.out
				.println("=================== Inside ad notification class ===================");
		System.out.println(" context is ===> " + context);
		 Invoking the default notification service 
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		System.out.println("ADNOTIFICATION OF THIS --------------> "
				+ AdNotification.this);
		mBuilder.setContentTitle(ad.getAdName());
		mBuilder.setContentText(ad.getProductName() + " for $" + ad.getPrice()
				+ " in " + ad.getStoreName() + " for ages "
				+ ad.getAgePreference() + " above !!!!");
		mBuilder.setTicker(ad.getAdCategory() + " Deals!!");
		mBuilder.setSmallIcon(R.drawable.logo295b);

		 Creates an explicit intent for an Activity in your app 
		Intent resultIntent = new Intent(context, NotificationView.class);
		System.out.println("RESULT INTENT IS ================   "
				+ resultIntent);
		resultIntent.putExtra("AdName", ad.getAdName());
		resultIntent.putExtra("ProdName", ad.getProductName());
		resultIntent.putExtra("AgePref", ad.getAgePreference());
		resultIntent.putExtra("Price", ad.getPrice());
		resultIntent.putExtra("Store", ad.getStoreName());

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		 notificationID allows you to update the notification later on. 
		notificationManager.notify(notificationId, mBuilder.build());

	}*/

	public void pushAdToUser(Advertisement ad) {
		System.out.println("=================== Inside ad notification class ===================");
		if(ad !=  null){
			Intent resultIntent = new Intent(this, NotificationView.class);
			resultIntent.putExtra("AdName", ad.getAdName());
			resultIntent.putExtra("ProdName", ad.getProductName());
			resultIntent.putExtra("AgePref", ad.getAgePreference());
			resultIntent.putExtra("Price", ad.getPrice());
			resultIntent.putExtra("Store", ad.getStoreName());
			startActivity(resultIntent);
		} else {
			// Creates an explicit intent for an Activity in your app Intent
			Intent resultIntent = new Intent(this, NotificationView.class);
			resultIntent.putExtra("AdName", "No ADS");
			startActivity(resultIntent);
			
		}

	}

}
