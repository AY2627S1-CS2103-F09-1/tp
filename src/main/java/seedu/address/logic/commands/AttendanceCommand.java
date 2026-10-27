package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
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
    public static final String MESSAGE_DUPLICATE_ATTENDANCE =
            "%1$s already has attendance on %2$s recorded as %3$s. Use deleteA to remove it first.";
    public static final String MESSAGE_UNUSUAL_YEAR = " Warning: the year %1$s is before %2$s. If the date is "
            + "a typo, use deleteA to remove the record and enter it again.";

    private static final Logger logger = LogsCenter.getLogger(AttendanceCommand.class);

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
        requireNotInFuture(date);
        Optional<Attendance> existingRecord = person.getAttendanceOn(date);
        if (existingRecord.isPresent()) {
            logger.info("Rejected duplicate attendance for " + person.getName()
                + " on " + Attendance.formatDate(date));
            throw new CommandException(String.format(MESSAGE_DUPLICATE_ATTENDANCE, person.getName(),
                    Attendance.formatDate(date), existingRecord.get().getStatus()));
        }
        Person updatedPerson = person.withAttendance(new Attendance(date, status));
        model.setPerson(person, updatedPerson);
        assert updatedPerson.hasAttendanceOn(date) : "Attendance record must exist after it is recorded";
        logger.info("Recorded attendance for " + person.getName() + " on "
            + Attendance.formatDate(date) + " as " + status);
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), Attendance.formatDate(date),
                status) + getUnusualYearWarning(date));
    }

    /**
     * Returns a warning if {@code date} is in a year before {@link Attendance#EARLIEST_EXPECTED_YEAR}, which
     * is most likely a typo, or an empty string otherwise. Such a date is still recorded.
     */
    private static String getUnusualYearWarning(LocalDate date) {
        if (date.getYear() >= Attendance.EARLIEST_EXPECTED_YEAR) {
            return "";
        }
        String paddedYear = String.format(Locale.ROOT, "%04d", date.getYear());
        return String.format(MESSAGE_UNUSUAL_YEAR, paddedYear, Attendance.EARLIEST_EXPECTED_YEAR);
    }

}
