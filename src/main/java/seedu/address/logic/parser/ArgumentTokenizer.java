package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix> value <prefix> value ...}<br>
 *     e.g. {@code some preamble text /t 11.00 /t 12.00 /k /m July}  where prefixes are {@code /t /k /m}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code /k} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code /t}
 *    in the above example.<br>
 * 4. A prefix is only recognized if it is preceded by a whitespace and followed by a whitespace or the end of
 *    the arguments string e.g. {@code /classroom} is not recognized as the prefix {@code /class}.<br>
 */
public class ArgumentTokenizer {

    /** Matches a whitespace-delimited token that starts with a slash, which is how all prefixes begin. */
    private static final Pattern PREFIX_LIKE_TOKEN = Pattern.compile("(?<=\\s)/\\S*");

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix> value <prefix> value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Returns the tokens in the arguments string that look like prefixes, i.e. start with a slash and are
     * preceded by a whitespace, but are not one of the {@code knownPrefixes}. The tokens are returned in the
     * order they appear, including repeated occurrences. Matching is case-sensitive.
     *
     * E.g. if {@code argsString} = " /name John /phone 123 /names" and {@code knownPrefixes} = {"/name"},
     * this method returns ["/phone", "/names"].
     *
     * @param argsString    Arguments string of the form: {@code preamble <prefix> value <prefix> value ...}
     * @param knownPrefixes Prefixes that are recognized and hence are not reported
     * @return              List of unrecognized prefix-like tokens, empty if there are none
     */
    public static List<String> findUnrecognizedPrefixes(String argsString, Prefix... knownPrefixes) {
        Set<String> knownPrefixStrings = Arrays.stream(knownPrefixes)
                .map(Prefix::getPrefix)
                .collect(Collectors.toSet());

        List<String> unrecognizedPrefixes = new ArrayList<>();
        Matcher matcher = PREFIX_LIKE_TOKEN.matcher(argsString);
        while (matcher.find()) {
            String token = matcher.group();
            if (!knownPrefixStrings.contains(token)) {
                unrecognizedPrefixes.add(token);
            }
        }
        return unrecognizedPrefixes;
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix> value <prefix> value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes) {
        return Arrays.stream(prefixes)
                .flatMap(prefix -> findPrefixPositions(argsString, prefix).stream())
                .collect(Collectors.toList());
    }

    /**
     * @see #findAllPrefixPositions(String, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            positions.add(extendedPrefix);
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix} and a whitespace
     * or the end of {@code argsString} after it. Returns -1 if no such
     * occurrence can be found.
     *
     * E.g if {@code argsString} = "e/hip/ 900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. Similarly, if
     * {@code argsString} = "e/hi /names", {@code prefix} = "/name" and
     * {@code fromIndex} = 0, this method returns -1 as "/name" is not followed
     * by whitespace. However, if {@code argsString} = "e/hi p/ 900",
     * {@code prefix} = "p/" and {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        int prefixIndex = argsString.indexOf(" " + prefix, fromIndex);
        while (prefixIndex != -1) {
            int prefixPosition = prefixIndex + 1; // +1 as offset for whitespace
            if (isEndOfToken(argsString, prefixPosition + prefix.length())) {
                return prefixPosition;
            }
            prefixIndex = argsString.indexOf(" " + prefix, prefixPosition);
        }
        return -1;
    }

    /**
     * Returns true if {@code index} is the end of {@code argsString} or points to a whitespace.
     */
    private static boolean isEndOfToken(String argsString, int index) {
        return index == argsString.length() || Character.isWhitespace(argsString.charAt(index));
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix> value <prefix> value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions) {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     */
    private static String extractArgumentValue(String argsString,
                                        PrefixPosition currentPrefixPosition,
                                        PrefixPosition nextPrefixPosition) {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition());

        return value.trim();
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
