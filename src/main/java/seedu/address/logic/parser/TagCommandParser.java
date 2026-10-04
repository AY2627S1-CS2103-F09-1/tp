package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object
 */
public class TagCommandParser implements Parser<TagCommand> {

    public static final String MESSAGE_MISSING_GROUP = "Missing group name";
    public static final String MESSAGE_MISSING_MEMBERS = "Missing members";
    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_GROUP + " or " + PREFIX_MEMBERS;

    /**
     * Parses the given {@code String} of arguments in the context of the TagCommand
     * and returns a TagCommand object for execution.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, text before the
     * first parameter, missing group name, missing members, invalid group name, and finally invalid members.
     * A parameter that is given without a value counts as missing.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public TagCommand parse(String args) throws ParseException {
        requireNoUnrecognizedPrefixes(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_MEMBERS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_MEMBERS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        String groupValue = argMultimap.getValue(PREFIX_GROUP)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParseException(MESSAGE_MISSING_GROUP));
        String membersValue = argMultimap.getValue(PREFIX_MEMBERS)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParseException(MESSAGE_MISSING_MEMBERS));

        Tag group = ParserUtil.parseGroupName(groupValue);
        List<Index> memberIndices = ParserUtil.parseMemberIndices(membersValue);

        return new TagCommand(group, memberIndices);
    }

    /**
     * Throws a {@code ParseException} if {@code args} contains a prefix-like token that the tag command does
     * not accept.
     */
    private static void requireNoUnrecognizedPrefixes(String args) throws ParseException {
        List<String> unrecognizedPrefixes =
                ArgumentTokenizer.findUnrecognizedPrefixes(args, PREFIX_GROUP, PREFIX_MEMBERS);
        if (!unrecognizedPrefixes.isEmpty()) {
            throw new ParseException(MESSAGE_UNKNOWN_PARAMETER);
        }
    }

}
