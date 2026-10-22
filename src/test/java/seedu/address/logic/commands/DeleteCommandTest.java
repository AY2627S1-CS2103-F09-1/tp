package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains unit tests for the checks in {@code DeleteCommand#deletePerson(Model, Person)}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void deletePerson_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DeleteCommand.deletePerson(null, ALICE));
        assertThrows(NullPointerException.class, () -> DeleteCommand.deletePerson(model, null));
    }

    @Test
    public void deletePerson_personInModel_removesPersonAndShowsNameAndClass() {
        CommandResult result = DeleteCommand.deletePerson(model, ALICE);

        assertEquals("Alice Pauline in class A1 has been deleted", result.getFeedbackToUser());
        assertFalse(model.hasPerson(ALICE));
    }

    @Test
    public void deletePerson_personNotInModel_throwsAssertionError() {
        Model emptyModel = new ModelManager();

        // Gradle runs the tests with assertions enabled
        assertThrows(AssertionError.class, () -> DeleteCommand.deletePerson(emptyModel, ALICE));
    }

    @Test
    public void deletePerson_modelDoesNotRemovePerson_throwsAssertionError() {
        Model modelThatKeepsPersons = new ModelManager(getTypicalAddressBook(), new UserPrefs()) {
            @Override
            public void deletePerson(Person target) {
                // Intentionally does nothing, to simulate a model that fails to remove the person
            }
        };

        assertThrows(AssertionError.class, () -> DeleteCommand.deletePerson(modelThatKeepsPersons, ALICE));
    }
}
