package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ViewAttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewAttendanceCommand;

public class ViewAttendanceCommandParserTest {

    private final ViewAttendanceCommandParser parser = new ViewAttendanceCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_success() {
        ViewAttendanceCommand expected = new ViewAttendanceCommand("John Tan", "A1");

        assertParseSuccess(parser, " /name John Tan /class A1", expected);
    }

    @Test
    public void parse_reversedOrderAndWhitespace_success() {
        ViewAttendanceCommand expected = new ViewAttendanceCommand("John Tan", "A1");

        assertParseSuccess(parser, " /class  A1  /name  John Tan  ", expected);
    }

    @Test
    public void parse_nameAndClassNotValidForAdding_success() {
        ViewAttendanceCommand expected = new ViewAttendanceCommand("@@@", "!!!");

        assertParseSuccess(parser, " /name @@@ /class !!!", expected);
    }

    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, " /class A1", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_missingClass_failure() {
        assertParseFailure(parser, " /name John Tan", MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_missingBothFields_reportsMissingName() {
        assertParseFailure(parser, "", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_blankName_reportsMissingName() {
        assertParseFailure(parser, " /name   /class A1", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_blankClass_reportsMissingClass() {
        assertParseFailure(parser, " /name John Tan /class   ", MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_duplicateNamePrefix_failure() {
        assertParseFailure(parser,
                " /name John Tan /name Jane Doe /class A1",
                getErrorMessageForDuplicatePrefixes("/name"));
    }

    @Test
    public void parse_duplicateClassPrefix_failure() {
        assertParseFailure(parser,
                " /name John Tan /class A1 /class A2",
                getErrorMessageForDuplicatePrefixes("/class"));
    }

    @Test
    public void parse_unknownParameter_failure() {
        assertParseFailure(parser, " /name John Tan /class A1 /foo bar", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /class A1 /email john@example.com",
                MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_unknownParameterAndRepeatedParameter_reportsUnknownParameter() {
        assertParseFailure(parser, " /name John Tan /name Jane Doe /class A1 /foo bar",
                MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_textBeforeFirstParameter_failure() {
        assertParseFailure(parser, " John /name John Tan /class A1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAttendanceCommand.MESSAGE_USAGE));
    }

}
