package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.model.person.Grade;

public class GradeCommandParserTest {

    private final GradeCommandParser parser = new GradeCommandParser();

    @Test
    public void parse_validArguments_success() {
        GradeCommand expected = new GradeCommand(
                Index.fromOneBased(5), new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, "5 /assessment Assignment 1 /score 97", expected);
    }

    @Test
    public void parse_prefixesInAnyCase_success() {
        GradeCommand expected = new GradeCommand(
                Index.fromOneBased(5), new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, "5 /ASSESSMENT Assignment 1 /Score 97", expected);
    }

    @Test
    public void parse_decimalScore_success() {
        GradeCommand expected = new GradeCommand(
                Index.fromOneBased(2), new Grade("Quiz-1", new BigDecimal("97.5")));

        assertParseSuccess(parser, "2 /score 97.5 /assessment Quiz-1", expected);
    }

    @Test
    public void parse_assessmentNameWithSymbolsAndAccents_success() {
        assertParseSuccess(parser, "1 /assessment Quiz #1 (Part A) /score 90",
                new GradeCommand(Index.fromOneBased(1), new Grade("Quiz #1 (Part A)", new BigDecimal("90"))));
        assertParseSuccess(parser, "1 /assessment Examen Finalé /score 90",
                new GradeCommand(Index.fromOneBased(1), new Grade("Examen Finalé", new BigDecimal("90"))));
    }

    @Test
    public void parse_prefixWithoutSpaceInAssessmentName_failure() {
        assertParseFailure(parser, "1 /assessment Quiz/score 90 /score 80",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "assessment name", "/score"));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " /assessment Assignment 1 /score 97",
                "Missing index. Please provide the index of the person.");
    }

    @Test
    public void parse_missingAssessment_failure() {
        assertParseFailure(parser, "1 /score 97", "Assessment name missing");
    }

    @Test
    public void parse_missingScore_failure() {
        assertParseFailure(parser, "1 /assessment Assignment 1", "Score missing");
    }

    @Test
    public void parse_invalidScore_failure() {
        assertParseFailure(parser, "1 /assessment Assignment 1 /score A", Grade.MESSAGE_INVALID_SCORE);
        assertParseFailure(parser, "1 /assessment Assignment 1 /score -1", Grade.MESSAGE_INVALID_SCORE);
        assertParseFailure(parser, "1 /assessment Assignment 1 /score 100.1", Grade.MESSAGE_INVALID_SCORE);
    }

    @Test
    public void parse_invalidAssessmentName_failure() {
        assertParseFailure(parser, "1 /assessment Exam,1 /score 90",
                Grade.MESSAGE_INVALID_ASSESSMENT_NAME);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 /assessment Quiz /score 90", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /assessment Exam /score 90",
                MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, "1 /assessment Quiz /score 90 /score 91",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

}
