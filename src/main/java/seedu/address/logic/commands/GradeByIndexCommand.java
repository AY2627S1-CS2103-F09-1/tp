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
 * Records a grade for the person identified using their displayed index.
 */
public class GradeByIndexCommand extends GradeCommand {

    private final Index targetIndex;
    private final Grade grade;

    /**
     * Creates a command that records {@code grade} for the person at {@code targetIndex}.
     */
    public GradeByIndexCommand(Index targetIndex, Grade grade) {
        requireAllNonNull(targetIndex, grade);
        this.targetIndex = targetIndex;
        this.grade = grade;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPeople = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPeople.size()) {
            throw new CommandException(Messages.getInvalidDisplayedIndexMessage(displayedPeople.size()));
        }
        return recordGrade(model, displayedPeople.get(targetIndex.getZeroBased()), grade);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeByIndexCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex) && grade.equals(otherCommand.grade);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("grade", grade)
                .toString();
    }

}
