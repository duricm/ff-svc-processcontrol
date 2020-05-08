package com.futurefoundation.processcontrol.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String email;
	private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private boolean isActive;
    private boolean promotioanlConsent;
    private String addressStreet;
	private String addressCity;
    private String addressPostalCode;
    private String addressState;
    private String addressCountry;
    private String addressStreet2;
    
    private String shippingAddressStreet;
	private String shippingAddressCity;
    private String shippingAddressPostalCode;
    private String shippingAddressState;
    private String shippingAddressCountry;
    private String shippingAddressStreet2;   
    private String defaultCurrencyId;
    private String socialSecurityNumber;
    private String username;
    private String password;
    private String bchAddress;
    private String btcAddress;
    
    private boolean debitCard;
       
    public boolean isDebitCard() {
		return debitCard;
	}

	public void setDebitCard(boolean debitCard) {
		this.debitCard = debitCard;
	}

	public String getBchAddress() {
		return bchAddress;
	}

	public void setBchAddress(String bchAddress) {
		this.bchAddress = bchAddress;
	}

	public String getBtcAddress() {
		return btcAddress;
	}

	public void setBtcAddress(String btcAddress) {
		this.btcAddress = btcAddress;
	}

	public String getShippingAddressStreet() {
		return shippingAddressStreet;
	}

	public void setShippingAddressStreet(String shippingAddressStreet) {
		this.shippingAddressStreet = shippingAddressStreet;
	}

	public String getShippingAddressStreet2() {
		return shippingAddressStreet2;
	}

	public void setShippingAddressStreet2(String shippingAddressStreet2) {
		this.shippingAddressStreet2 = shippingAddressStreet2;
	}

	public String getShippingAddressCity() {
		return shippingAddressCity;
	}

	public void setShippingAddressCity(String shippingAddressCity) {
		this.shippingAddressCity = shippingAddressCity;
	}

	public String getShippingAddressPostalCode() {
		return shippingAddressPostalCode;
	}

	public void setShippingAddressPostalCode(String shippingAddressPostalCode) {
		this.shippingAddressPostalCode = shippingAddressPostalCode;
	}

	public String getShippingAddressState() {
		return shippingAddressState;
	}

	public void setShippingAddressState(String shippingAddressState) {
		this.shippingAddressState = shippingAddressState;
	}

	public String getShippingAddressCountry() {
		return shippingAddressCountry;
	}

	public void setShippingAddressCountry(String shippingAddressCountry) {
		this.shippingAddressCountry = shippingAddressCountry;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAddressStreet2() {
		return addressStreet2;
	}

	public void setAddressStreet2(String addressStreet2) {
		this.addressStreet2 = addressStreet2;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isPromotioanlConsent() {
		return promotioanlConsent;
	}

	public void setPromotioanlConsent(boolean promotioanlConsent) {
		this.promotioanlConsent = promotioanlConsent;
	}

	public String getAddressStreet() {
		return addressStreet;
	}

	public void setAddressStreet(String addressStreet) {
		this.addressStreet = addressStreet;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	public String getAddressPostalCode() {
		return addressPostalCode;
	}

	public void setAddressPostalCode(String addressPostalCode) {
		this.addressPostalCode = addressPostalCode;
	}

	public String getAddressState() {
		return addressState;
	}

	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	public String getDefaultCurrencyId() {
		return defaultCurrencyId;
	}

	public void setDefaultCurrencyId(String defaultCurrencyId) {
		this.defaultCurrencyId = defaultCurrencyId;
	}

	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}
    
    public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// avoid this "No default constructor for entity"
    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
