package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // EP_1: Parser input is null
    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    // EP_2: Valid attendance deletion identified by index
    @Test
    public void parse_noArguments_reportsUsage() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, "1 /date 16-09-2026",
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE));
    }

    // EP_3_1: Valid attendance deletion identified by name and class
    // EP_3_2: Name, class, and date parameters are accepted in a different order
    @Test
    public void parse_validNameAndClass_success() {
        DeleteAttendanceByNameAndClassCommand expected =
                new DeleteAttendanceByNameAndClassCommand("John Tan", "A1", DATE);
        assertParseSuccess(parser, " /name John Tan /class A1 /date 16-09-2026", expected);
        assertParseSuccess(parser, " /date 16-09-2026 /class A1 /name John Tan", expected);
    }

    // EP_4: Parameter prefixes are recognised regardless of capitalisation
    @Test
    public void parse_prefixesInAnyCase_success() {
        DeleteAttendanceByNameAndClassCommand expected =
                new DeleteAttendanceByNameAndClassCommand("John Tan", "A1", DATE);

        assertParseSuccess(parser, " /Name John Tan /CLASS A1 /DATE 16-09-2026", expected);
    }

    // EP_5_1: Date is missing when student is identified by index
    // EP_5_2: Date is missing when student is identified by name and class
    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "1", MESSAGE_MISSING_DATE);
        assertParseFailure(parser, " /name John Tan /class A1", MESSAGE_MISSING_DATE);
    }

    // EP_6_1: Name is missing while class and date are present
    // EP_6_2: Class is missing while name and date are present
    @Test
    public void parse_missingNameOrClass_failure() {
        // Isolate each missing input while keeping other required inputs valid
        assertParseFailure(parser, " /class A1 /date 16-09-2026", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, " /name John Tan /date 16-09-2026", MESSAGE_MISSING_CLASS);
    }

    // EP_7: Index and name with class are supplied together
    @Test
    public void parse_mixedIdentificationForms_failure() {
        assertParseFailure(parser, "1 /name John Tan /class A1 /date 16-09-2026",
                ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
    }

    // EP_8_1: Index is zero
    // Boundary Value: 0, one below the smallest valid index 1
    // EP_8_2: Index contains non numeric characters
    // EP_8_3: Index exceeds the integer range
    @Test
    public void parse_indexNotPositiveInteger_failure() {
        // Pair each invalid index with a valid date
        assertParseFailure(parser, "0 /date 16-09-2026", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "abc /date 16-09-2026", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "99999999999 /date 16-09-2026", MESSAGE_INVALID_INDEX);
    }

    // EP_9: Student identifier is missing
    @Test
    public void parse_missingIdentifier_failure() {
        assertParseFailure(parser, " /date 16-09-2026", MESSAGE_INVALID_FORMAT);
    }

    // EP_10: A parameter prefix is joined to the preceding value without a space
    @Test
    public void parse_prefixWithoutSpace_reportsThePrefix() {
        assertParseFailure(parser, "1/date 16-09-2026",
                String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/date"));
        assertParseFailure(parser, " /name John Tan/class A1 /date 16-09-2026",
                String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/class"));
    }

    // EP_11_1: Date has the required shape but is not a real calendar date
    // EP_11_2: Date uses the wrong format
    @Test
    public void parse_invalidDate_failure() {
        // Pair each invalid date with a valid index
        assertParseFailure(parser, "1 /date 31-02-2026",
                String.format(MESSAGE_NONEXISTENT_DATE, "31-02-2026"));
        assertParseFailure(parser, "1 /date 2026-09-16", MESSAGE_INVALID_DATE);
    }

    // EP_12_1: Date parameter is repeated
    // EP_12_2: An unrecognised parameter is supplied
    @Test
    public void parse_duplicateOrUnknownParameter_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026 /date 17-09-2026",
                getErrorMessageForDuplicatePrefixes("/date"));
        assertParseFailure(parser, "1 /date 16-09-2026 /status present", MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_repeatedParameterAndStuckParameter_reportsRepeatedParameter() {
        assertParseFailure(parser, "1/date 16-09-2026 /date 16-09-2026 /date 17-09-2026",
                getErrorMessageForDuplicatePrefixes("/date"));
    }

    @Test
    public void parse_missingValuesBeforeInvalidValues_failure() {
        assertParseFailure(parser, "abc", MESSAGE_MISSING_DATE);
    }
}
