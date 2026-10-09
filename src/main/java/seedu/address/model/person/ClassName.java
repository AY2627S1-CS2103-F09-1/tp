package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Optional;

import seedu.address.commons.util.StringUtil;

/**
 * Represents the name of the class a Person belongs to in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidClassName(String)}
 * Class names are compared ignoring case and extra whitespace, but {@link #value} keeps the text as given.
 */
public class ClassName {

    /** The maximum number of characters in a class name. */
    public static final int MAX_LENGTH = 80;

    /** Message for a class name that is empty or blank. */
    public static final String MESSAGE_EMPTY = "Class name cannot be empty";
    /** Message for a class name that has more than {@link #MAX_LENGTH} characters. */
    public static final String MESSAGE_TOO_LONG = "Class name is too long";
    /** Message for a class name that contains a character, or starts with a character, that is not allowed. */
    public static final String MESSAGE_CONSTRAINTS = "Class name must start with a letter or digit and can only "
            + "contain letters, digits, spaces, and the characters - . _";

    /*
     * The first character of the class name must be a letter or digit, otherwise " " (a blank string)
     * becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[A-Za-z0-9][A-Za-z0-9 ._\\-]*";

    public final String value;

    private final String comparisonKey;

    /**
     * Constructs a {@code ClassName}.
     *
     * @param className A valid class name.
     */
    public ClassName(String className) {
        requireNonNull(className);
        checkArgument(isValidClassName(className), MESSAGE_CONSTRAINTS);
        value = className;
        comparisonKey = StringUtil.toComparisonKey(className);
    }

    /**
     * Returns true if a given string is a valid class name.
     */
    public static boolean isValidClassName(String test) {
        return getConstraintViolation(test).isEmpty();
    }

    /**
     * Returns the error message for the first constraint that {@code test} violates as a class name, or an
     * empty {@code Optional} if {@code test} is a valid class name.
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
     * Returns true if {@code test} is the same class name as this class name, ignoring case and extra
     * whitespace. Unlike the constructor, {@code test} is not required to be a valid class name.
     */
    public boolean matches(String test) {
        requireNonNull(test);
        return comparisonKey.equals(StringUtil.toComparisonKey(test));
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClassName otherClassName)) {
            return false;
        }

        return comparisonKey.equals(otherClassName.comparisonKey);
    }

    @Override
    public int hashCode() {
        return comparisonKey.hashCode();
    }

}
