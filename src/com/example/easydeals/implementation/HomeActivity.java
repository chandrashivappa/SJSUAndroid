package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;

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
	TextView emailText;
	MongoDBHandler mongoDB;
	ArrayList<Advertisement> advertisement = new ArrayList<Advertisement>();
	CustomHomeArrayAdapter customAdapter;
	View view;
	EasyDealsSession session;
	
	//inserted on oct 8 night

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = EasyDealsSession.getInstance();
		sessionEmail = session.getUserId();
		System.out.println("The user email inside home activity in onCreateView is ====> "+ sessionEmail);
		view = inflater.inflate(R.layout.home_view, container, false);
		emailText = (TextView) view.findViewById(R.id.textView1);
		mongoDB = new MongoDBHandler();
		
		
		
	try {
			advertisement = mongoDB.getAdsPushedToUser(sessionEmail);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ParseException e) {
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
