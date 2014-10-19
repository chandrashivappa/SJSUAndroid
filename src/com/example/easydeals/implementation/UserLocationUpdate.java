package com.example.easydeals.implementation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.example.easydeals.R;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.Advertisement;

public class UserLocationUpdate extends ActionBarActivity{

	LocationManager locationManager;
	String serviceProvider;
	double longitude, latitude;
	String email = null;
	MongoDBHandler mongoDB;
	Handler msgHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.example.easydeals.R.layout.activity_main);
		Intent locUpdateIntent = getIntent();
		email = locUpdateIntent.getExtras().getString("EMAIL");
		Thread periodicChecker = new PeriodicChecker(this);
		periodicChecker.start();
	}
		
		
	class PeriodicChecker extends Thread implements LocationListener{
		private UserLocationUpdate parent;
		private MainActivity mainParent;
		
		public PeriodicChecker(UserLocationUpdate parent){
			this.parent = parent;
		}
		
		public PeriodicChecker(MainActivity mainParent){
			this.mainParent = mainParent;
		}
		@Override
		public void run(){
			while(true){
				Looper.prepare();
				System.out.println("<================= Inside periodic checker =================>");
				// Getting LocationManager object
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				// Creating an empty criteria object
				Criteria criteria = new Criteria();
				// Getting the name of the provider that meets the criteria
				serviceProvider = locationManager.getBestProvider(criteria, false);

				if (serviceProvider != null && !serviceProvider.equals("")) {
					// Get the location from the given provider
					Location location = locationManager.getLastKnownLocation(serviceProvider);
					locationManager.requestLocationUpdates(serviceProvider, 200, 0, this);
					if (location != null){
						System.out.println("On location changed!!");
						onLocationChanged(location);
					} else {
						System.out.println("Location cannot be retrieved");
						parent.runOnUiThread(new Runnable(){
							public void run(){
								Toast.makeText(getBaseContext(), "Location can't be retrieved",	Toast.LENGTH_SHORT).show();
							}
						});
					}
				} else {
					System.out.println("No provider found");
					parent.runOnUiThread(new Runnable(){
						public void run(){
							Toast.makeText(getApplicationContext(), "No Provider Found",Toast.LENGTH_SHORT).show();
						}
					});
				}
				try {
					Thread.sleep(60000);
					Looper.loop();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}			

			@SuppressWarnings("deprecation")
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				List<String> locationAddress = new ArrayList<String>();
				System.out.println("The email id inside onLocationChanges is ===============> " + email);
				longitude = location.getLongitude();
				latitude = location.getLatitude();
				System.out.println("LONGITUDE =============> " + longitude);
				System.out.println("LATITUDE =============> " + latitude);
				try {
					locationAddress = getAddress(longitude, latitude);
					for(String add: locationAddress){
						System.out.println(add);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				mongoDB = new MongoDBHandler();
				//Advertisement advertisement = new Advertisement();
				//Advertisement advertisement  = mongoDB.getAdsByLocation(longitude, latitude);
				/*if(advertisement != null){
					System.out.println("AD NOT NULL");
					NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					int icon = R.drawable.logo295b;
					CharSequence notificationText = "Your AD Notification";
					long time = System.currentTimeMillis();
					
					resultIntent.putExtra("AdName", ad.getAdName());
					resultIntent.putExtra("ProdName", ad.getProductName());
					resultIntent.putExtra("AgePref", ad.getAgePreference());
					resultIntent.putExtra("Price", ad.getPrice());
					resultIntent.putExtra("Store", ad.getStoreName());
					
					Notification notification = new Notification(icon, notificationText, time);
					Context context = getApplicationContext();
					CharSequence contentTitle = "AD Notification";
					CharSequence contentText = advertisement.getProductName() + " for $" + advertisement.getPrice()
							+ " in " + advertisement.getStoreName() + " for ages "
							+ advertisement.getAgePreference() + " above !!!!";
					Intent notificationIntent = new Intent(getApplicationContext(), AdNotification.class);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					int SERVER_DATA_RECEIVED = 1;
					notificationManager.notify(SERVER_DATA_RECEIVED, notification);*/
					
				/*} else {
					NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					int icon = R.drawable.logo295b;
					CharSequence notificationText = "Your AD Notification";
					long time = System.currentTimeMillis();
					
					Notification notification = new Notification(icon, notificationText, time);
					Context context = getApplicationContext();
					CharSequence contentTitle = "AD Notification";
					CharSequence contentText = "This shop don't have any offers now!!";
					Intent notificationIntent = new Intent(getApplicationContext(), AdNotification.class);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					int SERVER_DATA_RECEIVED = 1;
					notificationManager.notify(SERVER_DATA_RECEIVED, notification);
					
					
				}*/
				
				
				/*Notification notification = new Notification(icon, notiText, meow);

				Context context = getApplicationContext();
				CharSequence contentTitle = "Your notification";
				CharSequence contentText = "Some data has arrived!";
				Intent notificationIntent = new Intent(this, YourActivityThatYouWantToLaunch.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

				int SERVER_DATA_RECEIVED = 1;
				notificationManager.notify(SERVER_DATA_RECEIVED, notification);*/
				/*mBuilder = new NotificationCompat.Builder(getBaseContext());
				mBuilder.setContentTitle("No ADS");
				mBuilder.setContentText("This shop don't have any offers now!!");
				mBuilder.setTicker("AD NOTIFICATION");
				mBuilder.setSmallIcon(R.drawable.logo295b);*/
				
				
				
				
				
				
				/*Intent notificationIntent = new Intent(getApplicationContext(), AdNotification.class);
				notificationIntent.putExtra("ADVERTISEMENT",advertisement);
				startActivity(notificationIntent);*/
				/*try{
					Runnable pushThread = new Runnable(){
						@Override
						public void run() {
							System.out.println("**************** Inside runnable thread after getting the locations ****************");
							Advertisement advertisement = mongoDB.getAdsByLocation(longitude, latitude);
							postAdvertisement(advertisement);
						}
						
					};
					Thread runnableThread = new Thread(pushThread);
					runnableThread.start();
					
				}catch(Exception e){
					e.printStackTrace();
				}*/
				/*try{
					Thread.sleep(20000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}*/
						
				//new NotificationAlert().execute(longitude, latitude);
				
				/*AdNotification adNotification = new AdNotification();
				adNotification.pushAdToUser(getApplicationContext(), email);*/
			}

			/*public void postAdvertisement(Advertisement advertisement){
				Intent notificationIntent = new Intent(getApplicationContext(), AdNotification.class);
				notificationIntent.putExtra("ADVERTISEMENT",advertisement);
				startActivity(notificationIntent);
			}*/
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
			
			
			public List<String> getAddress(double longitude, double latitude) throws IOException{
				
				Geocoder geocoder;
				List<Address> addresses;
				List<String> locationAddress = new ArrayList<String>();
				geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
				addresses = geocoder.getFromLocation(latitude, longitude, 1);

				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
				locationAddress.add(address);
				locationAddress.add(city);
				locationAddress.add(country);
				return locationAddress;
				
			}

			
			
		}
	
	 /*class NotificationAlert extends AsyncTask<Double, Void, Advertisement>{


		@Override
		protected Advertisement doInBackground(Double... params) {
			System.out.println("Inside async task after getting the locations");
			
			mongoDB = new MongoDBHandler();
			double longitude = params[0];
			double latitude = params[1];
			Advertisement advertisement = mongoDB.getAdsByLocation(longitude, latitude);
			return advertisement;
		}
		 
		protected void onPostExecute(Advertisement advertisement){
			Intent notificationIntent = new Intent(getApplicationContext(), AdNotification.class);
			notificationIntent.putExtra("ADVERTISEMENT",advertisement);
			startActivity(notificationIntent);
		}
		 
	 }*/
	
	
		
	}

