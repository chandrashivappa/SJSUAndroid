package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.easydeals.R;
import com.example.easydeals.adapter.CustomHomeArrayAdapter;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.implementation.CardDetailsCollectionActivity.CardPresentDetails;
import com.example.easydeals.pojo.Advertisement;
import com.example.easydeals.pojo.EasyDealsSession;
import com.example.easydeals.pojo.User;

public class HomeActivity extends ListFragment {
	String sessionEmail;
	TextView emailText;
	MongoDBHandler mongoDB;
	ArrayList<Advertisement> advertisement = new ArrayList<Advertisement>();
	CustomHomeArrayAdapter customAdapter;
	View view;
	EasyDealsSession session;
	
	//inserted on oct 8 night
	
	//async task class for inserting into mongo db
	public class AdDetails extends AsyncTask<String, Void, ArrayList<Advertisement>>{
	
		MongoDBHandler mongoDB;
		String email;
		Context context;
		boolean cardPresent = false;
				
		@Override
		protected ArrayList<Advertisement> doInBackground(String...params){
			email = params[0];
			
			ArrayList<Advertisement> advertisement = null;
			
			if(email != null){
				
				try {
					mongoDB = new MongoDBHandler();
					advertisement = mongoDB.getAdsPushedToUser(sessionEmail);
	
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			return advertisement;
		}
			    
	}



	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = EasyDealsSession.getInstance();
		sessionEmail = session.getUserId();
		System.out.println("The user email inside home activity in onCreateView is ====> "+ sessionEmail);
		view = inflater.inflate(R.layout.home_view, container, false);
		emailText = (TextView) view.findViewById(R.id.textView1);
		mongoDB = new MongoDBHandler();
		
		
		
//	try {
//			advertisement = mongoDB.getAdsPushedToUser(sessionEmail);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
		try {
			advertisement = new AdDetails().execute(sessionEmail).get();
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}

		String retrievedList[] = new String[advertisement.size()];
		int index = 0;
		for(Advertisement ad : advertisement){
			retrievedList[index] = ad.getAdName() + " : " + ad.getProductName() + " @" + ad.getStoreName();
			System.out.println(retrievedList[index]);
			index++;
		}
		
		customAdapter = new CustomHomeArrayAdapter(getActivity(), advertisement);
		ListView listView = (ListView)view.findViewById(android.R.id.list);
		System.out.println(listView);
		setListAdapter(customAdapter);
		return view;
	}
	


}
