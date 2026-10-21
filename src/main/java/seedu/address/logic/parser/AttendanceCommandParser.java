package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;

/**
 * Parses input arguments and creates an AttendanceCommand.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    private static final String MESSAGE_MISSING_INDEX = "Missing index. Please provide the index of the person.";
    private static final String MESSAGE_BOTH_IDENTIFIERS =
        "Use either an index or name and class, not both.";
    private static final String MESSAGE_MISSING_DATE = "Missing date. Please provide the date of attendance.";
    private static final String MESSAGE_MISSING_STATUS = "Missing status. Please provide the attendance status.";
    private static final String MESSAGE_INVALID_DATE =
        "Invalid date format. Please use the format DD-MM-YYYY.";
    private static final String MESSAGE_INVALID_STATUS =
        "Invalid status. Please use 'present', 'absent' or 'late'.";
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    @Override
    public AttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE,
                PREFIX_STATUS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE, PREFIX_STATUS);
        String indexArgument = argMultimap.getPreamble();
        String nameArgument = argMultimap.getValue(PREFIX_NAME).orElse("").trim();
        String classArgument = argMultimap.getValue(PREFIX_CLASS).orElse("").trim();
        String dateArgument = argMultimap.getValue(PREFIX_DATE).orElse("").trim();
        String statusArgument = argMultimap.getValue(PREFIX_STATUS).orElse("").trim();

        boolean hasIndex = !indexArgument.isBlank();
        boolean hasNameOrClass = !nameArgument.isBlank() || !classArgument.isBlank();

        if (hasIndex && hasNameOrClass) {
            throw new ParseException(MESSAGE_BOTH_IDENTIFIERS);
        }
        if (!hasIndex && !hasNameOrClass) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }
        if (hasNameOrClass && nameArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_NAME);
        }
        if (hasNameOrClass && classArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_CLASS);
        }
        if (dateArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_DATE);
        }
        if (statusArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_STATUS);
        }
        if (argMultimap.getAllValues(PREFIX_DATE).size() > 1 || argMultimap.getAllValues(PREFIX_STATUS).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_PARAMETERS);
        }

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

        if (hasIndex) {
            Index index = ParserUtil.parseIndex(indexArgument);
            return new AttendanceCommand(index, date, status);
        }

        Name name = ParserUtil.parseName(nameArgument);
        ClassName className = ParserUtil.parseClassName(classArgument);
        return new AttendanceCommand(name, className, date, status);
    }

}
