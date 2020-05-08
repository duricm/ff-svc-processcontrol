package com.futurefoundation.processcontrol.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;


public class FFRow {

    @Id
    @GeneratedValue
    private Long id;
	private List<String> row;
	private int rowNumber;
	private int isActive;
	
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public List<String> getRow() {
		return row;
	}
	public void setRow(List<String> row) {
		this.row = row;
	}

}
