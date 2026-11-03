package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceByIndexCommand;
import seedu.address.logic.commands.AttendanceByNameAndClassCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.model.person.Attendance;

public class AttendanceCommandParserTest {

    private final AttendanceCommandParser parser = new AttendanceCommandParser();

    // EP_1: Parser input is null
    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    // EP_2: Input contains invalid parameter
    @Test
    public void parse_noArguments_reportsUsage() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "   ", expected);
    }

    @Test
    public void parse_unknownParameter_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026 /status absent /foo bar",
                AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, "1 /foo bar", AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    // EP_3: Unknown parameter takes priority over a repeated parameter
    @Test
    public void parse_unknownParameterAndRepeatedParameter_reportsUnknownParameter() {
        assertParseFailure(parser, "1 /date 16-09-2026 /date 17-09-2026 /status absent /foo bar",
                AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    // EP_4: Accepted parameter prefixes are recognised regardless of capitalisation
    @Test
    public void parse_prefixesInAnyCase_success() {
        AttendanceCommand expectedCommand = new AttendanceByIndexCommand(
                Index.fromOneBased(1),
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, "1 /DATE 16-09-2026 /Status absent", expectedCommand);
    }

    // EP_5_1: Valid attendance arguments identify the student by index
    @Test
    public void parse_validIndexArguments_success() {
        String userInput = "1 /date 16-09-2026 /status absent";
        AttendanceCommand expectedCommand = new AttendanceByIndexCommand(
                Index.fromOneBased(1),
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // EP_5_2: Valid attendance arguments identify the student by name and class
    @Test
    public void parse_validNameAndClassArguments_success() {
        String userInput = " /name Alex Yeoh /class A1 /date 16-09-2026 /status absent";

        AttendanceCommand expectedCommand = new AttendanceByNameAndClassCommand(
                "Alex Yeoh",
                "A1",
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // EP_5_3: Extra spacing and capitalisation in name, class, and status values are accepted
    @Test
    public void parse_nameAndClassFormatting_success() {
        String userInput =
                " /name   alex   yeoh  /class  a1 /date 16-09-2026 /status LaTe";

        AttendanceCommand expectedCommand = new AttendanceByNameAndClassCommand(
                "alex   yeoh",
                "a1",
                LocalDate.of(2026, 9, 16),
                Attendance.Status.LATE);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // EP_5_4: Name and class values that cannot identify a valid contact are still parsed
    @Test
    public void parse_nameAndClassThatNoContactCanHave_success() {
        AttendanceCommand expectedCommand = new AttendanceByNameAndClassCommand(
                "@@@,",
                "!!!",
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, " /name @@@,  /class !!! /date 16-09-2026 /status absent",
                expectedCommand);
    }

    // EP_6: Unknown parameter message lists all accepted parameters
    @Test
    public void parse_unknownParameterMessage_listsEveryAcceptedParameter() {
        assertEquals("Unknown parameter. Use /name, /class, /date or /status."
                        + " A word that starts with / is always read as a parameter.",
                AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    // EP_7_1: Student identifier is missing
    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " /date 16-09-2026 /status absent",
            "Use either an index, or both /name and /class.");
    }

    // EP_7_2: Date is missing when student is identified by index
    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "1 /status absent",
            "Missing date. Please provide the date of attendance.");
    }

    // EP_7_3: Date is missing when student is identified by name and class
    @Test
    public void parse_missingDateWithNameAndClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /class A1 /status absent",
            "Missing date. Please provide the date of attendance.");
    }

    // EP_7_4: Status is missing when student is identified by index
    @Test
    public void parse_missingStatus_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026",
            "Missing status. Please provide the attendance status.");
    }

    // EP_7_5: Status is missing when student is identified by name and class
    @Test
    public void parse_missingStatusWithNameAndClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /class A1 /date 16-09-2026",
            "Missing status. Please provide the attendance status.");
    }

    // EP_7_6: Class is missing when student is identified by name and class
    @Test
    public void parse_missingClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /date 16-09-2026 /status absent", MESSAGE_MISSING_CLASS);
    }

    // EP_7_7: Name is missing when student is identified by name and class
    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, " /class A1 /date 16-09-2026 /status absent", MESSAGE_MISSING_NAME);
    }

    // EP_8: Index and name with class are supplied together
    @Test
    public void parse_bothIndexAndNameAndClass_failure() {
        assertParseFailure(parser,
                "1 /name Alex Yeoh /class A1 /date 16-09-2026 /status absent",
                "Use either an index or name and class, not both.");
        assertParseFailure(parser, "1 /name /date 16-09-2026 /status absent",
                ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS);
    }

    // EP_9: Index is not a positive integer
    // Boundary Value: 0, one below the smallest valid index 1
    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 /date 16-09-2026 /status absent",
                ParserUtil.MESSAGE_INVALID_INDEX);

        // once all required values are present, the index is validated before the date and status
        assertParseFailure(parser, "x /date invalid /status invalid", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    // EP_10: Date has the required shape but is not a real calendar date
    @Test
    public void parse_invalidDate_failure() {
        // A date that does not exist
        assertParseFailure(parser, "1 /date 31-02-2026 /status absent",
                String.format(ParserUtil.MESSAGE_NONEXISTENT_DATE, "31-02-2026"));

        // A day or a month that is out of range
        assertParseFailure(parser, "1 /date 16-13-2026 /status absent",
                "Invalid date format. Please use the format DD-MM-YYYY.");

        // Wrong date format
        assertParseFailure(parser, "1 /date 2026-09-16 /status absent",
                "Invalid date format. Please use the format DD-MM-YYYY.");
    }

    // EP_11: A parameter prefix is joined to the preceding value without a space
    @Test
    public void parse_prefixWithoutSpace_reportsThePrefix() {
        // After the date
        assertParseFailure(parser, "1 /date 16-09-2026/status late",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/status"));

        // After the index
        assertParseFailure(parser, "1/date 16-09-2026 /status late",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/date"));

        // In another case
        assertParseFailure(parser, "/name Alex Yeoh/CLASS A1 /date 16-09-2026 /status late",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/CLASS"));
    }

    // EP_12: Attendance status is not one of the accepted values
    @Test
    public void parse_invalidStatus_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026 /status excused",
                "Invalid status. Please use 'present', 'absent' or 'late'.");
    }

    // EP_13_1: Date prefix is repeated
    @Test
    public void parse_repeatedDatePrefix_failure() {
        assertParseFailure(parser,
                "1 /date 16-09-2026 /date 17-09-2026 /status absent",
                getErrorMessageForDuplicatePrefixes("/date"));
    }

    // EP_13_2: Status prefix is repeated
    @Test
    public void parse_repeatedStatusPrefix_failure() {
        assertParseFailure(parser,
                "1 /date 16-09-2026 /status absent /status present",
                getErrorMessageForDuplicatePrefixes("/status"));
    }

    // EP_13_3: Name prefix is repeated
    @Test
    public void parse_repeatedNamePrefix_failure() {
        assertParseFailure(parser,
                " /name Alex Yeoh /name John Tan /class A1 " + "/date 16-09-2026 /status absent",
                getErrorMessageForDuplicatePrefixes("/name"));
    }

    // EP_13_4: Class prefix is repeated
    @Test
    public void parse_repeatedClassPrefix_failure() {
        assertParseFailure(parser,
                "/name Alex Yeoh /class A1 /class A2 " + "/date 16-09-2026 /status absent",
                getErrorMessageForDuplicatePrefixes("/class"));
    }

    @Test
    public void parse_repeatedParameterAndStuckParameter_reportsRepeatedParameter() {
        assertParseFailure(parser,
                "1/date 16-09-2026 /date 16-09-2026 /date 17-09-2026 /status absent",
                getErrorMessageForDuplicatePrefixes("/date"));
    }

}
