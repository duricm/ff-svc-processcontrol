package com.bitcoin.card;

public class BitcoinConstants {
	

	static String DB_URL = "jdbc:postgresql://bitcoincom-card.cgll0kqdznrn.us-east-2.rds.amazonaws.com:5432/bitcoincard1?user=bch_admin&password=letitsnow890*()&ssl=false";

    static String USER_NAME_REQUIRED = "Username required";
    static String EMAIL_REQUIRED = "Email required";
    static String PASSWORD_REQUIRED = "Password required";
    
    static String VERIFY_CODE_REQUIRED = "Please enter code provided in email";
    
    static String FIRST_NAME_REQUIRED = "Given name required";
    static String LAST_NAME_REQUIRED = "Family name required";
    static String DATE_OF_BIRTH_REQUIRED = "Date of birth required";
    static String PHONE_NUMBER_REQUIRED = "Phone number required";
    static String BILLING_ADDRESS_REQUIRED = "Address required";
    static String CITY_REQUIRED = "City required";
    static String STATE_REQUIRED = "State required";
    static String COUNTRY_REQUIRED = "Country required";
    static String POSTAL_CODE_REQUIRED = "Address required";
    
    static String USERNAME_OR_EMAIL_REQUIRED = "Username or email required";
    
    static String CODE_REQUIRED = "Code required - check email";
    
    public static final String UNIQUE_EMAIL = "This email is already associated with a Bitcoin.com account. Please use a different email address.";
    public static final String UNIQUE_USER_NAME = "This username already exists. Try another.";
    
    public static final String AP_PROGRAM_ID = "c6833dd5-625b-4690-8861-de6077cd74b4";
    public static final String AP_CURRENCY_CODE = "USD";
    public static final String AP_DESIGN_ID = "8f880b0d-0a29-4ea2-b02f-7bd79522cb73";
    public static final String AP_CARD_TYPE = "PHYSICAL";
    


}
