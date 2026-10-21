package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.3.0";
    public static final String MESSAGE_DATA_FILE_KEPT = "ClassMates could not read the data file and started "
            + "with an empty list.\nThe file was kept as %s so that you can fix it.";
    public static final String MESSAGE_DATA_FILE_NOT_KEPT = "ClassMates could not read the data file and started "
            + "with an empty list.\nThe file could not be kept, and your next command will replace it.";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "classmates.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    /** A message for the user when the application starts, or an empty string if there is none. */
    private String startupMessage = "";

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing ClassMates ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(getAddressBookFilePath());
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath(), startupMessage);
    }

    /**
     * Returns the path of the preferences file. A test overrides it to run the application on other files.
     */
    protected Path getUserPrefsFilePath() {
        return USER_PREFS_FILE_PATH;
    }

    /**
     * Returns the path of the data file. A test overrides it to run the application on other files.
     */
    protected Path getAddressBookFilePath() {
        return ADDRESS_BOOK_FILE_PATH;
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample ClassMates.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty ClassMates.");
            startupMessage = keepUnreadableDataFile(storage);
            initialData = new AddressBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns the message for the user that was made when the model was initialized, or an empty string if
     * there is none.
     */
    String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Renames the data file of {@code storage}, which could not be read, so that its content is not lost when
     * the next command is saved, and saves an empty address book in its place so that the next run does not
     * start with the sample data. If the file can not be renamed, it is left as it is.
     *
     * @return A message for the user that says what happened to the file.
     */
    static String keepUnreadableDataFile(Storage storage) {
        Path dataFilePath = storage.getAddressBookFilePath();
        Path keptFilePath;
        try {
            keptFilePath = FileUtil.moveToBackup(dataFilePath);
        } catch (IOException e) {
            logger.warning("Could not keep the unreadable data file " + dataFilePath + ": "
                    + StringUtil.getDetails(e));
            return MESSAGE_DATA_FILE_NOT_KEPT;
        }
        logger.info("The unreadable data file " + dataFilePath + " was kept as " + keptFilePath);

        try {
            storage.saveAddressBook(new AddressBook());
        } catch (IOException e) {
            logger.warning("Could not save an empty data file " + dataFilePath + ": " + StringUtil.getDetails(e));
        }
        return String.format(MESSAGE_DATA_FILE_KEPT, keptFilePath);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting ClassMates " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping ClassMates ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
