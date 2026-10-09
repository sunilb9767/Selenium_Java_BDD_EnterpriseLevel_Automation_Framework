package stepdefs;
 
import config.ConfigReader;
import constants.PageUrlConstants;
import pages.InputsPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
 
// Step definitions for Web Inputs feature file.
public class InputsSteps {
 
    // Logger instance for InputsSteps class
    private static final Logger log = LogManager.getLogger(InputsSteps.class);
 
    // CHANGED: page object is now injected by PicoContainer (was: = new InputsPage())
    private final InputsPage inputsPage;
 
    // CHANGED (new constructor): PicoContainer creates one instance per scenario and passes it in
    public InputsSteps(InputsPage inputsPage) {
        this.inputsPage = inputsPage;
    }
 
    // Entered number, captured from the field's own reported value
    private String enteredNumber;
 
    // Entered text, captured from the field's own reported value
    private String enteredText;
 
    // Entered password, captured from the field's own reported value
    private String enteredPassword;
 
    // Entered date, captured from the field's own reported value
    private String enteredDate;
 
    // =====================================================================
    // Given steps
    // =====================================================================
 
    @Given("the user is on the inputs page")
    public void theUserIsOnTheInputsPage() {
        log.info("[InputsSteps] Navigating to inputs page");
        inputsPage.navigateTo(ConfigReader.getBaseUrl() + PageUrlConstants.INPUTS_PAGE);
    }
 
    // =====================================================================
    // When steps
    // =====================================================================
 
    // Test data comes from the feature file — the values are what this scenario verifies
    @When("the user enters {string} as the number, {string} as the text, {string} as the password, and {string} as the date")
    public void theUserEntersValuesIntoAllFields(String number, String text, String password, String isoDate) {
        log.info("[InputsSteps] Entering values into all four input fields");
 
        inputsPage.enterNumber(number);
        inputsPage.enterText(text);
        inputsPage.enterPassword(password);
        inputsPage.enterDate(isoDate);
 
        // Read back each field's own value rather than trusting the raw strings passed in
        enteredNumber = inputsPage.getEnteredNumber();
        enteredText = inputsPage.getEnteredText();
        enteredPassword = inputsPage.getEnteredPassword();
        enteredDate = inputsPage.getEnteredDate();
    }
 
    @When("the user attempts to enter {string} into the number field")
    public void theUserAttemptsToEnterIntoTheNumberField(String value) {
        log.info("[InputsSteps] Attempting to enter non-numeric value into number field: {}", value);
        inputsPage.enterNumber(value);
    }
 
    @And("the user clicks the Display Inputs button")
    public void theUserClicksTheDisplayInputsButton() {
        log.info("[InputsSteps] Clicking Display Inputs button");
        inputsPage.clickDisplayInputs();
    }
 
    @And("the user clicks the Clear Inputs button")
    public void theUserClicksTheClearInputsButton() {
        log.info("[InputsSteps] Clicking Clear Inputs button");
        inputsPage.clickClearInputs();
    }
 
    // =====================================================================
    // Then steps — one assertion per step, meaningful failure messages
    // =====================================================================
 
    @Then("the displayed number should match the entered number")
    public void theDisplayedNumberShouldMatchTheEnteredNumber() {
        log.info("[InputsSteps] Verifying displayed number matches entered number");
        Assert.assertTrue(
                inputsPage.getDisplayedNumber().equals(enteredNumber),
                "Expected displayed number to equal entered number: " + enteredNumber
        );
    }
 
    @And("the displayed text should match the entered text")
    public void theDisplayedTextShouldMatchTheEnteredText() {
        log.info("[InputsSteps] Verifying displayed text matches entered text");
        Assert.assertTrue(
                inputsPage.getDisplayedText().equals(enteredText),
                "Expected displayed text to equal entered text: " + enteredText
        );
    }
 
    @And("the displayed password should match the entered password")
    public void theDisplayedPasswordShouldMatchTheEnteredPassword() {
        log.info("[InputsSteps] Verifying displayed password matches entered password");
        Assert.assertTrue(
                inputsPage.getDisplayedPassword().equals(enteredPassword),
                "Expected displayed password to equal entered password"
        );
    }
 
    @And("the displayed date should match the entered date")
    public void theDisplayedDateShouldMatchTheEnteredDate() {
        log.info("[InputsSteps] Verifying displayed date matches entered date");
        Assert.assertTrue(
                inputsPage.getDisplayedDate().equals(enteredDate),
                "Expected displayed date to equal entered date: " + enteredDate
        );
    }
 
    @Then("the number field should be empty")
    public void theNumberFieldShouldBeEmpty() {
        log.info("[InputsSteps] Verifying number field is empty");
        Assert.assertTrue(
                inputsPage.getEnteredNumber().isEmpty(),
                "Expected number field to be empty but was: " + inputsPage.getEnteredNumber()
        );
    }
 
    @And("the text field should be empty")
    public void theTextFieldShouldBeEmpty() {
        log.info("[InputsSteps] Verifying text field is empty");
        Assert.assertTrue(
                inputsPage.getEnteredText().isEmpty(),
                "Expected text field to be empty but was: " + inputsPage.getEnteredText()
        );
    }
 
    @And("the password field should be empty")
    public void thePasswordFieldShouldBeEmpty() {
        log.info("[InputsSteps] Verifying password field is empty");
        Assert.assertTrue(
                inputsPage.getEnteredPassword().isEmpty(),
                "Expected password field to be empty"
        );
    }
 
    @And("the date field should be empty")
    public void theDateFieldShouldBeEmpty() {
        log.info("[InputsSteps] Verifying date field is empty");
        Assert.assertTrue(
                inputsPage.getEnteredDate().isEmpty(),
                "Expected date field to be empty but was: " + inputsPage.getEnteredDate()
        );
    }
}
 