package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.PersonMatchesKeyword;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noArgument_returnsListAllCommand() {
        assertParseSuccess(parser, "", new ListCommand());
        assertParseSuccess(parser, "     ", new ListCommand());
    }

    @Test
    public void parse_singleWordArgument_returnsListCommandWithKeyword() {
        ListCommand expectedListCommand = new ListCommand(new PersonMatchesKeyword("John"));

        // no leading and trailing whitespaces
        assertParseSuccess(parser, "John", expectedListCommand);

        // leading and trailing whitespaces
        assertParseSuccess(parser, " \n John \t ", expectedListCommand);
    }

    @Test
    public void parse_multiWordArgument_returnsListCommandWithWholePhrase() {
        ListCommand expectedListCommand = new ListCommand(new PersonMatchesKeyword("John Doe"));

        // no leading and trailing whitespaces
        assertParseSuccess(parser, "John Doe", expectedListCommand);

        // multiple whitespaces between words
        assertParseSuccess(parser, " John \t Doe  ", expectedListCommand);
    }

}
