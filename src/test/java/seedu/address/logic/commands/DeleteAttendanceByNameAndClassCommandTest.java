package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteAttendanceByNameAndClassCommand}.
 */
public class DeleteAttendanceByNameAndClassCommandTest {

    private static final String ALICE_NAME = ALICE.getName().toString();
    private static final String ALICE_CLASS = ALICE.getClassName().toString();
    private static final LocalDate DATE = LocalDate.of(2026, 9, 16);
    private static final LocalDate OTHER_DATE = LocalDate.of(2026, 9, 17);
    private static final Attendance ATTENDANCE = new Attendance(DATE, Attendance.Status.LATE);
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
        assertThrows(NullPointerException.class, () ->
                new DeleteAttendanceByNameAndClassCommand(null, ALICE_CLASS, DATE));
        assertThrows(NullPointerException.class, () ->
                new DeleteAttendanceByNameAndClassCommand(ALICE_NAME, null, DATE));
        assertThrows(NullPointerException.class, () ->
                new DeleteAttendanceByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, null));
    }

    @Test
    public void execute_nameAndClassMatch_success() {
        DeleteAttendanceByNameAndClassCommand command =
                new DeleteAttendanceByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, DATE);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithAttendance, ALICE.withAttendance(OTHER_ATTENDANCE));

        String expectedMessage = String.format(DeleteAttendanceCommand.MESSAGE_SUCCESS,
                "late", ALICE.getName(), Attendance.formatDate(DATE));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_caseAndExtraSpacesIgnored_successShowsStoredDetails() {
        DeleteAttendanceByNameAndClassCommand command =
                new DeleteAttendanceByNameAndClassCommand("  aLiCe   Pauline ", "a1", DATE);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithAttendance, ALICE.withAttendance(OTHER_ATTENDANCE));

        assertCommandSuccess(command, model,
                "Attendance of late for Alice Pauline on 16-09-2026 has been deleted", expectedModel);
    }

    @Test
    public void execute_personHiddenByFilter_success() {
        Person bensonWithAttendance = BENSON.withAttendance(ATTENDANCE);
        model.setPerson(BENSON, bensonWithAttendance);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertFalse(model.getFilteredPersonList().contains(bensonWithAttendance));

        DeleteAttendanceByNameAndClassCommand command = new DeleteAttendanceByNameAndClassCommand(
                BENSON.getName().toString(), BENSON.getClassName().toString(), DATE);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(bensonWithAttendance, BENSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(command, model,
                "Attendance of late for Benson Meier on 16-09-2026 has been deleted", expectedModel);
    }

    @Test
    public void execute_attendanceNotFound_throwsCommandException() {
        DeleteAttendanceByNameAndClassCommand command =
                new DeleteAttendanceByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, DATE.minusDays(1));

        assertCommandFailure(command, model, DeleteAttendanceCommand.MESSAGE_ATTENDANCE_NOT_FOUND);
    }

    @Test
    public void execute_noMatchingContact_throwsCommandException() {
        DeleteAttendanceByNameAndClassCommand command =
                new DeleteAttendanceByNameAndClassCommand("Nobody Here", ALICE_CLASS, DATE);

        assertCommandFailure(command, model, DeleteAttendanceByNameAndClassCommand.MESSAGE_NOT_FOUND);
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        DeleteAttendanceByNameAndClassCommand deleteAliceDate =
                new DeleteAttendanceByNameAndClassCommand("Alice", "A1", DATE);

        assertTrue(deleteAliceDate.equals(deleteAliceDate));
        assertTrue(deleteAliceDate.equals(
                new DeleteAttendanceByNameAndClassCommand("Alice", "A1", DATE)));
        assertFalse(deleteAliceDate.equals(null));
        assertFalse(deleteAliceDate.equals(1));
        assertFalse(deleteAliceDate.equals(
                new DeleteAttendanceByNameAndClassCommand("Bob", "A1", DATE)));
        assertFalse(deleteAliceDate.equals(
                new DeleteAttendanceByNameAndClassCommand("Alice", "B2", DATE)));
        assertFalse(deleteAliceDate.equals(
                new DeleteAttendanceByNameAndClassCommand("Alice", "A1", OTHER_DATE)));
        assertFalse(deleteAliceDate.equals(new DeleteAttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE)));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        DeleteAttendanceByNameAndClassCommand command =
                new DeleteAttendanceByNameAndClassCommand("Alice", "A1", DATE);
        String expected = DeleteAttendanceByNameAndClassCommand.class.getCanonicalName()
                + "{name=Alice, className=A1, date=" + DATE + "}";

        assertEquals(expected, command.toString());
    }
}
