package com.example.easydeals.implementation;

import java.text.ParseException;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.easydeals.R;
import com.example.easydeals.adapter.TabsPageAdapter;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.Advertisement;
import com.example.easydeals.pojo.EasyDealsSession;

public class UserHomePageActivity extends ActionBarActivity implements
		ActionBar.TabListener, LocationListener {
	private ViewPager viewPager;
	private TabsPageAdapter mAdapter;
	private ActionBar actionBar;
	private int numOfMessages = 0;
	private int notificationId = (int)System.currentTimeMillis();
	LocationManager locationManager;
	String serviceProvider;
	double longitude, latitude;
	String email;
	MongoDBHandler mongoDB;
	Handler msgHandler;
	HomeActivity homeFragment;
	EasyDealsSession session;
	String sessionEmail;
	Thread periodicChecker;
	double previousLong = 0.0;
	double previousLat = 0.0;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_complete_activity);
		session = EasyDealsSession.getInstance();
		sessionEmail = session.getUserId();
		type = session.getEmailType();
		System.out.println("The Session email id ---> " + sessionEmail);
		
		if(sessionEmail == null){
			Intent mainIntent = new Intent(this, MainActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
		} else {
			
			// Initialization
			viewPager = (ViewPager) findViewById(R.id.pager);
			actionBar = getSupportActionBar();
			mAdapter = new TabsPageAdapter(getSupportFragmentManager());
			viewPager.setAdapter(mAdapter);
			mAdapter.setValue(sessionEmail);
			actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
			actionBar.setHomeButtonEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setTitle("Easy Deals");
			actionBar.setTitle(Html.fromHtml("<font face=\"serif\" color=\"#FFFF66\"><big>" + getString(R.string.app_name) + "</big></font>"));
			
			// Adding tabs
			actionBar.addTab(actionBar.newTab().setText("Home").setTabListener(this));
			actionBar.addTab(actionBar.newTab().setText("History").setTabListener(this));
			Bundle fragBundle = new Bundle();
			fragBundle.putString("EMAIL", sessionEmail);
				
			// Getting LocationManager object
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			// Creating an empty criteria object
			Criteria criteria = new Criteria();
			
			// Getting the name of the provider that meets the criteria
			serviceProvider = locationManager.getBestProvider(criteria, false);
			if (serviceProvider != null && !serviceProvider.equals("")) {
				// Get the location from the given provider
				Location location = locationManager.getLastKnownLocation(serviceProvider);
				locationManager.requestLocationUpdates(serviceProvider, 5000, 0,this);
				if (location != null && session.getUserId() != null) {
					System.out.println("On location changed!!");
					onLocationChanged(location);
				} else {
					System.out.println("Location cannot be retrieved");
				}
			} else {
				System.out.println("No provider found");
			}
			/**
			 * 
			 * on swiping the viewpager make respective tab selected
			 * */
			homeFragment = new HomeActivity();
			homeFragment.setArguments(fragBundle);
			getSupportFragmentManager().beginTransaction().replace(R.id.pager, homeFragment).commit();
			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				
			@Override
			public void onPageSelected(int position) {
				// on changing the page make respective tab selected
				actionBar.setSelectedNavigationItem(position);
			}
				
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
				
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_icon, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			System.out.println("Pressed the settings button");
			Intent userData = new Intent(this, EditUser.class);
			userData.putExtra("EMAIL", sessionEmail);
			startActivity(userData);
			return true;
		case R.id.logout:
			System.out.println("Pressed the logout button");
			session.setUserId(null);
			if(type == 1){
				com.facebook.Session fbSession = com.facebook.Session.getActiveSession();
				if(fbSession != null){
					if(!fbSession.isClosed()){
						fbSession.closeAndClearTokenInformation();
					}
				}
			}
			Intent logoutIntent = new Intent(this, MainActivity.class);
			logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			System.out.println("Logged out");
			startActivity(logoutIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public String getUserEmail() {
		return sessionEmail;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Reselected!", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTrans) {
		// TODO Auto-generated method stub
		/*
		 * fragmentTrans.replace(R.id.home,homeFragment);
		 * fragmentTrans.commit();
		 */
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction fragmentTrans) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Advertisement[] adArray = null;
		List<Advertisement> advertisement = null;
		int listSize = 0;
		if (session.getUserId() != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			
			System.out.println(" LONGITUDE =============> "+ longitude);
			System.out.println(" LATITUDE =============> " + latitude);

			mongoDB = new MongoDBHandler();
			try {
				if (longitude != previousLong && latitude != previousLat) {
					previousLong = longitude;
					previousLat = latitude;
					advertisement = mongoDB.getAdsByLocation(longitude, latitude, sessionEmail);
					if(advertisement != null){
						listSize = advertisement.size();
						adArray = new Advertisement[listSize];
						int adArrayIndex = 0;
						for (Advertisement adsList : advertisement) {
							adArray[adArrayIndex] = adsList;
							adArrayIndex++;
						}
					}
				} else {
					System.out.println("No need to call db, as user is in same location");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		if (advertisement != null && listSize == 1) {
			System.out.println("AD NOT NULL");
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			CharSequence notificationText = "Easy Deals for Today!!";
			CharSequence contentTitle = "Easy Deals for you !!";
			CharSequence contentText = adArray[0].getAdName() + " for $" + adArray[0].getPrice() + " in " + adArray[0].getStoreName() + "!!";
			mBuilder.setContentTitle(contentTitle);
			mBuilder.setContentText(contentText).setNumber(++numOfMessages);
			mBuilder.setTicker(notificationText);
			mBuilder.setSmallIcon(R.drawable.notification_icon);
			mBuilder.setAutoCancel(true);
			PendingIntent resultPendingIntent = getPendingIntentForNotification(); 
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.notify(notificationId,mBuilder.build());
			System.out.println("Notification id inside list size 1 is ============> " + notificationId);
			sessionEmail = insertPushDetails(advertisement, sessionEmail);
		} else if (advertisement != null && listSize > 1) {
			System.out.println("Ad is not null and has more than one ad");
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			CharSequence notificationText = "Easy Deals on more than 1 of your favorite things";
			CharSequence contentTitle = "Deals on lot of products";
			CharSequence contentText = "";
			for (Advertisement ads : adArray) {
				contentText = contentText + " " + ads.getAdName() + " ";
			}
			contentText = "Deals on " + contentText + " all of these!!";
			mBuilder.setContentTitle(contentTitle);
			mBuilder.setContentText(contentText).setNumber(++numOfMessages);
			mBuilder.setTicker(notificationText);
			mBuilder.setSmallIcon(R.drawable.notification_icon);
			mBuilder.setWhen(System.currentTimeMillis());
			mBuilder.setAutoCancel(true);
			PendingIntent resultPendingIntent = getPendingIntentForNotification(); 
			mBuilder.setContentIntent(resultPendingIntent);
			notificationManager.notify(notificationId, mBuilder.build());
			System.out.println("Notification id inside list size > 1 is ============> " + notificationId);
			sessionEmail = insertPushDetails(advertisement, sessionEmail);
		}

	}
	
	public Intent getNotificationIntent(){
		Intent notificationIntent;
		if(session.getUserId()!= null){
			notificationIntent = new Intent(getApplicationContext(),UserHomePageActivity.class);
		} else {
			System.out.println("================Came till here to check on logout and pressing notification icon================");
			notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
		}
		return notificationIntent;
	}

	public PendingIntent getPendingIntentForNotification(){
		Context context = getApplicationContext();
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(UserHomePageActivity.class);
		Intent notificationIntent = getNotificationIntent();
		stackBuilder.addNextIntent(notificationIntent);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_CANCEL_CURRENT);
		return resultPendingIntent;
	}
	public String insertPushDetails(List<Advertisement> ad, String userEmail){
		try {
			userEmail = mongoDB.insertAdPushedDetails(ad,userEmail);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return userEmail;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	}
