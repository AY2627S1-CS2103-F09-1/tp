package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_CHANGE_UNDONE = "\nThe change was undone.";

    public static final String MESSAGE_UNEXPECTED_ERROR =
            "Something went wrong while carrying out the command. The details are in the log file."
                    + MESSAGE_CHANGE_UNDONE;

    public static final String FILE_OPS_ERROR_FORMAT =
            "Could not save data due to the following error: %s" + MESSAGE_CHANGE_UNDONE;

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder."
                    + MESSAGE_CHANGE_UNDONE;

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        ReadOnlyAddressBook addressBookBeforeCommand = new AddressBook(model.getAddressBook());
        CommandResult commandResult;
        try {
            Command command = addressBookParser.parseCommand(commandText);
            commandResult = command.execute(model);
        } catch (RuntimeException e) {
            throw handleUnexpectedError(e, addressBookBeforeCommand);
        }

        saveAddressBookOrUndo(addressBookBeforeCommand);

        return commandResult;
    }

    /**
     * Records {@code e}, an unexpected {@code RuntimeException}, in the log and sets the data of the model back
     * to {@code addressBookBeforeCommand}, as the command may have changed it before it failed. {@code Error}s
     * such as a failed assertion are not handled, so that they are not hidden during development.
     *
     * @return the {@code CommandException} to throw, which tells the user that something went wrong.
     */
    private CommandException handleUnexpectedError(RuntimeException e,
            ReadOnlyAddressBook addressBookBeforeCommand) {
        logger.severe("Unexpected error while executing a command: " + StringUtil.getDetails(e));
        undoCommand("The command failed unexpectedly", addressBookBeforeCommand);
        return new CommandException(MESSAGE_UNEXPECTED_ERROR, e);
    }

    /**
     * Saves the address book of the model. If it cannot be saved, the data of the model is set back to
     * {@code addressBookBeforeCommand}, so that the data in memory is never different from the data in the file
     * because of a command that appeared to fail.
     *
     * @throws CommandException if the address book cannot be saved.
     */
    private void saveAddressBookOrUndo(ReadOnlyAddressBook addressBookBeforeCommand) throws CommandException {
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            undoCommand("The address book could not be saved", addressBookBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            undoCommand("The address book could not be saved", addressBookBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }
    }

    /**
     * Sets the data of the model back to {@code addressBookBeforeCommand}, and logs that this was done
     * because of {@code reason}.
     */
    private void undoCommand(String reason, ReadOnlyAddressBook addressBookBeforeCommand) {
        logger.warning(reason + ", so the change of the command is undone");
        model.setAddressBook(addressBookBeforeCommand);
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
