package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ClearCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);

    private ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArguments_returnsClearCommand() throws ParseException {
        assertTrue(parser.parse("") instanceof ClearCommand);
        assertTrue(parser.parse("   ") instanceof ClearCommand);
        assertTrue(parser.parse(" \t \n ") instanceof ClearCommand);
    }

    @Test
    public void parse_extraText_throwsParseException() {
        assertParseFailure(parser, " 3", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " everything", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /name John Doe", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   x   ", MESSAGE_INVALID_FORMAT);
    }
}
