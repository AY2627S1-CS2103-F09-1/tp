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

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code GradeByIndexCommand}.
 */
public class GradeByIndexCommandTest {

    private static final Grade GRADE = new Grade("Assignment 1", new BigDecimal("97.5"));

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GradeByIndexCommand(null, GRADE));
        assertThrows(NullPointerException.class, () -> new GradeByIndexCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_validIndexUnfilteredList_recordsGrade() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withGrade(GRADE));

        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + person.getName() + " in class " + person.getClassName() + ".";

        assertCommandSuccess(new GradeByIndexCommand(INDEX_FIRST_PERSON, GRADE), model, expectedMessage,
                expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_recordsGradeOfDisplayedPerson() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(person, person.withGrade(GRADE));

        String expectedMessage = "Grade of 97.5 recorded for Assignment 1 for student "
                + person.getName() + " in class " + person.getClassName() + ".";

        assertCommandSuccess(new GradeByIndexCommand(INDEX_FIRST_PERSON, GRADE), model, expectedMessage,
                expectedModel);
    }

    @Test
    public void execute_scoreWithTrailingZeros_messageHidesZeros() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Grade grade = new Grade("Quiz", new BigDecimal("100.00"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withGrade(grade));

        String expectedMessage = "Grade of 100 recorded for Quiz for student "
                + person.getName() + " in class " + person.getClassName() + ".";

        assertCommandSuccess(new GradeByIndexCommand(INDEX_FIRST_PERSON, grade), model, expectedMessage,
                expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new GradeByIndexCommand(outOfBoundIndex, GRADE), model,
                Messages.getInvalidDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertCommandFailure(new GradeByIndexCommand(INDEX_SECOND_PERSON, GRADE), model,
                Messages.getInvalidDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_duplicateAssessment_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(person, person.withGrade(new Grade("Assignment 1", BigDecimal.TEN)));

        assertCommandFailure(new GradeByIndexCommand(INDEX_FIRST_PERSON, GRADE), model,
                String.format(GradeCommand.MESSAGE_DUPLICATE_GRADE, person.getName(), "10", "Assignment 1"));
    }

    @Test
    public void execute_duplicateAssessmentDifferentCaseAndSpaces_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(person, person.withGrade(new Grade("Assignment 1", BigDecimal.TEN)));

        GradeByIndexCommand command = new GradeByIndexCommand(INDEX_FIRST_PERSON,
                new Grade("  ASSIGNMENT   1 ", BigDecimal.ONE));

        assertCommandFailure(command, model,
                String.format(GradeCommand.MESSAGE_DUPLICATE_GRADE, person.getName(), "10", "Assignment 1"));
    }

    @Test
    public void execute_validGrade_gradeIsPresentInModel() throws Exception {
        new GradeByIndexCommand(INDEX_FIRST_PERSON, GRADE).execute(model);

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(updatedPerson.hasGradeFor(GRADE));
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        GradeByIndexCommand command = new GradeByIndexCommand(INDEX_FIRST_PERSON, GRADE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new GradeByIndexCommand(INDEX_FIRST_PERSON,
                new Grade("Assignment 1", new BigDecimal("97.50")))));

        assertFalse(command.equals(null));
        assertFalse(command.equals("not a GradeByIndexCommand"));
        assertFalse(command.equals(new GradeByIndexCommand(INDEX_SECOND_PERSON, GRADE)));
        assertFalse(command.equals(new GradeByIndexCommand(INDEX_FIRST_PERSON,
                new Grade("Assignment 1", BigDecimal.ONE))));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        GradeByIndexCommand command = new GradeByIndexCommand(INDEX_FIRST_PERSON, GRADE);
        String expected = GradeByIndexCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", grade=" + GRADE + "}";

        assertEquals(expected, command.toString());
    }

}
