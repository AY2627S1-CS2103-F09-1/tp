package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Records a grade for a person and an assessment. The person is identified by the subclass.
 */
public abstract class GradeCommand extends Command {

    public static final String COMMAND_WORD = "grade";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a grade for a person and assessment.\n"
            + "Parameters: INDEX /assessment ASSESSMENT_NAME /score SCORE\n"
            + "or: /name NAME /class CLASS /assessment ASSESSMENT_NAME /score SCORE\n"
            + "Example: " + COMMAND_WORD + " 5 /assessment Assignment 1 /score 97";

    public static final String MESSAGE_SUCCESS = "Grade of %s recorded for %s for student %s in class %s.";
    public static final String MESSAGE_DUPLICATE_GRADE =
            "%1$s already has a grade of %2$s for %3$s. Use deleteG to remove it first.";

    /**
     * Records {@code grade} in {@code person} and returns a success result.
     *
     * @throws CommandException If the person already has a grade for the assessment.
     */
    protected static CommandResult recordGrade(Model model, Person person, Grade grade)
            throws CommandException {
        requireAllNonNull(model, person, grade);
        Optional<Grade> existingGrade = person.getGradeFor(grade);
        if (existingGrade.isPresent()) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GRADE, person.getName(),
                    existingGrade.get().getScore().stripTrailingZeros().toPlainString(),
                    existingGrade.get().getAssessmentName()));
        }
        model.setPerson(person, person.withGrade(grade));
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                grade.getScore().stripTrailingZeros().toPlainString(), grade.getAssessmentName(),
                person.getName(), person.getClassName()));
    }

}
