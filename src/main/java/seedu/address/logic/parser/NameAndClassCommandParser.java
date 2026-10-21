package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of a command that identifies a contact by its name and class, such as
 * {@code viewA /name NAME /class CLASS}.
 *
 * @param <T> The command that is created.
 */
public abstract class NameAndClassCommandParser<T extends Command> implements Parser<T> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + " or " + PREFIX_CLASS;

    private final String invalidFormatMessage;

    /**
     * Creates a parser for a command whose usage message is {@code commandUsage}.
     */
    protected NameAndClassCommandParser(String commandUsage) {
        invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, commandUsage);
    }

    /**
     * Parses the given {@code String} of arguments and returns the command for execution. The name and class
     * are not checked against the rules for adding a contact, so a name or class that no contact has is
     * reported when the command runs.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, text before the first
     * parameter, missing name, and missing class.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public T parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER, PREFIX_NAME, PREFIX_CLASS);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }

        String name = ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
        String className = ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        return createCommand(name, className);
    }

    /**
     * Returns the command for the contact with the given {@code name} and {@code className}.
     */
    protected abstract T createCommand(String name, String className);

}
