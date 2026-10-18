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
	 * 
	 * @param browser browser name from config.properties or Jenkins parameter
	 */
	public static void initDriver(String browser) {

		// NEW [GRID]: Avoid leaking an existing remote session.
		// Prevent overwriting an active driver on the same thread.
		if (hasDriver()) {
			// Fail clearly if driver initialization is repeated.
			throw new IllegalStateException("WebDriver already initialized on this thread");
		}

		// Delegate browser creation entirely to DriverFactory
		WebDriver driver = DriverFactory.createDriver(browser);

		try {

			// Apply page load timeout — max seconds to wait for a full page to load
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));

			// Clear leftover cookies from any previous session
			driver.manage().deleteAllCookies();

			// Store driver in ThreadLocal so each thread accesses only its own instance
			threadLocalDriver.set(driver);

			log.info("[DriverManager] Driver initialized on thread: {}", Thread.currentThread().getName());

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

			throw new RuntimeException("[DriverManager] Driver setup failed after browser launch on thread: "
					+ Thread.currentThread().getName(), e);
		}
	}

	/**
	 * Returns the WebDriver instance for the currently running thread. CHANGED:
	 * fails fast instead of returning null. A missing driver now surfaces
	 * immediately with a clear message, not as a NullPointerException many calls
	 * later.
	 * 
	 * @throws IllegalStateException if no driver has been initialized for this
	 *                               thread
	 */
	public static WebDriver getDriver() {
		// Each thread retrieves only its own driver — never another thread's
		// Read only the driver belonging to the current thread.
		WebDriver driver = threadLocalDriver.get();

		// CHANGED: fail-fast guard
		if (driver == null) {
			throw new IllegalStateException(
					"[DriverManager] WebDriver has not been initialized for thread: " + Thread.currentThread().getName()
							+ ". Make sure the @Before hook ran DriverManager.initDriver() successfully.");
		}

		return driver;
	}

	/**
	 * CHANGED (new method): safe, non-throwing check for hooks, teardown and
	 * screenshot code that must keep working even when the browser failed to start.
	 */
	public static boolean hasDriver() {
		return threadLocalDriver.get() != null;
	}

	/**
	 * Quits the browser and cleans up all ThreadLocal slots for the current thread.
	 */
	public static void quitDriver() {
		// Read only the driver belonging to the current thread.
		WebDriver driver = threadLocalDriver.get();
		try {
			// Close the current browser session if it exists.
			if (driver != null)
				driver.quit();
		} catch (Exception e) {
			// Log Grid or browser shutdown errors without skipping cleanup.
			log.warn("[DriverManager] Browser quit failed: {}", e.getMessage(), e);
		} finally {
			// NEW [GRID]: Always clear ThreadLocal state, even if Grid is unreachable.
			try {
				// Clear thread-specific cached wait objects.
				WaitUtils.removeWaits();
			} finally {
				// Release the current thread’s WebDriver reference.
				threadLocalDriver.remove();
			}
		}
	}
}