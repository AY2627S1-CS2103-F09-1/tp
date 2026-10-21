package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

public class GradeCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_recordsGrade() {
        Index index = Index.fromOneBased(1);
        Grade grade = new Grade("Assignment 1", new BigDecimal("97.5"));
        Person person = model.getFilteredPersonList().get(index.getZeroBased());
        Person updatedPerson = person.withGrade(grade);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, updatedPerson);

        GradeCommand command = new GradeCommand(index, grade);
        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + person.getName() + " in class " + person.getClassName() + ".";
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validNameAndClass_recordsGrade() {
        Person person = model.getFilteredPersonList().get(0);
        Name name = person.getName();
        ClassName className = person.getClassName();
        Grade grade = new Grade("Assignment 1", new BigDecimal("97.5"));

        Person updatedPerson = person.withGrade(grade);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, updatedPerson);

        GradeCommand command = new GradeCommand(name, className, grade);

        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + person.getName() + " in class " + person.getClassName() + ".";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index index = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        GradeCommand command = new GradeCommand(index, new Grade("Quiz", BigDecimal.TEN));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_unknownNameAndClass_throwsCommandException() {
        GradeCommand command = new GradeCommand(new Name("Unknown Student"), new ClassName("A1"),
                new Grade("Assignment 1", BigDecimal.TEN));

        assertCommandFailure(command, model, seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_duplicateAssessment_throwsCommandException() {
        Index index = Index.fromOneBased(1);
        Person person = model.getFilteredPersonList().get(index.getZeroBased());
        model.setPerson(person, person.withGrade(new Grade("Assignment 1", BigDecimal.TEN)));

        GradeCommand command = new GradeCommand(index, new Grade("Assignment 1", BigDecimal.ONE));
        assertCommandFailure(command, model, GradeCommand.MESSAGE_DUPLICATE_GRADE);
    }

    @Test
    public void execute_duplicateAssessmentWithNameAndClass_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(0);
        Name name = person.getName();
        ClassName className = person.getClassName();

        model.setPerson(person, person.withGrade(new Grade("Assignment 1", BigDecimal.TEN)));

        GradeCommand command = new GradeCommand(name, className, new Grade("Assignment 1", BigDecimal.ONE));
        assertCommandFailure(command, model, GradeCommand.MESSAGE_DUPLICATE_GRADE);
    }

    @Test
    public void equals() {
        Index index = Index.fromOneBased(1);
        Grade grade = new Grade("Assignment 1", new BigDecimal("97"));
        GradeCommand command = new GradeCommand(index, grade);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new GradeCommand(index,
                new Grade("Assignment 1", new BigDecimal("97.0")))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("not a GradeCommand"));
        assertFalse(command.equals(new GradeCommand(Index.fromOneBased(2), grade)));
        assertFalse(command.equals(new GradeCommand(index, new Grade("Assignment 1", BigDecimal.ONE))));
    }

    @Test
    public void equals_nameAndClassCommands() {
        Person person = model.getFilteredPersonList().get(0);
        Name name = person.getName();
        ClassName className = person.getClassName();
        Grade grade = new Grade("Assignment 1", new BigDecimal("97"));

        GradeCommand command = new GradeCommand(name, className, grade);

        assertTrue(command.equals(new GradeCommand(new Name(name.toString()), new ClassName(className.toString()),
                new Grade("Assignment 1", new BigDecimal("97.0")))));

        assertFalse(command.equals(new GradeCommand(new Name("Unknown Student"), className, grade)));

        assertFalse(command.equals(new GradeCommand(name, new ClassName("A2"), grade)));
    }

    @Test
    public void toStringMethod_indexCommand() {
        Index index = Index.fromOneBased(1);
        Grade grade = new Grade("Assignment 1", new BigDecimal("97"));
        GradeCommand command = new GradeCommand(index, grade);

        String expected = GradeCommand.class.getCanonicalName()
                + "{index=" + Optional.of(index) + ", grade=" + grade + "}";
        assertEquals(expected, command.toString());
    }

    @Test
    public void toStringMethod_nameAndClassCommand() {
        Person person = model.getFilteredPersonList().get(0);
        Name name = person.getName();
        ClassName className = person.getClassName();
        Grade grade = new Grade("Assignment 1", new BigDecimal("97"));

        GradeCommand command = new GradeCommand(name, className, grade);

        String expected = GradeCommand.class.getCanonicalName()
                + "{name=" + Optional.of(name)
                + ", className=" + Optional.of(className)
                + ", grade=" + grade + "}";

        assertEquals(expected, command.toString());
    }

}
