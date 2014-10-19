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
	    
	    initMap(categoryMap);
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
	   	        			   
	   	        			   String str = InterestMapper.getMatchedCategory(categoryName);
	   	        			   
	   	        			   if (str != null) {
	   	        				   categoryMap.put(str, "YES");
	   	        			   }
	   	        		   }
	   	        		   	   	        		   
	   	        	   } 	        	    
	   	        	   
	   	        }

	               
	   	       if (response.getError() != null) {
	   	        	   Log.i(TAG, "Error happend " + response.getError().getErrorMessage());

	   	       }

	   	   }

	     }

	  });
		
   	
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
//	   	        	   System.out.println(response.getRawResponse());
	   	        	   
	   	        	   
	   	        	   String email = (String) response.getGraphObject().getProperty("email");
	   	        	   String updatedTime = (String) response.getGraphObject().getProperty("updated_time");
	   	        	   String birthDay = (String) response.getGraphObject().getProperty("birthday");
	   	        	   String firstName = (String) response.getGraphObject().getProperty("first_name");
	   	        	   String lastName = (String) response.getGraphObject().getProperty("last_name");
	   	        	   
	   	        	   userInfo.seteMail(email);
	   	        	   userInfo.setDob(birthDay);
	   	        	   userInfo.setfName(firstName);
	   	        	   userInfo.setlName(lastName);
	   	        	   userInfo.setEmailType(2);
	   	        	   
	   	        	   // Ideally categoryMap should not contain email. But to work with DAO layer
	   	        	   // we need to put it here.
	   	        	   categoryMap.put("eMail", email);
	  // 	        	   userInfo.setGender();
	   	        	   
	   	        	   
	   	        	}
                  }
 	        }});

   	RequestBatch requestBatch = new RequestBatch();
   	
  	requestBatch.add(likeRequest);
   	requestBatch.add(userRequest);
   	
   	requestBatch.addCallback(new RequestBatch.Callback() {
			
			@Override
			public void onBatchCompleted(RequestBatch batch) {
				// TODO Auto-generated method stub
								
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

		@Override
		protected String doInBackground(Map<String, String>... params) {
			System.out.println("Inside async task of user interest activity");
			mongoDB = new MongoDBHandler();
			Map<String, String> userInt = params[0];
			String email = userInt.get("eMail");
			try {
				mongoDB.insertUserInterestCollection(userInt);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return email;
		}

		protected void onPostExecute(String st){
			//Actions to be performed, after interest data is inserted into mongo db
			System.out.println("User interest data inserted!!");

			//For testing purposes retrieving data from mongodb
//			mongoDB = new MongoDBHandler();
//			mongoDB.getUserDataFromDB(st);
//			Intent successIntent = new Intent(getApplicationContext(), CardDetailsCollectionActivity.class);
//			successIntent.putExtra("EMAIL", st);
//			startActivity(successIntent);
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
				mongoDB.insertUserDataCollection(user);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return user.geteMail();
		}

		protected void onPostExecute(String st) {
			System.out.println("User data inserted in mongo db!!");

			// Moving to the user interest activity page, once the user info is
			// inserted
//			session = Session.getInstance();
//			session.setUserId(st);
//			Intent userIntent = new Intent(getApplicationContext(),
//					UserInterestActivity.class);
//			userIntent.putExtra("EMAIL", userInfo.geteMail());
//			startActivity(userIntent);
		}

	}

}
