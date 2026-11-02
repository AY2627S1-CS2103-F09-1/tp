package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandResult.DisplayTarget.GRADE_HISTORY;
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

    // EP_1: Student exists and has one or more grades
    // Checks that multiple grades are displayed newest first.
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
        assertEquals(GRADE_HISTORY, result.getDisplayTarget());
    }

    // EP_1: Student exists but has no grades
    // Checks that an empty message is displayed
    // Boundary Value: 0
    @Test
    public void execute_studentWithoutGrades_showsEmptyMessage() throws Exception {
        Person person = model.getAddressBook().getPersonList().get(0);
        ViewGradeCommand command = viewGradeOf(person);

        CommandResult result = command.execute(model);

        assertEquals("Here are the grades for " + person.getName() + " from class " + person.getClassName()
                        + "\n" + ViewGradeCommand.MESSAGE_NO_GRADES, result.getFeedbackToUser());
    }

    // EP_2: Student exists but has the same name as another student in a different class
    // Checks that the correct student is selected based on class name
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

    // EP_2: Student exists but name and class are in different case and spacing
    // Checks that the student is still found
    @Test
    public void execute_nameAndClassInOtherCaseAndSpacing_findsStudent() throws Exception {
        Person person = new PersonBuilder().withName("Alex Tan").withClassName("A1").build()
                .withGrade(new Grade("Quiz 1", new BigDecimal("80")));
        Model testModel = new ModelManager();
        testModel.addPerson(person);

        String output = new ViewGradeCommand("  aLEX    tan ", "a1").execute(testModel).getFeedbackToUser();

        assertTrue(output.contains("Quiz 1: 80"));
    }

    // EP_3: Student does not exist
    // Checks that a command exception is thrown
    @Test
    public void execute_unknownStudent_throwsCommandException() {
        ViewGradeCommand command = new ViewGradeCommand("Unknown Student", "A1");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(seedu.address.logic.Messages.MESSAGE_CONTACT_NOT_FOUND, exception.getMessage());
    }

    // Command equality behavior
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

    // Command equality behavior
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
