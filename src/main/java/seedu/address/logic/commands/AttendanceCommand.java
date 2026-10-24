package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Records a person's attendance for a date. The person is identified by the subclass.
 */
public abstract class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a person's attendance for a date.\n"
            + "Parameters: INDEX /date DD-MM-YYYY /status present|absent|late\n"
            + "or /name NAME /class CLASS /date DD-MM-YYYY /status present|absent|late\n"
            + "Example: " + COMMAND_WORD + " 5 /date 16-09-2026 /status absent";

    public static final String MESSAGE_SUCCESS = "Attendance for %1$s on %2$s recorded as %3$s.";
    public static final String MESSAGE_FUTURE_DATE = "Attendance date cannot be in the future.";
    public static final String MESSAGE_DUPLICATE_ATTENDANCE = "Attendance record already exists";

    /**
     * Checks that {@code date} is not after today.
     *
     * @throws CommandException If {@code date} is in the future.
     */
    protected static void requireNotInFuture(LocalDate date) throws CommandException {
        requireAllNonNull(date);
        if (date.isAfter(LocalDate.now())) {
            throw new CommandException(MESSAGE_FUTURE_DATE);
        }
    }

    /**
     * Records the attendance for {@code date} with {@code status} in {@code person} and returns a success
     * result.
     *
     * @throws CommandException If the person already has an attendance record for the date.
     */
    protected static CommandResult recordAttendance(Model model, Person person, LocalDate date,
            Attendance.Status status) throws CommandException {
        requireAllNonNull(model, person, date, status);
        if (person.hasAttendanceOn(date)) {
            throw new CommandException(MESSAGE_DUPLICATE_ATTENDANCE);
        }
        model.setPerson(person, person.withAttendance(new Attendance(date, status)));
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), Attendance.formatDate(date),
                status));
    }

}
