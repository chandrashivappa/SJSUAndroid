package com.example.easydeals.db;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.easydeals.constants.EasyDealsDisplay;
import com.example.easydeals.pojo.User;

public class SqlDbHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 6;
	
	private static final String DATABASE_NAME = "CMPE295B_MOBILEDB";
	
	private static final String INTEREST_TABLE_NAME="INTEREST_INFO";
	private static final String INTEREST_ID = "interestId";
	private static final String INTEREST = "interest";
	
	private static final String USER_INTEREST_TABLE_NAME = "USER_INTEREST";
	private static final String food = "food";
	private static final String books = "books";
	private static final String kids = "kids";
	private static final String drink = "drinks";
	private static final String electronics = "electronics";
	private static final String cloth = "clothingApparel";
	
	private static final String TABLE_NAME="USER_INFO";
	private static final String ID = "userId";
	private static final String FNAME = "firstName";
	private static final String LNAME = "lastName";
	private static final String EMAIL = "email";
	private static final String PWD = "password";
	private static final String DOB = "dob";
	private static final String GENDER = "gender1";
	private static final String LOCPER = "locationPermission";
	private static final String LOCLAT = "locationLatitude";
	private static final String LOCLONG = "locationLongitude";
	//private static final String LOCALT = "LOCATION_ALTITUDE";
	private static final String[] USER_COLUMNS = {ID, FNAME, LNAME, EMAIL, PWD, DOB, GENDER};
	private static final String[] INTERST_COLUMNS = {EMAIL, books, cloth, drink, electronics, food, kids, LOCPER, LOCLAT, LOCLONG};

	Set<String> userInterest = null;
	
	public SqlDbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_USER_INFO_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " + FNAME + " TEXT, " +
		LNAME + " TEXT, " + EMAIL + " TEXT, " + PWD + " TEXT, " + DOB +" TEXT, " + GENDER +" TEXT)";
		
		db.execSQL(CREATE_USER_INFO_TABLE);
		
		String CREATE_INTEREST_TABLE = "CREATE TABLE " + INTEREST_TABLE_NAME + " (" + INTEREST_ID + " INTEGER PRIMARY KEY, "
				+ INTEREST + " TEXT)";
		db.execSQL(CREATE_INTEREST_TABLE);
		 
		String CREATE_USER_INTEREST_TABLE = "CREATE TABLE " + USER_INTEREST_TABLE_NAME + " (" + EMAIL + " TEXT, " + books + " TEXT, " 
											+ cloth + " TEXT, " + drink + " TEXT, " + electronics + " TEXT, " + food + " TEXT, " 
											+ kids + " TEXT, " +   LOCPER + " TEXT, " + LOCLAT + " REAL, " + LOCLONG + " REAL)";
		db.execSQL(CREATE_USER_INTEREST_TABLE);
		List<String> interestList = addInterestRows();
		insertInterestData(db, interestList);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + INTEREST_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + USER_INTEREST_TABLE_NAME);
		onCreate(db);
	}
	
	public List<String> addInterestRows(){
		List<String> interestList = new ArrayList<String>();
		interestList.add("FOOD");
		interestList.add("BOOKS");
		interestList.add("KIDS");
		interestList.add("HOT & COLD DRINKS");
		interestList.add("CLOTHING & APPAREL");
		interestList.add("ELECTRONICS");
		return interestList;
	}
	
	public void insertInterestData(SQLiteDatabase db, List<String> iList){
		ContentValues interestValue = new ContentValues();
		for(int i = 0; i < iList.size(); i++){
			interestValue.put(INTEREST, iList.get(i));
			db.insert(INTEREST_TABLE_NAME, null, interestValue);
		}
	}
	
	public void insertUserRecord(User user){
		SQLiteDatabase db = this.getWritableDatabase(); 
		ContentValues value = new ContentValues();
		value.put(FNAME, user.getfName());
		value.put(LNAME, user.getlName());
		value.put(EMAIL, user.geteMail());
		value.put(PWD, user.getPwd());
		value.put(DOB, user.getDob());
		value.put(GENDER, user.getGender());
		db.insert(TABLE_NAME, null, value);
		db.close();
	}
	
	/*Inserting user selected interests along with latitude and longitude 
	(currently inserting the house location manually)*/
	public void insertUserInterest(Map<String,String> userInt) throws UnknownHostException{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues interestValue = new ContentValues();
		for(String key : userInt.keySet()){
			interestValue.put(key, userInt.get(key));
		}
		interestValue.put(LOCLAT, 37.358005);
		interestValue.put(LOCLONG, -121.997102);
		db.insert(USER_INTEREST_TABLE_NAME, null, interestValue);
	}
	
	public void getUserRecord(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		User user = null;
		Cursor cursor = db.query(TABLE_NAME, USER_COLUMNS, "USER_ID = ?", new String[] 
				{String.valueOf(id)}, null, null, null, null);
		if(cursor != null){
			cursor.moveToFirst();
			user = new User();
			user.setUserId(Integer.parseInt(cursor.getString(0)));
			user.setfName(cursor.getString(1));
			user.setlName(cursor.getString(2));
			user.seteMail(cursor.getString(3));
			user.setPwd(cursor.getString(4));
			user.setDob(cursor.getString(5));
			user.setGender(cursor.getString(6));
		}
		cursor.close();
		db.close();
		EasyDealsDisplay.display(user);
	}
	
	public void getUserInterest(String email){
		//books, cloth, drink, electronics, food, kids, LOCPER, LOCLAT, LOCLONG
		SQLiteDatabase db = this.getReadableDatabase();
		Map<String, String> userMap = null;
		Cursor cursor = db.query(USER_INTEREST_TABLE_NAME, INTERST_COLUMNS, "EMAIL = ?", new String []
				{String.valueOf(email)}, null, null, null, null);
		if(cursor != null){
			cursor.moveToFirst();
			userMap = new HashMap<String, String>();
			userMap.put("EMAIL", cursor.getString(0));
			userMap.put("BOOKS", cursor.getString(1));
			userMap.put("CLOTHING & APPARAEL", cursor.getString(2));
			userMap.put("HOT & COLD DRINKS", cursor.getString(3));
			userMap.put("ELECTRONICS", cursor.getString(4));
			userMap.put("FOOD", cursor.getString(5));
			userMap.put("KIDS", cursor.getString(6));
			userMap.put("LOCATION PERMISSION", cursor.getString(7));
			userMap.put("LOCATION LATITIDE", cursor.getString(8));
			userMap.put("LOCATION LONGITUDE", cursor.getString(9));
		}
		
		cursor.close();
		db.close();
		EasyDealsDisplay.userInterestDisplay(userMap);
	}
	
	
	

}
