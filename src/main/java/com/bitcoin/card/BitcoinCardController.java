package com.bitcoin.card;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
public class BitcoinCardController {
	
	private static String url = "jdbc:postgresql://3.136.241.73:5432/bitcoin-card?user=postgres&password=bch_admin&ssl=false";
	private static Connection conn;

    // Save
    @PostMapping("/users")
    //return 201 instead of 200
    @ResponseStatus(HttpStatus.CREATED)
    User newUser(@RequestBody User u) {
    	String sql = "insert into users (first_name, last_name, email, phone_number, date_of_birth, gender, is_active, promotional_consent" +
    	", address_street, address_city, address_postal_code, address_state, address_country, default_currency_id, social_security_number" +
    			", created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())";

    	
    try {
		if (conn == null)
			conn = DriverManager.getConnection(url);
		
    	PreparedStatement stmt = conn.prepareStatement(sql);  
    	stmt.setString(1,u.getFirstName());
    	stmt.setString(2,u.getLastName());
    	stmt.setString(3, u.getEmail());
    	stmt.setString(4, u.getPhoneNumber());
    	stmt.setString(5, u.getDateOfBirth());
    	stmt.setString(6, u.getGender());
    	stmt.setBoolean(7, u.isActive());
    	stmt.setBoolean(8, u.isPromotioanlConsent());
    	stmt.setString(9, u.getAddresStreet());
    	stmt.setString(10, u.getAddressCity());
    	stmt.setString(11, u.getAddressPostalCode());
    	stmt.setString(12, u.getAddressState());
    	stmt.setString(13, u.getAddressCountry());
    	stmt.setString(14, u.getDefaultCurrencyId());
    	stmt.setString(15, u.getSocialSecurityNumber());
    	System.out.println("Executing...");
    	stmt.execute();
    	System.out.println("Executed");
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		
		System.out.println("Error!!!");
		e.printStackTrace();
	}
    	
    	
        return u;
    }

    // Find
    @GetMapping("/books/{id}")
    User findOne(@PathVariable Long id) {
    	
    	User b = new User();

    	b.setId(1L);
    	
    	
        return b;
    }
    
    // Find
    @GetMapping("/user/{id}")
    User findUser(@PathVariable Long id) {
    	
    	System.out.println("Getting stuff...");
    	
    	//BitcoinRestClient brClient = new BitcoinRestClient();
    	
    	//brClient.callCardProviderAPI();
    	
    	User u = new User();
    	
    	try {
    		if (conn == null)
    			conn = DriverManager.getConnection(url);
    		
    		Statement s = conn.createStatement();
    		ResultSet r = s.executeQuery("select * from users where user_id = " + id);

    		setUserResultParameters(r, u);
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
         return u;
    }
    
    // Find
    @GetMapping("/user-email/{email}")
    User findUserByEmail(@PathVariable String email) {
    	
    	System.out.println("Getting stuff...");
    	
    	User u = new User();
    	
    	try {
    		if (conn == null)
    			conn = DriverManager.getConnection(url);
    		
    		Statement s = conn.createStatement();
    		ResultSet r = s.executeQuery("select * from users where email = " + email);

    		setUserResultParameters(r, u);
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
         return u;
    }


    // Save or update
    @PutMapping("/user")
    User saveOrUpdate(@RequestBody User u) {
    	
    	String sql = "update users set ";
    	
    try {
		if (conn == null)
			conn = DriverManager.getConnection(url);
		
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
		if (u.getAddresStreet() != null)
			sql += "address_street = '" + u.getAddresStreet() + "', ";
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
		
		sql += "updated_at= now() where user_id = " + u.getId();
		
		Statement s = conn.createStatement();
		s.execute(sql);

		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		
		System.out.println("Error!!!");
		e.printStackTrace();
	}

        return u;
    }

    // update author only
    @PatchMapping("/books/{id}")
    User patch(@RequestBody Map<String, String> update, @PathVariable Long id) {

        return null;

    }

    @DeleteMapping("/user/{id}")
    void deleteBook(@PathVariable Long id) {
    	
    	try {
    		if (conn == null)
    			conn = DriverManager.getConnection(url);
    		
    		Statement s = conn.createStatement();
    		boolean result = s.execute("delete from users where user_id = " + id);

    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
        
    }
    
    private void setUserResultParameters(ResultSet r, User u)
    {
		try {
			r.next();
			u.setId(r.getLong("user_id"));
			u.setFirstName(r.getString("first_name"));
			u.setLastName(r.getString("last_name"));
			u.setEmail(r.getString("email"));
			u.setPhoneNumber(r.getString("phone_number"));
			u.setDateOfBirth(r.getString("date_of_birth"));
			u.setGender(r.getString("gender"));
			u.setActive(r.getBoolean("is_active"));
			u.setPromotioanlConsent(r.getBoolean("promotional_consent"));
			u.setAddresStreet(r.getString("address_street"));
			u.setAddressCity(r.getString("address_city"));
			u.setAddressPostalCode(r.getString("address_postal_code"));
			u.setAddressState(r.getString("address_state"));
			u.setAddressCountry(r.getString("address_country"));
			u.setDefaultCurrencyId(r.getString("default_currency_id"));
			u.setSocialSecurityNumber(r.getString("social_security_number"));
			u.setCreatedAt(r.getTimestamp("created_at"));
			u.setUpdatedAt(r.getTimestamp("updated_at"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

}
