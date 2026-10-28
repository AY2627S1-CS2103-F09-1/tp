package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalRecords.ALICE_WITH_ATTENDANCE;
import static seedu.address.testutil.TypicalRecords.DATE;
import static seedu.address.testutil.TypicalRecords.OTHER_ATTENDANCE;
import static seedu.address.testutil.TypicalRecords.getModelWithAliceAttendance;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/** Contains tests for the shared attendance-deletion behavior. */
public class DeleteAttendanceCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = getModelWithAliceAttendance();
    }

    @Test
    public void deleteAttendance_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                DeleteAttendanceCommand.deleteAttendance(null, ALICE_WITH_ATTENDANCE, DATE));
        assertThrows(NullPointerException.class, () ->
                DeleteAttendanceCommand.deleteAttendance(model, null, DATE));
        assertThrows(NullPointerException.class, () ->
                DeleteAttendanceCommand.deleteAttendance(model, ALICE_WITH_ATTENDANCE, null));
    }

    @Test
    public void deleteAttendance_personNotInModel_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                DeleteAttendanceCommand.deleteAttendance(new ModelManager(), ALICE_WITH_ATTENDANCE, DATE));
    }

    @Test
    public void deleteAttendance_personDoesNotRemoveRecord_throwsAssertionError() {
        Person personThatKeepsAttendance = new Person(ALICE_WITH_ATTENDANCE.getName(),
                ALICE_WITH_ATTENDANCE.getClassName(), ALICE_WITH_ATTENDANCE.getEmail(),
                ALICE_WITH_ATTENDANCE.getTags(), ALICE_WITH_ATTENDANCE.getAttendanceRecords(),
                ALICE_WITH_ATTENDANCE.getGradeRecords()) {
            @Override
            public Person withoutAttendance(Attendance attendance) {
                return this;
            }
        };
        Model modelWithFaultyPerson = new ModelManager();
        modelWithFaultyPerson.addPerson(personThatKeepsAttendance);

        assertThrows(AssertionError.class, () -> DeleteAttendanceCommand.deleteAttendance(
                modelWithFaultyPerson, personThatKeepsAttendance, DATE));
    }

    @Test
    public void deleteAttendance_existingRecord_removesRecordAndShowsStoredDetails() throws CommandException {
        CommandResult result = DeleteAttendanceCommand.deleteAttendance(model, ALICE_WITH_ATTENDANCE, DATE);

        Model expectedModel = new ModelManager(getModelWithAliceAttendance().getAddressBook(), new UserPrefs());
        expectedModel.setPerson(ALICE_WITH_ATTENDANCE, ALICE.withAttendance(OTHER_ATTENDANCE));
        assertEquals(expectedModel, model);
        assertEquals("Attendance of absent for Alice Pauline on 16-09-2026 has been deleted",
                result.getFeedbackToUser());
    }

    @Test
    public void deleteAttendance_recordNotFound_throwsCommandException() {
        assertThrows(CommandException.class, DeleteAttendanceCommand.MESSAGE_ATTENDANCE_NOT_FOUND, () ->
                DeleteAttendanceCommand.deleteAttendance(model, ALICE_WITH_ATTENDANCE, DATE.minusDays(1)));
    }

    @Test
    public void deleteAttendance_modelDoesNotStoreChangedContact_throwsAssertionError() {
        // White-box negative test for model-update-failed partition targeted by the postcondition
        Model modelThatKeepsContacts = new ModelManager(model.getAddressBook(), new UserPrefs()) {
            @Override
            public void setPerson(Person target, Person editedPerson) {
                // Intentionally does nothing to simulate a model that silently ignores the update.
            }
        };

        assertThrows(AssertionError.class, () -> DeleteAttendanceCommand.deleteAttendance(
                modelThatKeepsContacts, ALICE_WITH_ATTENDANCE, DATE));
    }

    @Test
    public void deleteAttendance_yearZeroDate_messageShowsPaddedYear() throws CommandException {
        // Year zero is lower boundary supported by LocalDate and exercises date formatting
        LocalDate yearZero = LocalDate.of(0, 1, 1);
        Attendance yearZeroAttendance = new Attendance(yearZero, Attendance.Status.LATE);
        Person personWithYearZeroAttendance = ALICE_WITH_ATTENDANCE.withAttendance(yearZeroAttendance);
        model.setPerson(ALICE_WITH_ATTENDANCE, personWithYearZeroAttendance);

        CommandResult result = DeleteAttendanceCommand.deleteAttendance(
                model, personWithYearZeroAttendance, yearZero);

        assertEquals("Attendance of late for Alice Pauline on 01-01-0000 has been deleted",
                result.getFeedbackToUser());
    }
}
