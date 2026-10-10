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

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteByNameAndClassCommand}.
 */
public class DeleteByNameAndClassCommandTest {

    private static final String ALICE_NAME = ALICE.getName().toString();
    private static final String ALICE_CLASS = ALICE.getClassName().toString();

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteByNameAndClassCommand(null, ALICE_CLASS));
        assertThrows(NullPointerException.class, () -> new DeleteByNameAndClassCommand(ALICE_NAME, null));
    }

    @Test
    public void execute_nameAndClassMatch_success() {
        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand(ALICE_NAME, ALICE_CLASS);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                ALICE.getName(), ALICE.getClassName());
        assertEquals("Alice Pauline in class A1 has been deleted", expectedMessage);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_caseAndExtraSpacesIgnored_successShowsStoredNameAndClass() {
        DeleteByNameAndClassCommand deleteCommand =
                new DeleteByNameAndClassCommand("  aLiCe    PAULINE ", "a1");

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        // the message shows the name and class as stored, not as typed
        assertCommandSuccess(deleteCommand, model,
                "Alice Pauline in class A1 has been deleted", expectedModel);
    }

    @Test
    public void execute_sameNameInOtherClass_deletesOnlyMatchingClass() {
        Person aliceInOtherClass = new PersonBuilder(ALICE).withClassName("B2").build();
        model.addPerson(aliceInOtherClass);

        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand(ALICE_NAME, "b2");

        // only the copy in the other class is removed
        ModelManager expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model,
                "Alice Pauline in class B2 has been deleted", expectedModel);
        assertTrue(model.hasPerson(ALICE));
    }

    @Test
    public void execute_personHiddenByFilter_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertFalse(model.getFilteredPersonList().contains(BENSON));

        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand(
                BENSON.getName().toString(), BENSON.getClassName().toString());

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.deletePerson(BENSON);

        assertCommandSuccess(deleteCommand, model,
                "Benson Meier in class A1 has been deleted", expectedModel);

        // the filter is left as it was
        assertEquals(1, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(ALICE));
    }

    @Test
    public void execute_noMatchingName_throwsCommandException() {
        DeleteByNameAndClassCommand deleteCommand =
                new DeleteByNameAndClassCommand("Nobody Here", ALICE_CLASS);
        assertCommandFailure(deleteCommand, model, DeleteByNameAndClassCommand.MESSAGE_NOT_FOUND);
    }

    @Test
    public void execute_matchingNameButOtherClass_throwsCommandException() {
        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand(ALICE_NAME, "Z9");
        assertCommandFailure(deleteCommand, model, DeleteByNameAndClassCommand.MESSAGE_NOT_FOUND);
    }

    @Test
    public void execute_matchingClassButOtherName_throwsCommandException() {
        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand("Nobody Here", "A1");
        assertCommandFailure(deleteCommand, model, DeleteByNameAndClassCommand.MESSAGE_NOT_FOUND);
    }

    @Test
    public void execute_invalidNameAndClass_throwsCommandExceptionNotFound() {
        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand("R@chel", "A1/B2");
        assertCommandFailure(deleteCommand, model, "No contact found");
    }

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager();
        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand(ALICE_NAME, ALICE_CLASS);
        assertCommandFailure(deleteCommand, emptyModel, DeleteByNameAndClassCommand.MESSAGE_NOT_FOUND);
    }

    @Test
    public void equals() {
        DeleteByNameAndClassCommand deleteAliceCommand = new DeleteByNameAndClassCommand("Alice", "A1");
        DeleteByNameAndClassCommand deleteBobCommand = new DeleteByNameAndClassCommand("Bob", "A1");
        DeleteByNameAndClassCommand deleteAliceOtherClassCommand =
                new DeleteByNameAndClassCommand("Alice", "B2");

        // same object -> returns true
        assertTrue(deleteAliceCommand.equals(deleteAliceCommand));

        // same values -> returns true
        assertTrue(deleteAliceCommand.equals(new DeleteByNameAndClassCommand("Alice", "A1")));

        // different types -> returns false
        assertFalse(deleteAliceCommand.equals(1));

        // null -> returns false
        assertFalse(deleteAliceCommand.equals(null));

        // different name -> returns false
        assertFalse(deleteAliceCommand.equals(deleteBobCommand));

        // different class -> returns false
        assertFalse(deleteAliceCommand.equals(deleteAliceOtherClassCommand));

        // different kind of delete command -> returns false
        assertFalse(deleteAliceCommand.equals(new DeleteByIndexCommand(INDEX_FIRST_PERSON)));
    }

    @Test
    public void toStringMethod() {
        DeleteByNameAndClassCommand deleteCommand = new DeleteByNameAndClassCommand("Alice", "A1");
        String expected = DeleteByNameAndClassCommand.class.getCanonicalName()
                + "{name=Alice, className=A1}";
        assertEquals(expected, deleteCommand.toString());
    }
}
