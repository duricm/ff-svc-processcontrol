package com.bitcoin.card;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.bitcoin.card.error.UnauthorizedException;

public class TokenHandler {
	
	private static boolean ENABLE_TOKEN_VALIDATION = true;
	private static String badTokenMessage = "Bad token value";
	
    private BitcoinRestClient restClient = new BitcoinRestClient();
	CognitoHelper helper = new CognitoHelper();

	
    String decodeVerifyCognitoToken(Optional<String> authorization) 
	{
    	String tempTokenStr = "";
    	String username = "";
    	
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
                
	    JSONObject currentObject;
		try {
			currentObject = new JSONObject(header);
		} catch (JSONException e) {
			throw new UnauthorizedException(badTokenMessage);
		}
		
	    try
	    {
	    // Validating header values (alg and kid)
	    tempTokenStr = currentObject.getString("alg");
	    	    
	    if (! tempTokenStr.equals("RS256"))
	    	throw new UnauthorizedException("Invalid token algorithm.");
	    
	    //tempTokenStr = currentObject.getString("kid");
	    
	    //if (! tempTokenStr.equals("5Ou9LbM0E5irpt6nwqtY5YQaI+Bn1vYYVBmRdTMFRcE="))
	    //	throw new UnauthorizedException("Invalid Key ID: " + tempTokenStr);
	    
	    currentObject = new JSONObject(payload);
	   
	    tempTokenStr = currentObject.getString("token_use");
	    if (! tempTokenStr.equals("access"))
	    	throw new UnauthorizedException("Invalid token type: " + tempTokenStr);   
	    
	    tempTokenStr = currentObject.getString("iss");
	    if (! tempTokenStr.equals("https://cognito-idp.us-east-2.amazonaws.com/us-east-2_LxgFwANAr"))
	    	throw new UnauthorizedException("Invalid token issuer: " + tempTokenStr); 
	    
	    if (currentObject.getLong("exp") < (System.currentTimeMillis() / 1000))
	    		throw new UnauthorizedException("Token expired!");
	    
	    //username = currentObject.getString("username");
	    
		username = helper.getCognitoUser(token);

	/*    
	    System.out.println("Token is: " + hmacSha256(header + "." + payload, signature));
	    System.out.println("Original Token is: " + token);
	    System.out.println("Original signature is: " + signature);
	    
	   // if (! token.equals(hmacSha256(header + "." + payload, signature)))
	   // 		throw new UnauthorizedException("Token signature doesn't match.");

	    JWTVerifier verifier = JWT.require(algorithm)
	            .build(); //Reusable verifier instance
	    DecodedJWT jwt = verifier.verify(token);
	    
	    System.out.println("Token is : " + jwt.toString());    
		*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        return username;
	}
    
    private String hmacSha256(String data, String secret) {
        try {

            //MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);//digest.digest(secret.getBytes(StandardCharsets.UTF_8));

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            //Logger.getLogger(JWebToken.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }
    
    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
}

}
