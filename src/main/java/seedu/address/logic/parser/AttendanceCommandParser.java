package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_DATE;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.time.LocalDate;

import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;

/**
 * Parses input arguments and creates an AttendanceCommand.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_DATE + " or " + PREFIX_STATUS;
    private static final String MESSAGE_MISSING_STATUS = "Missing status. Please provide the attendance status.";
    private static final String MESSAGE_INVALID_STATUS =
            "Invalid status. Please use 'present', 'absent' or 'late'.";

    @Override
    public AttendanceCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);

        boolean isIdentifiedByIndex = ParserUtil.isIdentifiedByIndex(argMultimap);
        String dateArgument = ParserUtil.getNonBlankValue(argMultimap, PREFIX_DATE, MESSAGE_MISSING_DATE);
        String statusArgument = ParserUtil.getNonBlankValue(argMultimap, PREFIX_STATUS, MESSAGE_MISSING_STATUS);
        LocalDate date = ParserUtil.parseDate(dateArgument);
        Attendance.Status status = parseStatus(statusArgument);

        if (isIdentifiedByIndex) {
            return new AttendanceCommand(ParserUtil.parseIndex(argMultimap.getPreamble()), date, status);
        }
        Name name = ParserUtil.parseName(
                ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME));
        ClassName className = ParserUtil.parseClassName(
                ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS));
        return new AttendanceCommand(name, className, date, status);
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
