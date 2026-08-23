package pages;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
 
import base.BasePage;
 
// Page object for Register page — https://practice.expandtesting.com/register
public class RegisterPage extends BasePage {
 
	// Logger instance for RegisterPage class
	private static final Logger log = LogManager.getLogger(RegisterPage.class);
 
	// =====================================================================
	// Locators — all defined as final By constants
	// =====================================================================
 
	// Username input field locator
	private final By usernameField = By.id("username");
 
	// Password input field locator
	private final By passwordField = By.id("password");
 
	// Confirm password input field locator
	// NOTE: id="confirmPassword" inferred from this site's consistent naming
	// convention (matches id="username"/id="password" already confirmed on
	// LoginPage) — verify against DevTools before first run if it doesn't
	// resolve, since it could not be confirmed via direct HTML inspection.
	private final By confirmPasswordField = By.id("confirmPassword");
 
	// Register submit button locator
	private final By registerButton = By.cssSelector("button[type='submit']");
 
	// Inline validation/error message shown on THIS page when registration
	// fails (missing fields, password mismatch) — same #flash convention
	// used site-wide (see LoginPage.errorMessage, SecurePage.successMessage)
	private final By errorMessage = By.id("flash");
 
	// =====================================================================
	// Page actions
	// =====================================================================
 
	// Enter username into the username input field
	public RegisterPage enterUsername(String username) {
		log.info("[RegisterPage] Entering username: {}", username);
		type(usernameField, username);
		return this;
	}
 
	// Enter password into the password input field
	public RegisterPage enterPassword(String password) {
		log.info("[RegisterPage] Entering password");
		type(passwordField, password);
		return this;
	}
 
	// Enter password confirmation into the confirm password input field
	public RegisterPage enterConfirmPassword(String confirmPassword) {
		log.info("[RegisterPage] Entering confirm password");
		type(confirmPasswordField, confirmPassword);
		return this;
	}
 
	// Click the register button to submit the registration form
	public RegisterPage clickRegister() {
		log.info("[RegisterPage] Clicking register button");
		smartClick(registerButton);
		return this;
	}
 
	// =====================================================================
	// Page assertions
	// =====================================================================
 
	// Return error message text shown after a failed registration attempt
	public String getErrorMessage() {
		log.info("[RegisterPage] Getting error message text");
		return getText(errorMessage);
	}
 
	// Return true if error message is displayed on the page
	public boolean isErrorMessageDisplayed() {
		log.info("[RegisterPage] Checking if error message is displayed");
		return isDisplayed(errorMessage);
	}
 
	// Return the current page URL — used to verify user remains on register page
	public String getCurrentPageUrl() {
		log.info("[RegisterPage] Getting current page URL");
		return getCurrentUrl();
	}
}