package com.futurefoundation.processcontrol.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class UsernameOrEmail {
	
    @Id
    @GeneratedValue
    private Long id;
	private String usernameOrEmail;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsernameOrEmail() {
		return usernameOrEmail;
	}
	public void setUsernameOrEmail(String usernameOrEmail) {
		this.usernameOrEmail = usernameOrEmail;
	}

}
