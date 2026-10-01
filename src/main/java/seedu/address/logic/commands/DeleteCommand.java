package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person from the address book. The person is identified by the subclass.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a person identified either by the index number used in the displayed person list "
            + "or by name and class.\n"
            + "Parameters: INDEX (must be a positive integer) or "
            + PREFIX_NAME + " NAME " + PREFIX_CLASS + " CLASS\n"
            + "Examples: " + COMMAND_WORD + " 1, "
            + COMMAND_WORD + " " + PREFIX_NAME + " John Tan " + PREFIX_CLASS + " A1";

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
