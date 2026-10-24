package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_ASSESSMENT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
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

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, "1 /assessment Quiz 1",
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1"));
    }

    @Test
    public void parse_validNameAndClass_success() {
        DeleteGradeByNameAndClassCommand expected =
                new DeleteGradeByNameAndClassCommand("John Tan", "A1", "Quiz 1");
        assertParseSuccess(parser, " /name John Tan /class A1 /assessment Quiz 1", expected);
        assertParseSuccess(parser, " /assessment Quiz 1 /class A1 /name John Tan", expected);
    }

    @Test
    public void parse_prefixesInAnyCase_success() {
        DeleteGradeByNameAndClassCommand expected =
                new DeleteGradeByNameAndClassCommand("John Tan", "A1", "Quiz #1 (Part A)");

        assertParseSuccess(parser, " /Name John Tan /CLASS A1 /ASSESSMENT Quiz #1 (Part A)", expected);
    }

    @Test
    public void parse_missingAssessment_failure() {
        assertParseFailure(parser, "1", MESSAGE_MISSING_ASSESSMENT);
        assertParseFailure(parser, " /name John Tan /class A1", MESSAGE_MISSING_ASSESSMENT);
    }

    @Test
    public void parse_missingNameOrClass_failure() {
        assertParseFailure(parser, " /class A1 /assessment Quiz 1", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /name John Tan /assessment Quiz 1", MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_invalidIndexOrMixedForms_failure() {
        assertParseFailure(parser, "0 /assessment Quiz 1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 /name John Tan /class A1 /assessment Quiz 1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidAssessment_failure() {
        assertParseFailure(parser, "1 /assessment Quiz,1", Grade.MESSAGE_INVALID_ASSESSMENT_NAME);
    }

    @Test
    public void parse_prefixWithoutSpaceInAssessmentName_failure() {
        assertParseFailure(parser, "1 /assessment Quiz/name John",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "assessment name", "/name"));
    }

    @Test
    public void parse_duplicateOrUnknownParameter_failure() {
        assertParseFailure(parser, "1 /assessment Quiz /assessment Exam", MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, "1 /assessment Quiz /score 90", MESSAGE_UNKNOWN_PARAMETER);
    }
}
