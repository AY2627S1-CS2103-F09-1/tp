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
import static seedu.address.testutil.TypicalRecords.ALICE_WITH_ATTENDANCE;
import static seedu.address.testutil.TypicalRecords.DATE;
import static seedu.address.testutil.TypicalRecords.OTHER_ATTENDANCE;
import static seedu.address.testutil.TypicalRecords.OTHER_DATE;
import static seedu.address.testutil.TypicalRecords.getModelWithAliceAttendance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteAttendanceByIndexCommand}.
 */
public class DeleteAttendanceByIndexCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = getModelWithAliceAttendance();
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteAttendanceByIndexCommand(null, DATE));
        assertThrows(NullPointerException.class, () ->
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(ALICE_WITH_ATTENDANCE, ALICE.withAttendance(OTHER_ATTENDANCE));

        String expectedMessage = String.format(DeleteAttendanceCommand.MESSAGE_SUCCESS,
                "absent", ALICE.getName(), Attendance.formatDate(DATE));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        // BVA: list size + 1 (first index outside displayed-list partition)
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteAttendanceByIndexCommand command = new DeleteAttendanceByIndexCommand(outOfBoundsIndex, DATE);

        assertCommandFailure(command, model,
                Messages.getInvalidDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(ALICE_WITH_ATTENDANCE, ALICE.withAttendance(OTHER_ATTENDANCE));
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteAttendanceCommand.MESSAGE_SUCCESS,
                "absent", ALICE.getName(), Attendance.formatDate(DATE));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        // Index valid in address book but in the invalid EP for filtered list
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_SECOND_PERSON, DATE);

        assertCommandFailure(command, model,
                Messages.getInvalidDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        DeleteAttendanceByIndexCommand deleteFirstDate =
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE);

        assertTrue(deleteFirstDate.equals(deleteFirstDate));
        assertTrue(deleteFirstDate.equals(new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE)));
        assertFalse(deleteFirstDate.equals(null));
        assertFalse(deleteFirstDate.equals(1));
        assertFalse(deleteFirstDate.equals(new DeleteAttendanceByIndexCommand(INDEX_SECOND_PERSON, DATE)));
        assertFalse(deleteFirstDate.equals(new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, OTHER_DATE)));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE);
        String expected = DeleteAttendanceByIndexCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", date=" + DATE + "}";

        assertEquals(expected, command.toString());
    }
}
