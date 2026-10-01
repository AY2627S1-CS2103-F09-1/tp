package seedu.address.logic.commands;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person from the address book. The person is identified by the subclass.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "%1$s in class %2$s has been deleted";

    /**
     * Deletes {@code personToDelete} from {@code model} and returns the result telling the user who was
     * deleted.
     */
    protected static CommandResult deletePerson(Model model, Person personToDelete) {
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                personToDelete.getName(), personToDelete.getClassName()));
    }

}
