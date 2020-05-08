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
    	ResultSet r = s.executeQuery("select * from SPREADSHEET_DATA sd, CONFIG_COLUMNS cc, CONFIG_NAME cn where cn.CONFIG_" + sheetCondition + " = '" + sheet + "' AND sd.SHEET_ID = cn.CONFIG_ID and sd.SHEET_COLUMN_NAME = cc.COLUMN_NAME and sd.SHEET_ID = cc.CONFIG_ID order by sheet_row_number, cc.COLUMN_ORDER");
    	
    	
    	int rowNumber = 0, previousRowNumber = 0;
        String columnName;
        String cellValue;
        
    	FFRow row = new FFRow();
        
    	List<FFRow> rList = new ArrayList<FFRow>();
    	List<String> rowList = new ArrayList<>();
       
    	while (r.next())
    	{
    		System.out.println("Row number is: " + r.getInt("SHEET_ROW_NUMBER") + " previous row number: " + previousRowNumber +  
    				" cell value : " + r.getString("SHEET_CELL_VALUE"));
    		rowNumber = r.getInt("SHEET_ROW_NUMBER");
    	
    		if(rowNumber == previousRowNumber)
    		{
    			System.out.println("Adding cell value row equal: " + r.getString("SHEET_CELL_VALUE"));
    			rowList.add(r.getString("SHEET_CELL_VALUE"));
    		}
    		else
    		{
    			//Add row to the row list
    			previousRowNumber = rowNumber;
    			row.setRow(rowList);
    			rList.add(row);
    			
    			//Reset row objects for new row
    			row = new FFRow();
    			rowList = new ArrayList<>();
    			rowList.add(r.getString("SHEET_CELL_VALUE"));
    			System.out.println("Adding cell value row NOT equal: " + r.getString("SHEET_CELL_VALUE"));


    		
    		}		

        }
    	// Adding last row after while loop exited
    	row.setRow(rowList);
    	rList.add(row);	
    		
        	
        	return rList;
    }
    // Save
    @PostMapping(value = "/users", consumes = "*/*")
    @ResponseStatus(HttpStatus.CREATED)
    void newUser(@RequestBody User u, @RequestHeader(name = "authorization") Optional<String> authorization) throws SQLException {
    	String sql = "insert into users (first_name, last_name, email, phone_number, date_of_birth, gender, is_active, promotional_consent" +
    	", address_street, address_city, address_postal_code, address_state, address_country, default_currency_id, social_security_number" +
    			", user_name, address_street_2, shipping_address_street, shipping_address_city, shipping_address_postal_code, shipping_address_state, " + 
    	"shipping_address_country, shipping_address_street_2, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())";
    	    	
    	LOGGER.info("Adding new user to database...");
    	LOGGER.info("User data: \n" + u.toString());
    	


    	//String username = th.decodeVerifyCognitoToken(authorization);
    	
		if (conn == null)
			conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\\\sqlexpress;user=sa;password=secret");
		
    	PreparedStatement stmt = conn.prepareStatement(sql);  
    	stmt.setString(1,u.getFirstName());
    	stmt.setString(2,u.getLastName());
    	stmt.setString(3, u.getEmail());
    	stmt.setString(4, u.getPhoneNumber());
    	stmt.setString(5, u.getDateOfBirth());
    	stmt.setString(6, u.getGender());
    	stmt.setBoolean(7, u.isActive());
    	stmt.setBoolean(8, u.isPromotioanlConsent());
    	stmt.setString(9, u.getAddressStreet());
    	stmt.setString(10, u.getAddressCity());
    	stmt.setString(11, u.getAddressPostalCode());
    	stmt.setString(12, u.getAddressState());
    	stmt.setString(13, u.getAddressCountry());
    	stmt.setString(14, u.getDefaultCurrencyId());
    	stmt.setString(15, u.getSocialSecurityNumber());
    	stmt.setString(16, u.getUsername());
    	stmt.setString(17, u.getAddressStreet2());
    	
    	stmt.setString(18, u.getShippingAddressStreet());
    	stmt.setString(19, u.getShippingAddressCity());
    	stmt.setString(20, u.getShippingAddressPostalCode());
    	stmt.setString(21, u.getShippingAddressState());
    	stmt.setString(22, u.getShippingAddressCountry());
    	stmt.setString(23, u.getShippingAddressStreet2());
    	
    	LOGGER.info("Executing insert statement...");
    	stmt.execute();
    	LOGGER.info("Inserted new user in database.");

    }
    
    /*
    @GetMapping("/get-text")
    public @ResponseBody String getText() throws Exception {
    	
		if (conn == null)
			conn = DriverManager.getConnection(url);
    	
        final InputStream in = getClass().getResourceAsStream("/card.jpg");

    	PreparedStatement ps = conn.prepareStatement("INSERT INTO user_documents (user_id, document) VALUES (?, ?)");
    	ps.setInt(1, 2);
    	ps.setBinaryStream(2, in);
    	ps.execute();
    	ps.close();

    	return "Hello world";
    }

    
    @GetMapping("/get-image")
    public @ResponseBody byte[] getImage() throws IOException {
        final InputStream in = getClass().getResourceAsStream("/card.jpg");

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/get-image-with-media-type", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImageWithMediaType() throws IOException {
        final InputStream in = getClass().getResourceAsStream("/card.jpg");
        return IOUtils.toByteArray(in);
    }
*/
    
    @GetMapping(value = "/get-file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getFile() throws IOException {
    	System.out.println("MEHMED getting file...");
    	
    	File f = new File("/db_backup.sh");
        System.out.println("File path: " + f.getAbsolutePath());
    	
        final InputStream in = getClass().getResourceAsStream("C:\\projects\\bitcoincom-svc-cardapi\\db_backup.sh");
        
        System.out.println("Input stream is " + in);
        return IOUtils.toByteArray(in);
    }
    
     // Find
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/me")
    User findMyUserInfo(@RequestHeader(name = "authorization") Optional<String> authorization) {
    	
    	User u = new User();
    	
    	try {
    		if (conn == null)
    			conn = DriverManager.getConnection("");
    		    		
    		Statement s = conn.createStatement();
    		ResultSet r = s.executeQuery("select * from users where user_name = '" );
    		


    		    		
    	} catch (SQLException e) {
    		
	    	LOGGER.info("Exception!!!\n" + e.getMessage());

    		e.printStackTrace();
    	}
    	
    	LOGGER.info("Retrieved user data: \n" + u.toString());
    	
         return u;
    }
    
 
    // Save or update
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/users/me", consumes = "*/*")
    void updateUser(@RequestBody User u, @RequestHeader(name = "authorization") Optional<String> authorization) throws SQLException {
 
    	LOGGER.info("User data: \n" + u.toString());
    	

    	String sql = "update users set ";
 
		if (conn == null)
			conn = DriverManager.getConnection("");
		
		if (u.getFirstName() != null)
			sql += "first_name = '" + u.getFirstName() + "', ";
		if (u.getLastName() != null)
			sql += "last_name = '" + u.getLastName() + "', ";
		if (u.getEmail() != null)
			sql += "email = '" + u.getEmail() + "', ";
		if (u.getPhoneNumber() != null)
			sql += "phone_number = '" + u.getPhoneNumber() + "', ";
		if (u.getDateOfBirth() != null)
			sql += "date_of_birth = '" + u.getDateOfBirth() + "', ";
		if (u.getGender() != null)
			sql += "gender = '" + u.getGender() + "', ";
		if (u.getAddressStreet() != null)
			sql += "address_street = '" + u.getAddressStreet() + "', ";
		if (u.getAddressCity() != null)
			sql += "address_city = '" + u.getAddressCity() + "', ";
		if (u.getAddressPostalCode() != null)
			sql += "address_postal_code = '" + u.getAddressPostalCode() + "', ";
		if (u.getAddressState() != null)
			sql += "address_state = '" + u.getAddressState() + "', ";
		if (u.getAddressCountry() != null)
			sql += "address_country = '" + u.getAddressCountry() + "', ";
		if (u.getDefaultCurrencyId() != null)
			sql += "default_currency_id = '" + u.getDefaultCurrencyId() + "', ";
		if (u.getSocialSecurityNumber() != null)
			sql += "social_security_number = '" + u.getSocialSecurityNumber() + "', ";
		if (u.getAddressStreet2() != null)
			sql += "address_street_2 = '" + u.getAddressStreet2() + "', ";
		
		// Shipping address parameters
		if (u.getShippingAddressStreet() != null)
			sql += "shipping_address_street = '" + u.getShippingAddressStreet() + "', ";
		if (u.getShippingAddressCity() != null)
			sql += "shipping_address_city = '" + u.getShippingAddressCity() + "', ";
		if (u.getShippingAddressPostalCode() != null)
			sql += "shipping_address_postal_code = '" + u.getShippingAddressPostalCode() + "', ";
		if (u.getShippingAddressState() != null)
			sql += "shipping_address_state = '" + u.getShippingAddressState() + "', ";
		if (u.getShippingAddressCountry() != null)
			sql += "shipping_address_country = '" + u.getShippingAddressCountry() + "', ";
		if (u.getShippingAddressStreet2() != null)
			sql += "shipping_address_street_2 = '" + u.getShippingAddressStreet2() + "', ";
		
		sql += "updated_at= now() where user_name = '";
		
		Statement s = conn.createStatement();
		
		int result = s.executeUpdate(sql);
		
		if (result == 0)
			throw new UserNotFoundException(u.getId().toString());
		
    }

    @DeleteMapping("/users/me")
    void deleteUser(@RequestHeader(name = "authorization") Optional<String> authorization) throws SQLException {


    }
    

}
