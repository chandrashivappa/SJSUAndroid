

package com.example.easydeals.implementation.facebook;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.example.easydeals.db.MongoDBHandler;
import com.example.easydeals.pojo.User;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;

public class FacebookImpl {
	
	private static final String TAG="Facebook";
	
	private MongoDBHandler mongoDB;
	String email;
	com.example.easydeals.pojo.Session easyDealsSession;
	
	private  void initMap(Map<String, String> categoryMap) {
		categoryMap.put(InterestMapper.CUSTOM_CLOTHING, "NO");
		categoryMap.put(InterestMapper.ELECTRONICS, "NO");
		categoryMap.put(InterestMapper.FOOD, "NO");
		categoryMap.put(InterestMapper.DRINKS, "NO");
		categoryMap.put(InterestMapper.BOOKS, "NO");
		categoryMap.put(InterestMapper.KIDS, "NO");
	}
	
	public  String makeMeRequest(final Session session) {

	    final User userInfo = new User();
		 
	    final Map<String, String> categoryMap = new HashMap<String, String>();
	    System.out.println("the fb session ===> " + session.toString());
	    initMap(categoryMap);
	 // basic profile
	   	 Request userRequest = new Request(
	 	        session,
	 	        "/me",
	 	        null,
	 	        HttpMethod.GET,
	 	        new Request.Callback() {
	 	            public void onCompleted(Response response) {
	 	   	           if (session == Session.getActiveSession()) {
		   	        	   if (response != null) {
		   	        	   Log.i(TAG, "user response is " + response.getRawResponse());
		   	        	   email = (String) response.getGraphObject().getProperty("email");
		   	        	   easyDealsSession = com.example.easydeals.pojo.Session.getInstance();
		   	        	   easyDealsSession.setUserId(email);
		   	        	   System.out.println("the user email is set in easydeals session");
		   	        	   easyDealsSession.setEmailType(1);
		   	        	   System.out.println("Email of the facebook user inside facebook impl -----> " + email);
		   	        	   String updatedTime = (String) response.getGraphObject().getProperty("updated_time");
		   	        	   String birthDay = (String) response.getGraphObject().getProperty("birthday");
		   	        	   String firstName = (String) response.getGraphObject().getProperty("first_name");
		   	        
		   	        	   String lastName = (String) response.getGraphObject().getProperty("last_name");
		   	        	   
		   	        	   userInfo.seteMail(email);
		   	        	   userInfo.setDob(birthDay);
		   	        	   userInfo.setfName(firstName);
		   	        	   userInfo.setlName(lastName);
		   	        	   userInfo.setEmailType(1);
		   	        	}
		   	        	// Ideally categoryMap should not contain email. But to work with DAO layer
		   	        	   // we need to put it here.
		   	        	   
	   	        		   categoryMap.put("eMail", email);
	                  }
	 	        }});

	    
	    Request likeRequest =  new Request(
	        session,
	        "/me/likes",
	        null,
	        HttpMethod.GET,
	        new Request.Callback() {
	            public void onCompleted(Response response) {
	            	
	   	           if (session == Session.getActiveSession()) {
	   	        	   if (response != null && response.getRawResponse() != null) {
	   	        	   Log.i(TAG, "response is " + response.getRawResponse());
//	   	        	   System.out.println(response.getRawResponse());
	   	        	   response.getGraphObject().getProperty("data");
	   	        	   //response.getGraphObject().getPropertyAsList(propertyName, graphObjectClass);
	   	        	   JSONArray categories = (JSONArray) response.getGraphObject().getProperty("data");
	   	        	   Set<String> categorySet = InterestMapper.getFaceBookCategories();

	   	        	   if (categories.length() > 0) {
	   	        		  List<String>categoryList = new ArrayList<String>();
	   	        		   for (int i = 0; i < categories.length(); i++) {
	   	        			   JSONObject category = categories.optJSONObject(i);
	   	        			   
	   	        			   
	   	        			   String categoryName = category.optString("category");
	   	        			   String categoryValue = category.optString("name");
	   	        			   if (categoryName.equalsIgnoreCase("Retail and consumer merchandise") || categoryName.equalsIgnoreCase("Clothing") ||
	   	        					   categoryName.equalsIgnoreCase("Company")){
	   	        				   if(categoryValue.equalsIgnoreCase("The Children's Place") || categoryValue.equalsIgnoreCase("Target Baby")
	   	        						   || categoryValue.equalsIgnoreCase("Babies \"R\" Us") || categoryValue.equalsIgnoreCase("Toys \"R\" Us")){
	   	        					   categoryName = "Kids";
	   	        				   } else {
	   	        					   categoryName = "Clothing";
	   	        				   }
	   	        			   } else if(categoryName.equalsIgnoreCase("Local business") || categoryName.equalsIgnoreCase("Restaurant/cafe")){
	   	        				   if(categoryValue.equalsIgnoreCase("Olive Garden")){
	   	        					   categoryName = "Food";
	   	        				   } else {
	   	        					   categoryName = "Books";
	   	        				   }
	   	        			   } else if(categoryName.equalsIgnoreCase("Food/beverages")){
	   	        				   //check whether it is food or beverage
	   	        				   if(categoryValue.equalsIgnoreCase("Starbucks") || categoryValue.equalsIgnoreCase("Jamba Juice")){
	   	        					   categoryName = "Drinks";
	   	        				   } else {
	   	        					   categoryName = "Food";
	   	        				   }
	   	        				   
	   	        			   }
	   	        			   System.out.println("Category name inside fb impl =====> " + categoryName);
	   	        			   System.out.println("Category value inside fb impl =====> " + categoryValue);
	   	        			   
	   	        			   String str = InterestMapper.getMatchedCategory(categoryName);
	   	        			   System.out.println("The value of str inside facebook imple ---> " + str);
	   	        			   
	   	        			   if (str != null) {
	   	        				   categoryMap.put(str, "YES");
	   	        			   }
	   	        		   }
	   	        		   categoryMap.put("locationPermission", "YES");
	   	        		   for(Map.Entry<String, String> cat : categoryMap.entrySet()){
	   	        			   System.out.println("KEY ==> " + cat.getKey() + " VALUE ===> " + cat.getValue());
	   	        		   }
	   	        		
	   	        	   } 	        	    
	   	        }
	               
	   	       if (response.getError() != null) {
	   	        	   Log.i(TAG, "Error happend " + response.getError().getErrorMessage());
	   	       }
	   	   }
	     }
	  });
		
   	
    	
   	RequestBatch requestBatch = new RequestBatch();
   	
   	requestBatch.add(userRequest);
   	
   	requestBatch.add(likeRequest);
   	
   	requestBatch.addCallback(new RequestBatch.Callback() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onBatchCompleted(RequestBatch batch) {
				//calling async task to insert user data
				new MongoDBInsert().execute(userInfo);
				
				//calling async task to insert user interest in mongo db
				new MongoDBUpdate().execute(categoryMap);

			}
   		});
   	
