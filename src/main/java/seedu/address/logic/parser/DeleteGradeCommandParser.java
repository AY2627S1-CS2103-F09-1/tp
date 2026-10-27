package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_ASSESSMENT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSESSMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteGradeByIndexCommand;
import seedu.address.logic.commands.DeleteGradeByNameAndClassCommand;
import seedu.address.logic.commands.DeleteGradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@link DeleteGradeCommand}.
 */
public class DeleteGradeCommandParser implements Parser<DeleteGradeCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + ", " + PREFIX_CLASS + " or " + PREFIX_ASSESSMENT
            + ParserUtil.MESSAGE_SLASH_WORD_HINT;

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteGradeCommand.MESSAGE_USAGE);

    @Override
    public DeleteGradeCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT);
        ParserUtil.requireNoStuckPrefixes(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT);

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
     * Returns a command that deletes a grade from the person at the index in {@code argMultimap}.
     *
     * @throws ParseException If the index is missing or not a positive integer, or the
     *         assessment name is invalid or missing.
     */
    private static DeleteGradeByIndexCommand parseByIndex(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
        return new DeleteGradeByIndexCommand(index, parseAssessmentName(argMultimap));
    }

    /**
     * Returns a command that deletes a grade from the person identified by the name and class in
     * {@code argMultimap}.
     *
     * @throws ParseException If the name, class, or assessment name is invalid or missing.
     */
    private static DeleteGradeByNameAndClassCommand parseByNameAndClass(ArgumentMultimap argMultimap)
            throws ParseException {
        String name = ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
        String className = ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        return new DeleteGradeByNameAndClassCommand(name, className, parseAssessmentName(argMultimap));
    }

    /**
     * Returns the validated assessment name in {@code argMultimap}.
     *
     * @throws ParseException If the assessment name is invalid or missing.
     */
    private static String parseAssessmentName(ArgumentMultimap argMultimap) throws ParseException {
        String assessmentName = ParserUtil.getNonBlankValue(
                argMultimap, PREFIX_ASSESSMENT, MESSAGE_MISSING_ASSESSMENT);
        return ParserUtil.parseAssessmentName(assessmentName);
    }

}
