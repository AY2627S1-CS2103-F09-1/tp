package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_success_messageUsesAppName() {
        assertEquals("ClassMates has been cleared!", ClearCommand.MESSAGE_SUCCESS);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClearCommand().execute(null));
    }

    @Test
    public void execute_modelStillHasContacts_throwsAssertionError() {
        Model modelThatKeepsContacts = new ModelManager(getTypicalAddressBook(), new UserPrefs()) {
            @Override
            public void setAddressBook(ReadOnlyAddressBook newData) {
                // Intentionally does nothing, to simulate a model that fails to clear its data
            }
        };

        // Gradle runs the tests with assertions enabled
        assertThrows(AssertionError.class, () -> new ClearCommand().execute(modelThatKeepsContacts));
    }

    @Test
    public void messageUsage_describesCommandWithoutParameters() {
        assertEquals("clear: Clears all contacts from ClassMates. Nothing can be written after the command word.\n"
                + "Example: clear", ClearCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
