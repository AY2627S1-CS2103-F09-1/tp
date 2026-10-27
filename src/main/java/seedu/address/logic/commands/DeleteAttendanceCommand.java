package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Deletes an attendance record from a person. The person is identified by the subclass.
 */
public abstract class DeleteAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "deleteA";
    public static final String COMMAND_ALIAS = "da";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes an attendance record identified by its date from a contact identified either by the index "
            + "number used in the displayed contact list or by name and class.\n"
            + "Parameters: INDEX " + PREFIX_DATE + " DATE or "
            + PREFIX_NAME + " NAME " + PREFIX_CLASS + " CLASS " + PREFIX_DATE + " DATE\n"
            + "Examples: " + COMMAND_WORD + " 1 " + PREFIX_DATE + " 16-09-2026, "
            + COMMAND_WORD + " " + PREFIX_NAME + " John Tan " + PREFIX_CLASS + " A1 "
            + PREFIX_DATE + " 16-09-2026";

    public static final String MESSAGE_SUCCESS =
            "Attendance of %1$s for %2$s on %3$s has been deleted";
    public static final String MESSAGE_ATTENDANCE_NOT_FOUND = "Attendance for this date not found";

    /**
     * Deletes the attendance record for {@code date} from {@code person} and returns a success result.
     *
     * @throws CommandException If the person has no attendance record for the date or the record cannot be removed.
     */
    protected static CommandResult deleteAttendance(Model model, Person person, LocalDate date)
            throws CommandException {
        requireAllNonNull(model, person, date);
        Attendance attendance = person.getAttendanceRecords().stream()
                .filter(record -> record.getDate().equals(date))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_ATTENDANCE_NOT_FOUND));

        Person personWithoutAttendance = person.withoutAttendance(attendance);
        assert personWithoutAttendance.getAttendanceRecords().stream()
                .noneMatch(record -> record.getDate().equals(date)) : "Attendance record was not removed";

        model.setPerson(person, personWithoutAttendance);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                attendance.getStatus(), person.getName(), Attendance.formatDate(attendance.getDate())));
    }
}
