package config;

import constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// Reads and serves configuration values from config.properties and environment files.
public class ConfigReader {

	// Logger instance for ConfigReader class
	private static final Logger log = LogManager.getLogger(ConfigReader.class);

	// Holds all key-value pairs loaded from config.properties and env-specific file
	private static Properties properties;

	static {
		// Load all properties when the class is first accessed
		loadProperties();
	}

	// Loads base config first then overlays environment-specific properties on top
	private static void loadProperties() {

		properties = new Properties();

		// Step 1 — load base config.properties first
		try (FileInputStream baseConfig = new FileInputStream(FrameworkConstants.CONFIG_FILE_PATH)) {

			// Read all base key-value pairs into properties object
			properties.load(baseConfig);
			log.info("[ConfigReader] Base config.properties loaded successfully");

		} catch (IOException e) {
			// Stop execution if base config file is missing or unreadable
			log.error("[ConfigReader] Failed to load config.properties: {}", e.getMessage());
			throw new RuntimeException("[ConfigReader] Failed to load config.properties: " + e.getMessage());
		}

		// Step 2 — read env value from system property first, then from base config
		// System property takes priority — allows Jenkins to pass -Denv=staging
		String env = System.getProperty("env") != null ? System.getProperty("env") : properties.getProperty("env");

		log.info("[ConfigReader] Target environment: {}", env);

		// Step 3 — build path to environment-specific file using env value
		String envFilePath = FrameworkConstants.ENV_FILE_PATH + env + ".properties";

		// Step 4 — load environment file and overlay on top of base config
		try (FileInputStream envConfig = new FileInputStream(envFilePath)) {

			// Environment file values override matching keys from config.properties
			properties.load(envConfig);
			log.info("[ConfigReader] Environment file loaded: {}", envFilePath);

		} catch (IOException e) {
			// Stop execution if environment file is missing or unreadable
			log.error("[ConfigReader] Failed to load environment file: {}", envFilePath);
			throw new RuntimeException("[ConfigReader] Failed to load environment file: " + envFilePath);
		}
	}

	/**
	 * Returns value for a given key. Jenkins system properties always take priority
	 * over properties files.
	 * 
	 * @param key property key to look up
	 * @return value from system property if set, otherwise from loaded properties
	 */
	public static String get(String key) {

		// Check if key was passed as system property e.g. -Dbrowser=chrome via Jenkins
		String sysProp = System.getProperty(key);

		// Use system property if available, otherwise read from loaded properties
		String value = (sysProp != null && !sysProp.isEmpty()) ? sysProp : properties.getProperty(key);

		// Fail immediately with clear message if required key is missing
		if (value == null || value.isEmpty()) {
			log.error("[ConfigReader] Missing required property: '{}'", key);
			throw new RuntimeException("[ConfigReader] Missing required property: '" + key + "'");
		}

		return value;
	}

	// Returns the browser name to be used for test execution
	public static String getBrowser() {
		return get("browser");
	}

	// Returns true if tests should run in headless mode (CI), false for local runs
	public static boolean isHeadless() {
		return Boolean.parseBoolean(get("headless"));
	}

	// Returns the application base URL for the target environment
	public static String getBaseUrl() {
		return get("baseUrl");
	}

	// Returns explicit wait timeout in seconds used by WebDriverWait
	public static int getExplicitWait() {
		return Integer.parseInt(get("explicitWait"));
	}

	// Returns fluent wait max timeout in seconds for dynamic/unstable elements
	public static int getFluentWaitTimeout() {
		return Integer.parseInt(get("fluentWaitTimeout"));
	}

	// Returns polling interval in seconds for fluent wait retry attempts
	public static int getFluentWaitPolling() {
		return Integer.parseInt(get("fluentWaitPolling"));
	}

	// Returns page load timeout in seconds for full page loads
	public static int getPageLoadTimeout() {
		return Integer.parseInt(get("pageLoadTimeout"));
	}

	// Returns the target environment name: qa, staging, prod
	public static String getEnv() {
		return get("env");
	}

	// Returns valid username for positive test scenarios
	public static String getValidUsername() {
		return get("validUsername");
	}

	// Returns valid password for positive test scenarios
	public static String getValidPassword() {
		return get("validPassword");
	}

	// Returns invalid username for negative test scenarios
	public static String getInvalidUsername() {
		return get("invalidUsername");
	}

	// Returns invalid password for negative test scenarios
	public static String getInvalidPassword() {
		return get("invalidPassword");
	}
}