package com.example.easydeals.pojo;

import java.io.Serializable;
import java.util.Date;

public class Advertisement implements Serializable{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String retailerEmail;
	private String adName;
	private String adDesc;
	private String adCategory;
	private String productName;
	private Integer agePreference;
	private String sexPreference;
	private Double price;
	private String storeName;
	private String storeLocation; 
	private String city;
	private String state;
	private String zipcode;
	private Date startDate;
	private Date endDate;
	private int adCount;
	private int yesCount;
	private Integer productId;

	

	public Advertisement(){
		
	}
	
	public Advertisement(String adName, String productName, Double price, String storeName, String storeLocation){
		this.adName = adName;
		this.productName = productName;
		this.price = price;
		this.storeName = storeName;
		this.storeLocation = storeLocation;
	}
	public Integer getProductId() {
		return productId;
	}
	
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getYesCount() {
		return yesCount;
	}
	public void setYesCount(int yesCount) {
		this.yesCount = yesCount;
	}
	public int getAdCount() {
		return adCount;
	}
	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}
	public String getRetailerEmail() {
		return retailerEmail;
	}
	public void setRetailerEmail(String retailerEmail) {
		this.retailerEmail = retailerEmail;
	}
	public String getAdName() {
		return adName;
	}
	public void setAdName(String adName) {
		this.adName = adName;
	}
	public String getAdDesc() {
		return adDesc;
	}
	public void setAdDesc(String adDesc) {
		this.adDesc = adDesc;
	}
	public String getAdCategory() {
		return adCategory;
	}
	public void setAdCategory(String adCategory) {
		this.adCategory = adCategory;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getAgePreference() {
		return agePreference;
	}
	public void setAgePreference(Integer agePreference) {
		this.agePreference = agePreference;
	}
	public String getSexPreference() {
		return sexPreference;
	}
	public void setSexPreference(String sexPreference) {
		this.sexPreference = sexPreference;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStoreLocation() {
		return storeLocation;
	}
	public void setStoreLocation(String storeLocation) {
		this.storeLocation = storeLocation;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	

}
