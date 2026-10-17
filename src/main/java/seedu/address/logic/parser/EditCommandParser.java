package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNTAG;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter. Use " + PREFIX_NAME + ", "
            + PREFIX_CLASS + ", " + PREFIX_EMAIL + ", " + PREFIX_TAG + " or " + PREFIX_UNTAG;
    public static final String MESSAGE_CONFLICTING_GROUPS =
            "A group cannot be added and removed in the same command: %1$s";

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private static final Prefix[] EDIT_PREFIXES = {
        PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL, PREFIX_TAG, PREFIX_UNTAG
    };

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, an invalid index,
     * an invalid name, class, email, groups to add or groups to remove, groups that are both added and
     * removed, and finally that no detail is given. A name or class without a value is invalid, an email
     * without a value removes the email, and groups to add or remove without a value count as missing.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER, EDIT_PREFIXES);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, EDIT_PREFIXES);
        argMultimap.verifyNoDuplicatePrefixesFor(EDIT_PREFIXES);

        Index index = parseIndex(argMultimap.getPreamble());
        EditPersonDescriptor editPersonDescriptor = parseDescriptor(argMultimap);
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Returns the index in {@code preamble}.
     * @throws ParseException with the invalid command format message if {@code preamble} is not a valid index
     */
    private static Index parseIndex(String preamble) throws ParseException {
        try {
            return ParserUtil.parseIndex(preamble);
        } catch (ParseException pe) {
            throw new ParseException(INVALID_FORMAT_MESSAGE, pe);
        }
    }

    /**
     * Returns an {@code EditPersonDescriptor} with the details given in {@code argMultimap}.
     * @throws ParseException if a detail is invalid, or a group is both added and removed
     */
    private static EditPersonDescriptor parseDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_CLASS).isPresent()) {
            editPersonDescriptor.setClassName(
                    ParserUtil.parseClassName(argMultimap.getValue(PREFIX_CLASS).get()));
        }
        parseEmail(argMultimap, editPersonDescriptor);

        Set<Tag> groupsToAdd = parseGroups(argMultimap, PREFIX_TAG);
        Set<Tag> groupsToRemove = parseGroups(argMultimap, PREFIX_UNTAG);
        requireNoConflictingGroups(groupsToAdd, groupsToRemove);
        editPersonDescriptor.setTagsToAdd(groupsToAdd);
        editPersonDescriptor.setTagsToRemove(groupsToRemove);

        return editPersonDescriptor;
    }

    /**
     * Sets the email of {@code editPersonDescriptor} to the email given in {@code argMultimap}, or marks the
     * email as removed if the email is given without a value. Does nothing if no email is given.
     * @throws ParseException if the email is invalid
     */
    private static void parseEmail(ArgumentMultimap argMultimap, EditPersonDescriptor editPersonDescriptor)
            throws ParseException {
        Optional<String> email = argMultimap.getValue(PREFIX_EMAIL);
        if (email.isEmpty()) {
            return;
        }
        if (email.get().isBlank()) {
            editPersonDescriptor.setEmailCleared(true);
        } else {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(email.get()));
        }
    }

    /**
     * Returns the groups given for {@code prefix} in {@code argMultimap}, which is empty if the prefix is
     * not given.
     * @throws ParseException if the prefix is given without a value, or the groups are invalid
     */
    private static Set<Tag> parseGroups(ArgumentMultimap argMultimap, Prefix prefix) throws ParseException {
        Optional<String> groups = argMultimap.getValue(prefix);
        if (groups.isEmpty()) {
            return Collections.emptySet();
        }
        if (groups.get().isBlank()) {
            throw new ParseException(TagCommandParser.MESSAGE_MISSING_GROUP);
        }
        return ParserUtil.parseGroupNames(groups.get());
    }

    /**
     * Throws a {@code ParseException} naming the groups that are in both {@code groupsToAdd} and
     * {@code groupsToRemove}, if there are any.
     */
    private static void requireNoConflictingGroups(Set<Tag> groupsToAdd, Set<Tag> groupsToRemove)
            throws ParseException {
        String conflictingGroups = groupsToAdd.stream()
                .filter(groupsToRemove::contains)
                .map(group -> group.tagName)
                .collect(Collectors.joining(", "));
        if (!conflictingGroups.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_CONFLICTING_GROUPS, conflictingGroups));
        }
    }

}
