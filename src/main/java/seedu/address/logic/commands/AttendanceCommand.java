package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Records a person's attendance for a date.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a person's attendance for a date.\n"
            + "Parameters: INDEX /date DD-MM-YYYY /status present|absent|late\n"
            + "Example: " + COMMAND_WORD + " 5 /date 16-09-2026 /status absent";

    public static final String MESSAGE_SUCCESS =
            "Attendance for %1$s on %2$td-%2$tm-%2$tY recorded as %3$s.";
    public static final String MESSAGE_FUTURE_DATE = "Attendance date cannot be in the future.";
    public static final String MESSAGE_DUPLICATE_ATTENDANCE = "Attendance record already exists";

    private final Index index;
    private final LocalDate date;
    private final Attendance.Status status;

    /**
     * Creates an AttendanceCommand.
     *
     * @param index displayed index of the person
     * @param date date of the attendance
     * @param status attendance status
     */
    public AttendanceCommand(Index index, LocalDate date, Attendance.Status status) {
        requireNonNull(index);
        requireNonNull(date);
        requireNonNull(status);
        this.index = index;
        this.date = date;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (date.isAfter(LocalDate.now())) {
            throw new CommandException(MESSAGE_FUTURE_DATE);
        }

        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person person = lastShownList.get(index.getZeroBased());
        try {
            Person updatedPerson = person.withAttendance(new Attendance(date, status));
            model.setPerson(person, updatedPerson);
            return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), date, status.name()
                    .toLowerCase()));
        } catch (IllegalArgumentException e) {
            throw new CommandException(MESSAGE_DUPLICATE_ATTENDANCE);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttendanceCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index)
                && date.equals(otherCommand.date)
                && status == otherCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("date", date)
                .add("status", status)
                .toString();
    }

}