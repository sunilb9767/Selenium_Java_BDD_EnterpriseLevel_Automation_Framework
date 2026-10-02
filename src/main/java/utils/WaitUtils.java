package utils;

import config.ConfigReader;
import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

// Centralised utility class for all explicit and fluent wait operations.
// All wait logic lives here — BasePage delegates to this class for element resolution.
public class WaitUtils {

    // Logger instance for WaitUtils class
    private static final Logger log = LogManager.getLogger(WaitUtils.class);

    // Private constructor prevents instantiation — this is a static utility class
    private WaitUtils() {}

    // =====================================================================
    // ThreadLocal wait instances — one per thread, created once and reused
    // =====================================================================

    // Caches one WebDriverWait instance per thread — avoids repeated object creation
    private static final ThreadLocal<WebDriverWait> explicitWaitThread = new ThreadLocal<>();

    // Caches one FluentWait instance per thread — avoids repeated object creation
    private static final ThreadLocal<FluentWait<WebDriver>> fluentWaitThread = new ThreadLocal<>();

    // =====================================================================
    // Wait instance getters — return cached instance or create if not yet initialized
    // =====================================================================

    /**
     * Returns cached WebDriverWait for current thread.
     * Creates a new instance only if one does not exist for this thread yet.
     */
    private static WebDriverWait getExplicitWait() {

        // Check if WebDriverWait instance already exists for the current thread
        if (explicitWaitThread.get() == null) {

            // Create new WebDriverWait and cache it for the current thread
            explicitWaitThread.set(new WebDriverWait(DriverManager.getDriver(),
                    Duration.ofSeconds(ConfigReader.getExplicitWait())));

            log.debug("[WaitUtils] ExplicitWait instance created for thread: {}",
                    Thread.currentThread().getName());
        }

        // Return the cached WebDriverWait instance for this thread
        return explicitWaitThread.get();
    }

    /**
     * Returns cached FluentWait for current thread.
     * Creates a new instance only if one does not exist for this thread yet.
     */
    private static FluentWait<WebDriver> getFluentWait() {

        // Check if FluentWait instance already exists for the current thread
        if (fluentWaitThread.get() == null) {

            // Create new FluentWait and cache it for the current thread
            fluentWaitThread.set(new FluentWait<>(DriverManager.getDriver())
                    // Max time to keep retrying before throwing TimeoutException
                    .withTimeout(Duration.ofSeconds(ConfigReader.getFluentWaitTimeout()))
                    // How frequently to retry the condition during the wait period
                    .pollingEvery(Duration.ofSeconds(ConfigReader.getFluentWaitPolling()))
                    // Ignore NoSuchElementException — element may not be in DOM yet
                    .ignoring(NoSuchElementException.class)
                    // Ignore StaleElementReferenceException — element may re-render during wait
                    .ignoring(StaleElementReferenceException.class));

            log.debug("[WaitUtils] FluentWait instance created for thread: {}",
                    Thread.currentThread().getName());
        }

        // Return the cached FluentWait instance for this thread
        return fluentWaitThread.get();
    }

    // =====================================================================
    // ThreadLocal cleanup — must be called when driver is quit
    // =====================================================================

    /**
     * Removes wait instances from ThreadLocal for the current thread.
     * Must be called from DriverManager.quitDriver() to prevent memory leaks
     * in thread pool environments where threads are reused across tests.
     */
    public static void removeWaits() {

        // Remove explicit wait instance for the current thread
        explicitWaitThread.remove();

        // Remove fluent wait instance for the current thread
        fluentWaitThread.remove();

        log.debug("[WaitUtils] Wait instances removed for thread: {}",
                Thread.currentThread().getName());
    }

    // =====================================================================
    // Explicit wait methods — use for stable elements with known load time
    // =====================================================================

    // Wait until the element is visible in the DOM and return it
    public static WebElement waitForVisible(By locator) {
        log.debug("[WaitUtils] Waiting for element to be visible: {}", locator);
        return getExplicitWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Wait until the element is visible and enabled for clicking then return it
    public static WebElement waitForClickable(By locator) {
        log.debug("[WaitUtils] Waiting for element to be clickable: {}", locator);
        return getExplicitWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    // Wait until the element is fully removed from the DOM
    public static void waitForInvisible(By locator) {
        log.debug("[WaitUtils] Waiting for element to be invisible: {}", locator);
        getExplicitWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // Wait until the page title exactly matches the expected title
    public static void waitForTitle(String title) {
        log.debug("[WaitUtils] Waiting for page title: {}", title);
        getExplicitWait().until(ExpectedConditions.titleIs(title));
    }

    // Wait until the page URL contains the expected partial URL string
    public static void waitForUrlContains(String partialUrl) {
        log.debug("[WaitUtils] Waiting for URL to contain: {}", partialUrl);
        getExplicitWait().until(ExpectedConditions.urlContains(partialUrl));
    }
    
 // Wait until all matching elements are visible and return them — for tables, lists, header rows
    public static List<WebElement> waitForAllVisible(By locator) {
        log.debug("[WaitUtils] Waiting for all elements to be visible: {}", locator);
        return getExplicitWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    // =====================================================================
    // Fluent wait methods — use for dynamic/unstable elements
    // =====================================================================

    // Wait for a dynamic element using fluent wait — retries with custom polling
    public static WebElement fluentWaitForVisible(By locator) {
        log.debug("[WaitUtils] Fluent waiting for element to be visible: {}", locator);
        return getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Wait for a dynamic element to be clickable using fluent wait
    public static WebElement fluentWaitForClickable(By locator) {
        log.debug("[WaitUtils] Fluent waiting for element to be clickable: {}", locator);
        return getFluentWait().until(ExpectedConditions.elementToBeClickable(locator));
    }
}
