package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_ASSESSMENT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteGradeCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteGradeByIndexCommand;
import seedu.address.logic.commands.DeleteGradeByNameAndClassCommand;
import seedu.address.logic.commands.DeleteGradeCommand;
import seedu.address.model.person.Grade;

public class DeleteGradeCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteGradeCommand.MESSAGE_USAGE);

    private final DeleteGradeCommandParser parser = new DeleteGradeCommandParser();

    // EP_1: Parser input is null
    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    // EP_2: Valid grade deletion identified by index
    @Test
    public void parse_noArguments_reportsUsage() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, "1 /assessment Quiz 1",
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1"));
    }

    // EP_3_1: Valid grade deletion identified by name and class
    // EP_3_2: Name, class, and assessment parameters are accepted in a different order
    @Test
    public void parse_validNameAndClass_success() {
        DeleteGradeByNameAndClassCommand expected =
                new DeleteGradeByNameAndClassCommand("John Tan", "A1", "Quiz 1");
        assertParseSuccess(parser, " /name John Tan /class A1 /assessment Quiz 1", expected);
        assertParseSuccess(parser, " /assessment Quiz 1 /class A1 /name John Tan", expected);
    }


    // EP_4: Parameter prefixes are recognised regardless of capitalisation
    @Test
    public void parse_prefixesInAnyCase_success() {
        DeleteGradeByNameAndClassCommand expected =
                new DeleteGradeByNameAndClassCommand("John Tan", "A1", "Quiz #1 (Part A)");

        assertParseSuccess(parser, " /Name John Tan /CLASS A1 /ASSESSMENT Quiz #1 (Part A)", expected);
    }

    // EP_5_1: Assessment parameter is missing when student is identified by index
    // EP_5_2: Assessment parameter is missing when student is identified by name and class
    @Test
    public void parse_missingAssessment_failure() {
        assertParseFailure(parser, "1", MESSAGE_MISSING_ASSESSMENT);
        assertParseFailure(parser, " /name John Tan /class A1", MESSAGE_MISSING_ASSESSMENT);
    }

    // EP_6_1: Name is missing while class and assessment are present
    // EP_6_2: Class is missing while name and assessment are present
    @Test
    public void parse_missingNameOrClass_failure() {
        // Isolate each missing input while keeping other required inputs valid
        assertParseFailure(parser, " /class A1 /assessment Quiz 1", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /name John Tan /assessment Quiz 1", MESSAGE_MISSING_CLASS);
    }


    // EP_7: Index and name with class are supplied together
    @Test
    public void parse_mixedIdentificationForms_failure() {
        assertParseFailure(parser, "1 /name John Tan /class A1 /assessment Quiz 1",
                ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
    }

    // EP_8_1: Index is zero
    // Boundary Value: 0, one below the smallest valid index 1
    // EP_8_2: Index contains letters
    // EP_8_3: Index exceeds the integer range
    @Test
    public void parse_indexNotPositiveInteger_failure() {
        // Pair each invalid index with a valid assessment name
        assertParseFailure(parser, "0 /assessment Quiz 1", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "abc /assessment Quiz 1", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "99999999999 /assessment Quiz 1", ParserUtil.MESSAGE_INVALID_INDEX);
    }


    // EP_9: Student identifier is missing
    @Test
    public void parse_missingIdentifier_failure() {
        assertParseFailure(parser, " /assessment Quiz 1", MESSAGE_INVALID_FORMAT);
    }

    // EP_10: Assessment name contains invalid characters
    @Test
    public void parse_invalidAssessment_failure() {
        assertParseFailure(parser, "1 /assessment Quiz \u671f\u672b", Grade.MESSAGE_INVALID_ASSESSMENT_NAME);
    }


    // EP_11: Valid assessment name contains a comma
    @Test
    public void parse_assessmentNameWithComma_success() {
        assertParseSuccess(parser, "1 /assessment Test 1, Part A",
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Test 1, Part A"));
    }

    // EP_12: A parameter prefix is joined to the assessment value without a space
    @Test
    public void parse_prefixWithoutSpaceInAssessmentName_failure() {
        assertParseFailure(parser, "1 /assessment Quiz/name John",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/name"));
    }


    // EP_13_1: Assessment parameter is repeated
    // EP_13_2: An unrecognised parameter is supplied
    @Test
    public void parse_duplicateOrUnknownParameter_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /assessment Exam",
                getErrorMessageForDuplicatePrefixes("/assessment"));
        assertParseFailure(parser, "1 /assessment Quiz /score 90", MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_repeatedParameterAndStuckParameter_reportsRepeatedParameter() {
        assertParseFailure(parser, "1/assessment Quiz /assessment Quiz /assessment Exam",
                getErrorMessageForDuplicatePrefixes("/assessment"));
    }

    @Test
    public void parse_missingValuesBeforeInvalidValues_failure() {
        assertParseFailure(parser, "abc", MESSAGE_MISSING_ASSESSMENT);
    }
}
