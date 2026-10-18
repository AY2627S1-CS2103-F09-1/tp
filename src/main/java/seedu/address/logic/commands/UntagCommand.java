package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Removes contacts, identified by their displayed indices, from a group.
 * Either all of the contacts are removed or none of them are.
 */
public class UntagCommand extends GroupMembershipCommand {

    public static final String COMMAND_WORD = "untag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes the contacts identified by the index "
            + "numbers used in the displayed contact list from a group.\n"
            + "Parameters: " + PREFIX_GROUP + " GROUP_NAME " + PREFIX_MEMBERS + " INDEX[,INDEX]... "
            + "(each INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_GROUP + " Group A " + PREFIX_MEMBERS + " 1,3,5";

    public static final String MESSAGE_SUCCESS = "Contacts successfully removed from group %1$s: %2$s";
    public static final String MESSAGE_NOT_IN_GROUP =
            "One or more contacts are not in this group: %1$s";

    /**
     * Creates an {@code UntagCommand} to remove the contacts at {@code memberIndices} from {@code group}.
     * The indices must not contain duplicates, as a contact can only be changed once by a command.
     */
    public UntagCommand(Tag group, List<Index> memberIndices) {
        super(group, memberIndices);
    }

    @Override
    protected void requireMembersCanBeChanged(List<Person> members, Tag group) throws CommandException {
        requireNoMemberMatching(members, member -> !member.hasTag(group), MESSAGE_NOT_IN_GROUP);
    }

    @Override
    protected Person changeMembership(Person member, Tag group) {
        return member.withoutTag(group);
    }

    @Override
    protected String getSuccessMessage() {
        return MESSAGE_SUCCESS;
    }
}
