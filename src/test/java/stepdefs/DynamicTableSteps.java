package stepdefs;
 
import config.ConfigReader;
import constants.PageUrlConstants;
import pages.DynamicTablePage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
 
import java.util.List;
 
// Step definitions for Dynamic Table feature file.
public class DynamicTableSteps {
 
    // Logger instance for DynamicTableSteps class
    private static final Logger log = LogManager.getLogger(DynamicTableSteps.class);
 
    // Page object — initialized fresh per scenario via Hooks driver setup
    private final DynamicTablePage dynamicTablePage = new DynamicTablePage();
 
    // =====================================================================
    // Given steps
    // =====================================================================
 
    @Given("the user is on the dynamic table page")
    public void theUserIsOnTheDynamicTablePage() {
        log.info("[DynamicTableSteps] Navigating to dynamic table page");
        dynamicTablePage.navigateTo(ConfigReader.getBaseUrl() + PageUrlConstants.DYNAMIC_TABLE_PAGE);
    }
 
    // =====================================================================
    // When steps
    // =====================================================================
 
    @When("the user reloads the page")
    public void theUserReloadsThePage() {
        log.info("[DynamicTableSteps] Reloading page");
        dynamicTablePage.reloadPage();
    }
 
    // =====================================================================
    // Then steps — one assertion per step, meaningful failure messages
    // =====================================================================
 
    // Reusable across the initial-load and post-reload scenarios
    @Then("the Chrome CPU value in the table should match the Chrome CPU label")
    public void theChromeCpuValueInTheTableShouldMatchTheLabel() {
        log.info("[DynamicTableSteps] Verifying Chrome CPU value matches the label");
 
        String tableValue = dynamicTablePage.getCellValue("Chrome", "CPU");
        String labelValue = dynamicTablePage.getChromeCpuLabelValue();
 
        Assert.assertTrue(
                tableValue.equals(labelValue),
                "Expected table CPU value to equal label value — table: " + tableValue + ", label: " + labelValue
        );
    }
 
    @Then("the table should contain the following columns:")
    public void theTableShouldContainTheFollowingColumns(DataTable dataTable) {
        log.info("[DynamicTableSteps] Verifying table columns");
 
        List<String> expectedColumns = dataTable.asList();
        List<String> actualColumns = dynamicTablePage.getColumnHeaders();
 
        Assert.assertTrue(
                actualColumns.containsAll(expectedColumns) && expectedColumns.containsAll(actualColumns),
                "Expected columns " + expectedColumns + " but found: " + actualColumns
        );
    }
 
    @Then("the table should contain the following processes:")
    public void theTableShouldContainTheFollowingProcesses(DataTable dataTable) {
        log.info("[DynamicTableSteps] Verifying table processes");
 
        List<String> expectedProcesses = dataTable.asList();
        List<String> actualProcesses = dynamicTablePage.getProcessNames();
 
        Assert.assertTrue(
                actualProcesses.containsAll(expectedProcesses) && expectedProcesses.containsAll(actualProcesses),
                "Expected processes " + expectedProcesses + " but found: " + actualProcesses
        );
    }
}
 
