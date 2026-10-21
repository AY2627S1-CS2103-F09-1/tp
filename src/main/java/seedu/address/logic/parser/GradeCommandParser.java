package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSESSMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE;

import java.math.BigDecimal;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Name;

/** Parses arguments and creates a {@link GradeCommand}. */
public class GradeCommandParser implements Parser<GradeCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_ASSESSMENT + " or " + PREFIX_SCORE;
    private static final String MESSAGE_MISSING_IDENTIFIERS =
        "Use either an index, or both /name and /class.";
    private static final String MESSAGE_MISSING_ASSESSMENT = "Assessment name missing";
    private static final String MESSAGE_MISSING_SCORE = "Score missing";
    private static final String MESSAGE_BOTH_IDENTIFIERS =
            "Use either an index or name and class, not both.";

    @Override
    public GradeCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
            PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
            PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_ASSESSMENT, PREFIX_SCORE);

        String indexArgument = argMultimap.getPreamble();
        String nameArgument = argMultimap.getValue(PREFIX_NAME).orElse("").trim();
        String classArgument = argMultimap.getValue(PREFIX_CLASS).orElse("").trim();
        String assessmentArgument = argMultimap.getValue(PREFIX_ASSESSMENT).orElse("").trim();
        String scoreArgument = argMultimap.getValue(PREFIX_SCORE).orElse("").trim();

        boolean hasIndex = !indexArgument.isBlank();
        boolean hasNameOrClass = !nameArgument.isBlank() || !classArgument.isBlank();

        if (hasIndex && hasNameOrClass) {
            throw new ParseException(MESSAGE_BOTH_IDENTIFIERS);
        }
        if (!hasIndex && !hasNameOrClass) {
            throw new ParseException(MESSAGE_MISSING_IDENTIFIERS);
        }
        if (hasNameOrClass && nameArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_NAME);
        }
        if (hasNameOrClass && classArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_CLASS);
        }
        if (assessmentArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_ASSESSMENT);
        }
        if (scoreArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_SCORE);
        }
        if (argMultimap.getAllValues(PREFIX_ASSESSMENT).size() > 1
            || argMultimap.getAllValues(PREFIX_SCORE).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_PARAMETERS);
        }

        ParserUtil.requireNoEmbeddedPrefix(assessmentArgument, "assessment name");
        Optional<String> assessmentViolation = Grade.getAssessmentNameConstraintViolation(assessmentArgument);
        if (assessmentViolation.isPresent()) {
            throw new ParseException(assessmentViolation.get());
        }

        BigDecimal score;
        try {
            if (!scoreArgument.matches("(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)")) {
                throw new NumberFormatException();
            }
            score = new BigDecimal(scoreArgument);
            if (score.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new ParseException(Grade.MESSAGE_INVALID_SCORE, e);
        }

        Grade grade = new Grade(assessmentArgument, score);
        if (hasIndex) {
            Index index = ParserUtil.parseIndex(indexArgument);
            return new GradeCommand(index, grade);
        }

        Name name = ParserUtil.parseName(nameArgument);
        ClassName className = ParserUtil.parseClassName(classArgument);
        return new GradeCommand(name, className, grade);
    }

}
