package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

public class AttendanceCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_recordsAttendance() {
        Index index = Index.fromOneBased(1);
        LocalDate date = LocalDate.now().minusDays(1);
        Attendance.Status status = Attendance.Status.ABSENT;

        Person person = model.getFilteredPersonList().get(index.getZeroBased());
        Person updatedPerson = person.withAttendance(new Attendance(date, status));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(person, updatedPerson);

        AttendanceCommand command = new AttendanceCommand(index, date, status);
        String expectedMessage = String.format(
                AttendanceCommand.MESSAGE_SUCCESS, person.getName(), date, "absent");

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AttendanceCommand command = new AttendanceCommand(
                invalidIndex, LocalDate.now().minusDays(1), Attendance.Status.PRESENT);

        assertCommandFailure(command, model, seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_futureDate_throwsCommandException() {
        AttendanceCommand command = new AttendanceCommand(
                Index.fromOneBased(1), LocalDate.now().plusDays(1), Attendance.Status.PRESENT);

        assertCommandFailure(command, model, AttendanceCommand.MESSAGE_FUTURE_DATE);
    }

    @Test
    public void execute_duplicateDate_throwsCommandException() {
        Index index = Index.fromOneBased(1);
        LocalDate date = LocalDate.now().minusDays(1);
        Person person = model.getFilteredPersonList().get(index.getZeroBased());

        model.setPerson(person, person.withAttendance(new Attendance(date, Attendance.Status.PRESENT)));

        AttendanceCommand command = new AttendanceCommand(index, date, Attendance.Status.ABSENT);

        assertCommandFailure(command, model, AttendanceCommand.MESSAGE_DUPLICATE_ATTENDANCE);
    }

}
