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
import seedu.address.model.person.Person;

/**
 * Deletes an attendance record from the person identified by name and class.
 */
public class DeleteAttendanceByNameAndClassCommand extends DeleteAttendanceCommand {

    private static final Logger logger = LogsCenter.getLogger(DeleteAttendanceByNameAndClassCommand.class);

    private final String name;
    private final String className;
    private final LocalDate date;

    /**
     * Creates a command that deletes an attendance record from the named person in the given class.
     */
    public DeleteAttendanceByNameAndClassCommand(String name, String className, LocalDate date) {
        requireAllNonNull(name, className, date);
        this.name = name;
        this.className = className;
        this.date = date;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person person = model.findPerson(name, className)
                .orElseThrow(() -> {
                    logger.info("No contact found for name: " + name + ", class: " + className);
                    return new CommandException(MESSAGE_CONTACT_NOT_FOUND);
                });
        return deleteAttendance(model, person, date);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteAttendanceByNameAndClassCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name)
                && className.equals(otherCommand.className)
                && date.equals(otherCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("date", date)
                .toString();
    }
}
