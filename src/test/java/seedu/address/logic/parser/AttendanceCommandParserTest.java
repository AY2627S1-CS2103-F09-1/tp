package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceByIndexCommand;
import seedu.address.logic.commands.AttendanceByNameAndClassCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.model.person.Attendance;

public class AttendanceCommandParserTest {

    private final AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_unknownParameter_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026 /status absent /foo bar",
                AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, "1 /foo bar", AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_unknownParameterAndRepeatedParameter_reportsUnknownParameter() {
        assertParseFailure(parser, "1 /date 16-09-2026 /date 17-09-2026 /status absent /foo bar",
                AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_prefixesInAnyCase_success() {
        AttendanceCommand expectedCommand = new AttendanceByIndexCommand(
                Index.fromOneBased(1),
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, "1 /DATE 16-09-2026 /Status absent", expectedCommand);
    }

    @Test
    public void parse_validIndexArguments_success() {
        String userInput = "1 /date 16-09-2026 /status absent";
        AttendanceCommand expectedCommand = new AttendanceByIndexCommand(
                Index.fromOneBased(1),
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

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

    @Test
    public void parse_nameAndClassThatNoContactCanHave_success() {
        AttendanceCommand expectedCommand = new AttendanceByNameAndClassCommand(
                "@@@,",
                "!!!",
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, " /name @@@,  /class !!! /date 16-09-2026 /status absent", expectedCommand);
    }

    @Test
    public void parse_unknownParameterMessage_listsEveryAcceptedParameter() {
        assertEquals("Unknown parameter. Use /name, /class, /date or /status",
                AttendanceCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " /date 16-09-2026 /status absent",
            "Use either an index, or both /name and /class.");
    }

    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "1 /status absent",
            "Missing date. Please provide the date of attendance.");
    }

    @Test
    public void parse_missingDateWithNameAndClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /class A1 /status absent",
            "Missing date. Please provide the date of attendance.");
    }

    @Test
    public void parse_missingStatus_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026",
            "Missing status. Please provide the attendance status.");
    }

    @Test
    public void parse_missingStatusWithNameAndClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /class A1 /date 16-09-2026",
            "Missing status. Please provide the attendance status.");
    }

    @Test
    public void parse_missingClass_failure() {
        assertParseFailure(parser, " /name Alex Yeoh /date 16-09-2026 /status absent", MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, " /class A1 /date 16-09-2026 /status absent", MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_bothIndexAndNameAndClass_failure() {
        assertParseFailure(parser,
                "1 /name Alex Yeoh /class A1 /date 16-09-2026 /status absent",
                "Use either an index or name and class, not both.");
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 /date 16-09-2026 /status absent",
                ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidDate_failure() {
        // Invalid calendar date
        assertParseFailure(parser, "1 /date 31-02-2026 /status absent",
                "Invalid date format. Please use the format DD-MM-YYYY.");

        // Wrong date format
        assertParseFailure(parser, "1 /date 2026-09-16 /status absent",
                "Invalid date format. Please use the format DD-MM-YYYY.");
    }

    @Test
    public void parse_invalidStatus_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026 /status excused",
                "Invalid status. Please use 'present', 'absent' or 'late'.");
    }

    @Test
    public void parse_repeatedDatePrefix_failure() {
        assertParseFailure(parser,
                "1 /date 16-09-2026 /date 17-09-2026 /status absent",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_repeatedStatusPrefix_failure() {
        assertParseFailure(parser,
                "1 /date 16-09-2026 /status absent /status present",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_repeatedNamePrefix_failure() {
        assertParseFailure(parser,
                " /name Alex Yeoh /name John Tan /class A1 " + "/date 16-09-2026 /status absent",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_repeatedClassPrefix_failure() {
        assertParseFailure(parser,
                "/name Alex Yeoh /class A1 /class A2 " + "/date 16-09-2026 /status absent",
                MESSAGE_DUPLICATE_PARAMETERS);
    }

}
