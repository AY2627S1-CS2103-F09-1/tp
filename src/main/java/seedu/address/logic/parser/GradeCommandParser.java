package seedu.address.logic.parser;

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
            + PREFIX_CLASS + ", " + PREFIX_ASSESSMENT + " or " + PREFIX_SCORE;
    private static final String MESSAGE_MISSING_SCORE = "Score missing";

    @Override
    public GradeCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);

        boolean isIdentifiedByIndex = ParserUtil.isIdentifiedByIndex(argMultimap);
        Grade grade = parseGrade(argMultimap);

        if (isIdentifiedByIndex) {
            return new GradeByIndexCommand(ParserUtil.parseIndex(argMultimap.getPreamble()), grade);
        }
        String name = ParserUtil.getNonBlankValue(argMultimap, PREFIX_NAME, MESSAGE_MISSING_NAME);
        String className = ParserUtil.getNonBlankValue(argMultimap, PREFIX_CLASS, MESSAGE_MISSING_CLASS);
        return new GradeByNameAndClassCommand(name, className, grade);
    }

    /**
     * Returns the grade made from the assessment name and score in {@code argMultimap}.
     *
     * @throws ParseException If the assessment name or the score is missing or invalid.
     */
    private static Grade parseGrade(ArgumentMultimap argMultimap) throws ParseException {
        String assessmentName = ParserUtil.getNonBlankValue(
                argMultimap, PREFIX_ASSESSMENT, MESSAGE_MISSING_ASSESSMENT);
        String score = ParserUtil.getNonBlankValue(argMultimap, PREFIX_SCORE, MESSAGE_MISSING_SCORE);
        return new Grade(ParserUtil.parseAssessmentName(assessmentName), ParserUtil.parseScore(score));
    }

}
