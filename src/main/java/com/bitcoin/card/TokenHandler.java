package com.bitcoin.card;

import java.util.Base64.Decoder;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

import com.bitcoin.card.error.UnauthorizedException;

public class TokenHandler {
	
	private static boolean ENABLE_TOKEN_VALIDATION = true;
	private static String badTokenMessage = "Bad token value";
	
    private BitcoinRestClient restClient = new BitcoinRestClient();
	
    String decodeVerifyCognitoToken(Optional<String> authorization) 
	{
    	String tempTokenStr = "";
    	
    	String[] authorizationParts = null;
    	if (! ENABLE_TOKEN_VALIDATION)
    		return "";
    	    	
    	if (authorization.isPresent())
    		authorizationParts = authorization.get().split(" ");
    	
    	if (authorization.isEmpty() || authorizationParts.length != 2)
    		throw new UnauthorizedException(badTokenMessage);
    	
    	String token = authorizationParts[1];

        Decoder decoder = java.util.Base64.getUrlDecoder();
        String[] tokenParts = token.split("\\."); // split out the "parts" (header, payload and signature)
        
        if (tokenParts.length != 3)
        	throw new UnauthorizedException(badTokenMessage);

        String header = new String(decoder.decode(tokenParts[0]));
        String payload = new String(decoder.decode(tokenParts[1]));
        String signature = new String(decoder.decode(tokenParts[2]));
        
        System.out.println("Header is " + header);
        
	    JSONObject currentObject;
		try {
			currentObject = new JSONObject(header);
		} catch (JSONException e) {
			throw new UnauthorizedException(badTokenMessage);
		}
		
	    try
	    {
	    tempTokenStr = currentObject.getString("alg");
	    	    
	    if (! tempTokenStr.equals("RS256"))
	    	throw new UnauthorizedException("Invalid token algorithm.");
	    
	    currentObject = new JSONObject(payload);
	    tempTokenStr = currentObject.getString("token_use");
	    
	    
	    if (! tempTokenStr.equals("access"))
	    	throw new UnauthorizedException("Invalid token type.");   
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        return restClient.getCognitoUserInfo(token);
	}

}
