package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import base.BasePage;

//Page object for Login page — https://practice.expandtesting.com/login
public class LoginPage extends BasePage {

	// Logger instance for LoginPage class
	private static final Logger log = LogManager.getLogger(LoginPage.class);

	// =====================================================================
	// Locators — all defined as final By constants
	// =====================================================================

	// Username input field locator
	private final By usernameField = By.id("username");

	// Password input field locator
	private final By passwordField = By.id("password");

	// Login submit button locator
	private final By loginButton = By.cssSelector("button[type='submit']");

	// Error flash message shown after failed login attempt
	private final By errorMessage = By.id("flash");

	// =====================================================================
	// Page actions
	// =====================================================================

	// Enter username into the username input field
	public LoginPage enterUsername(String username) {
		log.info("[LoginPage] Entering username: {}", username);
		type(usernameField, username);
		return this;
	}

	// Enter password into the password input field
	public LoginPage enterPassword(String password) {
		log.info("[LoginPage] Entering password");
		type(passwordField, password);
		return this;
	}

	// Click the login button to submit credentials
	public LoginPage clickLogin() {
		log.info("[LoginPage] Clicking login button");
		smartClick(loginButton);
		return this;
	}

	// =====================================================================
	// Page assertions
	// =====================================================================

	// Return error message text shown after failed login attempt
	public String getErrorMessage() {
		log.info("[LoginPage] Getting error message text");
		return getText(errorMessage);
	}

	// Return true if error message is displayed on the page
	public boolean isErrorMessageDisplayed() {
		log.info("[LoginPage] Checking if error message is displayed");
		return isDisplayed(errorMessage);
	}

	// Returns the same #flash element's text as getErrorMessage(), but named
	// neutrally — used when this page is showing a SUCCESS message rather
	// than an error (e.g. "Successfully registered, you can log in now."
	// after a Register-page redirect). Kept as a separate method rather than
	// renaming getErrorMessage() so existing LoginSteps assertions are
	// unaffected.
	public String getFlashMessage() {
		log.info("[LoginPage] Getting flash message text");
		return getText(errorMessage);
	}

	// Return the current page URL — used to verify user remains on login page
	public String getCurrentPageUrl() {
		log.info("[LoginPage] Getting current page URL");
		return getCurrentUrl();
	}
}
