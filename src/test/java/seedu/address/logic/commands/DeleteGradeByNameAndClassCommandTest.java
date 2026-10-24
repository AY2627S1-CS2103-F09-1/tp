package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteGradeByNameAndClassCommand}.
 */
public class DeleteGradeByNameAndClassCommandTest {

    private static final String ALICE_NAME = ALICE.getName().toString();
    private static final String ALICE_CLASS = ALICE.getClassName().toString();
    private static final Grade GRADE = new Grade("Quiz 1", new BigDecimal("90.50"));
    private static final Grade OTHER_GRADE = new Grade("Exam", new BigDecimal("80"));

    private Model model;
    private Person aliceWithGrades;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        aliceWithGrades = ALICE.withGrade(GRADE).withGrade(OTHER_GRADE);
        model.setPerson(ALICE, aliceWithGrades);
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new DeleteGradeByNameAndClassCommand(null, ALICE_CLASS, "Quiz 1"));
        assertThrows(NullPointerException.class, () ->
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, null, "Quiz 1"));
        assertThrows(NullPointerException.class, () ->
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, null));
    }

    @Test
    public void constructor_invalidAssessment_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, "Quiz,1"));
        assertThrows(AssertionError.class, () ->
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, "   "));
    }

    @Test
    public void execute_nameAndClassMatch_success() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, "Quiz 1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithGrades, ALICE.withGrade(OTHER_GRADE));

        String expectedMessage = String.format(DeleteGradeCommand.MESSAGE_SUCCESS,
                "90.5", "Quiz 1", ALICE.getName(), ALICE.getClassName());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_caseAndExtraSpacesIgnored_successShowsStoredDetails() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand("  aLiCe   Pauline ", "a1", "QUIZ    1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceWithGrades, ALICE.withGrade(OTHER_GRADE));

        assertCommandSuccess(command, model,
                "Grade of 90.5 for Quiz 1 for student Alice Pauline in class A1 has been deleted",
                expectedModel);
    }

    @Test
    public void execute_sameNameInOtherClass_deletesOnlyMatchingStudentGrade() {
        Person aliceInOtherClass = new PersonBuilder(ALICE).withClassName("B2").build().withGrade(GRADE);
        model.addPerson(aliceInOtherClass);

        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, "b2", "Quiz 1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(aliceInOtherClass,
                new PersonBuilder(ALICE).withClassName("B2").build());

        assertCommandSuccess(command, model,
                "Grade of 90.5 for Quiz 1 for student Alice Pauline in class B2 has been deleted",
                expectedModel);
        assertEquals(2, model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().equals(ALICE.getName()))
                .count());
        assertEquals(2, model.getAddressBook().getPersonList().stream()
                .filter(person -> person.equals(aliceWithGrades))
                .findFirst().orElseThrow().getGradeRecords().size());
    }

    @Test
    public void execute_personHiddenByFilter_success() {
        Person bensonWithGrade = BENSON.withGrade(GRADE);
        model.setPerson(BENSON, bensonWithGrade);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertFalse(model.getFilteredPersonList().contains(bensonWithGrade));

        DeleteGradeByNameAndClassCommand command = new DeleteGradeByNameAndClassCommand(
                BENSON.getName().toString(), BENSON.getClassName().toString(), "Quiz 1");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(bensonWithGrade, BENSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(command, model,
                "Grade of 90.5 for Quiz 1 for student Benson Meier in class A1 has been deleted",
                expectedModel);
        assertEquals(1, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(aliceWithGrades));
    }

    @Test
    public void execute_gradeNotFound_throwsCommandException() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, "Assignment 1");

        assertCommandFailure(command, model, DeleteGradeCommand.MESSAGE_GRADE_NOT_FOUND);
    }

    @Test
    public void execute_noMatchingName_throwsCommandException() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand("Nobody Here", ALICE_CLASS, "Quiz 1");
        assertCommandFailure(command, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_matchingNameButOtherClass_throwsCommandException() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, "Z9", "Quiz 1");
        assertCommandFailure(command, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_matchingClassButOtherName_throwsCommandException() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand("Nobody Here", ALICE_CLASS, "Quiz 1");
        assertCommandFailure(command, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_invalidNameAndClass_throwsCommandExceptionNotFound() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand("R@chel", "A1/B2", "Quiz 1");
        assertCommandFailure(command, model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager();
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand(ALICE_NAME, ALICE_CLASS, "Quiz 1");
        assertCommandFailure(command, emptyModel, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void equals_variousCommands_followsEqualityContract() {
        DeleteGradeByNameAndClassCommand deleteAliceQuiz =
                new DeleteGradeByNameAndClassCommand("Alice", "A1", "Quiz 1");

        assertTrue(deleteAliceQuiz.equals(deleteAliceQuiz));
        assertTrue(deleteAliceQuiz.equals(
                new DeleteGradeByNameAndClassCommand("Alice", "A1", "Quiz 1")));
        assertFalse(deleteAliceQuiz.equals(null));
        assertFalse(deleteAliceQuiz.equals(1));
        assertFalse(deleteAliceQuiz.equals(
                new DeleteGradeByNameAndClassCommand("Bob", "A1", "Quiz 1")));
        assertFalse(deleteAliceQuiz.equals(
                new DeleteGradeByNameAndClassCommand("Alice", "B2", "Quiz 1")));
        assertFalse(deleteAliceQuiz.equals(
                new DeleteGradeByNameAndClassCommand("Alice", "A1", "Exam")));
        assertFalse(deleteAliceQuiz.equals(
                new DeleteGradeByIndexCommand(INDEX_FIRST_PERSON, "Quiz 1")));
    }

    @Test
    public void toString_validCommand_returnsFieldsRepresentation() {
        DeleteGradeByNameAndClassCommand command =
                new DeleteGradeByNameAndClassCommand("Alice", "A1", "Quiz 1");
        String expected = DeleteGradeByNameAndClassCommand.class.getCanonicalName()
                + "{name=Alice, className=A1, assessmentName=Quiz 1}";

        assertEquals(expected, command.toString());
    }
}
