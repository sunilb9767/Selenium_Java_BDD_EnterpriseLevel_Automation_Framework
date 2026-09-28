package base;

import driver.DriverManager;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// Base class for all Page Object classes.
// Handles element interactions only — all wait logic delegated to WaitUtils.
public class BasePage {

    // Logger instance for BasePage class
    private static final Logger log = LogManager.getLogger(BasePage.class);

    // WebDriver instance for the current thread
    protected WebDriver driver;

    public BasePage() {

        // Fetch the driver assigned to the current thread from DriverManager
        this.driver = DriverManager.getDriver();

        log.debug("[BasePage] Initialized for page: {}", this.getClass().getSimpleName());
    }

    // =====================================================================
    // Core browser actions
    // =====================================================================

    // Navigate the browser to the given URL
    public void navigateTo(String url) {
        log.info("[BasePage] Navigating to URL: {}", url);
        driver.get(url);
    }

    // Return the title of the currently loaded page
    public String getPageTitle() {
        log.debug("[BasePage] Fetching page title");
        return driver.getTitle();
    }

    // Return the current URL of the browser
    public String getCurrentUrl() {
        log.debug("[BasePage] Fetching current URL");
        return driver.getCurrentUrl();
    }

    // =====================================================================
    // Element interaction methods — explicit wait via WaitUtils
    // =====================================================================

 // Standard click — use this for normal buttons with no overlay issues
    // Waits for element to be clickable before clicking
    // Use this by default on all pages
    protected void click(By locator) {
        log.debug("[BasePage] Clicking element: {}", locator);
        WaitUtils.waitForClickable(locator).click();
    }
    
 // Smart click — use this when a button may be covered by an overlay
    // (cookie banners, notification popups, loading spinners, etc.)
    // Tries real browser click first — only falls back to JS click if intercepted
    // This preserves real user simulation while handling overlay issues gracefully
    protected void smartClick(By locator) {
        log.debug("[BasePage] Smart clicking element: {}", locator);
        try {
            scrollIntoView(locator);                     // bring element to center of screen
            WaitUtils.waitForClickable(locator).click(); // attempt real browser click
        } catch (ElementClickInterceptedException e) {
            log.warn("[BasePage] Click intercepted by overlay, falling back to JS click: {}", locator);
            jsClick(locator);                            // JS click as last resort only
        }
    }

    // Clear the field and type the given text into the element
    protected void type(By locator, String text) {
        log.debug("[BasePage] Typing into element: {}", locator);
        WebElement element = WaitUtils.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    // Retrieve the visible text content of the element
    protected String getText(By locator) {
        log.debug("[BasePage] Getting text from element: {}", locator);
        return WaitUtils.waitForVisible(locator).getText();
    }

    // Retrieve the value of the specified HTML attribute from the element
    protected String getAttribute(By locator, String attribute) {
        log.debug("[BasePage] Getting attribute '{}' from element: {}", attribute, locator);
        return WaitUtils.waitForVisible(locator).getAttribute(attribute);
    }

    // Return true if the element is visible on the page, false otherwise
    protected boolean isDisplayed(By locator) {
        try {
            return WaitUtils.waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            // Return false instead of throwing exception when element is not found
            log.warn("[BasePage] Element not visible, returning false: {}", locator);
            return false;
        }
    }

    // Return true if the checkbox or radio button element is selected
    protected boolean isSelected(By locator) {
        log.debug("[BasePage] Checking if element is selected: {}", locator);
        return WaitUtils.waitForVisible(locator).isSelected();
    }

    // Return true if the element is enabled for interaction
    protected boolean isEnabled(By locator) {
        log.debug("[BasePage] Checking if element is enabled: {}", locator);
        return WaitUtils.waitForVisible(locator).isEnabled();
    }

    // =====================================================================
    // Element interaction methods — fluent wait via WaitUtils
    // =====================================================================

    // Click a dynamic element using fluent wait — handles intermittent visibility
    protected void fluentClick(By locator) {
        log.debug("[BasePage] Fluent clicking element: {}", locator);
        WaitUtils.fluentWaitForClickable(locator).click();
    }

    // Type into a dynamic element using fluent wait — handles delayed field rendering
    protected void fluentType(By locator, String text) {
        log.debug("[BasePage] Fluent typing '{}' into element: {}", text, locator);
        WebElement element = WaitUtils.fluentWaitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    // =====================================================================
    // JavaScript executor methods
    // =====================================================================

    // Click the element using JavaScript — useful when normal click is intercepted
    protected void jsClick(By locator) {
        log.debug("[BasePage] JS clicking element: {}", locator);
        WebElement element = WaitUtils.waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // Set the element's value directly via JavaScript — avoids locale-dependent keystrokes on native fields like type="date"
    protected void setValueViaJs(By locator, String value) {
        log.debug("[BasePage] Setting value via JS on element: {}", locator);
        WebElement element = WaitUtils.waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                element, value);
    }

    // Scroll the element into the visible area of the browser window
    protected void scrollIntoView(By locator) {
        log.debug("[BasePage] Scrolling element into view: {}", locator);
        WebElement element = WaitUtils.waitForVisible(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Scroll the page to the very top
    protected void scrollToTop() {
        log.debug("[BasePage] Scrolling to top of page");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    // Scroll the page to the very bottom
    protected void scrollToBottom() {
        log.debug("[BasePage] Scrolling to bottom of page");
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
}