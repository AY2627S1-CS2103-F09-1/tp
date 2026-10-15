package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ViewGradeCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_studentWithGrades_returnsNewestFirst() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        Person updated = person.withGrade(new Grade("Quiz 1", new BigDecimal("80")))
                .withGrade(new Grade("Quiz 2", new BigDecimal("85")))
                .withGrade(new Grade("Quiz 3", new BigDecimal("90")));
        model.setPerson(person, updated);

        ViewGradeCommand command = new ViewGradeCommand(person.getName(), person.getClassName());
        CommandResult result = command.execute(model);

        assertEquals("Here are the grades for " + person.getName() + " from class " + person.getClassName()
                        + "\nQuiz 3: 90\nQuiz 2: 85\nQuiz 1: 80", result.getFeedbackToUser());
    }

    @Test
    public void execute_studentWithoutGrades_showsEmptyMessage() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        ViewGradeCommand command = new ViewGradeCommand(person.getName(), person.getClassName());

        CommandResult result = command.execute(model);

        assertEquals("Here are the grades for " + person.getName() + " from class " + person.getClassName()
                        + "\n" + ViewGradeCommand.MESSAGE_NO_GRADES, result.getFeedbackToUser());
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        ViewGradeCommand command = new ViewGradeCommand(new Name("Unknown Student"), new ClassName("A1"));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(ViewGradeCommand.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void execute_sameNameDifferentClass_selectsRequestedClass() throws Exception {
        Person otherClassPerson = new PersonBuilder().withName("Alex Tan").withClassName("A2").build()
            .withGrade(new Grade("Quiz 1", new BigDecimal("50")));
        Person targetPerson = new PersonBuilder().withName("Alex Tan").withClassName("A1").build()
            .withGrade(new Grade("Assignment 1", new BigDecimal("97")));

        Model testModel = new ModelManager();
        testModel.addPerson(otherClassPerson);
        testModel.addPerson(targetPerson);

        ViewGradeCommand command = new ViewGradeCommand(targetPerson.getName(), targetPerson.getClassName());
        String output = command.execute(testModel).getFeedbackToUser();
        assertTrue(output.contains("Assignment 1: 97"));
        assertFalse(output.contains("Quiz 1"));
    }

    @Test
    public void equals() {
        ViewGradeCommand command = new ViewGradeCommand(new Name("Alex Tan"), new ClassName("A1"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewGradeCommand(new Name("Alex Tan"), new ClassName("A1"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("not a ViewGradeCommand"));
        assertFalse(command.equals(new ViewGradeCommand(new Name("Jamie Tan"), new ClassName("A1"))));
        assertFalse(command.equals(new ViewGradeCommand(new Name("Alex Tan"), new ClassName("A2"))));
    }

    @Test
    public void toStringMethod() {
        Name name = new Name("Alex Tan");
        ClassName className = new ClassName("A1");
        ViewGradeCommand command = new ViewGradeCommand(name, className);

        String expected = ViewGradeCommand.class.getCanonicalName()
            + "{name=" + name + ", className=" + className + "}";

        assertEquals(expected, command.toString());
    }

}
