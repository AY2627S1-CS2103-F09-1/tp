package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Person;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + " " + person.getName().fullName + " ");
        sb.append(PREFIX_CLASS + " " + person.getClassName().value + " ");
        person.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL + " " + email.value + " "));
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name ->
                sb.append(PREFIX_NAME).append(" ").append(name.fullName).append(" "));
        descriptor.getClassName().ifPresent(className ->
                sb.append(PREFIX_CLASS).append(" ").append(className.value).append(" "));
        if (descriptor.isEmailCleared()) {
            sb.append(PREFIX_EMAIL).append(" ");
        } else {
            descriptor.getEmail().ifPresent(email ->
                    sb.append(PREFIX_EMAIL).append(" ").append(email.value).append(" "));
        }
        return sb.toString();
    }
}
