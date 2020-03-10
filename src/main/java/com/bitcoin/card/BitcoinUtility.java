package com.bitcoin.card;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bitcoin.card.error.UserNotFoundException;

public class BitcoinUtility {
	
	private static Connection conn;
	
    protected String getUserId(String username) throws SQLException
    {
    	
		if (conn == null)
			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    	
    	String id = "";
    	Statement s = conn.createStatement();

    	ResultSet r = s.executeQuery("select user_id from users where user_name = '" + username + "'");
    	if (r.next() == false)
    		throw new UserNotFoundException("User " + username + " not found in our system.");
    	else
    		id = r.getString("user_id");
    
    	return id;
    }

}
