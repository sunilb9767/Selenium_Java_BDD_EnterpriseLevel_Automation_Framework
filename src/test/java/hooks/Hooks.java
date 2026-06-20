package hooks;

import config.ConfigReader;
import driver.BrowserContext;
import driver.DriverManager;
import utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Cucumber lifecycle hooks — manages driver setup, teardown and screenshot per scenario.
public class Hooks {

    // Logger instance for Hooks class
    private static final Logger log = LogManager.getLogger(Hooks.class);

    /**
     * Runs before every Cucumber scenario.
     * Initializes driver and navigates to base URL.
     * @param scenario provides scenario name and tags at runtime
     */
    @Before
    public void setUp(Scenario scenario) {

        // Read browser from system property (Jenkins) or fall back to config.properties
    	 String browser = BrowserContext.get() != null
    	            ? BrowserContext.get()
    	            : ConfigReader.getBrowser();

        log.info("[Hooks] Starting scenario: {} | Browser: {}", scenario.getName(), browser);

        // Initialize the browser driver for the current thread
        DriverManager.initDriver(browser);
    }

    /**
     * Runs after every Cucumber scenario regardless of pass or fail.
     * Captures ONE screenshot on failure, closes browser.
     * @param scenario provides pass/fail status and ability to attach screenshots
     */
    @After
    public void tearDown(Scenario scenario) {

        if (scenario.isFailed()) {

            // Single capture — saved to disk AND bytes returned from the SAME screenshot
            ScreenshotUtil.ScreenshotResult screenshot =
                    ScreenshotUtil.captureScreenshot(scenario.getName());

            // Attach the SAME bytes to Cucumber report — no second screenshot taken
            scenario.attach(screenshot.bytes, "image/png", "Failure: " + scenario.getName());

            log.warn("[Hooks] Scenario FAILED: {} | Screenshot: {}",
                    scenario.getName(), screenshot.path);

        } else {
            log.info("[Hooks] Scenario PASSED: {}", scenario.getName());
        }

        // Quit browser and remove driver from ThreadLocal for this thread
        DriverManager.quitDriver();
        
     // Clear the browser binding for this thread — defensive cleanup alongside
        // WaitUtils.removeWaits() (already called inside DriverManager.quitDriver()).
        BrowserContext.clear();

        log.info("[Hooks] Scenario completed: {} | Status: {}",
                scenario.getName(), scenario.getStatus());
    }
}