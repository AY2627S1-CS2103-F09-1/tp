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
import seedu.address.model.person.Grade;
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

        ViewGradeCommand command = viewGradeOf(person);
        CommandResult result = command.execute(model);

        assertEquals("Here are the grades for " + person.getName() + " from class " + person.getClassName()
                        + "\nQuiz 3: 90\nQuiz 2: 85\nQuiz 1: 80", result.getFeedbackToUser());
    }

    @Test
    public void execute_studentWithoutGrades_showsEmptyMessage() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        ViewGradeCommand command = viewGradeOf(person);

        CommandResult result = command.execute(model);

        assertEquals("Here are the grades for " + person.getName() + " from class " + person.getClassName()
                        + "\n" + ViewGradeCommand.MESSAGE_NO_GRADES, result.getFeedbackToUser());
    }

    @Test
    public void execute_unknownStudent_throwsCommandException() {
        ViewGradeCommand command = new ViewGradeCommand("Unknown Student", "A1");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
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

        ViewGradeCommand command = viewGradeOf(targetPerson);
        String output = command.execute(testModel).getFeedbackToUser();
        assertTrue(output.contains("Assignment 1: 97"));
        assertFalse(output.contains("Quiz 1"));
    }

    @Test
    public void execute_nameAndClassInOtherCaseAndSpacing_findsStudent() throws Exception {
        Person person = new PersonBuilder().withName("Alex Tan").withClassName("A1").build()
                .withGrade(new Grade("Quiz 1", new BigDecimal("80")));
        Model testModel = new ModelManager();
        testModel.addPerson(person);

        String output = new ViewGradeCommand("  aLEX    tan ", "a1").execute(testModel).getFeedbackToUser();

        assertTrue(output.contains("Quiz 1: 80"));
    }

    @Test
    public void execute_nameOrClassThatNoContactCanHave_throwsContactNotFound() {
        // such a value is not checked as a name or class, and simply matches no contact
        for (ViewGradeCommand command : new ViewGradeCommand[] {
            new ViewGradeCommand("Tan, John", "A1"), new ViewGradeCommand("Alex Tan", "A1&")}) {
            CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

            assertEquals(seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
        }
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        ViewGradeCommand command = new ViewGradeCommand("Alex Tan", "A1");

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewGradeCommand("Alex Tan", "A1")));
        assertFalse(command.equals(null));
        assertFalse(command.equals("not a ViewGradeCommand"));
        assertFalse(command.equals(new ViewGradeCommand("Jamie Tan", "A1")));
        assertFalse(command.equals(new ViewGradeCommand("Alex Tan", "A2")));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        ViewGradeCommand command = new ViewGradeCommand("Alex Tan", "A1");

        String expected = ViewGradeCommand.class.getCanonicalName()
            + "{name=Alex Tan, className=A1}";

        assertEquals(expected, command.toString());
    }

    private static ViewGradeCommand viewGradeOf(Person person) {
        return new ViewGradeCommand(person.getName().toString(), person.getClassName().toString());
    }

}
