package constants;

//Holds all page URL paths used across the framework.
public class PageUrlConstants {

 // Private constructor prevents instantiation of constants class
 private PageUrlConstants() {}

 // Login page URL path
 public static final String LOGIN_PAGE  = "/login";

 // Secure page URL path — redirect after successful login
 public static final String SECURE_PAGE = "/secure";

 // Register page URL path
 public static final String REGISTER_PAGE = "/register";

 // Forgot password page URL path
 public static final String FORGOT_PASSWORD_PAGE = "/forgot-password";
 
 // Web inputs page URL path
 public static final String INPUTS_PAGE = "/inputs";
 
 // Dynamic table page URL path
 public static final String DYNAMIC_TABLE_PAGE = "/dynamic-table";
}
