package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BrowserContext {

    private static final Logger log = LogManager.getLogger(BrowserContext.class);

    private static final ThreadLocal<String> browser = new ThreadLocal<>();

    // System property key Jenkins/CMD use to override the browser: -Dbrowser=chrome
    private static final String BROWSER_SYSTEM_PROPERTY = "browser";

    private BrowserContext() {}

    public static void set(String browserName) {
        log.info("[BrowserContext] Binding browser '{}' to thread: {}",
                browserName, Thread.currentThread().getName());
        browser.set(browserName);
    }

    public static String get() {
        return browser.get();
    }

    /**
     * Resolves the browser to use for the current thread and binds it via set().
     *
     * Single source of truth for browser-resolution precedence, shared by every
     * TestNG runner (TestRunner, RerunRunner, and any future runner) so the
     * override rule only ever lives in one place:
     *
     *   1. JVM system property -Dbrowser=... (Jenkins/CMD) — always wins if present
     *   2. Otherwise, the value passed in from testng.xml's <parameter name="browser">
     *
     * Call this from each runner's @BeforeMethod instead of duplicating the
     * System.getProperty(...) ternary in every runner class.
     *
     * @param testngParamBrowser the browser value supplied via testng.xml's <parameter>
     */
    public static void resolveAndSet(String testngParamBrowser) {
        String resolvedBrowser = System.getProperty(BROWSER_SYSTEM_PROPERTY) != null
                ? System.getProperty(BROWSER_SYSTEM_PROPERTY)
                : testngParamBrowser;

        set(resolvedBrowser);
    }

    // Call from DriverManager.quitDriver() / an @AfterClass alongside WaitUtils.removeWaits()
    // to avoid leaking values into pooled threads on suites that reuse threads across classes.
    public static void clear() {
        browser.remove();
    }
}