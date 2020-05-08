package com.futurefoundation.processcontrol.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class ResponseMessage {

    @Id
    @GeneratedValue
    private Long id;
	private String message;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}



    
  

}
