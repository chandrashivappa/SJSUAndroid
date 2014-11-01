package com.example.easydeals.implementation;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.easydeals.R;
import com.example.easydeals.adapter.CustomHistoryAdapter;
import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.Advertisement;
import com.example.easydeals.pojo.EasyDealsSession;

public class HistoryActivity extends ListFragment implements OnItemSelectedListener{

	public Spinner spinner;
	EasyDealsSession session;
	String userEmail ;
	MongoDBHandler mongoDB;
	ArrayList<Advertisement>  advertisement;
	private static final String [] timePeriod = {"Select an Option", "This week", "Last week", "Last 30 days"};
	ListView adsHistoryList;
	ArrayAdapter<Advertisement> historyListAdapter;
	List<Advertisement> emptyList = null;
	CustomHistoryAdapter customHistoryAdapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        View historyView = inflater.inflate(R.layout.history_view, container, false);
	        session = EasyDealsSession.getInstance();
	        userEmail = session.getUserId();
	        mongoDB = new MongoDBHandler();
	        
	        spinner = (Spinner)historyView.findViewById(R.id.spinner1);
	        ListView listView = (ListView)historyView.findViewById(android.R.id.list);
	        System.out.println(listView);
	        ArrayAdapter<String> historyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, timePeriod);
	        historyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(historyAdapter);
	        spinner.setSelection(0);
	        spinner.setOnItemSelectedListener(this);
	        
	        return historyView;
	    }
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		String positionValue = null;
		switch(position){
		
		case 0:
			positionValue = parent.getItemAtPosition(position).toString();
			System.out.println("The item selected in the dropdown is " + positionValue);
			if(positionValue.equals(timePeriod[0])){
				System.out.println("You have to select an option to see history");
				customHistoryAdapter = new CustomHistoryAdapter(getActivity(),"No ads");
	        	setListAdapter(customHistoryAdapter);
			}
			break;
		case 1:
			positionValue = parent.getItemAtPosition(position).toString();
			if(positionValue.equals(timePeriod[1])){
					try {
						callMongoDB(1, userEmail);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
			}
			break;
		case 2:
			positionValue = parent.getItemAtPosition(position).toString();
			System.out.println("The item selected in the dropdown is " + positionValue);
			if(positionValue.equals(timePeriod[2])){
				System.out.println("You have selected to see last week deals");
				try {
					callMongoDB(2, userEmail);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case 3:
			positionValue = parent.getItemAtPosition(position).toString();
			System.out.println("The item selected in the dropdown is " + positionValue);
			if(positionValue.equals(timePeriod[3])){
				System.out.println("You have selected to see last 30 days deals");
				try {
					callMongoDB(3, userEmail);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	public void callMongoDB(int option, String email) throws UnknownHostException, ParseException{
		advertisement = mongoDB.getDealsHistory(option, email);
		
		if(advertisement != null){
        	customHistoryAdapter = new CustomHistoryAdapter(getActivity(), advertisement);
        	setListAdapter(customHistoryAdapter);
        }
	}
}
