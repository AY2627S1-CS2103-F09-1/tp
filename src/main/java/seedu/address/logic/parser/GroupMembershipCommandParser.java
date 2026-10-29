package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GroupMembershipCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments of the form {@code /group GROUP_NAME /members INDEX[,INDEX]...} and creates a
 * command that changes which contacts are in a group.
 *
 * @param <T> the type of command that is created
 */
public abstract class GroupMembershipCommandParser<T extends GroupMembershipCommand> implements Parser<T> {

    public static final String MESSAGE_MISSING_GROUP = "Missing group name";
    public static final String MESSAGE_MISSING_MEMBERS = "Missing members";
    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_GROUP + " or " + PREFIX_MEMBERS
            + ParserUtil.MESSAGE_SLASH_WORD_HINT;

    private final String invalidFormatMessage;

    /**
     * Creates a parser for a command whose usage is {@code commandUsage}, which is shown when there is text
     * before the first parameter.
     */
    protected GroupMembershipCommandParser(String commandUsage) {
        invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, commandUsage);
    }

    /**
     * Parses the given {@code String} of arguments in the context of the command
     * and returns a command object for execution.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, text before the
     * first parameter, missing group name, missing members, an invalid group name, and finally invalid members.
     * A parameter that is given without a value counts as missing.
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public T parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_GROUP, PREFIX_MEMBERS);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_MEMBERS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_MEMBERS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }

        String groupValue = ParserUtil.getNonBlankValue(argMultimap, PREFIX_GROUP, MESSAGE_MISSING_GROUP);
        String membersValue = ParserUtil.getNonBlankValue(argMultimap, PREFIX_MEMBERS, MESSAGE_MISSING_MEMBERS);

        Tag group = ParserUtil.parseGroupName(groupValue);
        List<Index> memberIndices = ParserUtil.parseMemberIndices(membersValue);

        return createCommand(group, memberIndices);
    }

    /**
     * Returns the command for {@code group} and the contacts at {@code memberIndices}. The indices do not
     * contain duplicates.
     */
    protected abstract T createCommand(Tag group, List<Index> memberIndices);

}
