package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ViewAttendanceCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_studentWithRecords_returnsAllRecordsChronologically() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        Person updated = person.withAttendance(new Attendance(LocalDate.of(2026, 9, 23), Attendance.Status.PRESENT))
                .withAttendance(new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.ABSENT));
        model.setPerson(person, updated);

        ViewAttendanceCommand command = new ViewAttendanceCommand(
                person.getName().toString(), person.getClassName().toString());
        CommandResult result = command.execute(model);

        assertEquals("Here is the attendance for " + person.getName() + " from class " + person.getClassName()
                + "\n16-Sep-2026  absent\n23-Sep-2026  present", result.getFeedbackToUser());
    }

    @Test
    public void execute_studentWithoutRecords_showsEmptyMessage() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        ViewAttendanceCommand command = new ViewAttendanceCommand(
                person.getName().toString(), person.getClassName().toString());

        CommandResult result = command.execute(model);

        assertEquals("Here is the attendance for " + person.getName() + " from class " + person.getClassName()
                + "\n" + ViewAttendanceCommand.MESSAGE_NO_RECORDS, result.getFeedbackToUser());
    }

    @Test
    public void execute_caseAndExtraSpacesDiffer_findsStudent() throws Exception {
        Person person = new PersonBuilder().withName("John Tan").withClassName("A1").build();
        Model testModel = new ModelManager();
        testModel.addPerson(person);

        ViewAttendanceCommand command = new ViewAttendanceCommand("  john   TAN ", "a1");

        assertEquals("Here is the attendance for John Tan from class A1\n"
                + ViewAttendanceCommand.MESSAGE_NO_RECORDS, command.execute(testModel).getFeedbackToUser());
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        ViewAttendanceCommand command = new ViewAttendanceCommand("Unknown Student", "A1");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(ViewAttendanceCommand.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void execute_nameAndClassNotValidForAdding_throwsContactNotFound() {
        ViewAttendanceCommand command = new ViewAttendanceCommand("@@@", "!!!");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(ViewAttendanceCommand.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void execute_sameNameDifferentClass_selectsRequestedClass() throws Exception {
        Person otherClassPerson = new PersonBuilder().withName("Alex Tan").withClassName("A2").build()
            .withAttendance(new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.ABSENT));

        Person targetPerson = new PersonBuilder().withName("Alex Tan").withClassName("A1").build()
            .withAttendance(new Attendance(LocalDate.of(2026, 9, 23), Attendance.Status.PRESENT));

        Model testModel = new ModelManager();
        testModel.addPerson(otherClassPerson);
        testModel.addPerson(targetPerson);

        ViewAttendanceCommand command = new ViewAttendanceCommand("Alex Tan", "A1");

        String output = command.execute(testModel).getFeedbackToUser();

        assertTrue(output.contains("present"));
        assertFalse(output.contains("absent"));
    }

    @Test
    public void equals() {
        ViewAttendanceCommand command = new ViewAttendanceCommand("Alex Tan", "A1");

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewAttendanceCommand("Alex Tan", "A1")));
        assertFalse(command.equals(null));
        assertFalse(command.equals("not a ViewAttendanceCommand"));
        assertFalse(command.equals(new ViewAttendanceCommand("Jamie Tan", "A1")));
        assertFalse(command.equals(new ViewAttendanceCommand("Alex Tan", "A2")));
    }

    @Test
    public void toStringMethod() {
        ViewAttendanceCommand command = new ViewAttendanceCommand("Alex Tan", "A1");

        String expected = ViewAttendanceCommand.class.getCanonicalName()
            + "{name=Alex Tan, className=A1}";

        assertEquals(expected, command.toString());
    }

}
