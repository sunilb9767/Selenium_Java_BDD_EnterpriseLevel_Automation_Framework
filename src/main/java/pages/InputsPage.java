package pages;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
 
import base.BasePage;
 
// Page object for Web Inputs page — https://practice.expandtesting.com/inputs
public class InputsPage extends BasePage {
 
	// Logger instance for InputsPage class
	private static final Logger log = LogManager.getLogger(InputsPage.class);
 
	// Number input field locator — matched on type attribute
	private final By numberInput = By.cssSelector("input[type='number']");
 
	// Text input field locator — matched on type attribute
	private final By textInput = By.cssSelector("input[type='text']");
 
	// Password input field locator — matched on type attribute
	private final By passwordInput = By.cssSelector("input[type='password']");
 
	// Date input field locator — matched on type attribute
	private final By dateInput = By.cssSelector("input[type='date']");
 
	// Number output locator — id confirmed from page HTML
	private final By numberOutput = By.id("output-number");
 
	// Text output locator — id confirmed from page HTML
	private final By textOutput = By.id("output-text");
 
	// Password output locator — id confirmed from page HTML
	private final By passwordOutput = By.id("output-password");
 
	// Date output locator — id confirmed from page HTML
	private final By dateOutput = By.id("output-date");
 
	// Display Inputs button locator — matched on visible button text
	private final By displayInputsButton = By.xpath("//button[normalize-space()='Display Inputs']");
 
	// Clear Inputs button locator — matched on visible button text
	private final By clearInputsButton = By.xpath("//button[normalize-space()='Clear Inputs']");
 
	// =====================================================================
	// Page actions
	// =====================================================================
 
	// Enter a value into the number input field
	public InputsPage enterNumber(String number) {
		log.info("[InputsPage] Entering number: {}", number);
		type(numberInput, number);
		return this;
	}
 
	// Enter a value into the text input field
	public InputsPage enterText(String text) {
		log.info("[InputsPage] Entering text: {}", text);
		type(textInput, text);
		return this;
	}
 
	// Enter a value into the password input field
	public InputsPage enterPassword(String password) {
		log.info("[InputsPage] Entering password");
		type(passwordInput, password);
		return this;
	}
 
	// Set the date via JS in ISO format (yyyy-MM-dd) — avoids locale-dependent sendKeys on type="date"
	public InputsPage enterDate(String isoDate) {
		log.info("[InputsPage] Entering date: {}", isoDate);
		setValueViaJs(dateInput, isoDate);
		return this;
	}
 
	// Click the Display Inputs button to echo entered values into the output fields
	public InputsPage clickDisplayInputs() {
		log.info("[InputsPage] Clicking Display Inputs button");
		smartClick(displayInputsButton);
		return this;
	}
 
	// Click the Clear Inputs button to reset all input fields
	public InputsPage clickClearInputs() {
		log.info("[InputsPage] Clicking Clear Inputs button");
		smartClick(clearInputsButton);
		return this;
	}
 
	// =====================================================================
	// Page assertions — entered values (read back from each INPUT field)
	// =====================================================================
 
	// Return the number field's own reported value, not the raw string passed to enterNumber()
	public String getEnteredNumber() {
		log.info("[InputsPage] Getting entered number");
		return getAttribute(numberInput, "value");
	}
 
	// Return the text field's own reported value
	public String getEnteredText() {
		log.info("[InputsPage] Getting entered text");
		return getAttribute(textInput, "value");
	}
 
	// Return the password field's own reported value
	public String getEnteredPassword() {
		log.info("[InputsPage] Getting entered password");
		return getAttribute(passwordInput, "value");
	}
 
	// Return the date field's own reported value — always ISO format (yyyy-MM-dd)
	public String getEnteredDate() {
		log.info("[InputsPage] Getting entered date");
		return getAttribute(dateInput, "value");
	}
 
	// =====================================================================
	// Page assertions — displayed values (read from OUTPUT fields)
	// =====================================================================
 
	// Return the displayed number text from the output field
	public String getDisplayedNumber() {
		log.info("[InputsPage] Getting displayed number");
		return getText(numberOutput);
	}
 
	// Return the displayed text from the output field
	public String getDisplayedText() {
		log.info("[InputsPage] Getting displayed text");
		return getText(textOutput);
	}
 
	// Return the displayed password text from the output field
	public String getDisplayedPassword() {
		log.info("[InputsPage] Getting displayed password");
		return getText(passwordOutput);
	}
 
	// Return the displayed date text from the output field
	public String getDisplayedDate() {
		log.info("[InputsPage] Getting displayed date");
		return getText(dateOutput);
	}
}
 