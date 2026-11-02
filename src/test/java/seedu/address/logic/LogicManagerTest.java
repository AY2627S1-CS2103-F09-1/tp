package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.Messages.getInvalidDisplayedIndexMessage;
import static seedu.address.logic.commands.CommandTestUtil.CLASS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.TagCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesKeyword;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private StorageManager storage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(addressBookFilePath());
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, getInvalidDisplayedIndexMessage(0));
    }

    @Test
    public void execute_clearWithExtraText_throwsParseExceptionAndKeepsContacts() {
        model.addPerson(AMY);

        assertParseException("clear 3",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        assertEquals(List.of(AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_storageThrowsIoExceptionOnClear_contactsAreRestored() {
        model.addPerson(AMY);
        model.addPerson(BOB);
        useStorageThatThrowsWhenSaving(DUMMY_IO_EXCEPTION);

        assertCommandException("clear", String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));

        assertEquals(List.of(BOB, AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_veryLongCommand_throwsParseExceptionAndKeepsContacts() {
        model.addPerson(AMY);
        String veryLongCommand = "list " + "a".repeat(1_000_000);

        assertParseException(veryLongCommand, AddressBookParser.MESSAGE_INPUT_TOO_LONG);
        assertEquals(List.of(AMY), model.getFilteredPersonList());
        assertFalse(Files.exists(addressBookFilePath()));
    }

    @Test
    public void execute_nullCommand_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> logic.execute(null));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_listWithKeyword_showsOnlyMatchingContacts() throws Exception {
        Person amy = new PersonBuilder(AMY).withTags("Group A").build();
        model.addPerson(amy);
        model.addPerson(BOB);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(new PersonMatchesKeyword("group a"));

        assertCommandSuccess("list   Group   A ", String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1),
                expectedModel);
        assertEquals(List.of(amy), model.getFilteredPersonList());
    }

    @Test
    public void execute_listWithEmailKeyword_showsOnlyMatchingContacts() throws Exception {
        model.addPerson(AMY);
        String email = AMY.getEmail().orElseThrow().toString();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(new PersonMatchesKeyword(email));

        assertCommandSuccess("list " + email, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_listWithKeywordMatchingNobody_showsEmptyList() throws Exception {
        model.addPerson(AMY);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(new PersonMatchesKeyword("zzz"));

        assertCommandSuccess("list zzz", String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteAfterListWithKeyword_usesIndexInSearchResults() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        logic.execute("list " + BOB.getName().fullName);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(new PersonMatchesKeyword(BOB.getName().fullName));
        expectedModel.deletePerson(BOB);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                BOB.getName(), BOB.getClassName());
        assertCommandSuccess("delete 1", expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagCommand_linksContactAndSavesAddressBook() throws Exception {
        Person amy = new PersonBuilder(AMY).withTags().build();
        model.addPerson(amy);
        Model expectedModel = new ModelManager();
        expectedModel.addPerson(amy.withTag(new Tag("Group A")));

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, "Group A", "Amy Bee (A1)");
        assertCommandSuccess("tag /group Group A /members 1", expectedMessage, expectedModel);

        assertEquals(expectedModel.getAddressBook(), new AddressBook(readSavedAddressBook()));
    }

    @Test
    public void execute_tagCommandContactAlreadyInGroup_throwsCommandExceptionAndDoesNotSave() {
        model.addPerson(new PersonBuilder(AMY).withTags("Group A").build());

        assertCommandException("tag /group group   a /members 1",
                String.format(TagCommand.MESSAGE_ALREADY_IN_GROUP, "Amy Bee (A1)"));
        assertFalse(Files.exists(addressBookFilePath()));
    }

    @Test
    public void execute_tagCommandIndexNotInList_throwsCommandExceptionAndDoesNotSave() {
        model.addPerson(new PersonBuilder(AMY).withTags().build());

        assertCommandException("tag /group Group A /members 1,2",
                String.format(TagCommand.MESSAGE_INDEX_NOT_FOUND, 2));
        assertFalse(Files.exists(addressBookFilePath()));
    }

    @Test
    public void execute_tagCommandMissingMembers_throwsParseException() {
        assertParseException("tag /group Group A", TagCommandParser.MESSAGE_MISSING_MEMBERS);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsIoExceptionOnDelete_contactIsRestoredInSameOrder() {
        model.addPerson(AMY);
        model.addPerson(BOB);
        useStorageThatThrowsWhenSaving(DUMMY_IO_EXCEPTION);

        assertCommandException("delete 1", String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));

        // the newest contact is shown first, as before the command
        assertEquals(List.of(BOB, AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_commandThrowsRuntimeException_throwsCommandExceptionAndUndoesChange() {
        Model modelThatFailsAfterAdding = new ModelManager() {
            @Override
            public void addPerson(Person person) {
                super.addPerson(person);
                throw new IllegalStateException("unexpected");
            }
        };
        logic = new LogicManager(modelThatFailsAfterAdding, storage);
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CLASS_DESC_AMY;

        assertThrows(CommandException.class, LogicManager.MESSAGE_UNEXPECTED_ERROR, () ->
                logic.execute(addCommand));

        // the contact that was added before the failure is removed again, and nothing is saved
        assertEquals(List.of(), modelThatFailsAfterAdding.getFilteredPersonList());
        assertFalse(Files.exists(addressBookFilePath()));
    }

    @Test
    public void execute_commandThrowsError_isNotHidden() {
        Model modelThatFailsAnAssertion = new ModelManager() {
            @Override
            public void addPerson(Person person) {
                throw new AssertionError("a bug");
            }
        };
        logic = new LogicManager(modelThatFailsAnAssertion, storage);
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CLASS_DESC_AMY;

        assertThrows(AssertionError.class, () -> logic.execute(addCommand));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    private Path addressBookFilePath() {
        return temporaryFolder.resolve("addressBook.json");
    }

    /**
     * Returns the address book that was saved to the file the logic under test writes to.
     */
    private ReadOnlyAddressBook readSavedAddressBook() throws Exception {
        return new JsonAddressBookStorage(addressBookFilePath()).readAddressBook().orElseThrow();
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        useStorageThatThrowsWhenSaving(e);

        // Triggers the saveAddressBook method by executing an add command, which must then be undone
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CLASS_DESC_AMY
                + EMAIL_DESC_AMY;
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    /**
     * Sets {@code logic} to one with a storage that throws the {@code IOException e} when it saves the
     * address book.
     */
    private void useStorageThatThrowsWhenSaving(IOException e) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);
    }
}
