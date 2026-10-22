package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Optional;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    /**
     * The maximum number of characters in an email. The length is checked before the format because matching
     * {@link #VALIDATION_REGEX} against a long input takes time that grows quickly with the length.
     */
    public static final int MAX_LENGTH = 254;

    /** Message for an email that has more than {@link #MAX_LENGTH} characters. */
    public static final String MESSAGE_TOO_LONG = "Email is too long";

    private static final String SPECIAL_CHARACTERS = "+_.-";
    public static final String MESSAGE_CONSTRAINTS = "Emails should be of the format local-part@domain "
            + "and adhere to the following constraints:\n"
            + "1. The local-part should only contain alphanumeric characters and these special characters, excluding "
            + "the parentheses, (" + SPECIAL_CHARACTERS + "). The local-part may not start or end with any special "
            + "characters.\n"
            + "2. The local-part is followed by an '@' and then a domain name. The domain name is made up of domain "
            + "labels separated by periods.\n"
            + "The domain name must:\n"
            + "    - end with a domain label at least 2 characters long\n"
            + "    - have each domain label start and end with alphanumeric characters\n"
            + "    - have each domain label consist of alphanumeric characters, separated only by hyphens, if any.";
    // alphanumeric and special characters
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+"; // alphanumeric characters except underscore
    private static final String LOCAL_PART_REGEX = "^" + ALPHANUMERIC_NO_UNDERSCORE + "([" + SPECIAL_CHARACTERS + "]"
            + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_LAST_PART_REGEX = "(" + DOMAIN_PART_REGEX + "){2,}$"; // At least two chars
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     * @throws IllegalArgumentException with the message of the constraint that {@code email} violates, such as
     *         {@code MESSAGE_TOO_LONG}, if {@code email} is not valid.
     */
    public Email(String email) {
        requireNonNull(email);
        Optional<String> constraintViolation = getConstraintViolation(email);
        checkArgument(constraintViolation.isEmpty(), constraintViolation.orElse(MESSAGE_CONSTRAINTS));
        value = email;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        return getConstraintViolation(test).isEmpty();
    }

    /**
     * Returns the error message for the first constraint that {@code test} violates as an email, or an empty
     * {@code Optional} if {@code test} is a valid email.
     */
    public static Optional<String> getConstraintViolation(String test) {
        requireNonNull(test);
        if (test.length() > MAX_LENGTH) {
            return Optional.of(MESSAGE_TOO_LONG);
        }
        if (!test.matches(VALIDATION_REGEX)) {
            return Optional.of(MESSAGE_CONSTRAINTS);
        }
        return Optional.empty();
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
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
