package com.bitcoin.card;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.bitcoin.card.entity.User;
import com.bitcoin.card.error.UnauthorizedException;

public class BitcoinRestClient extends BitcoinUtility {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	private String ternioBaseURL = "https://whitelabel.dev.api1.blockcard.ternio.co/v1/user";
	private String agilePartnersBaseURL = "https://api.sandbox.prysym.com/v1";

	private static Connection dbConn;
	
	public void createAPAccount(User u) throws SQLException
	{
	  try {

		String output;
		BufferedReader br = null;

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL(agilePartnersBaseURL + "/accounts/create");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer sk_test_tyZEMb7u1HkdqV7XD4Epu2XN8hsbYrmd");
		conn.setDoOutput(true);
		
	    JSONObject dataObj = new JSONObject();
	    JSONObject accountHolderObj = new JSONObject();
	    JSONObject billingObj = new JSONObject();
	    JSONObject addressObj = new JSONObject();
	    JSONObject documentsObj = new JSONObject();
	    JSONObject proofOfIdentityObj = new JSONObject();
	    	    
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    
	    dataObj.put("program_id", BitcoinConstants.AP_PROGRAM_ID);
	    dataObj.put("currency_code", BitcoinConstants.AP_CURRENCY_CODE);

	    accountHolderObj.put("country_code", u.getAddressCountry());
	    accountHolderObj.put("legal_entity_type", "INDIVIDUAL");  
	    accountHolderObj.put("first_name", u.getFirstName());
	    accountHolderObj.put("last_name", u.getLastName());
	    accountHolderObj.put("birth_date", u.getDateOfBirth());
	    accountHolderObj.put("email", u.getEmail());
	    accountHolderObj.put("phone", u.getPhoneNumber());

	    addressObj.put("country_code", u.getAddressCountry());
	    addressObj.put("country_subdivision_code", u.getAddressCountry() + "-" + u.getAddressState());
	    addressObj.put("city", u.getAddressCity());
	    addressObj.put("postal_code", u.getAddressPostalCode());
	    addressObj.put("line1", u.getAddressCity());
	    //billingObj.put("username", u.getUsername());
	    
	    proofOfIdentityObj.put("type", u.getAddressCity());
	    proofOfIdentityObj.put("front_side", "0170d50b-087c-c044-ae20-d647b9f4ef77");
	    proofOfIdentityObj.put("back_side", "0170d50b-087c-c044-ae20-d647b9f4ef77");
	    proofOfIdentityObj.put("live_photo", "0170d50b-087c-c044-ae20-d647b9f4ef77");
	    
	    documentsObj.put("proof_of_identity", proofOfIdentityObj);

	    billingObj.put("address", addressObj);

	    accountHolderObj.put("billing", billingObj);
	    accountHolderObj.put("documents", documentsObj);
	    dataObj.put("account_holder", accountHolderObj);
			
			System.out.println(dataObj.toString());

			wr.writeBytes(dataObj.toString());
		    wr.flush();
		    wr.close();

		if (conn.getResponseCode() != 200) {
			
			br =  new BufferedReader(new InputStreamReader(
					(conn.getErrorStream())));
				
				while ((output = br.readLine()) != null) {
					LOGGER.info(output);
				}
			
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
						
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		br =  new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		

		String jsonTempString = null;
		output = "";
		
		do {
			jsonTempString = br.readLine();
			output += jsonTempString;
			LOGGER.info("Output object: " + jsonTempString);
		} while (jsonTempString != null);
		
	    JSONObject currentObject = new JSONObject(output);
	    
	    JSONObject obj = currentObject.getJSONObject("data");
	    
	    LOGGER.info("Object is " + obj.toString());
	    
	    u.setCardProviderId(obj.getString("id"));
	    createAPCard(u);
	    	    		
		conn.disconnect();
		
		if (dbConn == null)
			dbConn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    	        
    	PreparedStatement ps = dbConn.prepareStatement("UPDATE USERS SET CARD_PROVIDER_ID = ? WHERE USER_NAME = ?");
    	ps.setString(1, u.getCardProviderId());
    	ps.setString(2, u.getUsername());

    	ps.execute();
    	ps.close();
    	LOGGER.info("Updated card provider id.");

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	  catch (JSONException e) {
		e.printStackTrace();
	}
	}
	
	public void createAPCard(User u) throws SQLException
	{
	  try {

		String output;
		BufferedReader br = null;

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL(agilePartnersBaseURL + "/cards/create");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer sk_test_tyZEMb7u1HkdqV7XD4Epu2XN8hsbYrmd");
		conn.setDoOutput(true);
		
	    JSONObject dataObj = new JSONObject();
	    JSONObject shippingObj = new JSONObject();
	    JSONObject addressObj = new JSONObject();
	    	    
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    
	    dataObj.put("program_id", BitcoinConstants.AP_PROGRAM_ID);
	    dataObj.put("account_id", u.getCardProviderId());
	    dataObj.put("currency_code", BitcoinConstants.AP_CURRENCY_CODE);
	    dataObj.put("design_id", BitcoinConstants.AP_DESIGN_ID);
	    dataObj.put("type", BitcoinConstants.AP_CARD_TYPE);

	    addressObj.put("country_code", u.getAddressCountry());
	    addressObj.put("country_subdivision_code", u.getAddressCountry() + "-" + u.getAddressState());
	    addressObj.put("city", u.getAddressCity());
	    addressObj.put("postal_code", u.getAddressPostalCode());
	    addressObj.put("line1", u.getAddressCity());
	    //billingObj.put("username", u.getUsername());

	    shippingObj.put("address", addressObj);

	    dataObj.put("shipping", shippingObj);
			
		System.out.println(dataObj.toString());

		wr.writeBytes(dataObj.toString());
		   wr.flush();
		   wr.close();

		if (conn.getResponseCode() != 200) {
			
			br =  new BufferedReader(new InputStreamReader(
					(conn.getErrorStream())));
				
				while ((output = br.readLine()) != null) {
					LOGGER.info(output);
				}
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
						
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		br =  new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		

		String jsonTempString = null;
		output = "";
		
		do {
			jsonTempString = br.readLine();
			output += jsonTempString;
			LOGGER.info("Output object: " + jsonTempString);
		} while (jsonTempString != null);
		
	    JSONObject currentObject = new JSONObject(output);
	    
	    JSONObject obj = currentObject.getJSONObject("data");
	    
	    LOGGER.info("Object is " + obj.toString());
	    
	    u.setCardProviderId(obj.getString("id"));
	    // createTernioWallet(u);
	    	    		
		conn.disconnect();
		
		if (dbConn == null)
			dbConn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    	        
    	PreparedStatement ps = dbConn.prepareStatement("UPDATE USERS SET CARD_PROVIDER_ID = ? WHERE USER_NAME = ?");
    	ps.setString(1, u.getCardProviderId());
    	ps.setString(2, u.getUsername());

    	ps.execute();
    	ps.close();
    	LOGGER.info("Updated card provider id.");

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	  catch (JSONException e) {
		e.printStackTrace();
	}
	}
	
	public void createTernioUser(User u) throws SQLException
	{
	  try {

		String output;
		BufferedReader br = null;

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL(ternioBaseURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.1BlsUKDhmDjC9npBm805AOFmgb6lD_DhueUmI8zMTwg");
		conn.setDoOutput(true);
		
	    JSONObject postObj = new JSONObject();
	    
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

			postObj.put("firstname", u.getFirstName());
			postObj.put("lastname", u.getLastName());
			postObj.put("email", u.getEmail());
			postObj.put("address1", u.getAddressCity());
			postObj.put("city", u.getAddressCity());
			postObj.put("state", u.getAddressState());
			postObj.put("zip_code", u.getAddressPostalCode());
			postObj.put("country", u.getAddressCountry());
			postObj.put("phone", u.getPhoneNumber());
			postObj.put("username", u.getUsername());
			
			System.out.println(postObj.toString());

			wr.writeBytes(postObj.toString());
		    wr.flush();
		    wr.close();

		if (conn.getResponseCode() != 200) {
			
			br =  new BufferedReader(new InputStreamReader(
					(conn.getErrorStream())));
				
				while ((output = br.readLine()) != null) {
					LOGGER.info(output);
				}
			
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
						
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		br =  new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		

		String jsonTempString = null;
		output = "";
		
		do {
			jsonTempString = br.readLine();
			output += jsonTempString;
			LOGGER.info("Output object: " + jsonTempString);
		} while (jsonTempString != null);
		
	    JSONObject currentObject = new JSONObject(output);
	    
	    JSONObject obj = currentObject.getJSONObject("data");
	    
	    LOGGER.info("Object is " + obj.toString());
	    
	    u.setCardProviderId(obj.getString("id"));
	    createTernioWallet(u);
	    	    		
		conn.disconnect();
		
		if (dbConn == null)
			dbConn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    	        
    	PreparedStatement ps = dbConn.prepareStatement("UPDATE USERS SET CARD_PROVIDER_ID = ? WHERE USER_NAME = ?");
    	ps.setString(1, u.getCardProviderId());
    	ps.setString(2, u.getUsername());

    	ps.execute();
    	ps.close();
    	LOGGER.info("Updated card provider id.");

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	  catch (JSONException e) {
		e.printStackTrace();
	}
	}
	
	public void createTernioWallet(User u) throws SQLException
	{
	  try {

		String output;
		BufferedReader br = null;

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL(ternioBaseURL + "/" + u.getCardProviderId() + "/wallet");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.1BlsUKDhmDjC9npBm805AOFmgb6lD_DhueUmI8zMTwg");

		System.out.println(ternioBaseURL + "/" + u.getCardProviderId() + "/wallet");
		
		if (conn.getResponseCode() != 200) {
			
			br =  new BufferedReader(new InputStreamReader(
					(conn.getErrorStream())));
				
				while ((output = br.readLine()) != null) {
					LOGGER.info(output);
				}
			
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
						
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		br =  new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String jsonTempString = null;
		output = "";
		
		do {
			jsonTempString = br.readLine();
			output += jsonTempString;
		} while (jsonTempString != null);
		
	    JSONObject currentObject = new JSONObject(output);
	    
	    JSONObject obj = currentObject.getJSONObject("data");
	    
	    System.out.println("Object is " + obj.toString());
	    
	    u.setBtcAddress(obj.getString("btc_address"));
	    u.setBchAddress(obj.getString("bch_address"));
	    
	    String userId = getUserId(u.getUsername());
	    
		if (dbConn == null)
			dbConn = DriverManager.getConnection(BitcoinConstants.DB_URL);
		
    	PreparedStatement ps = dbConn.prepareStatement("INSERT INTO CARD (BCH_ADDRESS, BTC_ADDRESS, USER_ID) VALUES (?, ?, ?)");
    	ps.setString(1, u.getBchAddress());
    	ps.setString(2, u.getBtcAddress());
    	ps.setInt(3, Integer.parseInt(userId));

    	ps.execute();
    	ps.close();
    	LOGGER.info("Updated BCH and BTC addresses.");

		
	    LOGGER.info("Create wallet output variable is " + output);
		
		conn.disconnect();
		
		signUpForTernioCard(u);
		
	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  } catch (JSONException e) {
		e.printStackTrace();
	}
	}
	
	public void signUpForTernioCard(User u)
	{
	  try {

		String output;
		BufferedReader br = null;

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL(ternioBaseURL + "/" + u.getCardProviderId() + "/card");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.1BlsUKDhmDjC9npBm805AOFmgb6lD_DhueUmI8zMTwg");
		conn.setDoOutput(true);
		
	    JSONObject postObj = new JSONObject();
	    
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

			postObj.put("plan", "virtual");
			postObj.put("firstname", u.getFirstName());
			postObj.put("lastname", u.getLastName());
			postObj.put("email", u.getEmail());
			postObj.put("birthdate", u.getDateOfBirth());
			postObj.put("address1", u.getAddressCity());
			postObj.put("city", u.getAddressCity());
			postObj.put("state", u.getAddressState());
			postObj.put("zip_code", u.getAddressPostalCode());
			postObj.put("country", u.getAddressCountry());
			postObj.put("phone", u.getPhoneNumber());
			postObj.put("username", u.getUsername());
			postObj.put("id1", "111111111");
			postObj.put("id1_type", "ssn");
			postObj.put("id2", "11111111");
			postObj.put("id2_type", "drivers_license");
			
			JSONObject dataObject = new JSONObject();
			dataObject.putOnce("data", postObj);

			System.out.println("Card object is: \n" + postObj.toString());
			
			wr.writeBytes(postObj.toString());
		    wr.flush();
		    wr.close();

		if (conn.getResponseCode() != 200) {
			
			br =  new BufferedReader(new InputStreamReader(
					(conn.getErrorStream())));
				
				while ((output = br.readLine()) != null) {
					LOGGER.info(output);
				}
			
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
						
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		br =  new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		

		String jsonTempString = null;
		output = "";
		System.out.println("Output from Server ....");
		
		do {
			jsonTempString = br.readLine();
			output += jsonTempString;
			System.out.println("jsonTempString " + jsonTempString);
		} while (jsonTempString != null);
		
		System.out.println("Output variable is " + output);
		
		
	    JSONObject currentObject = new JSONObject(output);
	    
	    System.out.println("Getting data JSON object");
	    JSONObject obj = currentObject.getJSONObject("data");
	    
	    System.out.println("Object is " + obj.toString());
	    	    	    	    		
		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	  catch (JSONException e) {
		e.printStackTrace();
	}
	}

	
	public void callCardProviderAPI()
	{
	  try {

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL(ternioBaseURL + "/ca41620b-17bb-4730-ab5f-5a65d5f0f236/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.1BlsUKDhmDjC9npBm805AOFmgb6lD_DhueUmI8zMTwg");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		
		
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server ....");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		
		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	}
	
	public String getTernioImageURL()
	{
		String imageUrl = "";
		
		try {

		URL url = new URL("https://whitelabel.dev.api1.blockcard.ternio.co/v1/user/ca41620b-17bb-4730-ab5f-5a65d5f0f236/card/image");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/html");
		conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.1BlsUKDhmDjC9npBm805AOFmgb6lD_DhueUmI8zMTwg");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		
		
		
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));


		String output = "", tempRead = "";
		
		while ((tempRead = br.readLine()) != null) {
			output += tempRead;
		}
		
		
		try
		{
			
	    JSONObject currentObject = new JSONObject(output);
	    
	    JSONObject obj = currentObject.getJSONObject("data");
	    
	    imageUrl = obj.getString("url");

	    System.out.println("OBJ is " + obj.toString());
	    
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}

	    
		LOGGER.info("Image URL is "+ imageUrl);
		
		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	
		return imageUrl;
	  
	}
	
	public String getTernioLedgerTransactions(String id)
	{
		String output = "", tempRead = "";
		
		try {

		URL url = new URL("https://whitelabel.dev.api1.blockcard.ternio.co/v1/user/dc543f24-a348-47a4-96b0-efdf9aa3d979/ledger");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/html");
		conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.1BlsUKDhmDjC9npBm805AOFmgb6lD_DhueUmI8zMTwg");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
				
		LOGGER.info("Response code is: " + conn.getResponseCode());
		
		LOGGER.info("Response message is: " + conn.getResponseMessage());

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		while ((tempRead = br.readLine()) != null) {
			output += tempRead;
		}
		
		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	
		return output;
	  
	}
	
	private void handleHttpResponse(HttpURLConnection conn) throws IOException
	{
		if (conn.getResponseCode() != 200) {
			if (conn.getResponseCode() == 401)
				throw new UnauthorizedException(conn.getResponseMessage());
			else
				throw new RuntimeException(conn.getResponseCode() + " " + conn.getResponseMessage());
			
			
		}
		
	}
	
		public String getCognitoUserInfo(String token)
		{
			String output = "", tempRead = "", responseStr = "";
			
			try {

			URL url = new URL("https://bitcoincom.auth.us-east-2.amazoncognito.com/oauth2/userInfo");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Bearer " + token);
					
			LOGGER.info("Response code is: " + conn.getResponseCode());
			
			LOGGER.info("Response message is: " + conn.getResponseMessage());

			handleHttpResponse(conn);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			while ((tempRead = br.readLine()) != null) {
				output += tempRead;
			}
			
		    JSONObject currentObject = new JSONObject(output);
		    
		    responseStr = currentObject.getString("username");
						
			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			return responseStr;
		  
		}

}
