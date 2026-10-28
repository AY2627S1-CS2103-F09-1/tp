package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalRecords.ALICE_WITH_GRADES;
import static seedu.address.testutil.TypicalRecords.OTHER_GRADE;
import static seedu.address.testutil.TypicalRecords.getModelWithAliceGrades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/** Contains tests for the shared grade-deletion behavior. */
public class DeleteGradeCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = getModelWithAliceGrades();
    }

    @Test
    public void deleteGrade_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                DeleteGradeCommand.deleteGrade(null, ALICE_WITH_GRADES, "Quiz 1"));
        assertThrows(NullPointerException.class, () ->
                DeleteGradeCommand.deleteGrade(model, null, "Quiz 1"));
        assertThrows(NullPointerException.class, () ->
                DeleteGradeCommand.deleteGrade(model, ALICE_WITH_GRADES, null));
    }

    @Test
    public void deleteGrade_personNotInModel_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                DeleteGradeCommand.deleteGrade(new ModelManager(), ALICE_WITH_GRADES, "Quiz 1"));
    }

    @Test
    public void deleteGrade_existingGrade_removesGradeAndShowsStoredDetails() throws CommandException {
        CommandResult result = DeleteGradeCommand.deleteGrade(model, ALICE_WITH_GRADES, "QUIZ   1");

        Model expectedModel = new ModelManager(getModelWithAliceGrades().getAddressBook(), new UserPrefs());
        expectedModel.setPerson(ALICE_WITH_GRADES, ALICE.withGrade(OTHER_GRADE));
        assertEquals(expectedModel, model);
        assertEquals("Grade of 90.5 for Quiz 1 for student Alice Pauline in class A1 has been deleted",
                result.getFeedbackToUser());
    }

    @Test
    public void deleteGrade_gradeNotFound_throwsCommandException() {
        assertThrows(CommandException.class, DeleteGradeCommand.MESSAGE_GRADE_NOT_FOUND, () ->
                DeleteGradeCommand.deleteGrade(model, ALICE_WITH_GRADES, "Assignment 1"));
    }

    @Test
    public void deleteGrade_modelDoesNotStoreChangedContact_throwsAssertionError() {
        // White-box negative test for model-update-failed partition targeted by the postcondition
        Model modelThatKeepsContacts = new ModelManager(model.getAddressBook(), new UserPrefs()) {
            @Override
            public void setPerson(Person target, Person editedPerson) {
                // Intentionally does nothing to simulate a model that silently ignores the update.
            }
        };

        assertThrows(AssertionError.class, () ->
                DeleteGradeCommand.deleteGrade(modelThatKeepsContacts, ALICE_WITH_GRADES, "Quiz 1"));
    }
}
