package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    private static final String WHITESPACE_REGEX = "\\s+";

    /**
     * Returns {@code s} in Unicode normalization form C, so that an accented letter typed as one character
     * and the same letter typed as a base letter followed by an accent are the same string.
     *
     * @param s cannot be null
     */
    public static String toNormalizedForm(String s) {
        requireNonNull(s);
        return Normalizer.normalize(s, Normalizer.Form.NFC);
    }

    /**
     * Returns {@code s} with the English letters {@code A-Z} converted to lower case and everything else
     * unchanged. Unlike {@link String#toLowerCase()}, it never changes the length of the text and never
     * makes a letter of another language equal to an English letter, so it is used to compare syntax such as
     * command words and prefixes.
     *
     * @param s cannot be null
     */
    public static String toAsciiLowerCase(String s) {
        requireNonNull(s);
        StringBuilder lowerCaseText = new StringBuilder(s.length());
        for (char character : s.toCharArray()) {
            boolean isUpperCaseEnglishLetter = character >= 'A' && character <= 'Z';
            lowerCaseText.append(isUpperCaseEnglishLetter ? (char) (character - 'A' + 'a') : character);
        }
        return lowerCaseText.toString();
    }

    /**
     * Returns {@code s} in lower case with leading and trailing whitespace removed and repeated whitespace
     * collapsed into a single space, so that strings that only differ in these respects have the same key.
     * Accented letters are compared in their normalized form, and so are letters that only look like
     * another letter, such as the full-width {@code Ａ}, which has the same key as {@code A}. This is Unicode
     * normalization form KC.
     *   <br>examples:<pre>
     *       toComparisonKey("John   TAN ") equals "john tan"
     *       toComparisonKey("Ｊohn") equals "john"
     *       </pre>
     * @param s cannot be null
     */
    public static String toComparisonKey(String s) {
        requireNonNull(s);
        return Normalizer.normalize(s, Normalizer.Form.NFKC).trim().replaceAll(WHITESPACE_REGEX, " ")
                .toLowerCase(Locale.ROOT);
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
