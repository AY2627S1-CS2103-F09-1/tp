package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_DATE;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import seedu.address.logic.commands.AttendanceByIndexCommand;
import seedu.address.logic.commands.AttendanceByNameAndClassCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Attendance;

/**
 * Parses input arguments and creates an AttendanceCommand.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter. Use " + PREFIX_NAME + ", "
            + PREFIX_CLASS + ", " + PREFIX_DATE + " or " + PREFIX_STATUS
            + ParserUtil.MESSAGE_SLASH_WORD_HINT;
    private static final String MESSAGE_MISSING_STATUS = "Missing status. Please provide the attendance status.";
    private static final String MESSAGE_INVALID_STATUS =
            "Invalid status. Please use 'present', 'absent' or 'late'.";
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE);

    /**
     * Parses attendance arguments. Problems are reported in this order: no arguments, unrecognized parameters,
     * repeated parameters, parameter missing a space before it, invalid combination or absence of contact identifiers,
     * missing date, missing status and invalid values in usage order.
     */
    @Override
    public AttendanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);
        ParserUtil.requireNoStuckPrefixes(args);

        boolean isIdentifiedByIndex = ParserUtil.isIdentifiedByIndex(argMultimap);
        String name = null;
        String className = null;
        if (!isIdentifiedByIndex) {
            name = ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
            className = ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        }
        String dateArgument = ParserUtil.getNonBlankValue(argMultimap, PREFIX_DATE, MESSAGE_MISSING_DATE);
        String statusArgument = ParserUtil.getNonBlankValue(argMultimap, PREFIX_STATUS, MESSAGE_MISSING_STATUS);

        if (isIdentifiedByIndex) {
            return new AttendanceByIndexCommand(ParserUtil.parseIndex(argMultimap.getPreamble()),
                    ParserUtil.parseDate(dateArgument), parseStatus(statusArgument));
        }
        return new AttendanceByNameAndClassCommand(name, className,
                ParserUtil.parseDate(dateArgument), parseStatus(statusArgument));
    }

    /**
     * Returns the attendance status described by {@code statusArgument}.
     *
     * @throws ParseException If {@code statusArgument} is not present, absent or late.
     */
    private static Attendance.Status parseStatus(String statusArgument) throws ParseException {
        try {
            return Attendance.Status.fromString(statusArgument);
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_STATUS, e);
        }
    }

}
