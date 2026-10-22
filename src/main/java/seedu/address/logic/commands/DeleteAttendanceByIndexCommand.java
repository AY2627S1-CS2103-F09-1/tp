package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes an attendance record from the person identified using their displayed index.
 */
public class DeleteAttendanceByIndexCommand extends DeleteAttendanceCommand {

    private final Index targetIndex;
    private final LocalDate date;

    /**
     * Creates a command that deletes an attendance record from the person at {@code targetIndex}.
     */
    public DeleteAttendanceByIndexCommand(Index targetIndex, LocalDate date) {
        requireAllNonNull(targetIndex, date);
        this.targetIndex = targetIndex;
        this.date = date;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPeople = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPeople.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return deleteAttendance(model, displayedPeople.get(targetIndex.getZeroBased()), date);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteAttendanceByIndexCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex) && date.equals(otherCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .toString();
    }
}
