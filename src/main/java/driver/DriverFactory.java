package driver;

import config.ConfigReader;
import constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
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

     switch (browserName) {

         case FrameworkConstants.CHROME:
             // Auto-download and configure the correct ChromeDriver binary
            

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

         case FrameworkConstants.FIREFOX:
             // Auto-download and configure the correct GeckoDriver binary
             

             FirefoxOptions firefoxOptions = new FirefoxOptions();
             
             firefoxOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

             // Enable headless mode for CI runs
             if (isHeadless) {
                 firefoxOptions.addArguments("--headless");
                 log.debug("[DriverFactory] Firefox running in headless mode");
             }

             return new FirefoxDriver(firefoxOptions);

         case FrameworkConstants.EDGE:
             // Auto-download and configure the correct EdgeDriver binary
             

             EdgeOptions edgeOptions = new EdgeOptions();
             
             edgeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);    // ← ADD THIS
             

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

         default:
             // Fail fast with a clear message instead of returning null
             log.error("[DriverFactory] Unsupported browser: {}", browser);
             throw new IllegalArgumentException(
                 "[DriverFactory] Unsupported browser: '" + browser + "'. " +
                 "Valid values: chrome, firefox, edge"
             );
     }
 }
}