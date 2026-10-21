package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Optional;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 * Names are compared ignoring case and extra whitespace, but {@link #fullName} keeps the text as given,
 * except that accented letters are stored in their normalized form (see
 * {@link StringUtil#toNormalizedForm(String)}).
 */
public class Name {

    /** The maximum number of characters in a name. */
    public static final int MAX_LENGTH = 80;

    /** Message for a name that is empty or blank. */
    public static final String MESSAGE_EMPTY = "Name cannot be empty";
    /** Message for a name that has more than {@link #MAX_LENGTH} characters. */
    public static final String MESSAGE_TOO_LONG = "Name is too long";
    /** Message for a name that contains a character that is not allowed. */
    public static final String MESSAGE_CONSTRAINTS = "Name can only contain English letters (accented letters "
            + "are allowed), digits, spaces and the symbols on a standard keyboard, except commas";

    /*
     * A character is allowed if it is a Latin letter, optionally followed by accents, a printable ASCII
     * character other than a comma, or a typographic apostrophe, which is what pasted text often contains.
     * This excludes control characters and letters of other scripts.
     * The alternatives do not overlap and the quantifiers are possessive, so a long invalid name is
     * rejected without backtracking.
     */
    public static final String VALIDATION_REGEX =
            "(?:\\p{IsLatin}\\p{M}*+|[\\x20-\\x7E&&[^,\\p{IsLatin}]]|[\\u2018\\u2019])++";

    public final String fullName;

    private final String comparisonKey;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        String normalizedName = StringUtil.toNormalizedForm(name);
        checkArgument(isValidName(normalizedName), MESSAGE_CONSTRAINTS);
        fullName = normalizedName;
        comparisonKey = StringUtil.toComparisonKey(normalizedName);
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
        String normalizedTest = StringUtil.toNormalizedForm(test);
        if (normalizedTest.isBlank()) {
            return Optional.of(MESSAGE_EMPTY);
        }
        if (normalizedTest.length() > MAX_LENGTH) {
            return Optional.of(MESSAGE_TOO_LONG);
        }
        if (!normalizedTest.matches(VALIDATION_REGEX)) {
            return Optional.of(MESSAGE_CONSTRAINTS);
        }
        return Optional.empty();
    }

    /**
     * Returns true if {@code test} is the same name as this name, ignoring case and extra whitespace.
     * Unlike the constructor, {@code test} is not required to be a valid name.
     */
    public boolean matches(String test) {
        requireNonNull(test);
        return comparisonKey.equals(StringUtil.toComparisonKey(test));
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
