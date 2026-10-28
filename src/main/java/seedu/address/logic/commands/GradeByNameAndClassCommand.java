package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Records a grade for the person identified by name and class.
 */
public class GradeByNameAndClassCommand extends GradeCommand {

    private static final Logger logger = LogsCenter.getLogger(GradeByNameAndClassCommand.class);

    private final String name;
    private final String className;
    private final Grade grade;

    /**
     * Creates a command that records {@code grade} for the named person in the given class. Neither needs to
     * be a valid name or class, and case and extra whitespace are ignored when they are matched.
     */
    public GradeByNameAndClassCommand(String name, String className, Grade grade) {
        requireAllNonNull(name, className, grade);
        this.name = name;
        this.className = className;
        this.grade = grade;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person person = model.findPerson(name, className)
                .orElseThrow(() -> {
                    logger.info("No contact found for name: " + name + ", class: " + className);
                    return new CommandException(MESSAGE_CONTACT_NOT_FOUND);
                });
        return recordGrade(model, person, grade);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeByNameAndClassCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name)
                && className.equals(otherCommand.className)
                && grade.equals(otherCommand.grade);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("grade", grade)
                .toString();
    }

}
