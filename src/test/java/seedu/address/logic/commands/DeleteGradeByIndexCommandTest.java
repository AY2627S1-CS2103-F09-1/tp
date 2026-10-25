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
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteGradeByIndexCommand}.
 */
public class DeleteGradeByIndexCommandTest {

    private static final Grade GRADE = new Grade("Quiz 1", new BigDecimal("90.50"));
    private static final Grade OTHER_GRADE = new Grade("Exam", new BigDecimal("80"));

    private Model model;
    private Person aliceWithGrades;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        aliceWithGrades = ALICE.withGrade(GRADE).withGrade(OTHER_GRADE);
        model.setPerson(ALICE, aliceWithGrades);
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteGradeByIndexCommand(null, "Quiz 1"));
        assertThrows(NullPointerException.class, () ->
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void constructor_invalidAssessment_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 期末"));
        assertThrows(AssertionError.class, () ->
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "   "));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "quiz   1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithGrades, ALICE.withGrade(OTHER_GRADE));

        String expectedMessage = String.format(DeleteGradeCommand.MESSAGE_SUCCESS,
                "90.5", "Quiz 1", ALICE.getName(), ALICE.getClassName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndex_showsStoredGradeAndPersonDetails() throws Exception {
        CommandResult result = new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "QUIZ 1").execute(model);

        assertEquals("Grade of 90.5 for Quiz 1 for student Alice Pauline in class A1 has been deleted",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_gradeNotFound_throwsCommandException() {
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Assignment 1");

        assertCommandFailure(command, model, DeleteGradeCommand.MESSAGE_GRADE_NOT_FOUND);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(outOfBoundsIndex, "Quiz 1");

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithGrades, ALICE.withGrade(OTHER_GRADE));
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteGradeCommand.MESSAGE_SUCCESS,
                "90.5", "Quiz 1", ALICE.getName(), ALICE.getClassName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_SECOND_PERSON, "Quiz 1");

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        DeleteGradeByIndexCommand deleteFirstQuiz =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1");

        assertTrue(deleteFirstQuiz.equals(deleteFirstQuiz));
        assertTrue(deleteFirstQuiz.equals(
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1")));
        assertFalse(deleteFirstQuiz.equals(null));
        assertFalse(deleteFirstQuiz.equals(1));
        assertFalse(deleteFirstQuiz.equals(
                new DeleteGradeByIndexCommand(INDEX_SECOND_PERSON, "Quiz 1")));
        assertFalse(deleteFirstQuiz.equals(
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Exam")));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1");
        String expected = DeleteGradeByIndexCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", assessmentName=Quiz 1}";

        assertEquals(expected, command.toString());
    }
}
