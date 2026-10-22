package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person from the address book. The person is identified by the subclass.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a contact identified either by the index number used in the displayed contact list "
            + "or by name and class.\n"
            + "Parameters: INDEX (must be a positive integer) or "
            + PREFIX_NAME + " NAME " + PREFIX_CLASS + " CLASS\n"
            + "Examples: " + COMMAND_WORD + " 1, "
            + COMMAND_WORD + " " + PREFIX_NAME + " John Tan " + PREFIX_CLASS + " A1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "%1$s in class %2$s has been deleted";

    private static final Logger logger = LogsCenter.getLogger(DeleteCommand.class);

    /**
     * Deletes {@code personToDelete} from {@code model} and returns the result telling the user who was
     * deleted. {@code personToDelete} must be in {@code model}.
     */
    protected static CommandResult deletePerson(Model model, Person personToDelete) {
        requireAllNonNull(model, personToDelete);
        assert model.hasPerson(personToDelete) : "The contact to delete must be in the model";

        model.deletePerson(personToDelete);
        assert !model.hasPerson(personToDelete) : "The contact must not be in the model after it is deleted";

        logger.info("Deleted contact: " + personToDelete.getName() + " in " + personToDelete.getClassName());
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                personToDelete.getName(), personToDelete.getClassName()));
    }

}
