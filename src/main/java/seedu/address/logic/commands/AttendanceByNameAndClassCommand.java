package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND;

import java.time.LocalDate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Records the attendance of the person identified by name and class.
 */
public class AttendanceByNameAndClassCommand extends AttendanceCommand {

    private static final Logger logger = LogsCenter.getLogger(AttendanceByNameAndClassCommand.class);

    private final String name;
    private final String className;
    private final LocalDate date;
    private final Attendance.Status status;

    /**
     * Creates a command that records the attendance of the named person in the given class. Neither needs to
     * be a valid name or class, and case and extra whitespace are ignored when they are matched.
     */
    public AttendanceByNameAndClassCommand(String name, String className, LocalDate date,
            Attendance.Status status) {
        requireAllNonNull(name, className, date, status);
        this.name = name;
        this.className = className;
        this.date = date;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        requireNotInFuture(date);
        Person person = model.findPerson(name, className)
                .orElseThrow(() -> {
                    logger.info("No contact found for name: " + name + ", class: " + className);
                    return new CommandException(MESSAGE_CONTACT_NOT_FOUND);
                });
        return recordAttendance(model, person, date, status);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttendanceByNameAndClassCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name)
                && className.equals(otherCommand.className)
                && date.equals(otherCommand.date)
                && status == otherCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("date", date)
                .add("status", status)
                .toString();
    }

}
