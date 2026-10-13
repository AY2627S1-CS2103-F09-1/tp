package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteByIndexCommand;
import seedu.address.logic.commands.DeleteByNameAndClassCommand;
import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validIndex_returnsDeleteByIndexCommand() {
        assertParseSuccess(parser, "1", new DeleteByIndexCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, " 1", new DeleteByIndexCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "   1   ", new DeleteByIndexCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "a", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 abc", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_noArguments_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validNameAndClass_returnsDeleteByNameAndClassCommand() {
        DeleteByNameAndClassCommand expectedCommand = new DeleteByNameAndClassCommand("John Tan", "A1");

        assertParseSuccess(parser, " /name John Tan /class A1", expectedCommand);

        // parameters in a different order
        assertParseSuccess(parser, " /class A1 /name John Tan", expectedCommand);

        // extra whitespace around values is trimmed
        assertParseSuccess(parser, "   /name    John Tan    /class   A1  ", expectedCommand);
    }

    @Test
    public void parse_nameAndClassAreNotValidated_returnsDeleteByNameAndClassCommand() {
        // values that could never be a name or class are accepted, and simply will not be found
        assertParseSuccess(parser, " /name R@chel 2 /class A1/B2",
                new DeleteByNameAndClassCommand("R@chel 2", "A1/B2"));

        // case is kept as typed, it is ignored only when matching
        assertParseSuccess(parser, " /name jOhN tAn /class a1",
                new DeleteByNameAndClassCommand("jOhN tAn", "a1"));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, " /class A1", MESSAGE_MISSING_NAME);

        // a name with no value counts as missing
        assertParseFailure(parser, " /name /class A1", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /name    /class A1", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_missingClass_throwsParseException() {
        assertParseFailure(parser, " /name John Tan", MESSAGE_MISSING_CLASS);

        // a class with no value counts as missing
        assertParseFailure(parser, " /name John Tan /class", MESSAGE_MISSING_CLASS);
        assertParseFailure(parser, " /class /name John Tan", MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_missingNameAndClass_reportsMissingNameOnly() {
        assertParseFailure(parser, " /name /class", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /class /name", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_unrecognizedParameter_throwsParseException() {
        assertParseFailure(parser, " /name John Tan /class A1 /phone 91234567", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /class A1 /email a@b.com", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /class A1 /tag friend", MESSAGE_UNKNOWN_PARAMETER);

        // an unknown parameter is reported even if the rest is an index
        assertParseFailure(parser, "1 /phone 91234567", MESSAGE_UNKNOWN_PARAMETER);

        // matching is case-sensitive and requires the whole token to match
        assertParseFailure(parser, " /Name John Tan /class A1", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /classes A1", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /nameJohn /class A1", MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_repeatedParameter_throwsParseException() {
        assertParseFailure(parser, " /name John Tan /name Mary Lim /class A1", MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, " /name John Tan /class A1 /class B2", MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, " /name John Tan /class A1 /name John Tan /class A1",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_indexMixedWithParameters_throwsParseException() {
        assertParseFailure(parser, "1 /name John Tan /class A1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 /name John Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 /class A1", MESSAGE_INVALID_FORMAT);

        // text before the first parameter that is not an index
        assertParseFailure(parser, "John Tan /name John Tan /class A1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_multipleProblems_reportsInDocumentedOrder() {
        // unrecognized parameter comes before repeated parameter
        assertParseFailure(parser, " /name John /name Mary /phone 91234567", MESSAGE_UNKNOWN_PARAMETER);

        // repeated parameter comes before an index mixed with parameters
        assertParseFailure(parser, "1 /name John /name Mary", MESSAGE_DUPLICATE_PARAMETERS);

        // an index mixed with parameters comes before a missing parameter
        assertParseFailure(parser, "1 /name John Tan", MESSAGE_INVALID_FORMAT);
    }
}
