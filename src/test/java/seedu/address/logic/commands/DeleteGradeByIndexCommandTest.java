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
import static seedu.address.testutil.TypicalRecords.ALICE_WITH_GRADES;
import static seedu.address.testutil.TypicalRecords.OTHER_GRADE;
import static seedu.address.testutil.TypicalRecords.getModelWithAliceGrades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteGradeByIndexCommand}.
 */
public class DeleteGradeByIndexCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = getModelWithAliceGrades();
    }

    // EP_1_1: Invalid constructor. Null index or null assessment name
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteGradeByIndexCommand(null, "Quiz 1"));
        assertThrows(NullPointerException.class, () ->
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, null));
    }

    // EP_1_2: Invalid constructor. Invalid assessment name
    @Test
    public void constructor_invalidAssessment_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 期末"));
        assertThrows(AssertionError.class, () ->
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "   "));
    }

    // EP_2_1: Invalid index. Unfiltered list
    // Boundary Value: list size + 1 (first index outside displayed-list partition)
    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(outOfBoundsIndex, "Quiz 1");

        assertCommandFailure(command, model,
                Messages.getInvalidDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    // EP_2_2: Invalid index. Filtered list
    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        // Index valid in address book but in the invalid EP for filtered list
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_SECOND_PERSON, "Quiz 1");

        assertCommandFailure(command, model,
                Messages.getInvalidDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    // EP_3_1: Valid index. Unfiltered list
    @Test
    public void execute_validIndexUnfilteredList_success() {
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "quiz   1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(ALICE_WITH_GRADES, ALICE.withGrade(OTHER_GRADE));

        String expectedMessage = String.format(DeleteGradeCommand.MESSAGE_SUCCESS,
                "90.5", "Quiz 1", ALICE.getName(), ALICE.getClassName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // EP_3_2: Valid index. Filtered list
    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(ALICE_WITH_GRADES, ALICE.withGrade(OTHER_GRADE));
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteGradeCommand.MESSAGE_SUCCESS,
                "90.5", "Quiz 1", ALICE.getName(), ALICE.getClassName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // Command equality behavior
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

    // Command equality behavior
    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        DeleteGradeByIndexCommand command =
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1");
        String expected = DeleteGradeByIndexCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", assessmentName=Quiz 1}";

        assertEquals(expected, command.toString());
    }

}
