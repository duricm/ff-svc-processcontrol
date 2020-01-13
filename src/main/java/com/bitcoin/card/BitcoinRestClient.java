package com.bitcoin.card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class BitcoinRestClient {
	
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
		
		
		System.out.println("Response code is: " + conn.getResponseCode());
		
		System.out.println("Response message is: " + conn.getResponseMessage());

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
		
		
		
		System.out.println("Response code is: " + conn.getResponseCode());
		
		System.out.println("Response message is: " + conn.getResponseMessage());

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));


		String output = "", tempRead = "";
		
		while ((tempRead = br.readLine()) != null) {
			output += tempRead;
			System.out.println(output);
		}
		
		System.out.println("Output is " + output);
		
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

	    
		System.out.println("Image URL is "+ imageUrl);
		
		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
	
		return imageUrl;
	  
	}

}