   	requestBatch.executeAsync();
   	
   	return userInfo.geteMail();
   	
	} 

	
	//async task class for inserting into mongo db
	class MongoDBUpdate extends AsyncTask<Map<String, String>, String, String>{
		MongoDBHandler mongoTest;
		@Override
		protected String doInBackground(Map<String, String>... params) {
			System.out.println("Inside async task of user interest activity");
			Map<String, String> userInt = new HashMap<String, String>(); 
			userInt = params[0];
			String email = userInt.get("eMail");
			System.out.println("The email got thru the category map iniside facebook impl ---> " + email);
			mongoTest = new MongoDBHandler();
			try {
				mongoTest.insertUserInterestCollection(userInt);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return email;
		}

		protected void onPostExecute(String st){
			//Actions to be performed, after interest data is inserted into mongo db
			System.out.println("User interest data inserted for user ---> !!" + st);
		}
	}


	// Async task, to insert data into mongo db, so that the main thread will
	// not be used.
	class MongoDBInsert extends AsyncTask<User, Void, String> {

		@Override
		protected String doInBackground(User... params) {
			System.out.println("Inside async task of register activity");

			// Instantiating mongo db handler class
			mongoDB = new MongoDBHandler();
			User user = (User) params[0];
			try {
				// calling the insert method to insert user data into mongo db
				System.out.println("User email inside async task of user insert " + user.geteMail());
				mongoDB.insertUserDataCollection(user);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return user.geteMail();
		}


		protected void onPostExecute(String st) {
			System.out.println("User data inserted in mongo db for user --->!!" + st);
			System.out.println("User email on postexecute of user data insert --->!!" + easyDealsSession.getUserId());
			System.out.println("The email type is  --->!!" + easyDealsSession.getEmailType());
			
			
		}

	}

}
