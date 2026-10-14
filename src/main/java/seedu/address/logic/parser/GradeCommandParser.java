package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSESSMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE;

import java.math.BigDecimal;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Grade;

/** Parses arguments and creates a {@link GradeCommand}. */
public class GradeCommandParser implements Parser<GradeCommand> {

    private static final String MESSAGE_MISSING_INDEX = "Missing index. Please provide the index of the person.";
    private static final String MESSAGE_MISSING_ASSESSMENT = "Assessment name missing";
    private static final String MESSAGE_MISSING_SCORE = "Score missing";

    @Override
    public GradeCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ASSESSMENT, PREFIX_SCORE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ASSESSMENT, PREFIX_SCORE);

        String indexArgument = argMultimap.getPreamble();
        String assessmentArgument = argMultimap.getValue(PREFIX_ASSESSMENT).orElse("").trim();
        String scoreArgument = argMultimap.getValue(PREFIX_SCORE).orElse("").trim();

        if (indexArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }
        if (assessmentArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_ASSESSMENT);
        }
        if (scoreArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_SCORE);
        }

        Index index = ParserUtil.parseIndex(indexArgument);
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

        return new GradeCommand(index, new Grade(assessmentArgument, score));
    }

}
