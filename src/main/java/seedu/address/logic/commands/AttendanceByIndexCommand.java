package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Records the attendance of the person identified using their displayed index.
 */
public class AttendanceByIndexCommand extends AttendanceCommand {

    private static final Logger logger = LogsCenter.getLogger(AttendanceByIndexCommand.class);

    private final Index targetIndex;
    private final LocalDate date;
    private final Attendance.Status status;

    /**
     * Creates a command that records the attendance of the person at {@code targetIndex}.
     */
    public AttendanceByIndexCommand(Index targetIndex, LocalDate date, Attendance.Status status) {
        requireAllNonNull(targetIndex, date, status);
        this.targetIndex = targetIndex;
        this.date = date;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        requireNotInFuture(date);
        List<Person> displayedPeople = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPeople.size()) {
            logger.info("Rejected an index outside the displayed list: " + targetIndex.getOneBased()
                    + " (list size: " + displayedPeople.size() + ")");
            throw new CommandException(Messages.getInvalidDisplayedIndexMessage(displayedPeople.size()));
        }
        return recordAttendance(model, displayedPeople.get(targetIndex.getZeroBased()), date, status);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttendanceByIndexCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex)
                && date.equals(otherCommand.date)
                && status == otherCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .add("status", status)
                .toString();
    }

}
