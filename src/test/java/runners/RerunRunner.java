package runners;

import driver.BrowserContext;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(features = "@target/rerun.txt", 
				glue = { "hooks", "stepdefs" }, 
				plugin = { "pretty",
						"html:target/cucumber-reports/rerun.html", 
						"json:target/cucumber-reports/rerun.json",
						"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:" }, 
				monochrome = true)

public class RerunRunner extends AbstractTestNGCucumberTests {

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	public void setBrowser(@Optional("chrome") String browser) {

		// Resolution precedence (-Dbrowser system property wins over testng.xml
				// <parameter>) now lives in one place — BrowserContext.resolveAndSet() —
				// shared with TestRunner instead of being duplicated in every runner.
				BrowserContext.resolveAndSet(browser);
	}

	// Sequential — reruns must be stable and debuggable, not fast
	@Override
	@DataProvider(parallel = false)
	public Object[][] scenarios() {
		return super.scenarios();
	}
}
