package utils;

import constants.FrameworkConstants;
import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Utility class responsible for capturing and saving screenshots on test failure.
public class ScreenshotUtil {

    // Logger instance for ScreenshotUtil class
    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);

    // Private constructor prevents instantiation — this is a static utility class
    private ScreenshotUtil() {}

    // Date-time format used in screenshot filename — ensures unique name per capture
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Captures ONE screenshot from the browser, saves it to disk,
     * and returns both the file path and raw bytes from the SAME capture.
     * This replaces the old captureScreenshot() + captureScreenshotAsBytes()
     * which were taking TWO separate screenshots for one failure.
     * @param scenarioName name of the failed scenario — used in filename
     * @return ScreenshotResult containing both the saved path and the bytes
     */
    public static ScreenshotResult captureScreenshot(String scenarioName) {

        // Generate timestamp to ensure every screenshot has a unique filename
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // Sanitize scenario name — remove special characters invalid in filenames
        String sanitizedName = scenarioName.replaceAll("[^a-zA-Z0-9_-]", "_");

        // Build full screenshot filename with scenario name and timestamp
        String fileName = sanitizedName + "_" + timestamp + ".png";

        // Build full destination path using screenshots folder from FrameworkConstants
        String destinationPath = FrameworkConstants.SCREENSHOTS_PATH + fileName;

        try {
            // Ensure screenshots directory exists — create it if missing
            File screenshotsDir = new File(FrameworkConstants.SCREENSHOTS_PATH);
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
                log.debug("[ScreenshotUtil] Screenshots directory created: {}",
                        FrameworkConstants.SCREENSHOTS_PATH);
            }

            // Capture the browser screenshot ONCE as bytes — single capture only
            byte[] screenshotBytes = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            // Write the SAME bytes to disk — no second browser screenshot call
            File destinationFile = new File(destinationPath);
            FileUtils.writeByteArrayToFile(destinationFile, screenshotBytes);

            log.info("[ScreenshotUtil] Screenshot saved: {}", destinationPath);

            // Return both path and bytes from the single capture
            return new ScreenshotResult(destinationFile.getAbsolutePath(), screenshotBytes);

        } catch (IOException e) {
            // Log error but do not throw — screenshot failure should never break test reporting
            log.error("[ScreenshotUtil] Failed to capture screenshot for '{}': {}",
                    scenarioName, e.getMessage());
            return new ScreenshotResult("", new byte[0]);
        }
    }

    /**
     * Simple holder class for screenshot path and bytes captured together.
     * Ensures only one browser screenshot call happens per failure.
     */
    public static class ScreenshotResult {

        // Absolute file path of the saved screenshot — used for any future disk-path needs
        public final String path;

        // Raw screenshot bytes — used to attach directly to Cucumber's scenario report
        public final byte[] bytes;

        public ScreenshotResult(String path, byte[] bytes) {
            this.path = path;
            this.bytes = bytes;
        }
    }
}