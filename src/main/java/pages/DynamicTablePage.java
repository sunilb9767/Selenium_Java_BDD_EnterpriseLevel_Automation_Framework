package pages;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
 
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import base.BasePage;
 
// Page object for Dynamic Table page — https://practice.expandtesting.com/dynamic-table
// Columns and rows change position, and cell values are randomized, on every page load —
// every lookup here resolves by header/row NAME, never by fixed index
public class DynamicTablePage extends BasePage {
 
	// Logger instance for DynamicTablePage class
	private static final Logger log = LogManager.getLogger(DynamicTablePage.class);
 
	// Column header cells locator, scoped to this page's table
	private final By tableHeaders = By.cssSelector("div.table-responsive table thead th");
 
	// Data row locator, scoped to this page's table
	private final By tableRows = By.cssSelector("div.table-responsive table tbody tr");
 
	// Chrome CPU comparison label locator
	private final By chromeCpuLabel = By.id("chrome-cpu");
 
	// Regex for the numeric/percentage portion of the label text — ignores any unrelated trailing content
	private static final Pattern CHROME_CPU_PATTERN = Pattern.compile("Chrome CPU:\\s*([\\d.]+%)");
 
	// =====================================================================
	// Page reads — resolve by name, never by fixed position
	// =====================================================================
 
	// Return all column header names, in their current on-page order
	public List<String> getColumnHeaders() {
		log.info("[DynamicTablePage] Getting column headers");
		return getTexts(tableHeaders);
	}
 
	// Return all process names from the Name column, wherever that column currently sits
	public List<String> getProcessNames() {
		log.info("[DynamicTablePage] Getting process names");
		return getColumnValues("Name");
	}
 
	// Return the value in columnHeader for the row whose Name cell equals rowName
	public String getCellValue(String rowName, String columnHeader) {
		log.info("[DynamicTablePage] Getting '{}' value for row: {}", columnHeader, rowName);
 
		List<String> headers = getColumnHeaders();
		int nameColumnIndex = headers.indexOf("Name");
		int targetColumnIndex = headers.indexOf(columnHeader);
 
		if (nameColumnIndex == -1) {
			throw new NoSuchElementException("[DynamicTablePage] 'Name' column not found in headers: " + headers);
		}
		if (targetColumnIndex == -1) {
			throw new NoSuchElementException("[DynamicTablePage] '" + columnHeader + "' column not found in headers: " + headers);
		}
 
		for (WebElement row : driver.findElements(tableRows)) {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			if (cells.get(nameColumnIndex).getText().equals(rowName)) {
				return cells.get(targetColumnIndex).getText();
			}
		}
 
		throw new NoSuchElementException("[DynamicTablePage] No row found for process name: " + rowName);
	}
 
	// Return every value in the given column, in row order
	private List<String> getColumnValues(String columnHeader) {
		int columnIndex = getColumnHeaders().indexOf(columnHeader);
		List<String> values = new ArrayList<>();
 
		for (WebElement row : driver.findElements(tableRows)) {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			values.add(cells.get(columnIndex).getText());
		}
 
		return values;
	}
 
	// Return just "X%" from the "Chrome CPU: X%" label — regex-extracted so any extra trailing content (ads, extensions) is ignored
	public String getChromeCpuLabelValue() {
		log.info("[DynamicTablePage] Getting Chrome CPU label value");
 
		String fullText = getText(chromeCpuLabel);
		Matcher matcher = CHROME_CPU_PATTERN.matcher(fullText);
 
		if (matcher.find()) {
			return matcher.group(1);
		}
 
		throw new IllegalStateException("[DynamicTablePage] Could not parse Chrome CPU label text: " + fullText);
	}
}
 
