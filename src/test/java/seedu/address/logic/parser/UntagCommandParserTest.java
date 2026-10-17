package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.GroupMembershipCommandParser.MESSAGE_MISSING_GROUP;
import static seedu.address.logic.parser.GroupMembershipCommandParser.MESSAGE_MISSING_MEMBERS;
import static seedu.address.logic.parser.GroupMembershipCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_DUPLICATE_MEMBER_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_MEMBER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_GROUP_NAME;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_MEMBER_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.tag.Tag;

/**
 * Tests the parts of the parsing that belong to {@code UntagCommandParser}. The rules that it shares with
 * {@code TagCommandParser} are tested in more detail in {@code TagCommandParserTest}.
 */
public class UntagCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE);

    private static final String GROUP_DESC = " /group Group A";
    private static final String MEMBERS_DESC = " /members 1,3";
    private static final Tag GROUP_A = new Tag("Group A");
    private static final List<Index> FIRST_AND_THIRD = List.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON);

    private UntagCommandParser parser = new UntagCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        UntagCommand expectedCommand = new UntagCommand(GROUP_A, FIRST_AND_THIRD);

        assertParseSuccess(parser, GROUP_DESC + MEMBERS_DESC, expectedCommand);

        // parameters in a different order
        assertParseSuccess(parser, MEMBERS_DESC + GROUP_DESC, expectedCommand);

        // whitespace only preamble
        assertParseSuccess(parser, "\t  \r  \n" + GROUP_DESC + MEMBERS_DESC, expectedCommand);
    }

    @Test
    public void parse_membersKeptInOrderGiven_success() {
        assertParseSuccess(parser, GROUP_DESC + " /members 3,1,2",
                new UntagCommand(GROUP_A,
                        List.of(INDEX_THIRD_PERSON, INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
    }

    @Test
    public void parse_whitespaceAroundValues_success() {
        assertParseSuccess(parser, "   /group     Group     A    /members    1 ,  3   ",
                new UntagCommand(GROUP_A, FIRST_AND_THIRD));
    }

    @Test
    public void parse_validInput_createsUntagCommandAndNotTagCommand() throws Exception {
        assertFalse(parser.parse(GROUP_DESC + MEMBERS_DESC).equals(new TagCommand(GROUP_A, FIRST_AND_THIRD)));
    }

    @Test
    public void parse_missingParameters_failure() {
        assertParseFailure(parser, MEMBERS_DESC, MESSAGE_MISSING_GROUP);
        assertParseFailure(parser, GROUP_DESC, MESSAGE_MISSING_MEMBERS);
        assertParseFailure(parser, "", MESSAGE_MISSING_GROUP);

        // parameters without a value
        assertParseFailure(parser, " /group" + MEMBERS_DESC, MESSAGE_MISSING_GROUP);
        assertParseFailure(parser, GROUP_DESC + " /members   ", MESSAGE_MISSING_MEMBERS);
    }

    @Test
    public void parse_nonEmptyPreamble_failureShowsUntagUsage() {
        assertParseFailure(parser, " preamble" + GROUP_DESC + MEMBERS_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " GroupA 1 3", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unrecognizedParameter_failure() {
        assertParseFailure(parser, " /name Group A" + MEMBERS_DESC, MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, GROUP_DESC + MEMBERS_DESC + " /tag friends", MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_repeatedParameter_failure() {
        assertParseFailure(parser, " /group Group B" + GROUP_DESC + MEMBERS_DESC, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, GROUP_DESC + " /members 2" + MEMBERS_DESC, MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_invalidGroupName_failure() {
        assertParseFailure(parser, " /group Group!" + MEMBERS_DESC, MESSAGE_INVALID_GROUP_NAME);
        assertParseFailure(parser, " /group " + "a".repeat(Tag.MAX_LENGTH + 1) + MEMBERS_DESC,
                MESSAGE_INVALID_GROUP_NAME);
    }

    @Test
    public void parse_invalidMembers_failure() {
        assertParseFailure(parser, GROUP_DESC + " /members 1,,3", MESSAGE_EMPTY_MEMBER);
        assertParseFailure(parser, GROUP_DESC + " /members 0",
                String.format(MESSAGE_INVALID_MEMBER_INDEX, "0"));
        assertParseFailure(parser, GROUP_DESC + " /members 1,3,1",
                String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, "1"));
    }
}
