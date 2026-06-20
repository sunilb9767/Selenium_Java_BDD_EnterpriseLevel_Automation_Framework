package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BrowserContext {

    private static final Logger log = LogManager.getLogger(BrowserContext.class);

    private static final ThreadLocal<String> browser = new ThreadLocal<>();

    private BrowserContext() {}

    public static void set(String browserName) {
        log.info("[BrowserContext] Binding browser '{}' to thread: {}",
                browserName, Thread.currentThread().getName());
        browser.set(browserName);
    }

    public static String get() {
        return browser.get();
    }

    // Call from DriverManager.quitDriver() / an @AfterClass alongside WaitUtils.removeWaits()
    // to avoid leaking values into pooled threads on suites that reuse threads across classes.
    public static void clear() {
        browser.remove();
    }
}