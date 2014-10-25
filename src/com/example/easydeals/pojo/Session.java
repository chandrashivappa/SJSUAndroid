package com.example.easydeals.pojo;

public class Session {
	
    private static Session mInstance= null;

	private String userEmail;
	private int emailType;


	protected Session(){}

    public static synchronized Session getInstance(){
    	if(null == mInstance){
    		mInstance = new Session();
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