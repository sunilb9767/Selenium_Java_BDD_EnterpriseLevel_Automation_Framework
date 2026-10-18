package driver;

import config.ConfigReader;
import constants.FrameworkConstants;
import java.net.URI;
import java.net.MalformedURLException;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/** Creates exactly one local or remote browser session per invocation. */
public final class DriverFactory {
	private static final Logger log = LogManager.getLogger(DriverFactory.class);

	// Prevent creating instances of this utility class.
	private DriverFactory() {
	}

	// Create one fresh local or remote browser session.
	public static WebDriver createDriver(String browser) {
		// Reject missing browser names before configuring a driver.
		if (browser == null || browser.isBlank()) {
			throw new IllegalArgumentException("Browser must not be blank");
		}
		// Normalize the browser name for consistent matching.
		String name = browser.trim().toLowerCase(Locale.ROOT);
		// Choose local or remote execution from configuration.
		String mode = ConfigReader.getExecutionMode(); // NEW [GRID]
		// Build browser options once for either execution mode.
		MutableCapabilities options = createOptions(name, ConfigReader.isHeadless());
		// Log the browser and execution mode for troubleshooting.
		log.info("[DriverFactory] Browser={} Mode={}", name, mode);
		try {
			// Route remote execution to Selenium Grid.
			if ("remote".equals(mode)) {
				// NEW [GRID]: Never silently fall back to a local browser.
				// Request a new browser session from the configured Grid.
				RemoteWebDriver driver = new RemoteWebDriver(URI.create(ConfigReader.getGridUrl()).toURL(), options);
				// Record the Grid session ID for tracing parallel tests.
				log.info("[DriverFactory] Grid session={}", driver.getSessionId());
				// Return the newly created remote browser session.
				return driver;
			}
			// Use local WebDriver when local execution is selected.
			return createLocal(name, options);
			// Handle an invalid remote URL conversion.
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid Selenium Grid URL", e);
			// Wrap Selenium startup failures with execution context.
		} catch (WebDriverException e) {
			throw new IllegalStateException("Failed to start " + mode + " browser " + name
					+ ". Check Grid availability, browser slots and capabilities.", e);
		}
	}

	// REFACTORED [GRID]: Options shared by local and remote execution.
	// Build browser-specific capabilities for local and remote sessions.
	private static MutableCapabilities createOptions(String name, boolean headless) {
		// Choose configuration based on the requested browser.
		switch (name) {
		case FrameworkConstants.CHROME:
			// Create Chrome-specific browser options.
			ChromeOptions chrome = new ChromeOptions();
			// Configure Chrome to continue once DOM is ready.
			chrome.setPageLoadStrategy(PageLoadStrategy.EAGER);
			// Apply stable Chrome startup arguments.
			chrome.addArguments("--start-maximized", "--disable-extensions", "--disable-dev-shm-usage");
			// Apply stable Chrome startup arguments.
			if (headless)
				chrome.addArguments("--headless=new", "--window-size=1920,1080");
			// Return Chrome options for local or Grid execution.
			return chrome;
		case FrameworkConstants.FIREFOX:
			// Create Firefox-specific browser options.
			FirefoxOptions firefox = new FirefoxOptions();
			// Configure Firefox page-load behavior.
			firefox.setPageLoadStrategy(PageLoadStrategy.EAGER);
			// Run Firefox headlessly when requested.
			if (headless)
				firefox.addArguments("-headless", "--width=1920", "--height=1080");
			// Return Firefox options for local or Grid execution.
			return firefox;
		case FrameworkConstants.EDGE:
			// Create Edge-specific browser options.
			EdgeOptions edge = new EdgeOptions();
			// Configure Edge page-load behavior.
			edge.setPageLoadStrategy(PageLoadStrategy.EAGER);
			// Apply stable Edge startup arguments.
			edge.addArguments("--start-maximized", "--disable-extensions", "--disable-dev-shm-usage");
			// Apply stable Edge startup arguments.
			if (headless)
				edge.addArguments("--headless=new", "--window-size=1920,1080");
			// Return Edge options for local or Grid execution.
			return edge;
		default:
			throw new IllegalArgumentException("Unsupported browser: " + name + " (supported: chrome, firefox, edge)");
		}
	}

	// Start the requested browser on the machine running the tests.
	private static WebDriver createLocal(String name, MutableCapabilities options) {
		// Choose configuration based on the requested browser.
		switch (name) {
		// Launch a local Chrome session.
		case FrameworkConstants.CHROME:
			return new ChromeDriver((ChromeOptions) options);
		// Launch a local Firefox session.
		case FrameworkConstants.FIREFOX:
			return new FirefoxDriver((FirefoxOptions) options);
		// Launch a local Edge session.
		case FrameworkConstants.EDGE:
			return new EdgeDriver((EdgeOptions) options);
		default:
			throw new IllegalArgumentException("Unsupported browser: " + name);
		}
	}
}
