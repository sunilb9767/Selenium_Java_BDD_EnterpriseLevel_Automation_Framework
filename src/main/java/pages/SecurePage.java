package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

// Page object for Secure page — shown after successful login
public class SecurePage extends BasePage {

    // Logger instance for SecurePage class
    private static final Logger log = LogManager.getLogger(SecurePage.class);

    // =====================================================================
    // Locators
    // =====================================================================

    // Success flash message displayed after successful login
    private final By successMessage = By.id("flash");

    // Logout button displayed on secure page
    private final By logoutButton = By.cssSelector("a.button");

    // Secure page heading — confirms user is on the correct page
    private final By securePageHeading = By.tagName("h2");

    // =====================================================================
    // Page actions
    // =====================================================================

    // Click the logout button to end the session
    public void clickLogout() {
        log.info("[SecurePage] Clicking logout button");
        click(logoutButton);
    }

    // =====================================================================
    // Page assertions
    // =====================================================================

    // Return success message text shown after successful login
    public String getSuccessMessage() {
        log.info("[SecurePage] Getting success message text");
        return getText(successMessage);
    }

    // Return true if logout button is visible on the secure page
    public boolean isLogoutButtonDisplayed() {
        log.info("[SecurePage] Checking if logout button is displayed");
        return isDisplayed(logoutButton);
    }

    // Return the secure page heading text
    public String getSecurePageHeading() {
        log.info("[SecurePage] Getting secure page heading");
        return getText(securePageHeading);
    }
}
