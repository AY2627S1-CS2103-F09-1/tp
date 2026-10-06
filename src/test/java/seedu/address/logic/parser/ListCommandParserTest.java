package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noArgument_returnsListCommand() {
        assertTrue(parser.parse("") instanceof ListCommand);
        assertTrue(parser.parse("     ") instanceof ListCommand);
    }

    @Test
    public void parse_singleWordArgument_returnsListCommand() {
        assertTrue(parser.parse("John") instanceof ListCommand);
        assertTrue(parser.parse(" \n John \t ") instanceof ListCommand);
    }

    @Test
    public void parse_multiWordArgument_returnsListCommand() {
        assertTrue(parser.parse("John Doe") instanceof ListCommand);
        assertTrue(parser.parse(" John \t Doe  Lee ") instanceof ListCommand);
    }

}
