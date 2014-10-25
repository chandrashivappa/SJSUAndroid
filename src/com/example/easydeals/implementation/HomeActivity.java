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
import com.example.easydeals.pojo.Session;

public class HomeActivity extends ListFragment {
	String sessionEmail;
	TextView emailText;
	MongoDBHandler mongoDB;
	ArrayList<Advertisement> advertisement = new ArrayList<Advertisement>();
	CustomHomeArrayAdapter customAdapter;
	View view;
	Session session;
	
	//inserted on oct 8 night

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		//sessionEmail = getArguments().getString("EMAIL");
		session = Session.getInstance();
		sessionEmail = session.getUserId();
		System.out.println("The user email inside home activity in onCreateView is ====> "+ sessionEmail);
		view = inflater.inflate(R.layout.home_view, container, false);
		emailText = (TextView) view.findViewById(R.id.textView1);
		mongoDB = new MongoDBHandler();
		//emailText.setText(eMail);
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
			//ads_list[index] = ad.getAdName() + " : " + ad.getProductName() + " @" + ad.getStoreName() +", " + ad.getStoreLocation() 
				//	+ " for $" + ad.getPrice();
			//ads_list[index] = ad.getAdName();
			retrievedList[index] = ad.getAdName() + " : " + ad.getProductName() + " @" + ad.getStoreName();
			System.out.println(retrievedList[index]);
			index++;
		}
		
		/*ArrayAdapter<String> adAdapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,
				ads_list);*/
		customAdapter = new CustomHomeArrayAdapter(getActivity(), advertisement);
		ListView listView = (ListView)view.findViewById(android.R.id.list);
		setListAdapter(customAdapter);
		//setListAdapter(customAdapter);
		//setListAdapter(new CustomHomeArrayAdapter(getActivity(), retrievedList));
		//setListAdapter(new CustomHomeArrayAdapter(getActivity(), advertisement));
		return view;
	}

}
