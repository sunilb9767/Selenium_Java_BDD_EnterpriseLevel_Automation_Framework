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

        	// Single WebDriver call — bytes only, no file written by our code
            byte[] screenshotBytes = ScreenshotUtil.captureAsBytes(scenario.getName());

            if (screenshotBytes.length > 0) {
            	 
                // Build the visible label shown in both Spark HTML and PDF reports.
                // This is the caption the stakeholder sees next to the screenshot.
                // It is NOT the disk filename — the adapter manages that.
                String label = ScreenshotUtil.buildScreenshotLabel(scenario.getName());
                
             // This is the only attach mechanism confirmed to work from @After
                // in grasshopper auto-mode. addScreenCaptureFromPath() from @After
                scenario.attach(screenshotBytes, "image/png", label);

                log.warn("[Hooks] FAILED: '{}' | Screenshot attached to reports.",
                        scenario.getName());
            }else {
            	log.error("[Hooks] FAILED but screenshot capture returned empty bytes: '{}'",
                        scenario.getName());
            }

        } else {
        	log.info("[Hooks] PASSED: '{}'", scenario.getName());
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