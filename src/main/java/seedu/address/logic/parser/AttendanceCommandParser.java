package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Attendance;

/**
 * Parses input arguments and creates an AttendanceCommand.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    private static final String MESSAGE_MISSING_ATTENDANCE_INFO = "Missing attendance information";
    private static final String MESSAGE_INVALID_DATE =
        "Invalid date format. Please use the format DD-MM-YYYY.";
    private static final String MESSAGE_INVALID_STATUS =
        "Invalid status. Please use 'present', 'absent' or 'late'.";
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    @Override
    public AttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_STATUS);
        String indexArgument = argMultimap.getPreamble();
        String dateArgument = argMultimap.getValue(PREFIX_DATE).orElse("").trim();
        String statusArgument = argMultimap.getValue(PREFIX_STATUS).orElse("").trim();

        if (indexArgument.isBlank() || dateArgument.isBlank() || statusArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_ATTENDANCE_INFO);
        }
        if (argMultimap.getAllValues(PREFIX_DATE).size() > 1 || argMultimap.getAllValues(PREFIX_STATUS).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_PARAMETERS);
        }

        Index index = ParserUtil.parseIndex(indexArgument);
        LocalDate date;

        try {
            date = LocalDate.parse(dateArgument, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE, e);
        }

        Attendance.Status status;
        try {
            status = Attendance.Status.fromString(statusArgument);
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_STATUS, e);
        }

        return new AttendanceCommand(index, date, status);
    }
}