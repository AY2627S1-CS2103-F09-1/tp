package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Tests the parts of {@code UiManager} that do not need a JavaFX window.
 */
public class UiManagerTest {

    private static final Path DATA_FILE_PATH = Paths.get("data", "classmates.json");

    @Test
    public void constructor_nullStartupMessage_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UiManager(null, DATA_FILE_PATH, null));
    }

    @Test
    public void constructor_startupMessage_success() {
        assertDoesNotThrow(() -> new UiManager(null, DATA_FILE_PATH, "ClassMates started with an empty list."));
        assertDoesNotThrow(() -> new UiManager(null, DATA_FILE_PATH, ""));
    }

    @Test
    public void constructor_withoutStartupMessage_success() {
        assertDoesNotThrow(() -> new UiManager(null, DATA_FILE_PATH));
    }
}
