package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSESSMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Deletes a grade from a person. The person is identified by the subclass.
 */
public abstract class DeleteGradeCommand extends Command {

    public static final String COMMAND_WORD = "deleteG";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a grade identified by its assessment from a contact identified either by the index "
            + "number used in the displayed contact list or by name and class.\n"
            + "Parameters: INDEX " + PREFIX_ASSESSMENT + " ASSESSMENT or "
            + PREFIX_NAME + " NAME " + PREFIX_CLASS + " CLASS "
            + PREFIX_ASSESSMENT + " ASSESSMENT\n"
            + "Examples: " + COMMAND_WORD + " 1 " + PREFIX_ASSESSMENT + " Quiz 1, "
            + COMMAND_WORD + " " + PREFIX_NAME + " John Tan " + PREFIX_CLASS + " A1 "
            + PREFIX_ASSESSMENT + " Quiz 1";

    public static final String MESSAGE_SUCCESS =
            "Grade of %s for %s for student %s in class %s has been deleted";
    public static final String MESSAGE_GRADE_NOT_FOUND = "Grade for this assessment not found";

    /**
     * Deletes the grade for {@code assessmentName} from {@code person} and returns a success result.
     *
     * @throws CommandException If the person has no grade for the assessment or the grade cannot be removed.
     */
    protected static CommandResult deleteGrade(Model model, Person person, String assessmentName)
            throws CommandException {
        requireAllNonNull(model, person, assessmentName);
        Grade grade = person.getGradeRecords().stream()
                .filter(record -> record.hasAssessmentName(assessmentName))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_GRADE_NOT_FOUND));

        Person personWithoutGrade = person.withoutGrade(grade);
        assert personWithoutGrade.getGradeRecords().stream()
                .noneMatch(record -> record.isSameAssessment(grade)) : "Grade was not removed";

        model.setPerson(person, personWithoutGrade);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                grade.getScore().stripTrailingZeros().toPlainString(), grade.getAssessmentName(),
                person.getName(), person.getClassName()));
    }
}
