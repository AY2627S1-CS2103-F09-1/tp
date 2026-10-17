package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNTAG;

import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

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
        appendGroups(sb, PREFIX_TAG, descriptor.getTagsToAdd());
        appendGroups(sb, PREFIX_UNTAG, descriptor.getTagsToRemove());
        return sb.toString();
    }

    /**
     * Appends {@code prefix} followed by the comma-separated names of {@code groups} to {@code sb}, if there
     * are any groups.
     */
    private static void appendGroups(StringBuilder sb, Prefix prefix, Set<Tag> groups) {
        if (groups.isEmpty()) {
            return;
        }
        String names = groups.stream().map(group -> group.tagName).collect(Collectors.joining(","));
        sb.append(prefix).append(" ").append(names).append(" ");
    }
}
