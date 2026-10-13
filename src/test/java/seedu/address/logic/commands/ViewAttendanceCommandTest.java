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
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;
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

    @Test
    public void execute_sameNameDifferentClass_selectsRequestedClass() throws Exception {
        Person otherClassPerson = new PersonBuilder().withName("Alex Tan").withClassName("A2").build()
            .withAttendance(new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.ABSENT));

        Person targetPerson = new PersonBuilder().withName("Alex Tan").withClassName("A1").build()
            .withAttendance(new Attendance(LocalDate.of(2026, 9, 23), Attendance.Status.PRESENT));

        Model testModel = new ModelManager();
        testModel.addPerson(otherClassPerson);
        testModel.addPerson(targetPerson);

        ViewAttendanceCommand command =
            new ViewAttendanceCommand(targetPerson.getName(), targetPerson.getClassName());

        String output = command.execute(testModel).getFeedbackToUser();

        assertTrue(output.contains("present"));
        assertFalse(output.contains("absent"));
    }

    @Test
    public void equals() {
        ViewAttendanceCommand command = new ViewAttendanceCommand(new Name("Alex Tan"), new ClassName("A1"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewAttendanceCommand(new Name("Alex Tan"), new ClassName("A1"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("not a ViewAttendanceCommand"));
        assertFalse(command.equals(new ViewAttendanceCommand(new Name("Jamie Tan"), new ClassName("A1"))));
        assertFalse(command.equals(new ViewAttendanceCommand(new Name("Alex Tan"), new ClassName("A2"))));
    }

    @Test
    public void toStringMethod() {
        Name name = new Name("Alex Tan");
        ClassName className = new ClassName("A1");
        ViewAttendanceCommand command = new ViewAttendanceCommand(name, className);

        String expected = ViewAttendanceCommand.class.getCanonicalName()
            + "{name=" + name + ", className=" + className + "}";

        assertEquals(expected, command.toString());
    }

}
