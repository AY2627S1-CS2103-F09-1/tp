package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Clears all contacts from ClassMates. "
            + "Nothing can be written after the command word.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "ClassMates has been cleared!";

    private static final Logger logger = LogsCenter.getLogger(ClearCommand.class);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        int clearedCount = model.getAddressBook().getPersonList().size();

        model.setAddressBook(new AddressBook());
        assert model.getAddressBook().getPersonList().isEmpty()
                : "The address book must be empty after it is cleared";

        logger.info("Cleared " + clearedCount + " contact(s)");
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
