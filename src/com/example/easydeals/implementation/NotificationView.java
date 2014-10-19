package com.example.easydeals.implementation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.example.easydeals.R;

public class NotificationView extends ActionBarActivity {

	TextView adName, prodName, storeName, price, agePref;
	String aName, pName, store;
	Double cost;
	Integer age;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_notification);

		Intent adIntent = getIntent();
		if (adIntent != null) {
			aName = adIntent.getExtras().getString("AdName");
			pName = adIntent.getExtras().getString("ProdName");
			cost = adIntent.getExtras().getDouble("Price");
			store = adIntent.getExtras().getString("Store");
			age = adIntent.getExtras().getInt("AgePref");

		} else {
			aName = "None";
			pName = "None";
			cost = 0.0;
			store = "None";
			age = 0;
		}

		System.out
				.println("============== Iniside notification view class ==============");

		adName = (TextView) findViewById(R.id.adName);
		prodName = (TextView) findViewById(R.id.prodName);
		price = (TextView) findViewById(R.id.priceValue);
		storeName = (TextView) findViewById(R.id.storeName);
		agePref = (TextView) findViewById(R.id.ageValue);

		adName.setText(aName);
		prodName.setText(pName);
		price.setText("$" + cost);
		storeName.setText(store);
		agePref.setText(age + " and above!!");

	}
}
