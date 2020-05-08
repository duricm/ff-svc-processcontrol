package com.futurefoundation.processcontrol;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import com.futurefoundation.processcontrol.entity.FFColumn;
import com.futurefoundation.processcontrol.entity.FFRow;
import com.futurefoundation.processcontrol.entity.User;
import com.futurefoundation.processcontrol.error.BadRequestException;
import com.futurefoundation.processcontrol.error.UnauthorizedException;
import com.futurefoundation.processcontrol.error.UserNotFoundException;
import com.futurefoundation.processcontrol.error.WrongFileTypeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.json.JSONObject;

@CrossOrigin(origins = {"http://localhost:3000", "https://card.btctest.net", "https://card.bitcoin.com", 
						"https://card.stage.cloud.bitcoin.com", "https://card.dev.cloud.bitcoin.com"}, allowCredentials = "true", 
methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
public class FFController {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	//private static String url = "jdbc:postgresql://3.136.241.73:5432/bitcoin-card?user=postgres&password=bch_admin&ssl=true&sslmode=verify-ca&sslrootcert=./.postgres/root.crt";

	private static Connection conn;
	
	
    @GetMapping(value = "/columns/{sheet}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FFColumn> getColumns(@PathVariable final String sheet) throws SQLException {
    
    	List<FFColumn> cList = new ArrayList<FFColumn>();

    	 String connectionStr = "jdbc:sqlserver://cfwsql2k14.cloudapp.net:1433;"
                 + "database=CFW.SPREADSHEET;"
                 + "user=mduric;"
                 + "password=mehmed2208;";
    	
    	if (conn == null)
			conn = DriverManager.getConnection(connectionStr);
		
		Statement s = conn.createStatement();
		ResultSet r = s.executeQuery("SELECT cc.COLUMN_NAME, cc.CONFIG_ID FROM CONFIG_COLUMNS cc, CONFIG_NAME cn " + 
									"where cc.CONFIG_ID = cn.CONFIG_ID and cn.CONFIG_NAME = '" + sheet + "' ORDER BY COLUMN_ORDER");
		
		while (r.next())
	    	cList.add(new FFColumn(r.getString("CONFIG_ID"), r.getString("COLUMN_NAME")));
    	
    	return cList;
    }

    @DeleteMapping(value = "/deleterow/{sheetId}/{rowNumber}", consumes = "*/*")
    @ResponseStatus(HttpStatus.OK)
    public List<FFRow> deleteRow(@PathVariable final String sheetId, @PathVariable final String rowNumber) throws Exception {

    	String connectionStr = "jdbc:sqlserver://cfwsql2k14.cloudapp.net:1433;"
             + "database=CFW.SPREADSHEET;"
             + "user=mduric;"
             + "password=mehmed2208;";
	
    	if (conn == null)
    		conn = DriverManager.getConnection(connectionStr);
	
    	PreparedStatement stmt = conn.prepareStatement("UPDATE SPREADSHEET_DATA SET SHEET_ROW_ACTIVE = 0 WHERE SHEET_ID = ? AND SHEET_ROW_NUMBER = ?");  
    	stmt.setString(1, sheetId);
    	stmt.setString(2, rowNumber);

    	int r = stmt.executeUpdate();
    	
    	return getRowsInternally(sheetId, true);
	
    }


    @PostMapping(value = "/addrow", consumes = "*/*")
    @ResponseStatus(HttpStatus.CREATED)
    public List<FFRow> addRow(@RequestBody String inputJsonStr) throws Exception {

      //String input = (String) inputJsonObj.get("input");

      JSONObject obj = new JSONObject(inputJsonStr);
    	
      String output = "The input you sent is :" + inputJsonStr;
      System.out.println(output);
      
 	 String connectionStr = "jdbc:sqlserver://cfwsql2k14.cloudapp.net:1433;"
             + "database=CFW.SPREADSHEET;"
             + "user=mduric;"
             + "password=mehmed2208;";
	
	if (conn == null)
		conn = DriverManager.getConnection(connectionStr);
	
	Statement s = conn.createStatement();
	ResultSet r = s.executeQuery("SELECT ISNULL(MAX(SHEET_ROW_NUMBER), -1) FROM SPREADSHEET_DATA WHERE SHEET_ID = " + obj.get("sheetId"));
	
	r.next();
	int rowNumber = r.getInt(1);
	
	System.out.println("Row number is: " + rowNumber);
	
	String sheetId = obj.getString("sheetId");
	System.out.println("Sheet id is: " + sheetId);
	obj.remove("sheetId");
	
	
	 Iterator keys = obj.keys();
	 while(keys.hasNext()) {
	   // loop to get the dynamic key
	   String currentColumn = (String)keys.next();
	 
	   String insertSt = "INSERT INTO SPREADSHEET_DATA VALUES (" + sheetId + ", '" + currentColumn + "', '" + 
			   obj.get(currentColumn) + "', " + (rowNumber + 1) + ", 1)";
	   
	   System.out.println(insertSt);
	 
	  //s = conn.createStatement();
	  s.executeUpdate(insertSt);
		
	 }
	 
 		return getRowsInternally(sheetId, true);

    
    }
    
    @GetMapping(value = "/sheetname", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getSheetNames() throws SQLException {
    	
    	List<String> sList = new ArrayList<String>();
    	
   	 	String connectionStr = "jdbc:sqlserver://cfwsql2k14.cloudapp.net:1433;"
             + "database=CFW.SPREADSHEET;"
             + "user=mduric;"
             + "password=mehmed2208;";
	
   	 	if (conn == null)
   	 		conn = DriverManager.getConnection(connectionStr);
	
   	 	Statement s = conn.createStatement();
   	 	ResultSet r = s.executeQuery("SELECT CONFIG_NAME FROM CONFIG_NAME");
    	
   	 	while (r.next())
   	 		sList.add(r.getString("CONFIG_NAME"));
	
    	return sList;
    }
    
    @GetMapping(value = "/rows/{sheet}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FFRow> getRows(@PathVariable final String sheet) throws SQLException {
    	
    	return getRowsInternally(sheet, false);
    	
    }

    private List<FFRow> getRowsInternally(String sheet, boolean isSheetId) throws SQLException
    {
    	
    	
      	 String connectionStr = "jdbc:sqlserver://cfwsql2k14.cloudapp.net:1433;"
                 + "database=CFW.SPREADSHEET;"
                 + "user=mduric;"
                 + "password=mehmed2208;";
    	
    	if (conn == null)
    		conn = DriverManager.getConnection(connectionStr);
    	
    	String sheetCondition;
    	
    	if (isSheetId)
    		sheetCondition = "ID";
    	else
    		sheetCondition = "NAME";
    	
    	
    	Statement s = conn.createStatement();
    	ResultSet r = s.executeQuery("select * from SPREADSHEET_DATA sd, CONFIG_COLUMNS cc, CONFIG_NAME cn where cn.CONFIG_" + sheetCondition + " = '" + sheet + "' AND sd.SHEET_ID = cn.CONFIG_ID and sd.SHEET_COLUMN_NAME = cc.COLUMN_NAME and sd.SHEET_ID = cc.CONFIG_ID and sd.SHEET_ROW_ACTIVE = 1 order by sheet_row_number, cc.COLUMN_ORDER");
    	
    	
    	int rowNumber = 0, previousRowNumber = 0, isActive = 0;
        
    	FFRow row = new FFRow();
    	
    	
        
    	List<FFRow> rList = new ArrayList<FFRow>();
    	List<String> rowList = new ArrayList<>();
    	
    	// Used if first row is not zero so that first empty row is not added in else statement. 
    	// previouwRowNumber = 0 assumes first row will be zero, if not, it means first row is not active
    	// and we need this variable to to add first empty row
    	boolean firstRun = true;
       
    	while (r.next())
    	{
    		rowNumber = r.getInt("SHEET_ROW_NUMBER");
    		isActive = r.getInt("SHEET_ROW_ACTIVE");
    		    		    	
    		if(rowNumber == previousRowNumber)
    		{
    			firstRun = false;
    			rowList.add(r.getString("SHEET_CELL_VALUE"));
    			row.setRowNumber(rowNumber);
    			row.setIsActive(isActive);
    		}
    		else
    		{
    			//Add row to the row list
    			previousRowNumber = rowNumber;

    			if (!firstRun)
    			{	
    				row.setRow(rowList);
    				rList.add(row);
    			}
    			else
    				firstRun = false;
    			
    			//Reset row objects for new row
    			row = new FFRow();
    			rowList = new ArrayList<>();
    			rowList.add(r.getString("SHEET_CELL_VALUE"));
    		
    		}		

        }
    	
    	// Adding last row after while loop exited
    	System.out.println("Adding last row: " + rowNumber);
    	row.setRowNumber(rowNumber);
		row.setIsActive(isActive);
    	row.setRow(rowList);
    	rList.add(row);	
    		
        	
        return rList;
    }
 
}
