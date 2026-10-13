package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;

public class ViewAttendanceCommandParserTest {

    private final ViewAttendanceCommandParser parser = new ViewAttendanceCommandParser();

    @Test
    public void parse_validArguments_success() {
        ViewAttendanceCommand expected = new ViewAttendanceCommand(
                new Name("John Tan"), new ClassName("A1"));

        assertParseSuccess(parser, " /name John Tan /class A1", expected);
    }

    @Test
    public void parse_reversedOrderAndWhitespace_success() {
        ViewAttendanceCommand expected = new ViewAttendanceCommand(
                new Name("John Tan"), new ClassName("A1"));

        assertParseSuccess(parser, " /class  A1  /name  John Tan  ", expected);
    }

    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, " /class A1", "Missing student name.");
    }

    @Test
    public void parse_missingClass_failure() {
        assertParseFailure(parser, " /name John Tan", "Missing class.");
    }

    @Test
    public void parse_missingBothFields_reportsMissingName() {
        assertParseFailure(parser, "", "Missing student name.");
    }

    @Test
    public void parse_duplicateNamePrefix_failure() {
        assertParseFailure(parser,
                " /name John Tan /name Jane Doe /class A1",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_duplicateClassPrefix_failure() {
        assertParseFailure(parser,
                " /name John Tan /class A1 /class A2",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, " /name John@Tan /class A1", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidClass_failure() {
        assertParseFailure(parser, " /name John Tan /class A1!", ClassName.MESSAGE_CONSTRAINTS);
    }

}
