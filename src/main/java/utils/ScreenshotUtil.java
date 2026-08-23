package utils;

import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;


// Utility class responsible for capturing and saving screenshots on test failure.
public class ScreenshotUtil {

    // Logger instance for ScreenshotUtil class
    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);

    // Private constructor prevents instantiation — this is a static utility class
    private ScreenshotUtil() {}

    /**
     * Captures the current browser state as a raw PNG byte array.
     *
     * Contract: ONE WebDriver call, returns bytes, zero side effects.
     * No file is written. No directory is created. No adapter API is called.
     * The caller (Hooks) passes these bytes directly to scenario.attach(),
     * which triggers the adapter's EmbedEventHandler to handle all disk I/O
     * and report linking in a single atomic operation.
     *
     * @param scenarioName used only for log context — not used in any filename
     * @return raw PNG bytes, or empty array if capture fails (never null)
     */
    public static byte[] captureAsBytes(String scenarioName) {
        try {
            byte[] bytes = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            log.debug("[ScreenshotUtil] Screenshot captured for: '{}'", scenarioName);
            return bytes;
        } catch (Exception e) {
            log.error("[ScreenshotUtil] Capture failed for '{}': {}", scenarioName, e.getMessage());
            return new byte[0];
        }
    }
 
    /**
     * Builds a report-friendly screenshot label from the scenario name.
     *
     * This string becomes the VISIBLE IMAGE LABEL in both Spark HTML and PDF
     * reports — it is what appears as the screenshot caption next to the image.
     * It is NOT used as the filename on disk (the adapter controls that).
     *
     * Centralised here so any future reporter or listener can reuse this
     * label-building logic without duplicating the sanitise logic.
     *
     * @param scenarioName raw Cucumber scenario name
     * @return sanitised label safe for report display
     */
    public static String buildScreenshotLabel(String scenarioName) {
        // Keep the label human-readable — replace only characters that break
        // HTML rendering or file system paths if the adapter ever uses this string
        return scenarioName.replaceAll("[^a-zA-Z0-9 _-]", "_");
    }
}