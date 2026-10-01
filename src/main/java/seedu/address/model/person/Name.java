package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;
import java.util.Optional;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 * Names are compared ignoring case and extra whitespace, but {@link #fullName} keeps the text as given.
 */
public class Name {

    public static final int MAX_LENGTH = 80;

    public static final String MESSAGE_EMPTY = "Name cannot be empty";
    public static final String MESSAGE_TOO_LONG = "Name is too long";
    /** Message for a name that contains a character, or starts with a character, that is not allowed. */
    public static final String MESSAGE_CONSTRAINTS = "Name must start with a letter and can only contain letters, "
            + "spaces, and the characters ' - . / (a / must be between two letters)";

    /*
     * The first character of the name must be a letter, otherwise " " (a blank string) becomes a valid input.
     * A slash is only valid between two letters, as in "s/o".
     */
    public static final String VALIDATION_REGEX = "[A-Za-z](?:[A-Za-z '.\\-]|(?<=[A-Za-z])/(?=[A-Za-z]))*";

    private static final String WHITESPACE_REGEX = "\\s+";

    public final String fullName;

    private final String comparisonKey;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
        comparisonKey = toComparisonKey(name);
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return getConstraintViolation(test).isEmpty();
    }

    /**
     * Returns the error message for the first constraint that {@code test} violates as a name, or an empty
     * {@code Optional} if {@code test} is a valid name.
     */
    public static Optional<String> getConstraintViolation(String test) {
        requireNonNull(test);
        if (test.isBlank()) {
            return Optional.of(MESSAGE_EMPTY);
        }
        if (test.length() > MAX_LENGTH) {
            return Optional.of(MESSAGE_TOO_LONG);
        }
        if (!test.matches(VALIDATION_REGEX)) {
            return Optional.of(MESSAGE_CONSTRAINTS);
        }
        return Optional.empty();
    }

    /**
     * Returns {@code name} in lower case with leading and trailing whitespace removed and repeated whitespace
     * collapsed, so that names that only differ in these respects have the same key.
     */
    private static String toComparisonKey(String name) {
        return name.trim().replaceAll(WHITESPACE_REGEX, " ").toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return comparisonKey.equals(otherName.comparisonKey);
    }

    @Override
    public int hashCode() {
        return comparisonKey.hashCode();
    }

}
