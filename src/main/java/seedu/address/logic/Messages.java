package seedu.address.logic;

import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX =
            "The contact index provided is invalid.";
    public static final String MESSAGE_NO_PERSON_DISPLAYED = " No contacts are displayed.";
    public static final String MESSAGE_DISPLAYED_INDEX_RANGE =
            " The displayed list has %1$s contact(s), so the index must be from 1 to %1$s.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$s contact(s) listed!";
    public static final String MESSAGE_DUPLICATE_PARAMETERS =
            "Each parameter can only be specified once. Repeated: %1$s";
    public static final String MESSAGE_MISSING_NAME = "Command requires a name";
    public static final String MESSAGE_MISSING_CLASS = "Command requires a class";
    public static final String MESSAGE_MISSING_DATE = "Missing date. Please provide the date of attendance.";
    public static final String MESSAGE_MISSING_ASSESSMENT = "Assessment name missing";
    public static final String MESSAGE_CONTACT_NOT_FOUND = "Contact not found";

    /**
     * Returns the message for an index that is larger than the number of contacts displayed, which says how
     * many contacts are displayed so that the user knows which index to use.
     */
    public static String getInvalidDisplayedIndexMessage(int displayedCount) {
        if (displayedCount == 0) {
            return MESSAGE_INVALID_PERSON_DISPLAYED_INDEX + MESSAGE_NO_PERSON_DISPLAYED;
        }
        return MESSAGE_INVALID_PERSON_DISPLAYED_INDEX + String.format(MESSAGE_DISPLAYED_INDEX_RANGE, displayedCount);
    }

    /**
     * Returns the message for parameters that were given more than once, which names them.
     *
     * @param repeatedPrefixes the prefixes that were repeated, such as {@code /date}
     */
    public static String getErrorMessageForDuplicatePrefixes(String... repeatedPrefixes) {
        return String.format(MESSAGE_DUPLICATE_PARAMETERS, String.join(", ", repeatedPrefixes));
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Class: ")
                .append(person.getClassName());
        person.getEmail().ifPresent(email -> builder.append("; Email: ").append(email));
        builder.append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
