package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import driver.BrowserContext;

// Entry point for Cucumber test execution via TestNG
@CucumberOptions(
        // Path to all feature files
        features = "src/test/resources/features",

        // Packages Cucumber scans for step definitions and hooks
        glue = {
                "hooks",       // Hooks.java — @Before @After lifecycle
                "stepdefs"     // all step definition classes
        },

        // Run only scenarios tagged with @regression by default
        tags = "@regression",

        plugin = {
                // Pretty console output — readable step-by-step logs
                "pretty",

                // Cucumber HTML report
                "html:target/cucumber-reports/cucumber.html",

                // JSON report — used by Jenkins Cucumber plugin
                "json:target/cucumber-reports/cucumber.json",

                // Rerun failed scenarios — written to file for rerun runner
                "rerun:target/rerun.txt",
                
             // Adapter generates both HTML and PDF via extent.properties
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },

        // Remove ANSI color codes from console output — cleaner in Jenkins logs
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

	@Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void captureBrowserParam(@Optional("chrome") String browser) {
        BrowserContext.set(browser);
    }
	
	
    // Override DataProvider to enable parallel scenario execution
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
