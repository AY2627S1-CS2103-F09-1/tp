package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeByIndexCommand;
import seedu.address.logic.commands.GradeByNameAndClassCommand;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.model.person.Grade;

public class GradeCommandParserTest {

    private final GradeCommandParser parser = new GradeCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validIndexArguments_success() {
        GradeCommand expected = new GradeByIndexCommand(
                Index.fromOneBased(5), new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, "5 /assessment Assignment 1 /score 97", expected);
    }

    @Test
    public void parse_validNameAndClassArguments_success() {
        String userInput = " /name Alex Yeoh /class A1 /assessment Assignment 1 /score 97";

        GradeCommand expectedCommand = new GradeByNameAndClassCommand(
                "Alex Yeoh",
                "A1",
                new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_nameAndClassFormatting_success() {
        String userInput =
                " /name   alex   yeoh  /class  a1 /assessment Assignment 1 /score 97";

        GradeCommand expectedCommand = new GradeByNameAndClassCommand(
                "alex   yeoh",
                "a1",
                new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_nameAndClassThatNoContactCanHave_success() {
        GradeCommand expectedCommand = new GradeByNameAndClassCommand(
                "@@@,",
                "!!!",
                new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, " /name @@@, /class !!! /assessment Assignment 1 /score 97",
                expectedCommand);
    }

    @Test
    public void parse_unknownParameterMessage_listsEveryAcceptedParameter() {
        assertEquals("Unknown parameter. Use /name, /class, /assessment or /score."
                        + " A word that starts with / is always read as a parameter.",
                GradeCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_unknownParameter_failure() {
        assertParseFailure(parser, "1 /assessment Quiz 1 /score 90 /foo bar",
                GradeCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, "1 /foo bar", GradeCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_unknownParameterAndRepeatedParameter_reportsUnknownParameter() {
        assertParseFailure(parser, "1 /assessment A /assessment B /score 90 /foo bar",
                GradeCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_prefixesInAnyCase_success() {
        GradeCommand expected = new GradeByIndexCommand(
                Index.fromOneBased(5), new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, "5 /ASSESSMENT Assignment 1 /Score 97", expected);
    }

    @Test
    public void parse_decimalScore_success() {
        GradeCommand expected = new GradeByIndexCommand(
                Index.fromOneBased(2), new Grade("Quiz-1", new BigDecimal("97.5")));

        assertParseSuccess(parser, "2 /score 97.5 /assessment Quiz-1", expected);
    }

    @Test
    public void parse_assessmentNameWithSymbolsAndAccents_success() {
        assertParseSuccess(parser, "1 /assessment Quiz #1 (Part A) /score 90",
                new GradeByIndexCommand(Index.fromOneBased(1),
                        new Grade("Quiz #1 (Part A)", new BigDecimal("90"))));
        assertParseSuccess(parser, "1 /assessment Examen Finalé /score 90",
                new GradeByIndexCommand(Index.fromOneBased(1),
                        new Grade("Examen Finalé", new BigDecimal("90"))));
    }

    @Test
    public void parse_prefixWithoutSpaceInAssessmentName_failure() {
        assertParseFailure(parser, "1 /assessment Quiz/score 90 /score 80",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/score"));
    }

    @Test
    public void parse_prefixWithoutSpace_reportsThePrefix() {
        // After the assessment name, so that the score looks missing
        assertParseFailure(parser, "1 /assessment Quiz/score 90",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/score"));

        // After the score
        assertParseFailure(parser, "1 /assessment Quiz /score 90/assessment Exam",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/assessment"));

        // After the index
        assertParseFailure(parser, "1/assessment Quiz /score 90",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/assessment"));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " /assessment Assignment 1 /score 97",
                "Use either an index, or both /name and /class.");
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
    public void parse_missingClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /assessment Assignment 1 /score 97",
                MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, " /class A1 /assessment Assignment 1 /score 97", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_bothIndexAndNameAndClass_failure() {
        assertParseFailure(parser,
                "1 /name Alex Yeoh /class A1 /assessment Assignment 1 /score 97",
                "Use either an index or name and class, not both.");
    }

    @Test
    public void parse_invalidScore_failure() {
        assertParseFailure(parser, "1 /assessment Assignment 1 /score A", Grade.MESSAGE_INVALID_SCORE);
        assertParseFailure(parser, "1 /assessment Assignment 1 /score -1", Grade.MESSAGE_INVALID_SCORE);
        assertParseFailure(parser, "1 /assessment Assignment 1 /score 100.1", Grade.MESSAGE_INVALID_SCORE);
    }

    @Test
    public void parse_invalidAssessmentName_failure() {
        assertParseFailure(parser, "1 /assessment Exam \u671f\u672b /score 90",
                Grade.MESSAGE_INVALID_ASSESSMENT_NAME);
    }

    @Test
    public void parse_assessmentNameWithComma_success() {
        assertParseSuccess(parser, "1 /assessment Test 1, Part A /score 90",
                new GradeByIndexCommand(Index.fromOneBased(1),
                        new Grade("Test 1, Part A", new BigDecimal("90"))));
    }

    @Test
    public void parse_scoreWithTooManyDecimalPlaces_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /score 90.12345",
                Grade.MESSAGE_TOO_MANY_DECIMAL_PLACES);
        assertParseSuccess(parser, "1 /assessment Quiz /score 90.1234",
                new GradeByIndexCommand(Index.fromOneBased(1), new Grade("Quiz", new BigDecimal("90.1234"))));
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 /assessment Quiz /score 90", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /assessment Exam /score 90",
                getErrorMessageForDuplicatePrefixes("/assessment"));
        assertParseFailure(parser, "1 /assessment Quiz /score 90 /score 91",
                getErrorMessageForDuplicatePrefixes("/score"));
    }

    @Test
    public void parse_duplicateAssessmentAndScorePrefixes_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /assessment Exam " + "/score 90 /score 95",
            getErrorMessageForDuplicatePrefixes("/assessment", "/score"));
    }

}
