package com.bitcoin.card;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class AccessToken {

    @Id
    @GeneratedValue
    private Long id;
	private String access_token;

	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

}
