package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND;

import java.util.Comparator;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/** Displays the full attendance history for a student. */
public class ViewAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "viewA";
    public static final String COMMAND_ALIAS = "va";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays all attendance records for a student.\n"
            + "Parameters: /name NAME /class CLASS\n"
            + "Example: " + COMMAND_WORD + " /name John Tan /class A1";
    public static final String MESSAGE_NO_ATTENDANCE = "No attendance records.";
    public static final String MESSAGE_SUCCESS = "Here is the attendance for %s from class %s\n%s";

    private final String name;
    private final String className;

    /**
     * Creates a command to show the attendance history of the student with the given {@code name} and
     * {@code className}. Neither needs to be a valid name or class, and case and extra whitespace are
     * ignored.
     */
    public ViewAttendanceCommand(String name, String className) {
        requireAllNonNull(name, className);
        this.name = name;
        this.className = className;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = model.findPerson(name, className)
                .orElseThrow(() -> new CommandException(MESSAGE_CONTACT_NOT_FOUND));

        String attendance = student.getAttendanceRecords().stream()
                .sorted(Comparator.comparing(Attendance::getDate).reversed())
                .map(Attendance::toDisplayString)
                .collect(Collectors.joining("\n"));
        if (attendance.isEmpty()) {
            attendance = MESSAGE_NO_ATTENDANCE;
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), student.getClassName(), attendance));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewAttendanceCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name) && className.equals(otherCommand.className);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("className", className).toString();
    }

}
