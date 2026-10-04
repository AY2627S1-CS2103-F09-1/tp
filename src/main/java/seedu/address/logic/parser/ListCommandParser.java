package seedu.address.logic.parser;

import seedu.address.logic.commands.ListCommand;

/**
 * Parses input arguments and creates a new ListCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * The arguments may be empty or any phrase, such as a multi-word name or class. The phrase is currently
     * ignored and is reserved for filtering the listed persons.
     */
    @Override
    public ListCommand parse(String args) {
        return new ListCommand();
    }

}
