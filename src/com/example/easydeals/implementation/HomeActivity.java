package com.example.easydeals.implementation;

import java.util.ArrayList;

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
import com.example.easydeals.pojo.Advertisement;
import com.example.easydeals.pojo.EasyDealsSession;

public class HomeActivity extends ListFragment {
	String sessionEmail;
	TextView welcomeMsg;
	MongoDBHandler mongoDB;
	ArrayList<Advertisement> advertisement = new ArrayList<Advertisement>();
	CustomHomeArrayAdapter customAdapter;
	View view;
	EasyDealsSession session;
	
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
		welcomeMsg = (TextView) view.findViewById(R.id.homeWelMsg);
		mongoDB = new MongoDBHandler();
		
		try {
			advertisement = new AdDetails().execute(sessionEmail).get();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}

		if(advertisement != null && !advertisement.isEmpty()){
			welcomeMsg.setText("Welcome " + sessionEmail + " !! Today's deals are ");
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
		} else {
			welcomeMsg.setText("Welcome " + sessionEmail + " !! Let's move to get deals!! ");
		}
		return view;
	}
	


}
