package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 * Tags are compared ignoring case, but {@link #tagName} keeps the casing that was given.
 * Leading and trailing whitespace is removed and repeated spaces are collapsed into a single space.
 */
public class Tag {

    public static final int MAX_LENGTH = 80;

    public static final String MESSAGE_CONSTRAINTS = "Tag names must be 1 to " + MAX_LENGTH
            + " characters long and can only contain letters, digits, spaces, hyphens, and underscores";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9 _\\-]{1," + MAX_LENGTH + "}";

    private static final String REPEATED_SPACES_REGEX = " {2,}";

    public final String tagName;

    private final String comparisonKey;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = normalize(tagName);
        comparisonKey = StringUtil.toComparisonKey(this.tagName);
    }

    /**
     * Returns true if a given string is a valid tag name.
     * Leading and trailing whitespace and repeated spaces are ignored when checking.
     */
    public static boolean isValidTagName(String test) {
        requireNonNull(test);
        return normalize(test).matches(VALIDATION_REGEX);
    }

    private static String normalize(String tagName) {
        return tagName.trim().replaceAll(REPEATED_SPACES_REGEX, " ");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return comparisonKey.equals(otherTag.comparisonKey);
    }

    @Override
    public int hashCode() {
        return comparisonKey.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
