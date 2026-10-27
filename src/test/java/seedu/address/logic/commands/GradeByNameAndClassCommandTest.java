package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code GradeByNameAndClassCommand}.
 */
public class GradeByNameAndClassCommandTest {

    private static final Grade GRADE = new Grade("Assignment 1", new BigDecimal("97.5"));

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GradeByNameAndClassCommand(null, "A1", GRADE));
        assertThrows(NullPointerException.class, () -> new GradeByNameAndClassCommand("Amy", null, GRADE));
        assertThrows(NullPointerException.class, () -> new GradeByNameAndClassCommand("Amy", "A1", null));
    }

    @Test
    public void execute_validNameAndClass_recordsGrade() {
        Person person = model.getFilteredPersonList().get(0);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withGrade(GRADE));

        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand(
                person.getName().toString(), person.getClassName().toString(), GRADE);
        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + person.getName() + " in class " + person.getClassName() + ".";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameAndClassWithDifferentCaseAndSpaces_recordsGrade() {
        Person person = model.getFilteredPersonList().get(0);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withGrade(GRADE));

        String name = "  " + person.getName().toString().toUpperCase().replace(" ", "   ") + " ";
        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand(
                name, person.getClassName().toString().toLowerCase(), GRADE);
        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + person.getName() + " in class " + person.getClassName() + ".";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personNotDisplayed_recordsGrade() {
        Person hiddenPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertFalse(model.getFilteredPersonList().contains(hiddenPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(hiddenPerson, hiddenPerson.withGrade(GRADE));

        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand(
                hiddenPerson.getName().toString(), hiddenPerson.getClassName().toString(), GRADE);
        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + hiddenPerson.getName() + " in class " + hiddenPerson.getClassName() + ".";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameOrClassNotValidForAContact_throwsContactNotFound() {
        assertCommandFailure(new GradeByNameAndClassCommand("Not, A Name", "A1", GRADE), model,
                Messages.MESSAGE_CONTACT_NOT_FOUND);
        assertCommandFailure(new GradeByNameAndClassCommand("Amy Bee", "!!!", GRADE), model,
                Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_unknownNameAndClass_throwsCommandException() {
        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand("Unknown Student", "A1", GRADE);

        assertCommandFailure(command, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_duplicateAssessment_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(0);
        model.setPerson(person, person.withGrade(new Grade("Assignment 1", BigDecimal.TEN)));

        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand(
                person.getName().toString(), person.getClassName().toString(), GRADE);

        assertCommandFailure(command, model,
                String.format(GradeCommand.MESSAGE_DUPLICATE_GRADE, person.getName(), "10", "Assignment 1"));
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand("Amy", "A1", GRADE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new GradeByNameAndClassCommand("Amy", "A1",
                new Grade("Assignment 1", new BigDecimal("97.50")))));

        assertFalse(command.equals(null));
        assertFalse(command.equals("not a GradeByNameAndClassCommand"));
        assertFalse(command.equals(new GradeByNameAndClassCommand("Benson", "A1", GRADE)));
        assertFalse(command.equals(new GradeByNameAndClassCommand("Amy", "A2", GRADE)));
        assertFalse(command.equals(new GradeByNameAndClassCommand("Amy", "A1",
                new Grade("Assignment 1", BigDecimal.ONE))));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        GradeByNameAndClassCommand command = new GradeByNameAndClassCommand("Alex Yeoh", "A1", GRADE);
        String expected = GradeByNameAndClassCommand.class.getCanonicalName()
                + "{name=Alex Yeoh, className=A1, grade=" + GRADE + "}";

        assertEquals(expected, command.toString());
    }

}
