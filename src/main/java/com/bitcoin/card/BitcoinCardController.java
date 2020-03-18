package com.bitcoin.card;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.bitcoin.card.entity.AccessToken;
import com.bitcoin.card.entity.Login;
import com.bitcoin.card.entity.ResponseMessage;
import com.bitcoin.card.entity.UpdatePassword;
import com.bitcoin.card.entity.User;
import com.bitcoin.card.entity.UserDocument;
import com.bitcoin.card.entity.Username;
import com.bitcoin.card.entity.UsernameOrEmail;
import com.bitcoin.card.entity.VerifyAccessCode;
import com.bitcoin.card.error.BadRequestException;
import com.bitcoin.card.error.UnauthorizedException;
import com.bitcoin.card.error.UserNotFoundException;
import com.bitcoin.card.error.WrongFileTypeException;
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
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

@CrossOrigin(origins = {"http://localhost:3000", "https://card.btctest.net", "https://card.bitcoin.com", 
						"https://card.stage.cloud.bitcoin.com/", "https://card.dev.cloud.bitcoin.com/"}, allowCredentials = "true", 
methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
public class BitcoinCardController extends BitcoinUtility {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	TokenHandler th = new TokenHandler();
	
	CognitoHelper helper = new CognitoHelper();
	BitcoinRestClient brClient = new BitcoinRestClient();

	//private static String url = "jdbc:postgresql://3.136.241.73:5432/bitcoin-card?user=postgres&password=bch_admin&ssl=true&sslmode=verify-ca&sslrootcert=./.postgres/root.crt";

	private static Connection conn;
	
	
    @GetMapping("/test5")
	public String test() {
    	
    	BitcoinRestClient brClient = new BitcoinRestClient();
    
    	User u = new User();
    	u.setFirstName("MEHMED");
    	u.setLastName("DURIC");
    	u.setEmail("test10h@yahoo.com");
    	u.setAddressStreet("111 Clarke Rd");
    	u.setAddressCity("Richmond");
    	u.setAddressState("VA");
    	u.setAddressPostalCode("23233");
    	u.setAddressCountry("US");
    	u.setPhoneNumber("+14834738459");
    	u.setUsername("test10h");
    	u.setSocialSecurityNumber("111111111");
    	u.setDateOfBirth("1969-12-31");
    	
    	try {
			brClient.createAPAccount(u);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    	
    	return "Test endpoint Mehmed Duric.";
    }

	
    @GetMapping("/cognito")
	public User getUserInfo() {

    	
    	/*AmazonCognitoIdentity identityClient = new AmazonCognitoIdentityClient(new AnonymousAWSCredentials());
		
    	// send a get id request. This only needs to be executed the first time
    	// and the result should be cached.
    	GetIdRequest idRequest = new GetIdRequest();
    	idRequest.setAccountId("7ljdp8s6flj41urv5eohbngef8");
    	idRequest.setIdentityPoolId("us-east-1_G7ighQ8h3");
    	// If you are authenticating your users through an identity provider
    	// then you can set the Map of tokens in the request
    	// Map providerTokens = new HashMap();
    	// providerTokens.put("graph.facebook.com", "facebook session key");
    	// idRequest.setLogins(providerTokens);
    				
    	GetIdResult idResp = identityClient.getId(idRequest);
    				
    	String identityId = idResp.getIdentityId();
    	
    	System.out.println("Identity id is" + identityId);
		 */
    	
    	
	       AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();             
	       AdminGetUserRequest userRequest = new AdminGetUserRequest()
	                      .withUsername("mcboatface2")
	                      .withUserPoolId("us-east-1_G7ighQ8h3");
	 
	 
	       AdminGetUserResult userResult = cognitoClient.adminGetUser(userRequest);
	 
	       User userResponse = new User();
	       userResponse.setUsername(userResult.getUsername());
	       
	       System.out.println("MFA Settings " + userResult.toString());
	       //userResponse.setUserStatus(userResult.getUserStatus());
	       //userResponse.setUserCreateDate(userResult.getUserCreateDate());
	       //userResponse.setLastModifiedDate(userResult.getUserLastModifiedDate());
	 
	 /*      List userAttributes = userResult.getUserAttributes();
	       for(AttributeTypeattribute: userAttributes) {
	              if(attribute.getName().equals("custom:companyName")) {
	                 userResponse.setCompanyName(attribute.getValue());
	}else if(attribute.getName().equals("custom:companyPosition")) {
	                 userResponse.setCompanyPosition(attribute.getValue());
	              }else if(attribute.getName().equals("email")) {
	                 userResponse.setEmail(attribute.getValue());
	   
	              }
	       }
	 */
	        
	       return userResponse;
	              
	}
	
	public AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
	      ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider = 
	           new ClasspathPropertiesFileCredentialsProvider();
	      
	 
	       return AWSCognitoIdentityProviderClientBuilder.standard()
	                      .withCredentials(propertiesFileCredentialsProvider)
	                             .withRegion("us-east-1")
	                             .build();
	 
	   }
	
    // Reset password will send reset code to user's email. After this update password should be
	// called with new password
    @PostMapping(value = "/reset-password", consumes = "*/*")
    @ResponseStatus(HttpStatus.OK)
    ResponseMessage resetPassword(@RequestBody UsernameOrEmail uoe, @RequestHeader(name = "authorization") Optional<String> authorization) throws SQLException 
    {
    	
    	//String username = th.decodeVerifyCognitoToken(authorization);
    	
    	String tempUoe = "";
    	String username = "";
    	
    	if (uoe.getUsernameOrEmail() != null)
    		tempUoe = uoe.getUsernameOrEmail();
    	else
    		throw new BadRequestException(BitcoinConstants.USERNAME_OR_EMAIL_REQUIRED);
    	
    	// See if parameter is email
    	if (tempUoe.indexOf('@') > 0)
    	{
    		if (conn == null)
    			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    		
        	PreparedStatement stmt = conn.prepareStatement("select user_name from users where email = ?");  
        	stmt.setString(1, tempUoe);
        	ResultSet r = stmt.executeQuery();
        	
	    	if (r.next() == false)
	    		throw new UserNotFoundException(tempUoe);
	    	else
	    		username = r.getString(1);

    	}
    	else 
    		username = tempUoe;
    	
    	ResponseMessage response = new ResponseMessage();
    	
    	response.setMessage(helper.resetPassword(username));
    		
    	return response;
    }
    
    @PostMapping(value = "/update-password/me", consumes = "*/*")
    @ResponseStatus(HttpStatus.OK)
    ResponseMessage updatePassword(@RequestBody UpdatePassword u, @RequestHeader(name = "authorization") Optional<String> authorization) 
    {
    	String username = th.decodeVerifyCognitoToken(authorization);
    	
    	LOGGER.info("Updating password for user " + username);

    	if (u.getNewPassword() == null)
    		throw new BadRequestException(BitcoinConstants.PASSWORD_REQUIRED);
    	else 
        	if (u.getCode() == null)
        		throw new BadRequestException(BitcoinConstants.CODE_REQUIRED);

    	ResponseMessage response = new ResponseMessage();
    	
    	response.setMessage(helper.updatePassword(username, u.getNewPassword(), u.getCode()));
    		
    	return response;
    }
    
    // Login user
    @PostMapping(value = "/login", consumes = "*/*")
    @ResponseStatus(HttpStatus.OK)
    AccessToken loginUser(@RequestBody Login l) 
    {
    	
    	if (l.getUsername() == null)
    		throw new BadRequestException(BitcoinConstants.USER_NAME_REQUIRED);
    	else 
        	if (l.getPassword() == null)
        		throw new BadRequestException(BitcoinConstants.PASSWORD_REQUIRED);
    	
    	AccessToken a = new AccessToken();
    	String accessToken;
    	
    	accessToken = helper.validateUser(l.getUsername(), l.getPassword());
    	    	
    	a.setAccess_token(accessToken);
    		
    	return a;
    }
    
    // Find
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/logout")
    void logoutUser(@RequestHeader(name = "authorization") Optional<String> authorization) {
    	
    	String username = th.decodeVerifyCognitoToken(authorization);
    	
    	LOGGER.info("Logging out user " + username);

    	String[] authorizationParts = null;
    	    	
    	if (authorization.isPresent())
    		authorizationParts = authorization.get().split(" ");
    	
    	if (authorization.isEmpty() || authorizationParts.length != 2)
    		throw new UnauthorizedException("Bad token value");
    	
    	String token = authorizationParts[1];
    	
    	helper.signoutUser(token);
    	    	
    }
    
    // Verify user's access code so user can be confirmed in Cognito
    @PostMapping(value = "/verify", consumes = "*/*")
    @ResponseStatus(HttpStatus.OK)
    boolean verifyAccessCode(@RequestBody VerifyAccessCode v) 
    {
    	boolean result;
    	
    	if (v.getUsername() == null)
    		throw new BadRequestException(BitcoinConstants.USER_NAME_REQUIRED);
    	else 
        	if (v.getCode() == null)
        		throw new BadRequestException(BitcoinConstants.VERIFY_CODE_REQUIRED);
    	
    	result = helper.verifyAccessCode(v.getUsername(), v.getCode());
    	
    	if (! result)
    		throw new UnauthorizedException(BitcoinConstants.WRONG_CODE);
    	
    		
    	return result;
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
    	
    	if (u.getUsername() == null)
    		throw new BadRequestException(BitcoinConstants.USER_NAME_REQUIRED);
    	else
    		if (u.getPassword() == null)
        		throw new BadRequestException(BitcoinConstants.PASSWORD_REQUIRED);
    		else
    			if (u.getEmail() == null)
    	    		throw new BadRequestException(BitcoinConstants.EMAIL_REQUIRED);

    	//String username = th.decodeVerifyCognitoToken(authorization);
    	
		if (conn == null)
			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
		
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
    	
    	// Create new user in Cognito
    	LOGGER.info("Creating Cognito user...");
    	boolean cognitoResult = helper.signUpUser(u.getUsername(), u.getPassword(), u.getEmail(), u.getPhoneNumber());
		
    	if (! cognitoResult)
    	{
    		LOGGER.info("Failed to create Cognito user + " + u.getUsername());
        	stmt = conn.prepareStatement("delete from users where user_name = ?");  
        	stmt.setString(1, u.getUsername());
		    stmt.execute();
		    throw new SQLException("Failed to create Cognito user + " + u.getUsername());
    	}
    	

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
    
    @PostMapping("/files")
    public void uploadUserDocument(@RequestParam("file") MultipartFile file, 
			 @RequestParam("type") String documentType,
			 @RequestHeader(name = "authorization") Optional<String> authorization
    		) throws SQLException, IOException {
    	
    	String username = th.decodeVerifyCognitoToken(authorization);
    	String id = "";
    	LOGGER.info("Uploading file " + file.getOriginalFilename() + " for user " + username + ".");
    	
		id = getUserId(username);
    	
    	String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.') + 1);
    	

    	if (! fileExtension.equalsIgnoreCase("PDF") && ! fileExtension.equalsIgnoreCase("JPG") && ! fileExtension.equalsIgnoreCase("PNG"))
    	{
    		LOGGER.info("Exception! Wrong file type " + fileExtension);
    	
    		throw new WrongFileTypeException(fileExtension);
    	}
    	
		if (conn == null)
			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    	
        final InputStream in = file.getInputStream();
        
    	PreparedStatement ps = conn.prepareStatement("INSERT INTO user_documents (user_id, document_name, document_type, document, document_id) VALUES (?, ?, ?, ?, nextval('document_id_seq'))");
    	ps.setInt(1, Integer.parseInt(id));
    	ps.setString(2, file.getOriginalFilename());
    	ps.setString(3, documentType.toUpperCase());
    	ps.setBinaryStream(4, in);
    	ps.execute();
    	ps.close();
    	LOGGER.info("Uploaded.");
    
    }
    
    @GetMapping(value = "/files")
    public List<UserDocument> getAllUserDocuments(@RequestHeader(name = "authorization") Optional<String> authorization) throws Exception {
    	
    	
    	String username = th.decodeVerifyCognitoToken(authorization);
    	String id = "";
    	List<UserDocument> docList = new ArrayList<UserDocument>();
    	UserDocument tempDoc = null;

    	LOGGER.info("Retrieving user document for user " + username);
    	
		if (conn == null)
			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    
		id = getUserId(username);
    	
    	PreparedStatement ps = conn.prepareStatement("select document_id, document_name, document_type from user_documents where user_id = ? ");
    	ps.setInt(1, Integer.parseInt(id));

    	ResultSet rs = ps.executeQuery();

    	while (rs.next())
    	{
    		tempDoc = new UserDocument();
    		tempDoc.setFileId(rs.getString(1));
    		tempDoc.setName(rs.getString(2));
    		tempDoc.setType(rs.getString(3));
    		
    		docList.add(tempDoc);
    		
    	}
    	
        return docList;
    
    }
 
    @GetMapping(value = "/files/{fileId}")
    public @ResponseBody ResponseEntity<byte[]> getUserDocument(@PathVariable("fileId") String fileId, @RequestHeader(name = "authorization") Optional<String> authorization) throws Exception {
    	
    	LOGGER.info("Retrieving document id " + fileId);
    	
    	String username = th.decodeVerifyCognitoToken(authorization);
    	String id = getUserId(username);
    	
		if (conn == null)
			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    
    	PreparedStatement ps = conn.prepareStatement("select document_name, document from user_documents where user_id = ? and document_id = ?");
    	ps.setInt(1, Integer.parseInt(id));
    	ps.setInt(2, Integer.parseInt(fileId));

    	ResultSet rs = ps.executeQuery();

    	if (rs.next() == false)
    		throw new UserNotFoundException("File not found!");

    	
    	String file = rs.getString(1);
    	
    	String fileExtension = file.substring(file.indexOf('.') + 1);


    	InputStream is = rs.getBinaryStream(2);

        HttpHeaders responseHeaders = new HttpHeaders();
        
        if (fileExtension.equalsIgnoreCase("PDF"))
            responseHeaders.set("Content-Type", "application/pdf");
        else
        	responseHeaders.set("Content-Type", "image/jpeg");
        
    	LOGGER.info("Retrieved.");
    	
        return ResponseEntity.ok()
        	      .headers(responseHeaders)
        	      .body(IOUtils.toByteArray(is));
    
    }
    
    @GetMapping(value = "/virtual-card", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[]  getVirtualCardImage(@RequestHeader(name = "authorization") Optional<String> authorization) throws Exception {
    	
    	
    	LOGGER.info("Retrieving user virtual card for user ");

    	String username = th.decodeVerifyCognitoToken(authorization);

    	
    	BitcoinRestClient brClient = new BitcoinRestClient();
    	
    	String ternioImageUrl = brClient.getTernioImageURL();
    	
		URL url = new URL(ternioImageUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
    	LOGGER.info("Retrieved.");

    	
    	return IOUtils.toByteArray(conn.getInputStream());
    }
    
    // Find
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/me")
    User findMyUserInfo(@RequestHeader(name = "authorization") Optional<String> authorization) {
   
    	String username = th.decodeVerifyCognitoToken(authorization);

    	LOGGER.info("Getting user details for " + username);
    	
    	User u = new User();
    	
    	try {
    		if (conn == null)
    			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    		    		
    		Statement s = conn.createStatement();
    		ResultSet r = s.executeQuery("select * from users where user_name = '" + username + "'");
    		
    		setUserResultParameters(r, u, username);

    		    		
    	} catch (SQLException e) {
    		
	    	LOGGER.info("Exception!!!\n" + e.getMessage());

    		e.printStackTrace();
    	}
    	
    	LOGGER.info("Retrieved user data: \n" + u.toString());
    	
         return u;
    }
    
    // Find
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/search")
    User findUser(@RequestParam Optional<String> username, @RequestParam Optional<String> email, @RequestHeader(name = "authorization") Optional<String> authorization) {
   
    	String userN = th.decodeVerifyCognitoToken(authorization);

    	LOGGER.info("Getting user details...");
    	String conditionStr = "";
    	
    	if (username.isPresent())
    		conditionStr = "user_name = '" + username.get();
    	else
    		if (email.isPresent())
    			conditionStr = "email = '" + email.get();
    		else
	    		throw new UserNotFoundException("Invalid parameter, email or username has to be provided.");

    	LOGGER.info("User query parameter : " + conditionStr);
    	
    	User u = new User();
    	
    	try {
    		if (conn == null)
    			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    		    		
    		Statement s = conn.createStatement();
    		ResultSet r = s.executeQuery("select * from users where " + conditionStr + "'");
    		
    		setUserResultParameters(r, u, username.orElse(email.orElse("No username or email")));
    		
    	} catch (SQLException e) {
    		
	    	LOGGER.info("Exception!!!\n" + e.getMessage());

    		e.printStackTrace();
    	}
    	
    	LOGGER.info("Retrieved user data: \n" + u.toString());
    	
         return u;
    }
    
    // Find
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/transactions")
    String findUserLedgerTransactions(@RequestHeader(name = "authorization") Optional<String> authorization) {
    	    	
    	String username = th.decodeVerifyCognitoToken(authorization);
    	    	
    	LOGGER.info("Getting user transactions...");
    
    	BitcoinRestClient brClient = new BitcoinRestClient();
    /*	
    	User u = new User();
    	u.setFirstName("MEHMED");
    	u.setLastName("DURIC");
    	u.setEmail("sssddddff@yahoo.com");
    	u.setAddresStreet("111 Clarke Rd");
    	u.setAddressCity("Richmond");
    	u.setAddressState("VA");
    	u.setAddressPostalCode("23233");
    	u.setAddressCountry("US");
    	u.setPhoneNumber("+1 310 867 5323");
    	u.setUserName("sssdddd4duser");
    	u.setSocialSecurityNumber("012345672");
    	u.setDateOfBirth("1969-12-31");
    	
    	brClient.createTernioUser(u);
	*/
    	return brClient.getTernioLedgerTransactions("");
		
    }
    

    // Save or update
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/users/me", consumes = "*/*")
    void updateUser(@RequestBody User u, @RequestHeader(name = "authorization") Optional<String> authorization) throws SQLException {
    	
    	String username = th.decodeVerifyCognitoToken(authorization);
    	u.setUsername(username);
    	
    	LOGGER.info("Updating user data for user: " + username);

    	LOGGER.info("User data: \n" + u.toString());
    	
    	if (u.isDebitCard())
    	if (u.getFirstName() == null)
    		throw new BadRequestException(BitcoinConstants.FIRST_NAME_REQUIRED);
    	else 
        	if (u.getLastName() == null)
        		throw new BadRequestException(BitcoinConstants.LAST_NAME_REQUIRED);
        	else 
            	if (u.getPhoneNumber() == null)
            		throw new BadRequestException(BitcoinConstants.PHONE_NUMBER_REQUIRED);
            	else 
                	if (u.getEmail() == null)
                		throw new BadRequestException(BitcoinConstants.EMAIL_REQUIRED);
                	else 
                    	if (u.getAddressStreet() == null)
                    		throw new BadRequestException(BitcoinConstants.BILLING_ADDRESS_REQUIRED);
                    	else 
                        	if (u.getAddressCity() == null)
                        		throw new BadRequestException(BitcoinConstants.CITY_REQUIRED);
                        	else 
                            	if (u.getAddressState() == null)
                            		throw new BadRequestException(BitcoinConstants.STATE_REQUIRED);
                            	else 
                                	if (u.getAddressCountry() == null)
                                		throw new BadRequestException(BitcoinConstants.COUNTRY_REQUIRED);
                                	else 
                                    	if (u.getAddressPostalCode() == null)
                                    		throw new BadRequestException(BitcoinConstants.POSTAL_CODE_REQUIRED);
    	
    	String sql = "update users set ";
 
		if (conn == null)
			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
		
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
			sql += "address_street_2 = '" + u.getShippingAddressStreet2() + "', ";
		
		sql += "updated_at= now() where user_name = '" + username + "'";
		
		Statement s = conn.createStatement();
		
		int result = s.executeUpdate(sql);
		
		if (result == 0)
			throw new UserNotFoundException(u.getId().toString());
		
    	try {
    		
    		// Check if we need to create card provider
    		if (u.isDebitCard())
    			brClient.createTernioUser(u);
		
    	} catch (SQLException e) {
			e.printStackTrace();
		}
		
		

    }

    @DeleteMapping("/users/me")
    void deleteUser(@RequestHeader(name = "authorization") Optional<String> authorization) throws SQLException {

    	String username = th.decodeVerifyCognitoToken(authorization);
    	String id = "";
    	
    	LOGGER.info("Deleting user: " + username);

    		if (conn == null)
    			conn = DriverManager.getConnection(BitcoinConstants.DB_URL);
    		
    		id = getUserId(username);
    		
    		Statement s = conn.createStatement();
    		
    		s.execute("delete from user_documents where user_id = " + id);
    		
    		s.execute("delete from users where user_id = " + id);
    			
        	LOGGER.info("Deleted user: " + username);
        
    }
    
    private void setUserResultParameters(ResultSet r, User u, String userIdentifier)
    {
		try {
			
	    	if (r.next() == false)
	    		throw new UserNotFoundException(userIdentifier);
	    	else
	    	{
	    		u.setId(r.getLong("user_id"));
	    		u.setFirstName(r.getString("first_name"));
	    		u.setLastName(r.getString("last_name"));
	    		u.setEmail(r.getString("email"));
	    		u.setPhoneNumber(r.getString("phone_number"));
	    		u.setDateOfBirth(r.getString("date_of_birth"));
	    		u.setGender(r.getString("gender"));
	    		u.setActive(r.getBoolean("is_active"));
	    		u.setPromotioanlConsent(r.getBoolean("promotional_consent"));
	    		u.setAddressStreet(r.getString("address_street"));
	    		u.setAddressStreet2(r.getString("address_street_2"));
	    		u.setAddressCity(r.getString("address_city"));
	    		u.setAddressPostalCode(r.getString("address_postal_code"));
	    		u.setAddressState(r.getString("address_state"));
	    		u.setAddressCountry(r.getString("address_country"));
	    		u.setDefaultCurrencyId(r.getString("default_currency_id"));
	    		u.setSocialSecurityNumber(r.getString("social_security_number"));
	    		u.setUsername(r.getString("user_name"));
	    		u.setCreatedAt(r.getTimestamp("created_at"));
	    		u.setUpdatedAt(r.getTimestamp("updated_at"));
	    		
	    		u.setShippingAddressStreet(r.getString("shipping_address_street"));
	    		u.setShippingAddressStreet2(r.getString("shipping_address_street_2"));
	    		u.setShippingAddressCity(r.getString("shipping_address_city"));
	    		u.setShippingAddressPostalCode(r.getString("shipping_address_postal_code"));
	    		u.setShippingAddressState(r.getString("shipping_address_state"));
	    		u.setShippingAddressCountry(r.getString("shipping_address_country"));
	    	}
		} catch (SQLException e) {
			
	    	LOGGER.info("Exception!!!\n" + e.getMessage());

			e.printStackTrace();
		}
    	
    }

}
