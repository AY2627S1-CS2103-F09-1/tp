package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesKeyword;

/**
 * Lists contacts in the address book to the user.
 * All contacts are listed unless a keyword is given, in which case only the contacts matching it are listed.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all contacts in the address book. "
            + "If a keyword is given, lists only the contacts whose name, class or tags contain it "
            + "(case-insensitive).\n"
            + "Parameters: [KEYWORD]\n"
            + "Example: " + COMMAND_WORD + " John";

    public static final String MESSAGE_SUCCESS = "Listed all contacts.";

    private final Predicate<Person> predicate;
    private final boolean isFiltered;

    /**
     * Creates a {@code ListCommand} that lists all contacts.
     */
    public ListCommand() {
        predicate = PREDICATE_SHOW_ALL_PERSONS;
        isFiltered = false;
    }

    /**
     * Creates a {@code ListCommand} that lists only the contacts accepted by {@code predicate}.
     */
    public ListCommand(PersonMatchesKeyword predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
        isFiltered = true;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        if (!isFiltered) {
            return new CommandResult(MESSAGE_SUCCESS);
        }
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return isFiltered == otherListCommand.isFiltered && predicate.equals(otherListCommand.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
