package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Deletes a grade from the person identified by name and class.
 */
public class DeleteGradeByNameAndClassCommand extends DeleteGradeCommand {

    private final String name;
    private final String className;
    private final String assessmentName;

    /**
     * Creates a command that deletes an assessment grade from the named person in the given class.
     */
    public DeleteGradeByNameAndClassCommand(String name, String className, String assessmentName) {
        requireAllNonNull(name, className, assessmentName);
        assert !assessmentName.isBlank()
                && Grade.getAssessmentNameConstraintViolation(assessmentName).isEmpty()
                : "assessmentName must be valid";
        this.name = name;
        this.className = className;
        this.assessmentName = assessmentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person person = model.findPerson(name, className)
                .orElseThrow(() -> new CommandException(MESSAGE_CONTACT_NOT_FOUND));
        return deleteGrade(model, person, assessmentName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteGradeByNameAndClassCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name)
                && className.equals(otherCommand.className)
                && assessmentName.equals(otherCommand.assessmentName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("assessmentName", assessmentName)
                .toString();
    }
}
