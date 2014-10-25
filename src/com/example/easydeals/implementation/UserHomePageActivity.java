package com.example.easydeals.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.easydeals.R;
import com.example.easydeals.adapter.TabsPageAdapter;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.Advertisement;
import com.example.easydeals.pojo.Session;

public class UserHomePageActivity extends ActionBarActivity implements
		ActionBar.TabListener, LocationListener {
	private ViewPager viewPager;
	private TabsPageAdapter mAdapter;
	private ActionBar actionBar;
	LocationManager locationManager;
	String serviceProvider;
	double longitude, latitude;
	String email;
	MongoDBHandler mongoDB;
	Handler msgHandler;
	HomeActivity homeFragment;
	Session session;
	String sessionEmail;
	Thread periodicChecker;
	double previousLong = 0.0;
	double previousLat = 0.0;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_complete_activity);
		Intent locUpdateIntent = getIntent();
		email = locUpdateIntent.getExtras().getString("EMAIL");
		System.out.println("The email using intent inside user home page activity is " + email);
		session = Session.getInstance();
		sessionEmail = session.getUserId();
		type = session.getEmailType();
		System.out.println("The Session email id ---> " + sessionEmail);
		// Initialization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
		mAdapter = new TabsPageAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		mAdapter.setValue(sessionEmail);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle("Easy Deals");

		// Adding tabs
		actionBar.addTab(actionBar.newTab().setText("Home").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("History").setTabListener(this));
		Bundle fragBundle = new Bundle();
		fragBundle.putString("EMAIL", sessionEmail);
		try {
			mongoDB = new MongoDBHandler();
			boolean flag = mongoDB.checkCardPresent(sessionEmail, type);
			if (!flag) {
				callCardDetailAlert();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		System.out
				.println("<================= When session email is not null =================>");
		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Creating an empty criteria object
		Criteria criteria = new Criteria();
		// Getting the name of the provider that meets the criteria
		serviceProvider = locationManager.getBestProvider(criteria, false);
		if (serviceProvider != null && !serviceProvider.equals("")) {
			// Get the location from the given provider
			Location location = locationManager
					.getLastKnownLocation(serviceProvider);
			locationManager.requestLocationUpdates(serviceProvider, 5000, 0,
					this);
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
		 * on swiping the viewpager make respective tab selected
		 * */
		homeFragment = new HomeActivity();
		homeFragment.setArguments(fragBundle);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.pager, homeFragment).commit();
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
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
			Intent logoutIntent = new Intent(this, MainActivity.class);
			logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			System.out.println("Stopped the thread");
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
			BigDecimal bigLat = new BigDecimal(latitude);
			BigDecimal bigLong = new BigDecimal(longitude);
			bigLat = bigLat.setScale(3, RoundingMode.HALF_DOWN);
			bigLong = bigLong.setScale(3, RoundingMode.HALF_DOWN);
			latitude = bigLat.doubleValue();
			longitude = bigLong.doubleValue();

			System.out.println("Formatted LONGITUDE =============> "
					+ longitude);
			System.out.println("Formatted LATITUDE =============> " + latitude);

			mongoDB = new MongoDBHandler();
			try {
				if (longitude != previousLong && latitude != previousLat) {
					previousLong = longitude;
					previousLat = latitude;
					advertisement = mongoDB.getAdsByLocation(longitude,
							latitude, sessionEmail);
					listSize = advertisement.size();
					adArray = new Advertisement[listSize];
					int adArrayIndex = 0;
					for (Advertisement adsList : advertisement) {
						adArray[adArrayIndex] = adsList;
						adArrayIndex++;
					}
				} else {
					System.out
							.println("No need to call db, as user is in same location");
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (advertisement != null && listSize == 1) {
			System.out.println("AD NOT NULL");
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			int icon = R.drawable.logo295b;
			CharSequence notificationText = "Your AD Notification";
			long time = System.currentTimeMillis();
			Notification notification = new Notification(icon,
					notificationText, time);
			Context context = getApplicationContext();
			CharSequence contentTitle = "AD Notification";
			CharSequence contentText = adArray[0].getAdName() + " for $"
					+ adArray[0].getPrice() + " in "
					+ adArray[0].getStoreName() + " for ages "
					+ adArray[0].getAgePreference() + " above !!!!";
			try {
				sessionEmail = mongoDB.insertAdPushedDetails(advertisement,
						sessionEmail);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent notificationIntent = new Intent(getApplicationContext(),
					UserHomePageActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			int SERVER_DATA_RECEIVED = 1;
			notificationManager.notify(SERVER_DATA_RECEIVED, notification);

		} else if (advertisement != null && listSize > 1) {

			System.out.println("Ad is not null and has more than one ad");
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			int icon = R.drawable.logo295b;
			CharSequence notificationText = "Deals on more than 1 of your favorite things";
			long time = System.currentTimeMillis();
			Notification notification = new Notification(icon,
					notificationText, time);
			Context context = getApplicationContext();
			CharSequence contentTitle = "Deals on lot of products";
			CharSequence contentText = "";
			for (Advertisement ads : adArray) {
				contentText = contentText + " " + ads.getAdName() + " ";
			}
			try {
				sessionEmail = mongoDB.insertAdPushedDetails(advertisement,
						sessionEmail);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contentText = "Deals on " + contentText + " all of these!!";
			Intent notificationIntent = new Intent(getApplicationContext(),
					UserHomePageActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			int SERVER_DATA_RECEIVED = 1;
			notificationManager.notify(SERVER_DATA_RECEIVED, notification);

		}

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

	public void callCardDetailAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				UserHomePageActivity.this);
		builder.setTitle("Card Details!");
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent cardIntent = new Intent(getApplicationContext(),
						CardDetailsCollectionActivity.class);
				cardIntent.putExtra("EMAIL", sessionEmail);
				startActivity(cardIntent);
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setMessage("You have not yet entered your card details!!!");
		AlertDialog theAlert = builder.create();
		theAlert.show();
	}
}
