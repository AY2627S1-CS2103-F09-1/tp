package seedu.address.ui;

import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

/**
 * Tests the creation of a {@code UiManager}. Starting it needs the JavaFX toolkit, so that is not tested here.
 */
public class UiManagerTest {

    private static final Path DATA_FILE_PATH = Paths.get("data", "classmates.json");

    @Test
    public void constructor_validArguments_doesNotThrow() {
        new UiManager(new LogicStub(), DATA_FILE_PATH);
        new UiManager(new LogicStub(), DATA_FILE_PATH, "");
        new UiManager(new LogicStub(), DATA_FILE_PATH, "a start-up message");
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UiManager(null, DATA_FILE_PATH, ""));
        assertThrows(NullPointerException.class, () -> new UiManager(new LogicStub(), null, ""));
        assertThrows(NullPointerException.class, () -> new UiManager(new LogicStub(), DATA_FILE_PATH, null));
        assertThrows(NullPointerException.class, () -> new UiManager(null, DATA_FILE_PATH));
    }

    /**
     * A {@code Logic} that is only passed on and never used.
     */
    private static class LogicStub implements Logic {
        @Override
        public CommandResult execute(String commandText) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }
    }
}
