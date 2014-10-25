package com.example.easydeals.implementation.facebook;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InterestMapper {
	
	private static Map<String, String> interestMap = new HashMap<String, String>();
	
	public static String CUSTOM_CLOTHING = "clothingApparel";
	public static String ELECTRONICS = "electronics";
	public static String FOOD = "food";
	public static String DRINKS = "drinks";
	public static String BOOKS = "books";
	public static String KIDS = "kids";
	public static String LOCATIONPERMISSION = "locationPermission";
	
	public static String FACEBOOK_CLOTHING = "Clothing";
	static {
		interestMap.put("Clothing", "clothingApparel");
		interestMap.put("Electronics", "electronics");
		interestMap.put("Food", "food");
		interestMap.put("Drinks", "drinks");
		interestMap.put("Books", "books");
		interestMap.put("Kids", "kids");
			
	}

	public static String getCustomizedInterest(String faceBookInterest) {
		return interestMap.get(faceBookInterest);
	}
	
	public static Set<String> getFaceBookCategories() {
		return interestMap.keySet();
	}
	
	public static String getMatchedCategory(String categoryName) {
		
		for (String str : interestMap.keySet()) {
			System.out.println("The string in interest map key set is ---> " + str);
			if (str.equalsIgnoreCase(categoryName)) {
				return interestMap.get(str);
			}
		}
		
		return null;
		
	}
	
}
