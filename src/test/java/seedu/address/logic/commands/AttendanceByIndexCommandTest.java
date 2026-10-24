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

import java.time.LocalDate;

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
 * {@code AttendanceByIndexCommand}.
 */
public class AttendanceByIndexCommandTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 16);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendanceByIndexCommand(null, DATE, Attendance.Status.PRESENT));
        assertThrows(NullPointerException.class, () ->
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, null, Attendance.Status.PRESENT));
        assertThrows(NullPointerException.class, () ->
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, null));
    }

    @Test
    public void execute_validIndexUnfilteredList_recordsAttendance() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withAttendance(new Attendance(DATE, Attendance.Status.ABSENT)));

        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.ABSENT);
        String expectedMessage = "Attendance for " + person.getName() + " on 16-09-2026 recorded as absent.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_recordsAttendanceOfDisplayedPerson() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(person, person.withAttendance(new Attendance(DATE, Attendance.Status.LATE)));

        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.LATE);
        String expectedMessage = "Attendance for " + person.getName() + " on 16-09-2026 recorded as late.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_todayDate_recordsAttendance() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        LocalDate today = LocalDate.now();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withAttendance(new Attendance(today, Attendance.Status.PRESENT)));

        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, today, Attendance.Status.PRESENT);
        String expectedMessage = "Attendance for " + person.getName() + " on " + Attendance.formatDate(today)
                + " recorded as present.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_yearZeroDate_messageShowsYearZero() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        LocalDate date = LocalDate.of(0, 1, 1);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withAttendance(new Attendance(date, Attendance.Status.LATE)));

        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, date, Attendance.Status.LATE);
        String expectedMessage = "Attendance for " + person.getName() + " on 01-01-0000 recorded as late.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(outOfBoundIndex, DATE, Attendance.Status.PRESENT);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_SECOND_PERSON, DATE, Attendance.Status.PRESENT);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_futureDate_throwsCommandException() {
        AttendanceByIndexCommand command = new AttendanceByIndexCommand(
                INDEX_FIRST_PERSON, LocalDate.now().plusDays(1), Attendance.Status.PRESENT);

        assertCommandFailure(command, model, AttendanceCommand.MESSAGE_FUTURE_DATE);
    }

    @Test
    public void execute_futureDateAndInvalidIndex_reportsFutureDate() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AttendanceByIndexCommand command = new AttendanceByIndexCommand(
                outOfBoundIndex, LocalDate.now().plusDays(1), Attendance.Status.PRESENT);

        assertCommandFailure(command, model, AttendanceCommand.MESSAGE_FUTURE_DATE);
    }

    @Test
    public void execute_duplicateDate_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(person, person.withAttendance(new Attendance(DATE, Attendance.Status.PRESENT)));

        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.ABSENT);

        assertCommandFailure(command, model, AttendanceCommand.MESSAGE_DUPLICATE_ATTENDANCE);
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.PRESENT);

        assertTrue(command.equals(command));
        assertTrue(command.equals(
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.PRESENT)));

        assertFalse(command.equals(null));
        assertFalse(command.equals("not an AttendanceByIndexCommand"));
        assertFalse(command.equals(
                new AttendanceByIndexCommand(INDEX_SECOND_PERSON, DATE, Attendance.Status.PRESENT)));
        assertFalse(command.equals(
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE.plusDays(1), Attendance.Status.PRESENT)));
        assertFalse(command.equals(
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.ABSENT)));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        AttendanceByIndexCommand command =
                new AttendanceByIndexCommand(INDEX_FIRST_PERSON, DATE, Attendance.Status.PRESENT);
        String expected = AttendanceByIndexCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", date=" + DATE + ", status=present}";

        assertEquals(expected, command.toString());
    }

}
