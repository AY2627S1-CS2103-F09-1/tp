package seedu.address.logic.parser;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.PersonMatchesKeyword;

/**
 * Parses input arguments and creates a new ListCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * Empty arguments list all persons. Otherwise, the whole argument, which may contain several words such as
     * a full name or class, is the keyword that the listed persons must match.
     */
    @Override
    public ListCommand parse(String args) {
        String keyword = args.trim();
        if (keyword.isEmpty()) {
            return new ListCommand();
        }

        return new ListCommand(new PersonMatchesKeyword(keyword));
    }

}
