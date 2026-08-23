package utils;

import java.util.UUID;

// Generates unique, non-persistent test data at runtime — avoids hardcoded
// values that collide across repeated test runs or parallel threads
// (e.g., a Register flow that persists usernames server-side would fail
// on the second run, or on a second parallel thread, if the username
// were a fixed literal).
public final class TestDataGenerator {

	// Private constructor prevents instantiation — this is a static utility class
	private TestDataGenerator() {}

	/**
	 * Builds a unique username by combining a readable prefix with a
	 * random UUID fragment.
	 *
	 * UUID is used instead of a timestamp deliberately: this framework runs
	 * scenarios in parallel threads (see TestRunner's
	 * {@code @DataProvider(parallel = true)}), and two threads could
	 * generate a timestamp in the same millisecond. A UUID fragment makes
	 * collisions effectively impossible even under parallel execution,
	 * while staying short enough to remain readable in logs and reports.
	 *
	 * @param prefix readable prefix, e.g. "qa_user"
	 * @return a unique username, e.g. "qa_user_8f3a1c2d"
	 */
	public static String uniqueUsername(String prefix) {
		String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
		return prefix + "-" + uniqueSuffix;
	}
}
