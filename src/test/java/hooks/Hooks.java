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
	 * Runs before every Cucumber scenario. Initializes driver and navigates to base
	 * URL.
	 * 
	 * @param scenario provides scenario name and tags at runtime
	 */
	@Before
	public void setUp(Scenario scenario) {

		// Read browser from system property (Jenkins) or fall back to config.properties
		String browser = BrowserContext.get() != null ? BrowserContext.get() : ConfigReader.getBrowser();

		// Log browser and unique thread identity for parallel execution.
		log.info("[Hooks] Starting scenario: {}", scenario.getName());

		// Initialize the browser driver for the current thread
		DriverManager.initDriver(browser);

		// Record the execution browser in the scenario report.
		scenario.log("Execution Browser: " + browser.toUpperCase(java.util.Locale.ROOT));
	}

	/**
	 * Runs after every Cucumber scenario regardless of pass or fail. Captures ONE
	 * screenshot on failure, closes browser.
	 * 
	 * @param scenario provides pass/fail status and ability to attach screenshots
	 */
	@After
	public void tearDown(Scenario scenario) {

		// Ensure scenario teardown runs even when screenshot handling fails.
		try {
			// Capture evidence only when the scenario fails.
			if (scenario.isFailed()) {

				// Single WebDriver call — bytes only, no file written by our code
				// Request a screenshot from the active local or remote browser.
				byte[] screenshotBytes = ScreenshotUtil.captureAsBytes(scenario.getName());

				// Attach screenshots only when capture succeeded.
				if (screenshotBytes.length > 0) {

					// Build the visible label shown in both Spark HTML and PDF reports.
					// This is the caption the stakeholder sees next to the screenshot.
					// It is NOT the disk filename — the adapter manages that.
					// Create a readable screenshot label for the report.
					String label = ScreenshotUtil.buildScreenshotLabel(scenario.getName());

					// This is the only attach mechanism confirmed to work from @After
					// in grasshopper auto-mode. addScreenCaptureFromPath() from @After
					// Attach screenshot bytes directly to the Cucumber report.
					scenario.attach(screenshotBytes, "image/png", label);

					log.warn("[Hooks] FAILED: '{}' | Screenshot attached to reports.", scenario.getName());
				} else {
					log.error("[Hooks] FAILED but screenshot capture returned empty bytes: '{}'", scenario.getName());
				}

			} else {
				log.info("[Hooks] PASSED: '{}'", scenario.getName());
			}

			// Always release the browser and scenario context.
		} finally {
			try {
				// Log completion before clearing the browser context.
				log.info("[Hooks] Scenario completed: {} | Status: {}", scenario.getName(), scenario.getStatus());
			} finally {
				try {
					// Close the local or remote browser session.
					DriverManager.quitDriver();
				} finally {
					// Clear thread-local browser and logging metadata.
					BrowserContext.clear();
				}
			}
		}
	}
}