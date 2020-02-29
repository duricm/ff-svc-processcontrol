package com.bitcoin.card.entity;

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
    private String addresStreet;
	private String addressCity;
    private String addressPostalCode;
    private String addressState;
    private String addressCountry;
	private String cardProviderId;
    private String addresStreet2;
    
    private String shippingAddresStreet;
	private String shippingAddressCity;
    private String shippingAddressPostalCode;
    private String shippingAddressState;
    private String shippingAddressCountry;
    private String shippingAddresStreet2;

    
    private String defaultCurrencyId;
    private String socialSecurityNumber;
    private String username;
    private String password;
    
    
    
    public String getShippingAddresStreet2() {
		return shippingAddresStreet2;
	}

	public void setShippingAddresStreet2(String shippingAddresStreet2) {
		this.shippingAddresStreet2 = shippingAddresStreet2;
	}

	public String getShippingAddresStreet() {
		return shippingAddresStreet;
	}

	public void setShippingAddresStreet(String shippingAddresStreet) {
		this.shippingAddresStreet = shippingAddresStreet;
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
    
    public String getAddresStreet2() {
		return addresStreet2;
	}

	public void setAddresStreet2(String addressStreet2) {
		this.addresStreet2 = addressStreet2;
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

	public String getAddresStreet() {
		return addresStreet;
	}

	public void setAddresStreet(String addresStreet) {
		this.addresStreet = addresStreet;
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

    
    public String getCardProviderId() {
		return cardProviderId;
	}

	public void setCardProviderId(String cardProviderId) {
		this.cardProviderId = cardProviderId;
	}

}