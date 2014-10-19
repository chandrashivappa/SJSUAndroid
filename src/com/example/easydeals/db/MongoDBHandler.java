package com.example.easydeals.db;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.easydeals.implementation.RegCompleteActivity;
import com.example.easydeals.pojo.Advertisement;
import com.example.easydeals.pojo.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBHandler {

	MongoClient mongoDB;
	List<String> yesList = new ArrayList<String>();
	int count = 0;
	
	private static final String MONGO_DB_NAME = "295B_MOBILEDB";
	private static final String USER_COLLECTION_NAME="USER_INFO";
	private static final String INTEREST_COLLECTION_NAME="INTEREST_INFO";
	private static final String AD_COLLECTION="ADVERTISEMENT";
	private static final String PUSHED_AD_DETAILS_COLLECTION = "PUSHED_AD_DETAILS";
	private static final String AD_COUNT_COLLECTION = "AD_COUNT";
	private static final String POS_COLLECTION = "POS_COLLECTION_NAME";
	private static final String LOCLAT = "locationLatitude";
	private static final String LOCLONG = "locationLongitude";
	private static final String MONGO_DB_HOST = "LOCALHOST";
	//AdNotification adNotification;
	RegCompleteActivity adNotification;
	//Advertisement advertisement;
	public MongoDBHandler() {
	}
	
	
	//Insert user details
	public boolean insertUserDataCollection(User user) throws UnknownHostException {
		boolean uniqueUserFlag = false;
		System.out.println("**************** Inside mongodb handler to insert user data ****************");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection userTable = db.getCollection(USER_COLLECTION_NAME);
		System.out.println("Got the user table ------> " + userTable.getName());
		uniqueUserFlag = isEmailUnique(user.geteMail());
		if(uniqueUserFlag){
			BasicDBObject userInfo = new BasicDBObject();
			userInfo.put("firstName", user.getfName());
			userInfo.put("lastName", user.getlName());
			userInfo.put("email", user.geteMail());
			userInfo.put("passWord", user.getPwd());
			userInfo.put("dob", user.getDob());
			userInfo.put("emailType", user.getEmailType());
			userTable.insert(userInfo);
			System.out.println("Inserted");
			mongoDB.close();
			return uniqueUserFlag;
		} else {
			uniqueUserFlag = false;
		}
		return uniqueUserFlag;
	}
	
	//check for unique email id for signup user
	public boolean isEmailUnique(String email){
		System.out.println("================Inside email uinque check================");
		boolean uniqueFlag = false;
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection userTable = db.getCollection(USER_COLLECTION_NAME);
			DBObject checkEmail = new BasicDBObject();
			checkEmail.put("email", email);
			
			DBObject retObject = userTable.findOne(checkEmail);
			if(retObject != null){
				System.out.println("Email already exists");
				uniqueFlag =  false;
			} else {
				System.out.println("Email not exists");
				uniqueFlag =  true;
			}
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return uniqueFlag;
	}
	
	//authenticate user
	public boolean authenticateUser(String eMail, String password) throws UnknownHostException{
		boolean flag = false;
		System.out.println("+++++++++++++ to check whether user exists and if then verify pwd +++++++++++++++");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection userTable = db.getCollection(USER_COLLECTION_NAME);
		DBObject queryUser = new BasicDBObject();
		queryUser.put("email", eMail);
		queryUser.put("passWord", password);
		
		DBObject retObject =  userTable.findOne(queryUser);
		if(retObject != null){
			System.out.println("The ret object is --------------> " + retObject);
			flag = true;
		} else {
			flag = false;
		}
		
		return flag;
	}
	
	public void createInterestCollection(List<String> interestList) throws UnknownHostException{
		/*currently manually creating the interest table and inserting the values
		 * in the local	mongo db. When deploying in cloud the below can be used*/	
		System.out.println("**************** Inside mongodb handler to insert interest data ****************");
		int count = 1;
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection interestTable = db.getCollection(INTEREST_COLLECTION_NAME);
		BasicDBObject interestInfo = new BasicDBObject();
		for(String interest : interestList){
			interestInfo.put("interestId", count);
			interestInfo.put("interest", interest);
			count++;
		}
		interestTable.insert(interestInfo);
		mongoDB.close();
	}
	
	public void insertUserInterestCollection(Map<String,String> userInt) throws UnknownHostException{
		System.out.println("**************** Inside mongodb handler to insert user interest data ****************");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection userCollection = db.getCollection(USER_COLLECTION_NAME);
		
		//Querying the USER_INFO collection to get the user based on the email id.
		String email = userInt.get("eMail");
		System.out.println("The user email id is ===> " + email);
		DBObject queryUser = new BasicDBObject();
		queryUser.put("email", email);
		
		
		userInt.remove("eMail");
		
		//Putting the user interest details in the userInterestInfo object
		DBObject parentUserObject = userCollection.findOne(queryUser);
		BasicDBObject userInterestInfo = new BasicDBObject();
		for(String key : userInt.keySet()){
			userInterestInfo.put(key, userInt.get(key));
		}
		
		// inserting the interest into main user collection based on the email
		parentUserObject.put("interests", userInterestInfo);
		parentUserObject.put(LOCLAT, 37.358005);
		parentUserObject.put(LOCLONG,-121.997102);
		userCollection.update(queryUser, parentUserObject);
		mongoDB.close();
	}
	
	//update user interests
	public void updateUserInterestCollections(Map<String, String> updatedUserInterest) throws UnknownHostException{
		System.out.println("**************** Inside mongodb handler to update user interest data ****************");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection userCollection = db.getCollection(USER_COLLECTION_NAME);
		
		//Querying the USER_INFO collection to get the user based on the email id.
		String email = updatedUserInterest.get("eMail");
		System.out.println("The user email id is ===> " + email);
		DBObject queryUser = new BasicDBObject();
		queryUser.put("email", email);
		updatedUserInterest.remove("eMail");
		
		//Updating the interest values of respective user
		DBCursor cursor = userCollection.find(queryUser);
		if(cursor.hasNext()){
			BasicDBObject updateValue = new BasicDBObject().append("$set", new BasicDBObject().append("interests.food", updatedUserInterest.get("food")).append("interests.kids", updatedUserInterest.get("kids")).
					append("interests.books", updatedUserInterest.get("books")).append("interests.electronics", updatedUserInterest.get("electronics")).append("interests.drinks", updatedUserInterest.get("drinks")).
					append("interests.clothingApparel", updatedUserInterest.get("clothingApparel")));
			userCollection.update(new BasicDBObject().append("email", email), updateValue);
		}
		System.out.println("Updated");
		cursor.close();
		mongoDB.close();
	}
	
	
	//insert user card details
	public String insertUserCardDetails(String email, String cardNum){
		String cardType;
		System.out.println("--------------Inside mongoDB handler to insert user card details--------------");
		if(cardNum.length() == 12){
			cardType = "Costco";
		} else {
			cardType = "Credit";
		}
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection userCollection = db.getCollection(USER_COLLECTION_NAME);
			
			DBObject searchUser = new BasicDBObject();
			searchUser.put("email",  email);
			
			//inserting card details in cardDetails field
			DBObject userDetails = userCollection.findOne(searchUser);
			BasicDBObject cardDetails = new BasicDBObject();
			cardDetails.put("cardType", cardType);
			cardDetails.put("cardNumber" , cardNum);
			
			//inserting carddetails field inside user info
			userDetails.put("cardDetails", cardDetails);
			userCollection.update(searchUser, userDetails);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return email;
	}
	
	//Insert details of the ads pushed with respect to the user
	public String insertAdPushedDetails(List<Advertisement> advertisement, String email) throws ParseException{
		System.out.println("**************** Inside mongodb handler to insert pushed ad details w.r.t. user ****************");
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection pushedAdCollection = db.getCollection(PUSHED_AD_DETAILS_COLLECTION);
			
			//Getting the date format
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			Calendar calendar = new GregorianCalendar();
			String pushedDate = sdf.format(calendar.getTime());
			Date pushDate = sdf.parse(pushedDate);
			
			//putting the values of the ad pushed for the user in basic db object 
			for(Advertisement adv : advertisement){
				BasicDBObject adPushed = new BasicDBObject();
				adPushed.put("userEmail", email);
				adPushed.put("adName", adv.getAdName());
				adPushed.put("adCategory", adv.getAdCategory());
				adPushed.put("productName", adv.getProductName());
				adPushed.put("storeName", adv.getStoreName());
				adPushed.put("storeLocation", adv.getStoreLocation());
				adPushed.put("price", adv.getPrice());
				adPushed.put("pushedDate",pushDate);
				adPushed.put("adId", adv.getId());
				
				//inserting the pushed ads into mongodb collection PUSHED_AD_DETAILS
				pushedAdCollection.insert(adPushed);
				System.out.println("The retailer id inside enter pushed ad details ============> " + adv.getId());
				insertAdCount(adv.getRetailerEmail(), adv.getId());
				
			}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return email;
	}
	
	//Getting the ads pushed to user to check so that, the same ad need not be pushed again
	public ArrayList<Advertisement> getAdsPushedToUser(String email) throws UnknownHostException, ParseException{
		System.out.println("Inside mongodb handler to get the pushed ads to display in a list");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection pushedAdCollection = db.getCollection(PUSHED_AD_DETAILS_COLLECTION);
		
		//date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = new GregorianCalendar();
		String pushedDate = sdf.format(calendar.getTime());
		Date pushDate = sdf.parse(pushedDate);
		
		DBObject pushedAdsQuery = new BasicDBObject();
		List<BasicDBObject> adsPushedToday = new ArrayList<BasicDBObject>();
		adsPushedToday.add(new BasicDBObject("userEmail", email));
		adsPushedToday.add(new BasicDBObject("pushedDate", pushDate));
		pushedAdsQuery.put("$and", adsPushedToday);

		DBCursor cursor = pushedAdCollection.find(pushedAdsQuery);
		
		ArrayList<Advertisement> advList = new ArrayList<Advertisement>();
		if(cursor != null){
			while(cursor.hasNext()){
				DBObject adsPushed = cursor.next();
				Advertisement listAds = new Advertisement();
				listAds.setAdName((String)adsPushed.get("adName"));
				listAds.setAdCategory((String)adsPushed.get("adCategory"));
				listAds.setProductName((String)adsPushed.get("productName"));
				listAds.setStoreName((String)adsPushed.get("storeName"));
				listAds.setStoreLocation((String)adsPushed.get("storeLocation"));
				listAds.setPrice((Double)adsPushed.get("price"));
				advList.add(listAds);
			}
		} else {
			Advertisement listEmptyAds = new Advertisement();
			listEmptyAds.setAdName("No Ads as of Now");
			listEmptyAds.setAdCategory(" !!!");
			listEmptyAds.setProductName("!!. Let's wait");
			listEmptyAds.setStoreName(" and watch..");
			listEmptyAds.setStoreLocation(" when we move ;)");
			listEmptyAds.setPrice(0.0);
			advList.add(listEmptyAds);
		}
		return advList;
	}

	//inserting the count of the respective ads pushed to several users for web ui
	public void insertAdCount(String retEmail, String adId) {
		System.out.println("====== Inside the insert ad count method ======");
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection adCountCollection = db.getCollection(AD_COUNT_COLLECTION);
			
			System.out.println("The retailer object id is " + adId);
			
			DBObject idSearchQuery = new BasicDBObject();
			idSearchQuery.put("adId", adId);
			
			DBCursor adDetails = adCountCollection.find(idSearchQuery);
			
			if(adDetails.hasNext()){
				System.out.println("When there is ad details ================>");
				BasicDBObject updateValue = new BasicDBObject().append("$inc", new BasicDBObject().append("count",1));
				adCountCollection.update(new BasicDBObject().append("adId", adId), updateValue);
			} else {
				System.out.println("When there is no ad details ================>");
				BasicDBObject newAdCount = new BasicDBObject();
				newAdCount.put("adId", adId);
				newAdCount.put("retailerEmail", retEmail);
				newAdCount.put("count", 1);
				adCountCollection.insert(newAdCount);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
		
	//This method is called from the getAdsByLocation method. This gets input as user email and ads that are
	//available in the user present location. The ads category is compared with the users interest category
	//and if it matches the ad can be pushed
	public List<Advertisement> getUserInterest(String email, Advertisement[] ad){
		System.out.println("**************** Inside mongodb handler to check for ad ****************");
		Map<String, String> userInt = null;
		List<Advertisement> resultList = new ArrayList<Advertisement>();
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection userCollection = db.getCollection(USER_COLLECTION_NAME);
			
			DBObject queryInterest = new BasicDBObject();
			queryInterest.put("email", email);
			DBCursor cursor = userCollection.find(queryInterest);
			while(cursor.hasNext()){
				DBObject temp = cursor.next();
				System.out.println("The specific fields " + temp.toString());
				BasicDBObject bdb = (BasicDBObject) temp.get("interests");
				userInt = new HashMap<String, String>();
				userInt.put("food", (String) bdb.get("food"));
				userInt.put("kids", (String) bdb.get("kids"));
				userInt.put("books", (String) bdb.get("books"));
				userInt.put("clothingApparel", (String) bdb.get("clothingApparel"));
				userInt.put("electronics", (String) bdb.get("electronics"));
				userInt.put("drinks", (String) bdb.get("drinks"));
				userInt.put("locationPermission", (String)bdb.get("locationPermission"));
			}
			cursor.close();
			mongoDB.close();
			//Comparing the interests checked by user with ad category. The result is added to the resultlist
			for(Map.Entry<String, String> userlikedCategory : userInt.entrySet()){
				if((userlikedCategory.getValue()).equals("YES")){
					String interestKey = userlikedCategory.getKey();
					for(Advertisement adList : ad){
						 if(adList.getAdCategory().equalsIgnoreCase(interestKey)){
							 resultList.add(adList);
						 }
					}
				}
				
			}
				
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	//This method is called from getAdsByLocation, to first get the pos recommended data for the user email
	public Integer[] getPOSRecommendation(String email) throws UnknownHostException{
		System.out.println("------------- Getting pos recommended data -------------");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection posCollection = db.getCollection(POS_COLLECTION);
		Integer [] recommendedProductId = null;
		DBObject queryUserProduct = new BasicDBObject();
		queryUserProduct.put("email", email);
		
		DBCursor recommendedId = posCollection.find(queryUserProduct);
		
		if(recommendedId != null){
			int count = recommendedId.count();
			System.out.println("Recommended product count is ===> " + count);
			recommendedProductId = new Integer[count];
			int index = 0;
			while(recommendedId.hasNext()){
				DBObject recommendedItems = recommendedId.next();
				recommendedProductId[index] = Integer.parseInt(String.valueOf(recommendedItems.get("productId")));
				index++;
			}
		} else {
			System.out.println("There are no recommendations for user");
		}
	
		return recommendedProductId;
	}
	

	//This method is called from the UserHomePageActivity, on detecting the location change of user phone.
	@SuppressWarnings("null")
	public List<Advertisement> getAdsByLocation(double longitude, double latitude, String email) throws ParseException{
		System.out.println("=================== Inside retrieve ad based on location ===================");
		Advertisement advertisement[] = null;
		List<Advertisement> resultList = new ArrayList<Advertisement>();
		List<Advertisement> finalList = new ArrayList<Advertisement>();
		Integer [] recommendation = null;
		try {
			//get the recommended product ids for the user from POS_NAME_COLLECTION
			recommendation = getPOSRecommendation(email);
			
			//Get the ads of the recommended product ids from pos, if pos returns product ids
			if(recommendation!= null){
				System.out.println("------------- When pos has ids for this user -------------");
				advertisement = getAdsBasedOnProductLocation(recommendation,longitude, latitude);
				//if there are ads for the recommended product ids, then check with user interest
				if(advertisement != null){
					System.out.println("------------- when there are ads for the pos ids -------------");
					//get user interest for the corresponding ads --> it matches the ad category in ad table with users interest in user info
					resultList = getUserInterest(email, advertisement);
					if(resultList != null){
						System.out.println("------------- when pos ads and user interest match -------------");
						//printing the ads that match user interest for testing purpose
						System.out.println("Resultant advertisements are ");
						for(Advertisement adv : resultList){
							System.out.println(adv.getId() + " ----> " + adv.getAdCategory() + " " + adv.getAdName() + " " + adv.getProductName() + " " +
									adv.getPrice() + " !!!");
						}
						//if resultList is not null, then check whether the ads has been pushed for the respective user
						finalList = isAdAlreadyPushed(resultList, email);
					} else {
						//if the recommended ads and user interest doesn't match, 1. then push only pos recommended products
						// which is nothing but the advertisements. 2. Check whether the ads have already been pushed. If not
						//push the ads
						for(Advertisement ad: advertisement){
							resultList.add(ad);
						}
						//check whether already pushed
						finalList = isAdAlreadyPushed(resultList, email);
					}
				} else {
					//if there are no ads for the recommended product ids. then push ads based on user interest
					//1. Get ads based on user location from advertisement collection. 2. Then get compare with user interest 
					//3. check whether already pushed.4. if not push
					finalList = getAdsOnlyBasedOnUserInterest(longitude, latitude, email);
				}
			} else {
				//if there are no recommendations from pos, then push ads based on user interest.
				finalList = getAdsOnlyBasedOnUserInterest(longitude, latitude, email);
			}
		} catch(UnknownHostException e){
			e.printStackTrace();
		}
		return finalList;
	}
	
	//This method is called from getAdsByLocation, when there are no recommended pos data or when there are no ads corresponding to
	//the recommended pos product ids
	public List<Advertisement> getAdsOnlyBasedOnUserInterest(double longitude, double latitude, String email) throws UnknownHostException, ParseException{
		System.out.println("------------- Getting ads only based on user interest as there are no ads related to pos/no pos -------------");
		Advertisement [] advertisement = null;
		List<Advertisement> resultList = new ArrayList<Advertisement>();
		List<Advertisement> finalList = new ArrayList<Advertisement>();
	
		advertisement = getAdsBasedOnProductLocation(null, longitude, latitude);
		if(advertisement != null){
			resultList = getUserInterest(email, advertisement);
			//The ads that match user interest
			System.out.println("Resultant advertisements are ");
			for(Advertisement adv : resultList){
				System.out.println(adv.getId() + " ----> " + adv.getAdCategory() + " " + adv.getAdName() + " " + adv.getProductName() + " " +
				adv.getPrice() + " !!!");
				
			}
			
			//To check whether the ads has been pushed
			finalList = isAdAlreadyPushed(resultList, email);
	}
		return finalList;
	}
	//This method is called from getAdsByLocation, after a set of recommended product ids is received from pos.
	public Advertisement[] getAdsBasedOnProductLocation(Integer [] recommendation, double longitude, double latitude) throws UnknownHostException, ParseException{
		System.out.println("------------- Getting ads based on pos data and location -------------");
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection adCollection = db.getCollection(AD_COLLECTION);
		Advertisement advertisement[] = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = new GregorianCalendar();
		String startDate = sdf.format(calendar.getTime());
		Date stDate = sdf.parse(startDate);

		DBObject queryAdByLoc = new BasicDBObject();
		List<BasicDBObject> locationObject = new ArrayList<BasicDBObject>();

		//Getting advertisements that match the location, and the advertisement expiry date
		//is not less than the current date
		if(recommendation != null){
			locationObject.add(new BasicDBObject("storeLongitude", longitude));
			locationObject.add(new BasicDBObject("storeLatitude", latitude));
			locationObject.add(new BasicDBObject("startDate", new BasicDBObject("$lte", stDate)));
			locationObject.add(new BasicDBObject("endDate", new BasicDBObject("$gte", stDate)));
			locationObject.add(new BasicDBObject("productId", new BasicDBObject("$in", recommendation)));
			queryAdByLoc.put("$and", locationObject);
		} else {
			locationObject.add(new BasicDBObject("storeLongitude", longitude));
			locationObject.add(new BasicDBObject("storeLatitude", latitude));
			locationObject.add(new BasicDBObject("startDate", new BasicDBObject("$lte", stDate)));
			locationObject.add(new BasicDBObject("endDate", new BasicDBObject("$gte", stDate)));
			queryAdByLoc.put("$and", locationObject);
		}
		DBCursor advDetails = adCollection.find(queryAdByLoc);
		int adCount = advDetails.count();
		advertisement = new Advertisement[adCount];
		int index = 0;
		if(adCount != 0 || advDetails != null){
			while(advDetails.hasNext()){
				DBObject adNotify = advDetails.next();
				advertisement[index] = new Advertisement();
				advertisement[index].setAdName((String)adNotify.get("adName"));
				advertisement[index].setProductName((String)adNotify.get("productName"));
				advertisement[index].setPrice((Double)adNotify.get("price"));
				advertisement[index].setStoreName((String)adNotify.get("storeName"));
				advertisement[index].setStoreLocation((String)adNotify.get("storeLocation"));
				advertisement[index].setAgePreference(((Number)adNotify.get("agePreference")).intValue());
				advertisement[index].setAdCategory((String)adNotify.get("adCategory"));
				advertisement[index].setRetailerEmail((String)adNotify.get("retailerEmail"));
				advertisement[index].setEndDate((Date)adNotify.get("endDate"));
				advertisement[index].setAdDesc((String)adNotify.get("adDesc"));
				advertisement[index].setId(adNotify.get("_id").toString());
				System.out.println("The object id of the retailer is ---> " + adNotify.get("_id").toString());
				index++;
			}
		}
		return advertisement;
	}
	
	//This method is called from getAdsByLocation, to check whether the ads that match user interest has already
	//been pushed or not. If the ad is already pushed, that will not be added to the list. If its not pushed, it will
	//be added and pushed.
	@SuppressWarnings("unchecked")
	public List<Advertisement> isAdAlreadyPushed(List<Advertisement> resultList, String userEmail){
		System.out.println("Inside mongodb handler to get the pushed ads to display in a list");
		List<Advertisement> finalList = new ArrayList<Advertisement>();
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection pushedAdCollection = db.getCollection(PUSHED_AD_DETAILS_COLLECTION);
			System.out.println("USER EMAIL ==========> " +userEmail);
			
			DBObject retrieveAdsForUser = new BasicDBObject();
			retrieveAdsForUser.put("userEmail", userEmail);
			DBCursor resultCursor = pushedAdCollection.find(retrieveAdsForUser);

			@SuppressWarnings("rawtypes")
			Set pushedIdSet = new HashSet();
			while(resultCursor.hasNext()){
				DBObject result = resultCursor.next();
				System.out.println(result);
				String tmp = (String)result.get("adId");
				pushedIdSet.add(tmp);
			}
			for(Advertisement ad : resultList){
				if(pushedIdSet.contains(ad.getId())){
					System.out.println("the ad is pushed");
				} else {
					System.out.println("the ad is not pushed");
					finalList.add(ad);
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return finalList;
	}

	//Getting only user interest to set the user interest to check, when the user selects
	//to edit the user interest.
	public Map<String, String> getOnlyUserInterest(String email){
		Map<String, String> userInt = null;
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection userCollection = db.getCollection(USER_COLLECTION_NAME);
			
			DBObject queryInterest = new BasicDBObject();
			queryInterest.put("email", email);
			DBCursor cursor = userCollection.find(queryInterest);
			while(cursor.hasNext()){
				DBObject temp = cursor.next();
				System.out.println("The specific fields " + temp.toString());
				BasicDBObject bdb = (BasicDBObject) temp.get("interests");
				System.out.println("BDB =========> " + bdb.toString());
				System.out.println("BDB size is ======> " +bdb.size());
				userInt = new HashMap<String, String>();
							
				System.out.println("The value is ---------------> " + bdb.get("food"));
				userInt.put("food", (String) bdb.get("food"));
				userInt.put("kids", (String) bdb.get("kids"));
				userInt.put("books", (String) bdb.get("books"));
				userInt.put("clothingApparel", (String) bdb.get("clothingApparel"));
				userInt.put("electronics", (String) bdb.get("electronics"));
				userInt.put("drinks", (String) bdb.get("drinks"));
				userInt.put("locationPermission", (String)bdb.get("locationPermission"));
			} 
		}catch(Exception e){
			e.printStackTrace();
	}
		return userInt;
	}
	
	/*To retrieve pushed ads for respective user based on the option selected
	 * Options are
	 * 1 --> This week deals
	 * 2 --> Last week deals
	 * 3 --> Last month deals
	 * */
	
	public ArrayList<Advertisement> getDealsHistory(int option, String userEmail) throws ParseException, UnknownHostException{

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = new GregorianCalendar();
		int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
		
		mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
		DB db = mongoDB.getDB(MONGO_DB_NAME);
		DBCollection pushedAdCollection = db.getCollection(PUSHED_AD_DETAILS_COLLECTION);
		System.out.println("USER EMAIL ==========> " +userEmail);

		ArrayList<Advertisement> advertisement = new ArrayList<Advertisement>();
		Date [] dateArray = null;
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		if(option > 0 && option < 4){
			if(option == 1){
				dateArray = new Date[currentDay];
				for(int i = 0 ; i < currentDay ; i++){
					System.out.println("The value of i is ---> " + i);
					dateArray[i] = sdf.parse((sdf.format(calendar.getTime())));
					calendar.add(Calendar.DATE, 1);
				}
			} else if(option == 2){
				dateArray = new Date[7];
				for(int i = 0 ; i < dateArray.length; i++){
					calendar.add(Calendar.DATE, -1);
					dateArray[i] = sdf.parse((sdf.format(calendar.getTime())));
				}
			}else {
				Calendar c = new GregorianCalendar();
				dateArray = new Date[30];
				for(int i = 0 ; i <dateArray.length; i++){
					dateArray[i] = sdf.parse((sdf.format(c.getTime())));
					c.add(Calendar.DATE, -1);
				}
			}
		}
		
		DBObject dateQueryObject = new BasicDBObject();
		List<BasicDBObject> queries = new ArrayList<BasicDBObject>();
		queries.add(new BasicDBObject("userEmail", userEmail));
		queries.add(new BasicDBObject("pushedDate", new BasicDBObject("$in", dateArray)));
		dateQueryObject.put("$and", queries);
		
		DBCursor thisweekDeals = pushedAdCollection.find(dateQueryObject);
		if(thisweekDeals != null){
			System.out.println("The ads of this week are--->");
			while(thisweekDeals.hasNext()){
				DBObject deals = thisweekDeals.next();
				Advertisement ads = new Advertisement();
				ads.setAdName((String)deals.get("adName"));
				ads.setAdCategory((String)deals.get("adCategory"));
				ads.setProductName((String)deals.get("productName"));
				ads.setPrice((Double)deals.get("price"));
				ads.setStoreName((String)deals.get("storeName"));
				ads.setStoreLocation((String)deals.get("storeLocation"));
				advertisement.add(ads);
			}
		}
		return advertisement;
	}
	
	
	//For testing purpose..to retrieve data from mongo db
	public void getUserDataFromDB(String email){
		System.out.println("**************** Inside mongodb handler to retrieve user data ****************");
		try {
			mongoDB = new MongoClient(MONGO_DB_HOST, 27017);
			DB db = mongoDB.getDB(MONGO_DB_NAME);
			DBCollection userCollection = db.getCollection(USER_COLLECTION_NAME);
			
			DBObject searchQuery = new BasicDBObject();
			searchQuery.put("email", email);
			System.out.println("Finished search query");
			
			DBCursor cursor = userCollection.find(searchQuery);
			while(cursor.hasNext()){
				System.out.println(".......................> "+cursor.next());
				
			}
			cursor.close();
			mongoDB.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	
	
	
	}
