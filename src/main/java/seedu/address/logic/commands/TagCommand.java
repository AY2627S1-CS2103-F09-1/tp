package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Links contacts, identified by their displayed indices, to a group.
 * Either all of the contacts are linked or none of them are.
 */
public class TagCommand extends GroupMembershipCommand {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Links the contacts identified by the index "
            + "numbers used in the displayed contact list to a group.\n"
            + "Parameters: " + PREFIX_GROUP + " GROUP_NAME " + PREFIX_MEMBERS + " INDEX[,INDEX]... "
            + "(each INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_GROUP + " Group A " + PREFIX_MEMBERS + " 1,3,5";

    public static final String MESSAGE_SUCCESS = "Contacts successfully linked to group %1$s: %2$s";
    public static final String MESSAGE_ALREADY_IN_GROUP =
            "One or more contacts are already in this group: %1$s";

    /**
     * Creates a {@code TagCommand} to link the contacts at {@code memberIndices} to {@code group}.
     *
     * @throws IllegalArgumentException if {@code memberIndices} is empty or contains duplicates.
     */
    public TagCommand(Tag group, List<Index> memberIndices) {
        super(group, memberIndices);
    }

    @Override
    protected void requireMembersCanBeChanged(List<Person> members, Tag group) throws CommandException {
        requireNoMemberMatching(members, member -> member.hasTag(group), MESSAGE_ALREADY_IN_GROUP);
    }

    @Override
    protected Person changeMembership(Person member, Tag group) {
        return member.withTag(group);
    }

    @Override
    protected String getSuccessMessage() {
        return MESSAGE_SUCCESS;
    }
}
