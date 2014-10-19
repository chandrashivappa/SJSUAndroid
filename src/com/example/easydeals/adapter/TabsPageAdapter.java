package com.example.easydeals.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.easydeals.implementation.HistoryActivity;
import com.example.easydeals.implementation.HomeActivity;

public class TabsPageAdapter extends FragmentPagerAdapter{

	Bundle bundle = new Bundle();
	String email;
	public TabsPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public void setValue(String email){
		this.email = email;
	}
	
	public String getValue(){
		return email;
	}
	@Override
	public Fragment getItem(int index) {
		switch(index){
		case 0:
			Fragment homeFragment = new HomeActivity();
			bundle.putString("EMAIL", getValue());
			homeFragment.setArguments(bundle);
			return homeFragment;
			
		case 1:
			return new HistoryActivity();
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
