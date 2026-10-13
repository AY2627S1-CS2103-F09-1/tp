package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

public class ViewAttendanceCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_studentWithRecords_returnsAllRecordsChronologically() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        Person updated = person.withAttendance(new Attendance(LocalDate.of(2026, 9, 23), Attendance.Status.PRESENT))
                .withAttendance(new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.ABSENT));
        model.setPerson(person, updated);

        ViewAttendanceCommand command = new ViewAttendanceCommand(person.getName(), person.getClassName());
        CommandResult result = command.execute(model);

        assertEquals("Here is the attendance for " + person.getName() + " from class " + person.getClassName()
                + "\n16-09-2026  absent\n23-09-2026  present", result.getFeedbackToUser());
    }

    @Test
    public void execute_studentWithoutRecords_showsEmptyMessage() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        ViewAttendanceCommand command = new ViewAttendanceCommand(person.getName(), person.getClassName());

        CommandResult result = command.execute(model);

        assertEquals("Here is the attendance for " + person.getName() + " from class " + person.getClassName()
                + "\n" + ViewAttendanceCommand.MESSAGE_NO_RECORDS, result.getFeedbackToUser());
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        ViewAttendanceCommand command = new ViewAttendanceCommand(new Name("Unknown Student"), new ClassName("A1"));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(ViewAttendanceCommand.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
    }

}
