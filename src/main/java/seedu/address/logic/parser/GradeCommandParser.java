package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_ASSESSMENT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSESSMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE;

import seedu.address.logic.commands.GradeByIndexCommand;
import seedu.address.logic.commands.GradeByNameAndClassCommand;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Grade;

/** Parses arguments and creates a {@link GradeCommand}. */
public class GradeCommandParser implements Parser<GradeCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter. Use " + PREFIX_NAME + ", "
            + PREFIX_CLASS + ", " + PREFIX_ASSESSMENT + " or " + PREFIX_SCORE
            + ParserUtil.MESSAGE_SLASH_WORD_HINT;
    private static final String MESSAGE_MISSING_SCORE = "Score missing";
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, GradeCommand.MESSAGE_USAGE);

    /**
     * Parses grade arguments. Problems are reported in this order: no arguments, unrecognized parameters, repeated
     * parameters, parameter that is missing a space before it, invalid combination or absence of contact identifiers,
     * missing assessment, missing score and invalid values in usage order.
     */
    @Override
    public GradeCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        ParserUtil.requireNoStuckPrefixes(args);

        boolean isIdentifiedByIndex = ParserUtil.isIdentifiedByIndex(argMultimap);
        String name = null;
        String className = null;
        if (!isIdentifiedByIndex) {
            name = ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
            className = ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        }
        String assessmentName = ParserUtil.getNonBlankValue(
                argMultimap, PREFIX_ASSESSMENT, MESSAGE_MISSING_ASSESSMENT);
        String score = ParserUtil.getNonBlankValue(argMultimap, PREFIX_SCORE, MESSAGE_MISSING_SCORE);

        if (isIdentifiedByIndex) {
            return new GradeByIndexCommand(
                    ParserUtil.parseIndex(argMultimap.getPreamble()), parseGrade(assessmentName, score));
        }
        return new GradeByNameAndClassCommand(name, className, parseGrade(assessmentName, score));
    }

    /**
     * Returns the grade made from the given assessment name and score.
     *
     * @throws ParseException If the assessment name or the score is invalid.
     */
    private static Grade parseGrade(String assessmentName, String score) throws ParseException {
        return new Grade(ParserUtil.parseAssessmentName(assessmentName), ParserUtil.parseScore(score));
    }

}
