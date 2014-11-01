package com.example.easydeals.pojo;

public class EasyDealsSession {
	
    private static EasyDealsSession mInstance= null;

	private String userEmail;
	private int emailType;


	protected EasyDealsSession(){}

    public static synchronized EasyDealsSession getInstance(){
    	if(null == mInstance){
    		mInstance = new EasyDealsSession();
    	}
    	return mInstance;
    }
	
	public void setUserId(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public String getUserId() {
		return this.userEmail;
	}
	public int getEmailType() {
		return emailType;
	}
	
	public void setEmailType(int emailType) {
		this.emailType = emailType;
	}

	
}