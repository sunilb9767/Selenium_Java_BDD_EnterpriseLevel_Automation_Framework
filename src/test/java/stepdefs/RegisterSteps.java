package stepdefs;

import config.ConfigReader;
import constants.MessageConstants;
import constants.PageUrlConstants;
import pages.LoginPage;
import pages.RegisterPage;
import utils.TestDataGenerator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

// Step definitions for Register feature file.
public class RegisterSteps {

    // Logger instance for RegisterSteps class
    private static final Logger log = LogManager.getLogger(RegisterSteps.class);

    // Fixed test password used only for the happy-path scenario. This is NOT
    // a secret and never used as a real credential anywhere else in the
    // framework — it exists purely to satisfy "matching passwords" for a
    // registration that will always be discarded/disposable test data.
    // Only the username needs to be unique per run (see TestDataGenerator);
    // the password can safely stay constant.
    private static final String DEFAULT_TEST_PASSWORD = "Test@12345";

    // Page objects — initialized fresh per scenario via Hooks driver setup
    private final RegisterPage registerPage = new RegisterPage();
    private final LoginPage loginPage = new LoginPage();

    // =====================================================================
    // Given steps
    // =====================================================================

    @Given("the user is on the register page")
    public void theUserIsOnTheRegisterPage() {
        log.info("[RegisterSteps] Navigating to register page");
        registerPage.navigateTo(ConfigReader.getBaseUrl() + PageUrlConstants.REGISTER_PAGE);
    }

    // =====================================================================
    // When steps
    // =====================================================================

    @When("the user registers with a new unique username and matching passwords")
    public void theUserRegistersWithANewUniqueUsernameAndMatchingPasswords() {
        // A fresh, collision-safe username is generated per execution so this
        // scenario stays repeatable across CI runs and parallel threads —
        // registering the same literal username twice would fail on a real system.
        String uniqueUsername = TestDataGenerator.uniqueUsername("qa-user");
        log.info("[RegisterSteps] Registering with generated username: {}", uniqueUsername);

        registerPage.enterUsername(uniqueUsername);
        registerPage.enterPassword(DEFAULT_TEST_PASSWORD);
        registerPage.enterConfirmPassword(DEFAULT_TEST_PASSWORD);
        registerPage.clickRegister();
    }

    // Reusable step — used in the negative Scenario Outline only
    @When("the user attempts to register with username {string} password {string} and confirm password {string}")
    public void theUserAttemptsToRegisterWith(String username, String password, String confirmPassword) {
        log.info("[RegisterSteps] Attempting registration with username: '{}'", username);

        registerPage.enterUsername(username);
        registerPage.enterPassword(password);
        registerPage.enterConfirmPassword(confirmPassword);
        registerPage.clickRegister();
    }

    // =====================================================================
    // Then steps — one assertion per step, meaningful failure messages
    // =====================================================================

    @Then("the user should be navigated to the login page")
    public void theUserShouldBeNavigatedToTheLoginPage() {
        log.info("[RegisterSteps] Verifying navigation to login page after registration");
        Assert.assertTrue(
                registerPage.getCurrentPageUrl().contains(PageUrlConstants.LOGIN_PAGE),
                "Expected URL to contain '" + PageUrlConstants.LOGIN_PAGE
                        + "' but was: " + registerPage.getCurrentPageUrl()
        );
    }

    // Fixed, always-the-same-outcome assertion — asserts against
    // MessageConstants (single source of truth), same pattern LoginSteps
    // already uses for its one fixed-outcome assertion (LOGIN_SUCCESS).
    @And("the login page should display the successful registration message")
    public void theLoginPageShouldDisplayTheSuccessfulRegistrationMessage() {
        log.info("[RegisterSteps] Verifying successful registration message on login page");
        Assert.assertTrue(
                loginPage.getFlashMessage().contains(MessageConstants.REGISTER_SUCCESS),
                "Expected message to contain '" + MessageConstants.REGISTER_SUCCESS
                        + "' but was: " + loginPage.getFlashMessage()
        );
    }

    // Single reusable assertion step — handles all registration error
    // messages from the Examples table (mirrors LoginSteps' equivalent step)
    @Then("the registration error message {string} should be displayed")
    public void theRegistrationErrorMessageShouldBeDisplayed(String expectedMessage) {
        log.info("[RegisterSteps] Verifying registration error message: {}", expectedMessage);
        Assert.assertTrue(
                registerPage.getErrorMessage().contains(expectedMessage),
                "Expected error message to contain '" + expectedMessage
                        + "' but was: " + registerPage.getErrorMessage()
        );
    }

    @And("the user should stay on the register page")
    public void theUserShouldStayOnTheRegisterPage() {
        log.info("[RegisterSteps] Verifying user stays on register page");
        Assert.assertTrue(
                registerPage.getCurrentPageUrl().contains(PageUrlConstants.REGISTER_PAGE),
                "Expected URL to contain '" + PageUrlConstants.REGISTER_PAGE
                        + "' but was: " + registerPage.getCurrentPageUrl()
        );
    }
}