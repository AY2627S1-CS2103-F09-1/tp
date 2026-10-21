package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewGradeCommand;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;

public class ViewGradeCommandParserTest {

    private final ViewGradeCommandParser parser = new ViewGradeCommandParser();

    @Test
    public void parse_validArguments_success() {
        ViewGradeCommand expected = new ViewGradeCommand(new Name("John Tan"), new ClassName("A1"));

        assertParseSuccess(parser, " /name John Tan /class A1", expected);
    }

    @Test
    public void parse_reversedArgumentOrder_success() {
        ViewGradeCommand expected = new ViewGradeCommand(new Name("John Tan"), new ClassName("A1"));

        assertParseSuccess(parser, " /class A1 /name John Tan", expected);
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
    public void parse_duplicateName_failure() {
        assertParseFailure(parser, " /name John /name Jane /class A1", MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_duplicateClassPrefix_failure() {
        assertParseFailure(parser, " /name John Tan /class A1 /class A2", MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, " /name John, Tan /class A1", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidClass_failure() {
        assertParseFailure(parser, " /name John Tan /class A1!", ClassName.MESSAGE_CONSTRAINTS);
    }

}
