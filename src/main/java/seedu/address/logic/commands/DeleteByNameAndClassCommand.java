package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified by name and class from the address book. The whole address book is searched,
 * regardless of which persons are currently displayed.
 */
public class DeleteByNameAndClassCommand extends DeleteCommand {

    public static final String MESSAGE_NOT_FOUND = "No contact found";

    private final String name;
    private final String className;

    /**
     * Creates a DeleteByNameAndClassCommand to delete the person with the given {@code name} and
     * {@code className}. Neither needs to be a valid name or class, and case and extra whitespace are
     * ignored.
     */
    public DeleteByNameAndClassCommand(String name, String className) {
        requireAllNonNull(name, className);
        this.name = name;
        this.className = className;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToDelete = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.hasNameAndClass(name, className))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_NOT_FOUND));

        return deletePerson(model, personToDelete);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteByNameAndClassCommand otherDeleteCommand)) {
            return false;
        }

        return name.equals(otherDeleteCommand.name)
                && className.equals(otherDeleteCommand.className);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .toString();
    }
}
