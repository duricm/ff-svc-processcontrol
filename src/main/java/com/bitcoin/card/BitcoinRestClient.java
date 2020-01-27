package com.bitcoin.card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.bitcoin.card.error.UnauthorizedException;

public class BitcoinRestClient {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public void callCardProviderAPI()
	{
	  try {

		  // Old test user c7b59df7-c91e-40ed-9021-a6d14a447c9d
		URL url = new URL("https://whitelabel.dev.api1.blockcard.ternio.co/v1/user/ca41620b-17bb-4730-ab5f-5a65d5f0f236/");
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
	
	public String getTernioImageURL(int id)
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
