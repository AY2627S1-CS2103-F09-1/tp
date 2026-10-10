package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.model.person.Attendance;

public class AttendanceCommandParserTest {

    private final AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_validArguments_success() {
        String userInput = "1 /date 16-09-2026 /status absent";
        AttendanceCommand expectedCommand = new AttendanceCommand(
                Index.fromOneBased(1),
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_statusCapitalization_success() {
        String userInput = "2 /date 16-09-2026 /status LaTe";
        AttendanceCommand expectedCommand = new AttendanceCommand(
                Index.fromOneBased(2),
                LocalDate.of(2026, 9, 16),
                Attendance.Status.LATE);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " /date 16-09-2026 /status absent",
            "Missing index. Please provide the index of the person.");
    }

    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "1 /status absent",
            "Missing date. Please provide the date of attendance.");
    }

    @Test
    public void parse_missingStatus_failure() {
        assertParseFailure(parser, "1 /date 16-09-2026",
            "Missing status. Please provide the attendance status.");
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

}
