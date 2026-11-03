package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
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

    // EP_1_1: Valid positive index with no surrounding spaces
    // EP_1_2: Valid positive index with leading whitespace
    // EP_1_3: Valid positive index with leading and trailing whitespace
    // Boundary Value: 1, the smallest valid index
    @Test
    public void parse_validIndex_returnsDeleteByIndexCommand() {
        assertParseSuccess(parser, "1", new DeleteByIndexCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, " 1", new DeleteByIndexCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "   1   ", new DeleteByIndexCommand(INDEX_FIRST_PERSON));
    }

    // EP_2_1: Index contains letters
    // EP_2_2: Index is zero
    // Boundary Value: 0, one below the smallest valid index 1
    // EP_2_3: Index is negative
    // EP_2_4: Index has extra text after the number
    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "a", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "0", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "1 abc", MESSAGE_INVALID_INDEX);
    }

    // EP_3_1: Index exceeds the maximum integer value
    // Boundary Value: Integer.MAX_VALUE + 1
    // EP_3_2: Index contains an extremely long sequence of digits
    @Test
    public void parse_outOfRangeIndex_throwsParseException() {
        assertParseFailure(parser, Long.toString(Integer.MAX_VALUE + 1L), MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "9".repeat(1000), MESSAGE_INVALID_INDEX);
    }


    // EP_4: Parser input is null
    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }


    // EP_5_1: No index or name and class are supplied
    // EP_5_2: Input contains only whitespace
    @Test
    public void parse_noArguments_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    // EP_6_1: Valid name and class in the expected order
    // EP_6_2: Valid name and class in a different order
    // EP_6_3: Extra whitespace around parameter values is trimmed
    @Test
    public void parse_validNameAndClass_returnsDeleteByNameAndClassCommand() {
        DeleteByNameAndClassCommand expectedCommand = new DeleteByNameAndClassCommand("John Tan", "A1");

        assertParseSuccess(parser, " /name John Tan /class A1", expectedCommand);

        // parameters in a different order
        assertParseSuccess(parser, " /class A1 /name John Tan", expectedCommand);

        // extra whitespace around values is trimmed
        assertParseSuccess(parser, "   /name    John Tan    /class   A1  ", expectedCommand);
    }

    // EP_7: Parameter prefixes are recognised regardless of capitalisation
    @Test
    public void parse_prefixesInAnyCase_returnsDeleteByNameAndClassCommand() {
        assertParseSuccess(parser, " /NAME John Tan /Class A1",
                new DeleteByNameAndClassCommand("John Tan", "A1"));
    }

    // EP_8_1: Name and class values are accepted without contact field validation
    // EP_8_2: Name and class capitalisation is preserved by the parser
    @Test
    public void parse_nameAndClassAreNotValidated_returnsDeleteByNameAndClassCommand() {
        // values that could never be a name or class are accepted, and simply will not be found
        assertParseSuccess(parser, " /name R@chel 2 /class A1/B2",
                new DeleteByNameAndClassCommand("R@chel 2", "A1/B2"));

        // case is kept as typed, it is ignored only when matching
        assertParseSuccess(parser, " /name jOhN tAn /class a1",
                new DeleteByNameAndClassCommand("jOhN tAn", "a1"));
    }

    // EP_9_1: Name parameter is missing
    // EP_9_2: Name parameter has no value
    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, " /class A1", MESSAGE_MISSING_NAME);

        // a name with no value counts as missing
        assertParseFailure(parser, " /name /class A1", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /name    /class A1", MESSAGE_MISSING_NAME);
    }

    // EP_9_3: Class parameter is missing
    // EP_9_4: Class parameter has no value
    @Test
    public void parse_missingClass_throwsParseException() {
        assertParseFailure(parser, " /name John Tan", MESSAGE_MISSING_CLASS);

        // a class with no value counts as missing
        assertParseFailure(parser, " /name John Tan /class", MESSAGE_MISSING_CLASS);
        assertParseFailure(parser, " /class /name John Tan", MESSAGE_MISSING_CLASS);
    }

    // EP_9_5: Both name and class have no value
    @Test
    public void parse_missingNameAndClass_reportsMissingNameOnly() {
        assertParseFailure(parser, " /name /class", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /class /name", MESSAGE_MISSING_NAME);
    }

    // EP_10_1: Removed phone parameter is unrecognised
    // EP_10_2: Email parameter is unrecognised
    // EP_10_3: Tag parameter is unrecognised
    // EP_10_4: Unknown parameter is reported when an index is also supplied
    // EP_10_5: Longer tokens beginning with a known prefix are unrecognised
    // EP_10_6: A known prefix joined directly to its value is unrecognised
    @Test
    public void parse_unrecognizedParameter_throwsParseException() {
        assertParseFailure(parser, " /name John Tan /class A1 /phone 91234567", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /class A1 /email a@b.com", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /class A1 /tag friend", MESSAGE_UNKNOWN_PARAMETER);

        // an unknown parameter is reported even if the rest is an index
        assertParseFailure(parser, "1 /phone 91234567", MESSAGE_UNKNOWN_PARAMETER);

        // matching requires the whole token to match
        assertParseFailure(parser, " /NAMES John Tan /class A1", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /name John Tan /classes A1", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /nameJohn /class A1", MESSAGE_UNKNOWN_PARAMETER);
    }

    // EP_11_1: Name parameter is repeated
    // EP_11_2: Class parameter is repeated
    // EP_11_3: Both name and class parameters are repeated
    @Test
    public void parse_repeatedParameter_throwsParseException() {
        assertParseFailure(parser, " /name John Tan /name Mary Lim /class A1",
                getErrorMessageForDuplicatePrefixes("/name"));
        assertParseFailure(parser, " /name John Tan /class A1 /class B2",
                getErrorMessageForDuplicatePrefixes("/class"));
        assertParseFailure(parser, " /name John Tan /class A1 /name John Tan /class A1",
                getErrorMessageForDuplicatePrefixes("/name", "/class"));
    }

    // EP_12_1: Class prefix is joined to the preceding name value
    // EP_12_2: Name prefix is joined to the preceding class value
    // EP_12_3: Joined prefix detection ignores capitalisation
    @Test
    public void parse_parameterMissingSpaceBeforeIt_throwsParseException() {
        assertParseFailure(parser, " /name John/class A1 /class A2",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/class"));
        assertParseFailure(parser, " /name John Tan /class A1/name Mary",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/name"));

        // matching is case-insensitive
        assertParseFailure(parser, " /name John/CLASS A1 /class A2",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/CLASS"));
    }

    // EP_13: Slash within a name value is not treated as a parameter prefix
    @Test
    public void parse_slashInsideName_returnsDeleteByNameAndClassCommand() {
        assertParseSuccess(parser, " /name Kevin s/o Kumar /class A1",
                new DeleteByNameAndClassCommand("Kevin s/o Kumar", "A1"));
    }

    // EP_14_1: Index is supplied together with name and class
    // EP_14_2: Index is supplied together with name only
    // EP_14_3: Index is supplied together with class only
    // EP_14_4: Non index text appears before the name and class parameters
    @Test
    public void parse_indexMixedWithParameters_throwsParseException() {
        assertParseFailure(parser, "1 /name John Tan /class A1", ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
        assertParseFailure(parser, "1 /name John Tan", ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
        assertParseFailure(parser, "1 /class A1", ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);

        // text before the first parameter that is not an index
        assertParseFailure(parser, "John Tan /name John Tan /class A1",
                ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
    }

    // EP_15_1: Unrecognised parameter error takes priority over repeated parameter error
    // EP_15_2: Repeated parameter error takes priority over mixed identification error
    // EP_15_3: Mixed identification error takes priority over missing parameter error
    @Test
    public void parse_multipleProblems_reportsInDocumentedOrder() {
        // unrecognized parameter comes before repeated parameter
        assertParseFailure(parser, " /name John /name Mary /phone 91234567", MESSAGE_UNKNOWN_PARAMETER);

        // repeated parameter comes before an index mixed with parameters
        assertParseFailure(parser, "1 /name John /name Mary", getErrorMessageForDuplicatePrefixes("/name"));

        // repeated parameter comes before a parameter that is missing a preceding space
        assertParseFailure(parser, " /name John/class A1 /name Mary /class A2",
                getErrorMessageForDuplicatePrefixes("/name"));

        // an index mixed with parameters comes before a missing parameter
        assertParseFailure(parser, "1 /name John Tan", ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
    }

}
