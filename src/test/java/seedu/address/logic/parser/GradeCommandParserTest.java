package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Name;

public class GradeCommandParserTest {

    private final GradeCommandParser parser = new GradeCommandParser();

    @Test
    public void parse_validIndexArguments_success() {
        GradeCommand expected = new GradeCommand(
                Index.fromOneBased(5), new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, "5 /assessment Assignment 1 /score 97", expected);
    }

    @Test
    public void parse_validNameAndClassArguments_success() {
        String userInput = " /name Alex Yeoh /class A1 /assessment Assignment 1 /score 97";

        GradeCommand expectedCommand = new GradeCommand(
                new Name("Alex Yeoh"),
                new ClassName("A1"),
                new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_nameAndClassFormatting_success() {
        String userInput =
                " /name   alex   yeoh  /class  a1 /assessment Assignment 1 /score 97";

        GradeCommand expectedCommand = new GradeCommand(
                new Name("alex   yeoh"),
                new ClassName("a1"),
                new Grade("Assignment 1", new BigDecimal("97")));

        assertParseSuccess(parser, userInput, expectedCommand);
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
        assertParseFailure(parser, " /name Alex Yeoh /assessment Assignment 1 /score 97", MESSAGE_MISSING_CLASS);
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

    @Test
    public void parse_duplicateAssessmentAndScorePrefixes_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /assessment Exam " + "/score 90 /score 95",
            MESSAGE_DUPLICATE_PARAMETERS);
    }

}
