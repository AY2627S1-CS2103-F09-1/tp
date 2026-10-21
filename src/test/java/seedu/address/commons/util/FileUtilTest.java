package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path testFolder;

    @Test
    public void writeToFile_newFile_createsFileWithContent() throws IOException {
        Path file = testFolder.resolve("data.json");

        FileUtil.writeToFile(file, "first");

        assertEquals("first", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_existingFile_replacesContent() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "first, and a longer content");

        FileUtil.writeToFile(file, "second");

        assertEquals("second", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_success_leavesNoTemporaryFile() throws IOException {
        Path file = testFolder.resolve("data.json");

        FileUtil.writeToFile(file, "first");
        FileUtil.writeToFile(file, "second");

        assertEquals(List.of("data.json"), listFileNames());
    }

    @Test
    public void writeToFile_nonAsciiContent_keptAsUtf8() throws IOException {
        Path file = testFolder.resolve("data.json");

        FileUtil.writeToFile(file, "José 李明");

        assertEquals("José 李明", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_missingFolder_throwsIoExceptionAndKeepsNothing() {
        Path file = testFolder.resolve("missing").resolve("data.json");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(file, "first"));
        assertFalse(Files.exists(file));
        assertFalse(Files.exists(testFolder.resolve("missing")));
    }

    @Test
    public void writeToFile_replacementFails_keepsOldContentAndRemovesTemporaryFile() throws IOException {
        // A non-empty folder can not be replaced by a file, which makes the final step of the writing fail
        Path target = testFolder.resolve("data.json");
        Files.createDirectory(target);
        Files.writeString(target.resolve("inner.txt"), "old content");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(target, "new content"));

        assertEquals("old content", Files.readString(target.resolve("inner.txt")));
        assertEquals(List.of("data.json"), listFileNames());
    }

    @Test
    public void replaceFile_atomicMoveSupported_movesOnceAtomically() throws IOException {
        Path source = testFolder.resolve("source.tmp");
        Path target = testFolder.resolve("target.json");
        Files.writeString(source, "new");
        Files.writeString(target, "old");
        List<List<CopyOption>> attempts = new ArrayList<>();

        FileUtil.replaceFile(source, target, (from, to, options) -> {
            attempts.add(List.of(options));
            Files.move(from, to, options);
        });

        assertEquals(List.of(List.of(StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)),
                attempts);
        assertEquals("new", Files.readString(target));
        assertFalse(Files.exists(source));
    }

    @Test
    public void replaceFile_atomicMoveNotSupported_movesAgainWithoutAtomicMove() throws IOException {
        Path source = testFolder.resolve("source.tmp");
        Path target = testFolder.resolve("target.json");
        Files.writeString(source, "new");
        Files.writeString(target, "old");
        List<List<CopyOption>> attempts = new ArrayList<>();

        FileUtil.replaceFile(source, target, (from, to, options) -> {
            attempts.add(List.of(options));
            if (List.of(options).contains(StandardCopyOption.ATOMIC_MOVE)) {
                throw new AtomicMoveNotSupportedException(from.toString(), to.toString(), "not supported");
            }
            Files.move(from, to, options);
        });

        assertEquals(List.of(List.of(StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE),
                List.of(StandardCopyOption.REPLACE_EXISTING)), attempts);
        assertEquals("new", Files.readString(target));
        assertFalse(Files.exists(source));
    }

    @Test
    public void replaceFile_otherFailure_isNotRetried() {
        Path source = testFolder.resolve("source.tmp");
        Path target = testFolder.resolve("target.json");
        List<List<CopyOption>> attempts = new ArrayList<>();

        FileUtil.FileMover failingMover = (from, to, options) -> {
            attempts.add(List.of(options));
            throw new IOException("disk failure");
        };

        assertThrows(IOException.class, "disk failure", () -> FileUtil.replaceFile(source, target, failingMover));

        assertEquals(1, attempts.size());
    }

    @Test
    public void moveToBackup_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.moveToBackup(null));
    }

    @Test
    public void moveToBackup_existingFile_renamesFileAndKeepsContent() throws IOException {
        Path file = testFolder.resolve("data.json");
        Files.writeString(file, "precious");

        Path backup = FileUtil.moveToBackup(file);

        assertFalse(Files.exists(file));
        assertEquals("precious", Files.readString(backup));
        assertEquals(testFolder, backup.getParent());
        assertTrue(backup.getFileName().toString().startsWith("data.json.bad-"));
    }

    @Test
    public void moveToBackup_missingFile_throwsNoSuchFileException() {
        Path file = testFolder.resolve("data.json");

        assertThrows(NoSuchFileException.class, () -> FileUtil.moveToBackup(file));
    }

    private List<String> listFileNames() throws IOException {
        try (Stream<Path> files = Files.list(testFolder)) {
            return files.map(path -> path.getFileName().toString()).sorted().toList();
        }
    }
}
