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

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteAttendanceByIndexCommand}.
 */
public class DeleteAttendanceByIndexCommandTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 16);
    private static final LocalDate OTHER_DATE = LocalDate.of(2026, 9, 17);
    private static final Attendance ATTENDANCE = new Attendance(DATE, Attendance.Status.ABSENT);
    private static final Attendance OTHER_ATTENDANCE = new Attendance(OTHER_DATE, Attendance.Status.PRESENT);

    private Model model;
    private Person aliceWithAttendance;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        aliceWithAttendance = ALICE.withAttendance(ATTENDANCE).withAttendance(OTHER_ATTENDANCE);
        model.setPerson(ALICE, aliceWithAttendance);
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
        expectedModel.setPerson(aliceWithAttendance, ALICE.withAttendance(OTHER_ATTENDANCE));

        String expectedMessage = String.format(DeleteAttendanceCommand.MESSAGE_SUCCESS,
                "absent", ALICE.getName(), Attendance.formatDate(DATE));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_yearZeroDate_messageShowsYearZero() throws Exception {
        LocalDate yearZero = LocalDate.of(0, 1, 1);
        Attendance yearZeroAttendance = new Attendance(yearZero, Attendance.Status.LATE);
        model.setPerson(aliceWithAttendance, aliceWithAttendance.withAttendance(yearZeroAttendance));

        CommandResult result = new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, yearZero).execute(model);

        assertEquals("Attendance of late for " + ALICE.getName() + " on 01-01-0000 has been deleted",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_validIndex_showsStoredStatusAndPersonDetails() throws Exception {
        CommandResult result = new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE).execute(model);

        assertEquals("Attendance of absent for Alice Pauline on 16-09-2026 has been deleted",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_attendanceNotFound_throwsCommandException() {
        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE.minusDays(1));

        assertCommandFailure(command, model, DeleteAttendanceCommand.MESSAGE_ATTENDANCE_NOT_FOUND);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteAttendanceByIndexCommand command = new DeleteAttendanceByIndexCommand(outOfBoundsIndex, DATE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithAttendance, ALICE.withAttendance(OTHER_ATTENDANCE));
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteAttendanceCommand.MESSAGE_SUCCESS,
                "absent", ALICE.getName(), Attendance.formatDate(DATE));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteAttendanceByIndexCommand command =
                new DeleteAttendanceByIndexCommand(INDEX_SECOND_PERSON, DATE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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
