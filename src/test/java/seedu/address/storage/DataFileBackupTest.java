package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DataFileBackupTest {

    private static final LocalDateTime TIME = LocalDateTime.of(2026, 10, 19, 14, 30, 12);
    private static final String BACKUP_NAME = "classmates.json.unreadable-20261019-143012";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void backUpUnreadableFile_null_throwsNullPointerException() {
        Path file = temporaryFolder.resolve("classmates.json");
        assertThrows(NullPointerException.class, () -> DataFileBackup.backUpUnreadableFile(null, TIME));
        assertThrows(NullPointerException.class, () -> DataFileBackup.backUpUnreadableFile(file, null));
    }

    @Test
    public void backUpUnreadableFile_fileExists_copiesItAndNamesTheCopyInMessage() throws IOException {
        Path file = temporaryFolder.resolve("classmates.json");
        Files.writeString(file, "{ not valid json");

        String message = DataFileBackup.backUpUnreadableFile(file, TIME);

        assertEquals(String.format(DataFileBackup.MESSAGE_BACKED_UP, BACKUP_NAME), message);
        assertEquals("{ not valid json", Files.readString(temporaryFolder.resolve(BACKUP_NAME)));
        assertEquals("{ not valid json", Files.readString(file));
    }

    @Test
    public void backUpUnreadableFile_copyAlreadyExists_keepsExistingCopyAndSaysNoCopyWasMade() throws IOException {
        Path file = temporaryFolder.resolve("classmates.json");
        Files.writeString(file, "newer unreadable content");
        Path existingCopy = temporaryFolder.resolve(BACKUP_NAME);
        Files.writeString(existingCopy, "older copy");

        String message = DataFileBackup.backUpUnreadableFile(file, TIME);

        assertEquals(DataFileBackup.MESSAGE_NOT_BACKED_UP, message);
        assertEquals("older copy", Files.readString(existingCopy));
    }

    @Test
    public void backUpUnreadableFile_fileDoesNotExist_saysNoCopyWasMade() {
        Path file = temporaryFolder.resolve("classmates.json");

        String message = DataFileBackup.backUpUnreadableFile(file, TIME);

        assertEquals(DataFileBackup.MESSAGE_NOT_BACKED_UP, message);
        assertFalse(Files.exists(temporaryFolder.resolve(BACKUP_NAME)));
    }
}
