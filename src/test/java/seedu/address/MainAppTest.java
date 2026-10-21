package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.UiManager;

public class MainAppTest {

    private static final String UNREADABLE_CONTENT = "{ this is not valid json";

    @TempDir
    public Path testFolder;

    @Test
    public void keepUnreadableDataFile_existingFile_keepsContentUnderAnotherName() throws IOException {
        Path dataFile = testFolder.resolve("data").resolve("classmates.json");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, UNREADABLE_CONTENT);

        String message = MainApp.keepUnreadableDataFile(createStorage(dataFile));

        List<Path> keptFiles = listFiles(dataFile.getParent()).stream()
                .filter(file -> file.getFileName().toString().startsWith("classmates.json.bad-"))
                .toList();
        assertEquals(1, keptFiles.size());
        assertEquals(UNREADABLE_CONTENT, Files.readString(keptFiles.get(0)));
        assertTrue(message.contains(keptFiles.get(0).toString()));
        assertTrue(message.startsWith("ClassMates could not read the data file"));
    }

    @Test
    public void keepUnreadableDataFile_existingFile_savesEmptyAddressBookInItsPlace() throws Exception {
        Path dataFile = testFolder.resolve("classmates.json");
        Files.writeString(dataFile, UNREADABLE_CONTENT);
        Storage storage = createStorage(dataFile);

        MainApp.keepUnreadableDataFile(storage);

        // The next run reads an empty address book, and not the sample data that is used when there is no file
        assertEquals(new AddressBook(), new AddressBook(storage.readAddressBook().get()));
    }

    @Test
    public void keepUnreadableDataFile_fileCanNotBeRenamed_leavesFileAndSaysSo() throws IOException {
        // A file that does not exist can not be renamed
        Path dataFile = testFolder.resolve("missing.json");

        String message = MainApp.keepUnreadableDataFile(createStorage(dataFile));

        assertEquals(MainApp.MESSAGE_DATA_FILE_NOT_KEPT, message);
        assertFalse(Files.exists(dataFile));
        assertTrue(listFiles(testFolder).isEmpty());
    }

    @Test
    public void keepUnreadableDataFile_thenReadAgain_doesNotThrow() throws Exception {
        Path dataFile = testFolder.resolve("classmates.json");
        Files.writeString(dataFile, UNREADABLE_CONTENT);
        Storage storage = createStorage(dataFile);
        try {
            storage.readAddressBook();
        } catch (DataLoadingException e) {
            MainApp.keepUnreadableDataFile(storage);
        }

        assertTrue(storage.readAddressBook().isPresent());
    }

    @Test
    public void keepUnreadableDataFile_emptyDataFileCanNotBeSaved_stillSaysThatFileWasKept() throws IOException {
        Path dataFile = testFolder.resolve("classmates.json");
        Files.writeString(dataFile, UNREADABLE_CONTENT);

        String message = MainApp.keepUnreadableDataFile(createStorageThatCanNotSave(dataFile));

        assertTrue(message.startsWith("ClassMates could not read the data file"));
        assertTrue(message.contains("classmates.json.bad-"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void initModelManager_missingDataFile_usesSampleDataWithoutMessage() {
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(testFolder.resolve("classmates.json")), new UserPrefs());

        assertEquals(SampleDataUtil.getSampleAddressBook(), new AddressBook(model.getAddressBook()));
        assertEquals("", app.getStartupMessage());
    }

    @Test
    public void initModelManager_readableDataFile_usesItsDataWithoutMessage() throws IOException {
        Path dataFile = testFolder.resolve("classmates.json");
        Storage storage = createStorage(dataFile);
        storage.saveAddressBook(getTypicalAddressBook());
        MainApp app = new MainApp();

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(getTypicalAddressBook(), new AddressBook(model.getAddressBook()));
        assertEquals("", app.getStartupMessage());
    }

    @Test
    public void initModelManager_unreadableDataFile_startsEmptyAndKeepsFile() throws IOException {
        Path dataFile = testFolder.resolve("classmates.json");
        Files.writeString(dataFile, UNREADABLE_CONTENT);
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataFile), new UserPrefs());

        assertEquals(new AddressBook(), new AddressBook(model.getAddressBook()));
        assertTrue(app.getStartupMessage().startsWith("ClassMates could not read the data file"));
        assertTrue(app.getStartupMessage().contains("classmates.json.bad-"));
        assertEquals(1, listFiles(testFolder).stream()
                .filter(file -> file.getFileName().toString().startsWith("classmates.json.bad-")).count());
    }

    @Test
    public void filePaths_default_areTheFilesInTheUserGuide() {
        MainApp app = new MainApp();

        assertEquals(Path.of("preferences.json"), app.getUserPrefsFilePath());
        assertEquals(Path.of("data", "classmates.json"), app.getAddressBookFilePath());
    }

    @Test
    public void init_missingDataFile_createsUiWithoutMessage() throws Exception {
        MainApp app = createAppOnFilesIn(testFolder);

        app.init();

        assertTrue(app.ui instanceof UiManager);
        assertEquals("", app.getStartupMessage());
        assertEquals(SampleDataUtil.getSampleAddressBook(), new AddressBook(app.model.getAddressBook()));
        assertTrue(Files.exists(testFolder.resolve("preferences.json")));
    }

    @Test
    public void init_unreadableDataFile_startsEmptyAndKeepsFile() throws Exception {
        Files.writeString(testFolder.resolve("classmates.json"), UNREADABLE_CONTENT);
        MainApp app = createAppOnFilesIn(testFolder);

        app.init();

        assertTrue(app.ui instanceof UiManager);
        assertTrue(app.getStartupMessage().contains("classmates.json.bad-"));
        assertEquals(new AddressBook(), new AddressBook(app.model.getAddressBook()));
    }

    private MainApp createAppOnFilesIn(Path folder) {
        return new MainApp() {
            @Override
            protected Path getUserPrefsFilePath() {
                return folder.resolve("preferences.json");
            }

            @Override
            protected Path getAddressBookFilePath() {
                return folder.resolve("classmates.json");
            }
        };
    }

    private Storage createStorageThatCanNotSave(Path dataFile) {
        return new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("disk is full");
            }
        };
    }

    private Storage createStorage(Path dataFile) {
        return new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
    }

    private List<Path> listFiles(Path folder) throws IOException {
        try (Stream<Path> files = Files.list(folder)) {
            return files.toList();
        }
    }
}
