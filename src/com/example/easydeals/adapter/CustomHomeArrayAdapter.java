package com.example.easydeals.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.easydeals.R;
import com.example.easydeals.pojo.Advertisement;

public class CustomHomeArrayAdapter extends ArrayAdapter<Advertisement>{
	
	public CustomHomeArrayAdapter(Context context,ArrayList<Advertisement> ads){
		super(context,R.layout.custom_home_layout,ads);
	}
	
	//Populate items in the list
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		Advertisement advertisement = getItem(position);
		
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_home_layout, parent, false);
		}

		TextView adName = (TextView)convertView.findViewById(R.id.adNameText);
		adName.setText(advertisement.getProductName() + "'s, " + advertisement.getAdName() + " for $" + advertisement.getPrice() + " for " + 
				" @ " + advertisement.getStoreName());
		return convertView;
	}
	
}
