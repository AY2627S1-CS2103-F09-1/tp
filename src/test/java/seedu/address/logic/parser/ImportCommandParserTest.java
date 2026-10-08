package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ImportCommandParserTest {

    @Test
    public void parse_missingPath_throwsParseException() {
        ImportCommandParser parser = new ImportCommandParser();
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("   "));

        assertEquals("Specify file path", exception.getMessage());
    }

    @Test
    public void parse_pathWithCrOrLf_throwsParseException() {
        ImportCommandParser parser = new ImportCommandParser();
        String errorMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE);

        ParseException exceptionCr = assertThrows(ParseException.class, () -> parser.parse("  students\r.csv "));
        assertEquals(errorMessage, exceptionCr.getMessage());

        ParseException exceptionLf = assertThrows(ParseException.class, () -> parser.parse(" students\n.csv  "));
        assertEquals(errorMessage, exceptionLf.getMessage());
    }

    @Test
    public void parse_validPath_returnsImportCommand() throws ParseException {
        ImportCommandParser parser = new ImportCommandParser();

        ImportCommand command = parser.parse(" students.csv ");

        assertEquals(new ImportCommand("students.csv"), command);
    }
}
