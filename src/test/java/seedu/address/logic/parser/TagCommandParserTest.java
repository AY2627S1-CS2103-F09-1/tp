package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_DUPLICATE_MEMBER_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_MEMBER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_GROUP_NAME;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_MEMBER_INDEX;
import static seedu.address.logic.parser.TagCommandParser.MESSAGE_MISSING_GROUP;
import static seedu.address.logic.parser.TagCommandParser.MESSAGE_MISSING_MEMBERS;
import static seedu.address.logic.parser.TagCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE);

    private static final String GROUP_DESC = " /group Group A";
    private static final String MEMBERS_DESC = " /members 1,3";
    private static final Tag GROUP_A = new Tag("Group A");
    private static final List<Index> FIRST_AND_THIRD = List.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON);

    private TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        TagCommand expectedCommand = new TagCommand(GROUP_A, FIRST_AND_THIRD);

        assertParseSuccess(parser, GROUP_DESC + MEMBERS_DESC, expectedCommand);

        // parameters in a different order
        assertParseSuccess(parser, MEMBERS_DESC + GROUP_DESC, expectedCommand);

        // whitespace only preamble
        assertParseSuccess(parser, "\t  \r  \n" + GROUP_DESC + MEMBERS_DESC, expectedCommand);
    }

    @Test
    public void parse_singleMember_success() {
        assertParseSuccess(parser, GROUP_DESC + " /members 2",
                new TagCommand(GROUP_A, List.of(INDEX_SECOND_PERSON)));
    }

    @Test
    public void parse_membersKeptInOrderGiven_success() {
        assertParseSuccess(parser, GROUP_DESC + " /members 3,1,2",
                new TagCommand(GROUP_A,
                        List.of(INDEX_THIRD_PERSON, INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
    }

    @Test
    public void parse_whitespaceAroundMembers_success() {
        TagCommand expectedCommand = new TagCommand(GROUP_A, FIRST_AND_THIRD);

        assertParseSuccess(parser, GROUP_DESC + " /members    1 ,  3   ", expectedCommand);
        assertParseSuccess(parser, GROUP_DESC + " /members 1, 3", expectedCommand);
    }

    @Test
    public void parse_groupNameWithExtraSpaces_success() {
        assertParseSuccess(parser, "   /group     Group     A    " + MEMBERS_DESC,
                new TagCommand(GROUP_A, FIRST_AND_THIRD));
    }

    @Test
    public void parse_groupNameWithAllowedSymbols_success() {
        assertParseSuccess(parser, " /group team-1_Alpha 2" + MEMBERS_DESC,
                new TagCommand(new Tag("team-1_Alpha 2"), FIRST_AND_THIRD));

        // a group name made only of digits is fine since it follows its prefix
        assertParseSuccess(parser, " /group 123" + MEMBERS_DESC,
                new TagCommand(new Tag("123"), FIRST_AND_THIRD));

        // longest allowed name
        String longestName = "a".repeat(Tag.MAX_LENGTH);
        assertParseSuccess(parser, " /group " + longestName + MEMBERS_DESC,
                new TagCommand(new Tag(longestName), FIRST_AND_THIRD));
    }

    @Test
    public void parse_groupNameKeepsTypedCasing_success() throws Exception {
        TagCommand parsed = parser.parse(" /group gROUP a" + MEMBERS_DESC);

        assertEquals(new TagCommand(GROUP_A, FIRST_AND_THIRD), parsed);
        assertTrue(parsed.toString().contains("[gROUP a]"));
    }

    @Test
    public void parse_missingGroup_failure() {
        assertParseFailure(parser, MEMBERS_DESC, MESSAGE_MISSING_GROUP);

        // group parameter without a value
        assertParseFailure(parser, " /group" + MEMBERS_DESC, MESSAGE_MISSING_GROUP);
        assertParseFailure(parser, " /group   " + MEMBERS_DESC, MESSAGE_MISSING_GROUP);
    }

    @Test
    public void parse_missingMembers_failure() {
        assertParseFailure(parser, GROUP_DESC, MESSAGE_MISSING_MEMBERS);

        // members parameter without a value
        assertParseFailure(parser, GROUP_DESC + " /members", MESSAGE_MISSING_MEMBERS);
        assertParseFailure(parser, GROUP_DESC + " /members   ", MESSAGE_MISSING_MEMBERS);
    }

    @Test
    public void parse_noArguments_reportsMissingGroupOnly() {
        assertParseFailure(parser, "", MESSAGE_MISSING_GROUP);
        assertParseFailure(parser, "     ", MESSAGE_MISSING_GROUP);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, " preamble" + GROUP_DESC + MEMBERS_DESC, MESSAGE_INVALID_FORMAT);

        // the format without prefixes is not supported
        assertParseFailure(parser, " GroupA 1 3", MESSAGE_INVALID_FORMAT);

        // group given without its prefix
        assertParseFailure(parser, " Group A" + MEMBERS_DESC, MESSAGE_INVALID_FORMAT);

        // members given without their prefix
        assertParseFailure(parser, " 1,3" + GROUP_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unrecognizedParameter_failure() {
        assertParseFailure(parser, " /name Group A" + MEMBERS_DESC, MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, GROUP_DESC + MEMBERS_DESC + " /tag friends", MESSAGE_UNKNOWN_PARAMETER);

        // matching is case-sensitive and requires the whole token to match
        assertParseFailure(parser, " /Group Group A" + MEMBERS_DESC, MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, GROUP_DESC + " /memberss 1", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /groupA" + MEMBERS_DESC, MESSAGE_UNKNOWN_PARAMETER);

        // reported even when a required parameter is missing
        assertParseFailure(parser, " /name Group A", MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_repeatedParameter_failure() {
        // multiple groups
        assertParseFailure(parser, " /group Group B" + GROUP_DESC + MEMBERS_DESC,
                MESSAGE_DUPLICATE_PARAMETERS);

        // multiple members
        assertParseFailure(parser, GROUP_DESC + " /members 2" + MEMBERS_DESC, MESSAGE_DUPLICATE_PARAMETERS);

        // both repeated
        assertParseFailure(parser, GROUP_DESC + MEMBERS_DESC + GROUP_DESC + MEMBERS_DESC,
                MESSAGE_DUPLICATE_PARAMETERS);

        // reported even if one of the values is invalid
        assertParseFailure(parser, " /group !!" + GROUP_DESC + MEMBERS_DESC, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, GROUP_DESC + " /members x" + MEMBERS_DESC, MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_invalidGroupName_failure() {
        assertParseFailure(parser, " /group Group!" + MEMBERS_DESC, MESSAGE_INVALID_GROUP_NAME);
        assertParseFailure(parser, " /group Group/A" + MEMBERS_DESC, MESSAGE_INVALID_GROUP_NAME);

        // too long name
        assertParseFailure(parser, " /group " + "a".repeat(Tag.MAX_LENGTH + 1) + MEMBERS_DESC,
                MESSAGE_INVALID_GROUP_NAME);
    }

    @Test
    public void parse_emptyMemberEntry_failure() {
        assertParseFailure(parser, GROUP_DESC + " /members 1,3,", MESSAGE_EMPTY_MEMBER);
        assertParseFailure(parser, GROUP_DESC + " /members ,1,3", MESSAGE_EMPTY_MEMBER);
        assertParseFailure(parser, GROUP_DESC + " /members 1,,3", MESSAGE_EMPTY_MEMBER);
        assertParseFailure(parser, GROUP_DESC + " /members 1, ,3", MESSAGE_EMPTY_MEMBER);
    }

    @Test
    public void parse_memberNotPositiveInteger_failure() {
        assertParseFailure(parser, GROUP_DESC + " /members abc",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "abc"));
        assertParseFailure(parser, GROUP_DESC + " /members 1,0,3",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "0"));
        assertParseFailure(parser, GROUP_DESC + " /members -1",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "-1"));
        assertParseFailure(parser, GROUP_DESC + " /members 1.5",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "1.5"));
        assertParseFailure(parser, GROUP_DESC + " /members 99999999999",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "99999999999"));

        // indices separated by spaces instead of commas
        assertParseFailure(parser, GROUP_DESC + " /members 1 3",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "1 3"));
    }

    @Test
    public void parse_repeatedMemberIndex_failure() {
        assertParseFailure(parser, GROUP_DESC + " /members 1,1",
                String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, "1"));
        assertParseFailure(parser, GROUP_DESC + " /members 1,3,1,3",
                String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, "1, 3"));
    }

    @Test
    public void parse_multipleProblems_reportsInFixedOrder() {
        // a missing parameter is reported before any invalid value
        assertParseFailure(parser, " /group !!", MESSAGE_MISSING_MEMBERS);
        assertParseFailure(parser, " /members x", MESSAGE_MISSING_GROUP);

        // invalid group name before invalid members
        assertParseFailure(parser, " /group !! /members x", MESSAGE_INVALID_GROUP_NAME);

        // invalid entry before repeated index
        assertParseFailure(parser, GROUP_DESC + " /members 1,1,x",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "x"));
    }
}
