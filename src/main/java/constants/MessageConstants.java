package constants;

//Holds all expected UI message strings used in assertions across the framework.
public class MessageConstants {

 // Private constructor prevents instantiation of constants class
 private MessageConstants() {}

 // Success message shown after successful login
 public static final String LOGIN_SUCCESS       = "You logged into a secure area!";

 // Error message shown when username is invalid or empty
 public static final String INVALID_USERNAME    = "Your username is invalid!";

 // Error message shown when password is invalid or empty
 public static final String INVALID_PASSWORD    = "Your password is invalid!";

 // Success message shown after successful logout
 public static final String LOGOUT_SUCCESS      = "You logged out of the secure area!";
}