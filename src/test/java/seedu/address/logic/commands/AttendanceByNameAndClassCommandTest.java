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

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code AttendanceByNameAndClassCommand}.
 */
public class AttendanceByNameAndClassCommandTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 16);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendanceByNameAndClassCommand(null, "A1", DATE, Attendance.Status.PRESENT));
        assertThrows(NullPointerException.class, () ->
                new AttendanceByNameAndClassCommand("Amy", null, DATE, Attendance.Status.PRESENT));
        assertThrows(NullPointerException.class, () ->
                new AttendanceByNameAndClassCommand("Amy", "A1", null, Attendance.Status.PRESENT));
        assertThrows(NullPointerException.class, () ->
                new AttendanceByNameAndClassCommand("Amy", "A1", DATE, null));
    }

    @Test
    public void execute_validNameAndClass_recordsAttendance() {
        Person person = model.getFilteredPersonList().get(0);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withAttendance(new Attendance(DATE, Attendance.Status.ABSENT)));

        AttendanceByNameAndClassCommand command = new AttendanceByNameAndClassCommand(
                person.getName().toString(), person.getClassName().toString(), DATE, Attendance.Status.ABSENT);
        String expectedMessage = "Attendance for " + person.getName() + " on 16-09-2026 recorded as absent.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameAndClassWithDifferentCaseAndSpaces_recordsAttendance() {
        Person person = model.getFilteredPersonList().get(0);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, person.withAttendance(new Attendance(DATE, Attendance.Status.PRESENT)));

        String name = "  " + person.getName().toString().toUpperCase().replace(" ", "   ") + " ";
        AttendanceByNameAndClassCommand command = new AttendanceByNameAndClassCommand(
                name, person.getClassName().toString().toLowerCase(), DATE, Attendance.Status.PRESENT);
        String expectedMessage = "Attendance for " + person.getName() + " on 16-09-2026 recorded as present.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personNotDisplayed_recordsAttendance() {
        Person hiddenPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertFalse(model.getFilteredPersonList().contains(hiddenPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(hiddenPerson,
                hiddenPerson.withAttendance(new Attendance(DATE, Attendance.Status.LATE)));

        AttendanceByNameAndClassCommand command = new AttendanceByNameAndClassCommand(
                hiddenPerson.getName().toString(), hiddenPerson.getClassName().toString(), DATE,
                Attendance.Status.LATE);
        String expectedMessage = "Attendance for " + hiddenPerson.getName() + " on 16-09-2026 recorded as late.";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameOrClassNotValidForAContact_throwsContactNotFound() {
        AttendanceByNameAndClassCommand invalidName = new AttendanceByNameAndClassCommand(
                "Not, A Name", "A1", DATE, Attendance.Status.PRESENT);
        AttendanceByNameAndClassCommand invalidClass = new AttendanceByNameAndClassCommand(
                "Amy Bee", "!!!", DATE, Attendance.Status.PRESENT);

        assertCommandFailure(invalidName, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
        assertCommandFailure(invalidClass, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_unknownNameAndClass_throwsCommandException() {
        AttendanceByNameAndClassCommand command = new AttendanceByNameAndClassCommand(
                "Unknown Student", "A1", DATE, Attendance.Status.PRESENT);

        assertCommandFailure(command, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_futureDate_throwsCommandException() {
        AttendanceByNameAndClassCommand command = new AttendanceByNameAndClassCommand(
                "Amy", "A1", LocalDate.now().plusDays(1), Attendance.Status.PRESENT);

        assertCommandFailure(command, model, AttendanceCommand.MESSAGE_FUTURE_DATE);
    }

    @Test
    public void execute_duplicateDate_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(0);
        model.setPerson(person, person.withAttendance(new Attendance(DATE, Attendance.Status.PRESENT)));

        AttendanceByNameAndClassCommand command = new AttendanceByNameAndClassCommand(
                person.getName().toString(), person.getClassName().toString(), DATE, Attendance.Status.ABSENT);

        assertCommandFailure(command, model, String.format(AttendanceCommand.MESSAGE_DUPLICATE_ATTENDANCE,
                person.getName(), "16-09-2026", Attendance.Status.PRESENT));
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        AttendanceByNameAndClassCommand command =
                new AttendanceByNameAndClassCommand("Amy", "A1", DATE, Attendance.Status.PRESENT);

        assertTrue(command.equals(command));
        assertTrue(command.equals(
                new AttendanceByNameAndClassCommand("Amy", "A1", DATE, Attendance.Status.PRESENT)));

        assertFalse(command.equals(null));
        assertFalse(command.equals("not an AttendanceByNameAndClassCommand"));
        assertFalse(command.equals(
                new AttendanceByNameAndClassCommand("Benson", "A1", DATE, Attendance.Status.PRESENT)));
        assertFalse(command.equals(
                new AttendanceByNameAndClassCommand("Amy", "A2", DATE, Attendance.Status.PRESENT)));
        assertFalse(command.equals(
                new AttendanceByNameAndClassCommand("Amy", "A1", DATE.plusDays(1), Attendance.Status.PRESENT)));
        assertFalse(command.equals(
                new AttendanceByNameAndClassCommand("Amy", "A1", DATE, Attendance.Status.ABSENT)));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        AttendanceByNameAndClassCommand command =
                new AttendanceByNameAndClassCommand("Alex Yeoh", "A1", DATE, Attendance.Status.PRESENT);
        String expected = AttendanceByNameAndClassCommand.class.getCanonicalName()
                + "{name=Alex Yeoh, className=A1, date=" + DATE + ", status=present}";

        assertEquals(expected, command.toString());
    }

}
