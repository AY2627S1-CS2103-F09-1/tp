package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments and creates a ViewAttendanceCommand. */
public class ViewAttendanceCommandParser implements Parser<ViewAttendanceCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + " or " + PREFIX_CLASS;

    /**
     * Parses the given {@code String} of arguments in the context of the ViewAttendanceCommand
     * and returns a ViewAttendanceCommand object for execution. The name and class are not checked against
     * the rules for adding a contact, so a name or class that no contact has is reported when the command runs.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, text before the first
     * parameter, missing name, and missing class.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ViewAttendanceCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER, PREFIX_NAME, PREFIX_CLASS);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, ViewAttendanceCommand.MESSAGE_USAGE));
        }

        String name = getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
        String className = getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        return new ViewAttendanceCommand(name, className);
    }

    /**
     * Returns the value of {@code prefix} in {@code argMultimap}.
     *
     * @throws ParseException with {@code missingMessage} if the prefix is absent or has a blank value
     */
    private static String getNonBlankValue(ArgumentMultimap argMultimap, Prefix prefix, String missingMessage)
            throws ParseException {
        return argMultimap.getValue(prefix)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ParseException(missingMessage));
    }

}
