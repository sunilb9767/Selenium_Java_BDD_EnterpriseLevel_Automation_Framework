package stepdefs;

import config.ConfigReader;
import constants.MessageConstants;
import constants.PageUrlConstants;
import pages.LoginPage;
import pages.SecurePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

// Step definitions for Login feature file.
public class LoginSteps {

    // Logger instance for LoginSteps class
    private static final Logger log = LogManager.getLogger(LoginSteps.class);

    // Page objects — initialized fresh per scenario via Hooks driver setup
    private final LoginPage loginPage   = new LoginPage();
    private final SecurePage securePage = new SecurePage();
    
    // =====================================================================
    // Given steps
    // =====================================================================

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        log.info("[LoginSteps] Navigating to login page");
        // Append login page path to baseUrl — resolves correctly across all environments
        loginPage.navigateTo(ConfigReader.getBaseUrl() + PageUrlConstants.LOGIN_PAGE);
    }

    // =====================================================================
    // When steps
    // =====================================================================

    @When("the user enters valid credentials")
    public void theUserEntersValidCredentials() {
        log.info("[LoginSteps] Entering valid credentials from config");
        // Valid credentials fetched from qa.properties — never hardcoded
        loginPage.enterUsername(ConfigReader.getValidUsername());
        loginPage.enterPassword(ConfigReader.getValidPassword());
    }

    /**
     * Maps readable credential scenario label from Examples table
     * to actual credential values fetched from config.
     * No actual credentials ever appear in the feature file.
     */
    @When("the user attempts login with {string}")
    public void theUserAttemptsLoginWith(String credentialScenario) {
        log.info("[LoginSteps] Attempting login with scenario: {}", credentialScenario);

        switch (credentialScenario) {
        
        case "valid credentials":
            // Valid username and password from config — used by data-driven positive scenario
            loginPage.enterUsername(ConfigReader.getValidUsername());
            loginPage.enterPassword(ConfigReader.getValidPassword());
            break;

            case "invalid username":
                // Invalid username from config, valid password from config
            	loginPage.enterUsername(ConfigReader.getInvalidUsername());
                loginPage.enterPassword(ConfigReader.getValidPassword());
                break;

            case "invalid password":
                // Valid username from config, invalid password from config
            	loginPage.enterUsername(ConfigReader.getValidUsername());
                loginPage.enterPassword(ConfigReader.getInvalidPassword());
                break;

            case "empty username":
                // Empty string for username — tests blank field validation
                loginPage.enterUsername("");
                loginPage.enterPassword(ConfigReader.getValidPassword());
                break;

            case "empty password":
                // Valid username from config, empty string for password
            	loginPage.enterUsername(ConfigReader.getValidUsername());
                loginPage.enterPassword("");
                break;

            default:
                // Fail fast if an unrecognised scenario label is used in Examples table
                throw new IllegalArgumentException(
                        "[LoginSteps] Unknown credential scenario: '"
                                + credentialScenario + "'. "
                                + "Valid values: valid credentials, invalid username, invalid password, "
                                + "empty username, empty password"
                );
        }
    }

    // Reusable step — used in data driven Scenario Outline only
    @When("the user enters {string} in username field")
    public void theUserEntersInUsernameField(String username) {
        log.info("[LoginSteps] Entering username from data table");
        loginPage.enterUsername(username);
    }

    // Reusable step — used in data driven Scenario Outline only
    @And("the user enters {string} in password field")
    public void theUserEntersInPasswordField(String password) {
        log.info("[LoginSteps] Entering password from data table");
        loginPage.enterPassword(password);
    }

    @And("the user submits the login form")
    public void theUserSubmitsTheLoginForm() {
        log.info("[LoginSteps] Submitting login form");
        loginPage.clickLogin();
    }

    // =====================================================================
    // Then steps — one assertion per step, no if/else logic
    // =====================================================================

    @Then("the user should be navigated to secure page")
    public void theUserShouldBeNavigatedToSecurePage() {
        log.info("[LoginSteps] Verifying navigation to secure page");
        Assert.assertTrue(
                loginPage.getCurrentPageUrl().contains(PageUrlConstants.SECURE_PAGE),
                "Expected URL to contain '" + PageUrlConstants.SECURE_PAGE
                        + "' but was: " + loginPage.getCurrentPageUrl()
        );
    }

    @And("the secure page should display successful login message")
    public void theSecurePageShouldDisplaySuccessfulLoginMessage() {
        log.info("[LoginSteps] Verifying success message on secure page");
        Assert.assertTrue(
                securePage.getSuccessMessage().contains(MessageConstants.LOGIN_SUCCESS),
                "Expected success message to contain '"
                        + MessageConstants.LOGIN_SUCCESS
                        + "' but was: " + securePage.getSuccessMessage()
        );
    }

    @And("the logout button should be displayed on secure page")
    public void theLogoutButtonShouldBeDisplayedOnSecurePage() {
        log.info("[LoginSteps] Verifying logout button on secure page");
        Assert.assertTrue(
                securePage.isLogoutButtonDisplayed(),
                "Logout button was not displayed on secure page"
        );
    }

    // Single reusable assertion step — handles all error messages from Examples table
    @Then("the error message {string} should be displayed")
    public void theErrorMessageShouldBeDisplayed(String expectedMessage) {
        log.info("[LoginSteps] Verifying error message: {}", expectedMessage);
        Assert.assertTrue(
                loginPage.getErrorMessage().contains(expectedMessage),
                "Expected error message to contain '" + expectedMessage
                        + "' but was: " + loginPage.getErrorMessage()
        );
    }

    @And("the user should stay on the login page")
    public void theUserShouldStayOnTheLoginPage() {
        log.info("[LoginSteps] Verifying user stays on login page");
        Assert.assertTrue(
                loginPage.getCurrentPageUrl().contains(PageUrlConstants.LOGIN_PAGE),
                "Expected URL to contain '" + PageUrlConstants.LOGIN_PAGE
                        + "' but was: " + loginPage.getCurrentPageUrl()
        );
    }
}
