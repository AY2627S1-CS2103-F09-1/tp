package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/** Records a grade for a person and an assessment. */
public class GradeCommand extends Command {

    public static final String COMMAND_WORD = "grade";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a grade for a person and assessment.\n"
            + "Parameters: INDEX /assessment ASSESSMENT_NAME /score SCORE\n"
            + "or: /name NAME /class CLASS /assessment ASSESSMENT_NAME /score SCORE\n"
            + "Example: " + COMMAND_WORD + " 5 /assessment Assignment 1 /score 97";
    public static final String MESSAGE_SUCCESS =
            "Grade of %s recorded for %s for student %s in class %s.";
    public static final String MESSAGE_DUPLICATE_GRADE = "Grade for this assessment already exists";

    private final Optional<Index> index;
    private final Optional<Name> name;
    private final Optional<ClassName> className;
    private final Grade grade;

    /**
     * Creates a GradeCommand.
     *
     * @param index displayed index of the person
     * @param grade grade to be recorded
     */
    public GradeCommand(Index index, Grade grade) {
        requireNonNull(index);
        requireNonNull(grade);
        this.index = Optional.of(index);
        this.name = Optional.empty();
        this.className = Optional.empty();
        this.grade = grade;
    }

    /**
     * Creates a GradeCommand identifying a person by name and class.
     *
     * @param name name of the person
     * @param className class of the person
     * @param grade grade to be recorded
     */
    public GradeCommand(Name name, ClassName className, Grade grade) {
        requireNonNull(name);
        requireNonNull(className);
        requireNonNull(grade);
        this.index = Optional.empty();
        this.name = Optional.of(name);
        this.className = Optional.of(className);
        this.grade = grade;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person person;
        if (index.isPresent()) {
            List<Person> displayedPeople = model.getFilteredPersonList();
            if (index.get().getZeroBased() >= displayedPeople.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            person = displayedPeople.get(index.get().getZeroBased());
        } else {
            person = model.getAddressBook().getPersonList().stream()
                    .filter(p -> p.getName().equals(name.get()) && p.getClassName().equals(className.get()))
                    .findFirst().orElseThrow(() -> new CommandException(MESSAGE_CONTACT_NOT_FOUND));
        }
        try {
            model.setPerson(person, person.withGrade(grade));
        } catch (IllegalArgumentException e) {
            throw new CommandException(MESSAGE_DUPLICATE_GRADE);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, grade.getScore().stripTrailingZeros().toPlainString(),
                grade.getAssessmentName(), person.getName(), person.getClassName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index) && grade.equals(otherCommand.grade)
            && name.equals(otherCommand.name) && className.equals(otherCommand.className);
    }

    @Override
    public String toString() {
        if (index.isPresent()) {
            return new ToStringBuilder(this).add("index", index).add("grade", grade).toString();
        }
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("grade", grade)
                .toString();
    }

}
