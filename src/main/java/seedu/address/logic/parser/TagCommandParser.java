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
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_GROUP, PREFIX_MEMBERS);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_MEMBERS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_MEMBERS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        String groupValue = getNonBlankValue(argMultimap, PREFIX_GROUP, MESSAGE_MISSING_GROUP);
        String membersValue = getNonBlankValue(argMultimap, PREFIX_MEMBERS, MESSAGE_MISSING_MEMBERS);

        Tag group = ParserUtil.parseGroupName(groupValue);
        List<Index> memberIndices = ParserUtil.parseMemberIndices(membersValue);

        return new TagCommand(group, memberIndices);
    }

    /**
     * Returns the value of {@code prefix} in {@code argMultimap}.
     *
     * @throws ParseException with {@code missingMessage} if the prefix is absent or its value is blank.
     */
    private static String getNonBlankValue(ArgumentMultimap argMultimap, Prefix prefix, String missingMessage)
            throws ParseException {
        return argMultimap.getValue(prefix)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParseException(missingMessage));
    }

}
