package com.example.easydeals.constants;

import java.util.Map;

import com.example.easydeals.pojo.User;

public class EasyDealsDisplay {
	
	
	public static void display(User u) {
		System.out.println("User first name ====> " + u.getfName());
		System.out.println("User last name ====> " + u.getlName());
		System.out.println("User email ====> " + u.geteMail());
		System.out.println("User dob ====> " + u.getDob());
		System.out.println("User gender ====> " + u.getGender());
	}
	
	
	public static void userInterestDisplay(Map<String, String> userMap){
		System.out.println("*****************************************");
		for(String key : userMap.keySet()){
			System.out.println(key + " ==============> " + userMap.get(key));
		}
	}

}
