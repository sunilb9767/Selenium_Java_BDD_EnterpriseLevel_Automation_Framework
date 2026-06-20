package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "@target/rerun.txt",
        glue = {"hooks", "stepdefs"},
        plugin = {
            "pretty",
            "html:target/cucumber-reports/rerun.html",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)

public class RerunRunner extends AbstractTestNGCucumberTests{

}
