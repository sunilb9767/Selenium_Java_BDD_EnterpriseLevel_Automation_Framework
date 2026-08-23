package driver;

import config.ConfigReader;
import constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

//Responsible only for creating and configuring browser driver instances.
public class DriverFactory {

	// Logger instance for DriverFactory class
	private static final Logger log = LogManager.getLogger(DriverFactory.class);

	/**
	 * Creates a new WebDriver instance for the given browser name.
	 * @param browser browser name from config.properties or Jenkins parameter
	 * @return configured WebDriver instance ready for use
	 */
	public static WebDriver createDriver(String browser) {

		// Normalize input to avoid case/space mismatches like "Chrome" or " chrome "
		String browserName = browser.trim().toLowerCase();

		// Read headless flag from config — true in CI/Jenkins, false for local runs
		boolean isHeadless = ConfigReader.isHeadless();

		log.info("[DriverFactory] Launching browser: {} | Headless: {}", browserName, isHeadless);

		try {

			switch (browserName) {

				case FrameworkConstants.CHROME:
					return createChromeDriver(isHeadless);

				case FrameworkConstants.FIREFOX:
					return createFirefoxDriver(isHeadless);

				case FrameworkConstants.EDGE:
					return createEdgeDriver(isHeadless);

				default:
					// Fail fast with a clear message instead of returning null
					log.error("[DriverFactory] Unsupported browser: {}", browser);
					throw new IllegalArgumentException(
						"[DriverFactory] Unsupported browser: '" + browser + "'. " +
						"Valid values: chrome, firefox, edge"
					);
			}

		} catch (SessionNotCreatedException e) {
			// Most common real-world cause: installed browser version and the
			// driver binary version are out of sync (e.g., Chrome auto-updated
			// but the cached chromedriver binary didn't).
			log.error("[DriverFactory] Failed to start '{}' — browser and driver version mismatch. " +
					"Update the browser or clear the driver cache and retry. Details: {}",
					browserName, e.getMessage());
			throw new RuntimeException(
				"[DriverFactory] Could not start '" + browserName + "': installed browser version " +
				"is likely incompatible with the driver binary. Update the browser, or clear the " +
				"driver cache (e.g., ~/.cache/selenium) and retry.", e
			);

		} catch (WebDriverException e) {
			// Catches other startup failures: driver binary missing/corrupt,
			// port conflicts, insufficient permissions, etc.
			log.error("[DriverFactory] Failed to start '{}': {}", browserName, e.getMessage());
			throw new RuntimeException(
				"[DriverFactory] Could not start browser '" + browserName + "'. " +
				"Check that the browser is installed and no stale driver process is holding the port.", e
			);
		}
	}

	// =====================================================================
	// Per-browser driver creation — extracted from the switch for clarity
	// =====================================================================

	private static WebDriver createChromeDriver(boolean isHeadless) {

		ChromeOptions chromeOptions = new ChromeOptions();

		chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

		// Launch Chrome in maximized window to avoid responsive layout issues
		chromeOptions.addArguments("--start-maximized");

		// Suppress automation-related UI flags using modern Chromium approach
		chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

		// Disable extensions to prevent interference during test execution
		chromeOptions.addArguments("--disable-extensions");

		chromeOptions.addArguments("--disable-dev-shm-usage");

		// Enable headless mode for CI runs — uses new headless implementation
		if (isHeadless) {
			chromeOptions.addArguments("--headless=new");
			log.debug("[DriverFactory] Chrome running in headless mode");
		}

		return new ChromeDriver(chromeOptions);
	}

	private static WebDriver createFirefoxDriver(boolean isHeadless) {

		FirefoxOptions firefoxOptions = new FirefoxOptions();

		firefoxOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

		// Enable headless mode for CI runs
		if (isHeadless) {
			firefoxOptions.addArguments("--headless");
			log.debug("[DriverFactory] Firefox running in headless mode");
		}

		return new FirefoxDriver(firefoxOptions);
	}

	private static WebDriver createEdgeDriver(boolean isHeadless) {

		EdgeOptions edgeOptions = new EdgeOptions();

		edgeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

		// Launch Edge in maximized window to avoid responsive layout issues
		edgeOptions.addArguments("--start-maximized");

		// Suppress automation-related UI flags using modern Chromium approach
		edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

		// Disable extensions to prevent interference during test execution
		edgeOptions.addArguments("--disable-extensions");

		edgeOptions.addArguments("--disable-dev-shm-usage");

		// Enable headless mode for CI runs
		if (isHeadless) {
			edgeOptions.addArguments("--headless=new");
			log.debug("[DriverFactory] Edge running in headless mode");
		}

		return new EdgeDriver(edgeOptions);
	}
}