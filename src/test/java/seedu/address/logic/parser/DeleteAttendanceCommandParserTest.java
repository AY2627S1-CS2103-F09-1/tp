package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_DATE;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.DeleteAttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_DATE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_NONEXISTENT_DATE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteAttendanceByIndexCommand;
import seedu.address.logic.commands.DeleteAttendanceByNameAndClassCommand;
import seedu.address.logic.commands.DeleteAttendanceCommand;

public class DeleteAttendanceCommandParserTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 16);
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAttendanceCommand.MESSAGE_USAGE);

    private final DeleteAttendanceCommandParser parser = new DeleteAttendanceCommandParser();

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, "1 /date 16-09-2026",
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE));
    }

    @Test
    public void parse_validNameAndClass_success() {
        DeleteAttendanceByNameAndClassCommand expected =
                new DeleteAttendanceByNameAndClassCommand("John Tan", "A1", DATE);
        assertParseSuccess(parser, " /name John Tan /class A1 /date 16-09-2026", expected);
        assertParseSuccess(parser, " /date 16-09-2026 /class A1 /name John Tan", expected);
    }

    @Test
    public void parse_prefixesInAnyCase_success() {
        DeleteAttendanceByNameAndClassCommand expected =
                new DeleteAttendanceByNameAndClassCommand("John Tan", "A1", DATE);

        assertParseSuccess(parser, " /Name John Tan /CLASS A1 /DATE 16-09-2026", expected);
    }

    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "1", MESSAGE_MISSING_DATE);
        assertParseFailure(parser, " /name John Tan /class A1", MESSAGE_MISSING_DATE);
    }

    @Test
    public void parse_missingNameOrClass_failure() {
        assertParseFailure(parser, " /class A1 /date 16-09-2026", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /name John Tan /date 16-09-2026", MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_invalidIndexOrMixedForms_failure() {
        assertParseFailure(parser, "1 /name John Tan /class A1 /date 16-09-2026", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_indexNotPositiveInteger_failure() {
        assertParseFailure(parser, "0 /date 16-09-2026", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "abc /date 16-09-2026", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "99999999999 /date 16-09-2026", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_missingIndexAndName_failure() {
        assertParseFailure(parser, " /date 16-09-2026", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_prefixWithoutSpace_reportsThePrefix() {
        assertParseFailure(parser, "1/date 16-09-2026",
                String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/date"));
        assertParseFailure(parser, " /name John Tan/class A1 /date 16-09-2026",
                String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/class"));
    }

    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, "1 /date 31-02-2026",
                String.format(MESSAGE_NONEXISTENT_DATE, "31-02-2026"));
        assertParseFailure(parser, "1 /date 2026-09-16", MESSAGE_INVALID_DATE);
    }

    @Test
    public void parse_duplicateOrUnknownParameter_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026 /date 17-09-2026",
                getErrorMessageForDuplicatePrefixes("/date"));
        assertParseFailure(parser, "1 /date 16-09-2026 /status present", MESSAGE_UNKNOWN_PARAMETER);
    }
}
