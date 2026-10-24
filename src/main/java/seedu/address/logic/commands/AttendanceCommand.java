package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Records a person's attendance for a date.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a person's attendance for a date.\n"
            + "Parameters: INDEX /date DD-MM-YYYY /status present|absent|late\n"
            + "or /name NAME /class CLASS /date DD-MM-YYYY /status present|absent|late\n"
            + "Example: " + COMMAND_WORD + " 5 /date 16-09-2026 /status absent";

    public static final String MESSAGE_SUCCESS =
            "Attendance for %1$s on %2$td-%2$tm-%2$tY recorded as %3$s.";
    public static final String MESSAGE_FUTURE_DATE = "Attendance date cannot be in the future.";
    public static final String MESSAGE_DUPLICATE_ATTENDANCE = "Attendance record already exists";

    private final Optional<Index> index;
    private final Optional<Name> name;
    private final Optional<ClassName> className;
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
        this.index = Optional.of(index);
        this.name = Optional.empty();
        this.className = Optional.empty();
        this.date = date;
        this.status = status;
    }

    /**
     * Creates an AttendanceCommand identifying a person by name and class.
     *
     * @param name name of the person
     * @param className class of the person
     * @param date date of the attendance
     * @param status attendance status
     */
    public AttendanceCommand(Name name, ClassName className, LocalDate date, Attendance.Status status) {
        requireNonNull(name);
        requireNonNull(className);
        requireNonNull(date);
        requireNonNull(status);
        this.index = Optional.empty();
        this.name = Optional.of(name);
        this.className = Optional.of(className);
        this.date = date;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (date.isAfter(LocalDate.now())) {
            throw new CommandException(MESSAGE_FUTURE_DATE);
        }

        Person person;

        if (index.isPresent()) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (index.get().getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            person = lastShownList.get(index.get().getZeroBased());
        } else {
            person = model.findPerson(name.get(), className.get())
                    .orElseThrow(() -> new CommandException(MESSAGE_CONTACT_NOT_FOUND));
        }

        try {
            Person updatedPerson = person.withAttendance(new Attendance(date, status));
            model.setPerson(person, updatedPerson);
            return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), date, status));
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
                && status == otherCommand.status
                && name.equals(otherCommand.name)
                && className.equals(otherCommand.className);
    }

    @Override
    public String toString() {
        if (index.isPresent()) {
            return new ToStringBuilder(this)
                    .add("index", index)
                    .add("date", date)
                    .add("status", status)
                    .toString();
        }
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("date", date)
                .add("status", status)
                .toString();
    }

}
