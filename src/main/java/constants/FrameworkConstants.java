package constants;

import java.io.File;

//Holds all fixed constant values used across the framework.
public class FrameworkConstants {

 // Private constructor prevents instantiation of constants class
 private FrameworkConstants() {}

 // Supported browser names
 public static final String CHROME  = "chrome";
 public static final String FIREFOX = "firefox";
 public static final String EDGE    = "edge";

 // OS-independent path to config.properties — works on Windows, Mac, and Linux
 public static final String CONFIG_FILE_PATH = "src" + File.separator
         + "main" + File.separator
         + "resources" + File.separator
         + "config" + File.separator
         + "config.properties";

 // OS-independent base path to environment-specific properties files
 public static final String ENV_FILE_PATH = "src" + File.separator
         + "main" + File.separator
         + "resources" + File.separator
         + "environments" + File.separator;

 // Output path for the Extent HTML report — fixed across all environments
 public static final String SPARK_REPORT_PATH = "target" + File.separator
         + "extent-reports" + File.separator
         + "SparkReport" + File.separator
         + "Spark.html";

 // Output folder for failure screenshots — fixed across all environments
 public static final String PDF_REPORT_PATH = "target" + File.separator
         + "extent-reports" + File.separator
         + "PdfReport" + File.separator
         + "ExtentReport.pdf";
 
//← UPDATED — Screenshots folder under target/
 public static final String SCREENSHOTS_PATH = "target" + File.separator
         + "extent-reports" + File.separator
         + "screenshots" + File.separator;


 // Log messages prefix for easy console filtering
 public static final String LOG_PREFIX = "[Framework] ";
}