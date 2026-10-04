package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Links contacts, identified by their displayed indices, to a group.
 * Either all of the contacts are linked or none of them are.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Links the contacts identified by the index "
            + "numbers used in the displayed contact list to a group.\n"
            + "Parameters: " + PREFIX_GROUP + " GROUP_NAME " + PREFIX_MEMBERS + " INDEX[,INDEX]... "
            + "(each INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_GROUP + " Group A " + PREFIX_MEMBERS + " 1,3,5";

    public static final String MESSAGE_SUCCESS = "Contacts successfully linked to group %1$s: %2$s";
    public static final String MESSAGE_INDEX_NOT_FOUND = "Contact index not found: %1$s";
    public static final String MESSAGE_ALREADY_IN_GROUP =
            "One or more contacts are already in this group: %1$s";

    private static final String LIST_SEPARATOR = ", ";

    private final Tag group;
    private final List<Index> memberIndices;

    /**
     * Creates a {@code TagCommand} to link the contacts at {@code memberIndices} to {@code group}.
     * The indices must not contain duplicates.
     */
    public TagCommand(Tag group, List<Index> memberIndices) {
        requireNonNull(group);
        requireNonNull(memberIndices);
        this.group = group;
        this.memberIndices = List.copyOf(memberIndices);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> members = findMembers(model.getFilteredPersonList());
        requireNoneInGroup(members);

        for (Person member : members) {
            model.setPerson(member, member.withTag(group));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, group.tagName, formatContacts(members)));
    }

    /**
     * Returns the contacts at {@code memberIndices} in {@code shownContacts}, in the order given.
     *
     * @throws CommandException if any index is not in {@code shownContacts}.
     */
    private List<Person> findMembers(List<Person> shownContacts) throws CommandException {
        List<Index> missingIndices = memberIndices.stream()
                .filter(index -> index.getZeroBased() >= shownContacts.size())
                .toList();
        if (!missingIndices.isEmpty()) {
            String missing = missingIndices.stream()
                    .map(index -> String.valueOf(index.getOneBased()))
                    .collect(Collectors.joining(LIST_SEPARATOR));
            throw new CommandException(String.format(MESSAGE_INDEX_NOT_FOUND, missing));
        }

        List<Person> members = new ArrayList<>();
        for (Index index : memberIndices) {
            members.add(shownContacts.get(index.getZeroBased()));
        }
        return members;
    }

    /**
     * Throws a {@code CommandException} naming the contacts in {@code members} that are already in the group.
     */
    private void requireNoneInGroup(List<Person> members) throws CommandException {
        List<Person> alreadyInGroup = members.stream()
                .filter(member -> member.hasTag(group))
                .toList();
        if (!alreadyInGroup.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_IN_GROUP, formatContacts(alreadyInGroup)));
        }
    }

    /**
     * Returns the names and classes of {@code contacts}, which tells apart contacts that share a name.
     */
    private static String formatContacts(List<Person> contacts) {
        return contacts.stream()
                .map(contact -> contact.getName() + " (" + contact.getClassName() + ")")
                .collect(Collectors.joining(LIST_SEPARATOR));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagCommand otherTagCommand)) {
            return false;
        }

        return group.equals(otherTagCommand.group)
                && memberIndices.equals(otherTagCommand.memberIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group", group)
                .add("memberIndices", memberIndices)
                .toString();
    }
}
