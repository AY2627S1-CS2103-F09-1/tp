package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteByIndexCommand;
import seedu.address.logic.commands.DeleteByNameAndClassCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + " or " + PREFIX_CLASS;

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution. The person to delete is identified either by an
     * index alone, or by the name and class parameters alone.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, an invalid index or
     * an index mixed with parameters, missing name, and missing class.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER, PREFIX_NAME, PREFIX_CLASS);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS);

        boolean hasParameter = argMultimap.getValue(PREFIX_NAME).isPresent()
                || argMultimap.getValue(PREFIX_CLASS).isPresent();
        if (!hasParameter) {
            return parseIndex(argMultimap.getPreamble());
        }
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        return parseNameAndClass(argMultimap);
    }

    /**
     * Returns a command that deletes the person at the index given in {@code preamble}.
     * @throws ParseException if {@code preamble} is not a valid index, including when it is empty
     */
    private static DeleteByIndexCommand parseIndex(String preamble) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(preamble);
            return new DeleteByIndexCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(INVALID_FORMAT_MESSAGE, pe);
        }
    }

    /**
     * Returns a command that deletes the person with the name and class given in {@code argMultimap}.
     * A parameter with no value counts as missing.
     * @throws ParseException if the name or the class is missing
     */
    private static DeleteByNameAndClassCommand parseNameAndClass(ArgumentMultimap argMultimap)
            throws ParseException {
        String name = getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
        String className = getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        return new DeleteByNameAndClassCommand(name, className);
    }

    /**
     * Returns the value of {@code prefix} in {@code argMultimap}.
     * @throws ParseException with {@code missingMessage} if the prefix is absent or has a blank value
     */
    private static String getNonBlankValue(ArgumentMultimap argMultimap, Prefix prefix, String missingMessage)
            throws ParseException {
        return argMultimap.getValue(prefix)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParseException(missingMessage));
    }

}
