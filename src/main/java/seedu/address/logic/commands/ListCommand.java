package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
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

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all contacts in ClassMates. "
            + "If a keyword is given, lists only the contacts whose name, class or tags contain it "
            + "(case-insensitive).\n"
            + "Parameters: [KEYWORD]\n"
            + "Example: " + COMMAND_WORD + " John";

    public static final String MESSAGE_SUCCESS = "Listed all contacts.";

    private static final Logger logger = LogsCenter.getLogger(ListCommand.class);

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
        assert isShowingExactlyMatchingPersons(model) : "The displayed list must be the contacts that match";

        logger.info("Listed " + model.getFilteredPersonList().size() + " of "
                + model.getAddressBook().getPersonList().size() + " contacts, "
                + (isFiltered ? "matching " + predicate : "without a keyword"));
        if (!isFiltered) {
            return new CommandResult(MESSAGE_SUCCESS);
        }
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    /**
     * Returns true if the displayed contacts of {@code model} are exactly the contacts in its address book
     * that are accepted by the predicate.
     */
    private boolean isShowingExactlyMatchingPersons(Model model) {
        long expectedCount = model.getAddressBook().getPersonList().stream().filter(predicate).count();
        return model.getFilteredPersonList().size() == expectedCount
                && model.getFilteredPersonList().stream().allMatch(predicate);
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
