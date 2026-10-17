package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Changes which contacts are in a group, for the contacts identified by their displayed indices.
 * Either all of the contacts are changed or none of them are.
 */
public abstract class GroupMembershipCommand extends Command {

    public static final String MESSAGE_INDEX_NOT_FOUND = "Contact index not found: %1$s";

    private static final String LIST_SEPARATOR = ", ";

    private final Tag group;
    private final List<Index> memberIndices;

    /**
     * Creates a command to change the group membership of the contacts at {@code memberIndices} of the
     * displayed list, for {@code group}.
     * The indices must not contain duplicates, as a contact can only be changed once by a command.
     */
    protected GroupMembershipCommand(Tag group, List<Index> memberIndices) {
        requireNonNull(group);
        requireNonNull(memberIndices);
        assert hasNoDuplicates(memberIndices) : "memberIndices must not contain duplicates";
        this.group = group;
        this.memberIndices = List.copyOf(memberIndices);
    }

    /**
     * Returns true if no index appears more than once in {@code indices}.
     */
    private static boolean hasNoDuplicates(List<Index> indices) {
        return indices.stream().map(Index::getOneBased).distinct().count() == indices.size();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> members = findMembers(model.getFilteredPersonList());
        requireMembersCanBeChanged(members, group);

        for (Person member : members) {
            model.setPerson(member, changeMembership(member, group));
        }
        return new CommandResult(String.format(getSuccessMessage(), group.tagName, formatContacts(members)));
    }

    /**
     * Throws a {@code CommandException} if the membership of any of {@code members} cannot be changed for
     * {@code group}. Nothing has been changed when this is called.
     */
    protected abstract void requireMembersCanBeChanged(List<Person> members, Tag group)
            throws CommandException;

    /**
     * Returns a copy of {@code member} with the membership of {@code group} changed.
     */
    protected abstract Person changeMembership(Person member, Tag group);

    /**
     * Returns the message to show on success. It is formatted with the group name and the contacts.
     */
    protected abstract String getSuccessMessage();

    /**
     * Throws a {@code CommandException} that names the contacts in {@code members} accepted by
     * {@code isProblem}, if there are any. {@code message} is formatted with the names of those contacts.
     */
    protected static void requireNoMemberMatching(List<Person> members, Predicate<Person> isProblem,
            String message) throws CommandException {
        List<Person> problemMembers = members.stream().filter(isProblem).toList();
        if (!problemMembers.isEmpty()) {
            throw new CommandException(String.format(message, formatContacts(problemMembers)));
        }
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

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        GroupMembershipCommand otherCommand = (GroupMembershipCommand) other;
        return group.equals(otherCommand.group)
                && memberIndices.equals(otherCommand.memberIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), group, memberIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group", group)
                .add("memberIndices", memberIndices)
                .toString();
    }
}
