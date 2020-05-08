package com.futurefoundation.processcontrol.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Card {

    @Id
    @GeneratedValue
    private Long id;
    private String cardProvider;
    private String bchAddress;
    private String btcAddress;
    private String cardProviderId;
    private boolean kycApproved;
    private boolean cardCreated;


	public String getCardProvider() {
		return cardProvider;
	}

	public void setCardProvider(String cardProvider) {
		this.cardProvider = cardProvider;
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

	public String getCardProviderId() {
		return cardProviderId;
	}

	public void setCardProviderId(String cardProviderId) {
		this.cardProviderId = cardProviderId;
	}

	public boolean isKycApproved() {
		return kycApproved;
	}

	public void setKycApproved(boolean kycApproved) {
		this.kycApproved = kycApproved;
	}

	public boolean isCardCreated() {
		return cardCreated;
	}

	public void setCardCreated(boolean cardCreated) {
		this.cardCreated = cardCreated;
	}

	// avoid this "No default constructor for entity"
    public Card() {
    }

}
