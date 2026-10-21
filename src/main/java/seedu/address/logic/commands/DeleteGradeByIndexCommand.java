package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Deletes a grade from the person identified using their displayed index.
 */
public class DeleteGradeByIndexCommand extends DeleteGradeCommand {

    private final Index targetIndex;
    private final String assessmentName;

    /**
     * Creates a command that deletes an assessment grade from the person at {@code targetIndex}.
     */
    public DeleteGradeByIndexCommand(Index targetIndex, String assessmentName) {
        requireAllNonNull(targetIndex, assessmentName);
        assert !assessmentName.isBlank()
                && Grade.getAssessmentNameConstraintViolation(assessmentName).isEmpty()
                : "assessmentName must be valid";
        this.targetIndex = targetIndex;
        this.assessmentName = assessmentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPeople = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPeople.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return deleteGrade(model, displayedPeople.get(targetIndex.getZeroBased()), assessmentName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteGradeByIndexCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex)
                && assessmentName.equals(otherCommand.assessmentName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("assessmentName", assessmentName)
                .toString();
    }
}
