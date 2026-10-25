package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_DATE;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteAttendanceByIndexCommand;
import seedu.address.logic.commands.DeleteAttendanceByNameAndClassCommand;
import seedu.address.logic.commands.DeleteAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@link DeleteAttendanceCommand}.
 */
public class DeleteAttendanceCommandParser implements Parser<DeleteAttendanceCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + ", " + PREFIX_CLASS + " or " + PREFIX_DATE
            + ParserUtil.MESSAGE_SLASH_WORD_HINT;

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAttendanceCommand.MESSAGE_USAGE);

    @Override
    public DeleteAttendanceCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE);
        ParserUtil.requireNoStuckPrefixes(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE);

        boolean hasNameOrClass = argMultimap.getValue(PREFIX_NAME).isPresent()
                || argMultimap.getValue(PREFIX_CLASS).isPresent();
        if (!hasNameOrClass) {
            return parseByIndex(argMultimap);
        }
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        return parseByNameAndClass(argMultimap);
    }

    /**
     * Returns a command that deletes attendance from the person at the index in {@code argMultimap}.
     *
     * @throws ParseException If the index is missing or not a positive integer, or the
     *         date is invalid or missing.
     */
    private static DeleteAttendanceByIndexCommand parseByIndex(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
        return new DeleteAttendanceByIndexCommand(index, parseDate(argMultimap));
    }

    /**
     * Returns a command that deletes attendance from the person identified by the name and class in
     * {@code argMultimap}.
     *
     * @throws ParseException If the name, class, or date is invalid or missing.
     */
    private static DeleteAttendanceByNameAndClassCommand parseByNameAndClass(ArgumentMultimap argMultimap)
            throws ParseException {
        String name = ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
        String className = ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        return new DeleteAttendanceByNameAndClassCommand(name, className, parseDate(argMultimap));
    }

    /**
     * Returns the parsed attendance date in {@code argMultimap}.
     *
     * @throws ParseException If the date is invalid or missing.
     */
    private static LocalDate parseDate(ArgumentMultimap argMultimap) throws ParseException {
        String date = ParserUtil.getNonBlankValue(argMultimap, PREFIX_DATE, MESSAGE_MISSING_DATE);
        return ParserUtil.parseDate(date);
    }
}
