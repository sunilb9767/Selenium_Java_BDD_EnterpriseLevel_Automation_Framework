package driver;

import config.ConfigReader;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import java.time.Duration;

// Responsible only for storing and managing WebDriver instances per thread.
public class DriverManager {

    // Logger instance for DriverManager class
    private static final Logger log = LogManager.getLogger(DriverManager.class);

    // Holds a separate WebDriver instance for each parallel thread
    private static final ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();

    /**
     * Initializes the browser and stores the driver for the current thread.
     * @param browser browser name from config.properties or Jenkins parameter
     */
    public static void initDriver(String browser) {

        // Delegate browser creation entirely to DriverFactory
        WebDriver driver = DriverFactory.createDriver(browser);

        try {

            // Apply page load timeout — max seconds to wait for a full page to load
            driver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));

            // Clear leftover cookies from any previous session
            driver.manage().deleteAllCookies();

            // Store driver in ThreadLocal so each thread accesses only its own instance
            threadLocalDriver.set(driver);

            log.info("[DriverManager] Driver initialized on thread: {}",
                    Thread.currentThread().getName());

        } catch (Exception e) {
            // Driver was created successfully, but post-creation setup failed
            // (bad timeout config, flaky session, etc.). The browser is running
            // but was never stored in ThreadLocal, so nothing else will ever
            // quit it — clean it up here before this thread loses its handle.
            log.error("[DriverManager] Post-creation setup failed on thread: {} — quitting orphaned driver. Reason: {}",
                    Thread.currentThread().getName(), e.getMessage());

            try {
                driver.quit();
            } catch (Exception quitEx) {
                log.warn("[DriverManager] Failed to quit orphaned driver cleanly: {}", quitEx.getMessage());
            }

            throw new RuntimeException(
                "[DriverManager] Driver setup failed after browser launch on thread: "
                        + Thread.currentThread().getName(), e);
        }
    }

    /**
     * Returns the WebDriver instance for the currently running thread.
     */
    public static WebDriver getDriver() {
        // Each thread retrieves only its own driver — never another thread's
        return threadLocalDriver.get();
    }

    /**
     * Quits the browser and cleans up all ThreadLocal slots for the current thread.
     */
    public static void quitDriver() {

        // Guard against NullPointerException if driver was never initialized
        if (threadLocalDriver.get() != null) {

            // Close all browser windows and shut down the WebDriver process
            threadLocalDriver.get().quit();

            log.info("[DriverManager] Driver closed on thread: {}",
                    Thread.currentThread().getName());

            // Remove cached wait instances from WaitUtils ThreadLocal for this thread
            WaitUtils.removeWaits();

            // Remove stale driver reference so thread-pool threads start clean next time
            threadLocalDriver.remove();
        }
    }
}