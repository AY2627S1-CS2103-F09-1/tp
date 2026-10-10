package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    private static final String WHITESPACE_REGEX = "\\s+";

    /**
     * Returns {@code s} in lower case with leading and trailing whitespace removed and repeated whitespace
     * collapsed into a single space, so that strings that only differ in these respects have the same key.
     *   <br>examples:<pre>
     *       toComparisonKey("John   TAN ") equals "john tan"
     *       </pre>
     * @param s cannot be null
     */
    public static String toComparisonKey(String s) {
        requireNonNull(s);
        return s.trim().replaceAll(WHITESPACE_REGEX, " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
