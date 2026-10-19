package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void writeToFile_null_throwsNullPointerException() {
        Path file = temporaryFolder.resolve("data.json");
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(null, "content"));
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(file, null));
    }

    @Test
    public void writeToFile_fileDoesNotExist_createsFileWithContent() throws IOException {
        Path file = temporaryFolder.resolve("data.json");

        FileUtil.writeToFile(file, "content");

        assertEquals("content", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_fileExists_replacesContent() throws IOException {
        Path file = temporaryFolder.resolve("data.json");
        FileUtil.writeToFile(file, "a long old content that is longer than the new one");

        FileUtil.writeToFile(file, "new");

        assertEquals("new", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_nonAsciiContent_isKeptAsUtf8() throws IOException {
        Path file = temporaryFolder.resolve("data.json");

        FileUtil.writeToFile(file, "Zoë 名前");

        assertEquals("Zoë 名前", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_success_leavesNoTemporaryFile() throws IOException {
        Path file = temporaryFolder.resolve("data.json");

        FileUtil.writeToFile(file, "content");
        FileUtil.writeToFile(file, "content again");

        assertEquals(List.of(file.getFileName()), listFileNames(temporaryFolder));
    }

    @Test
    public void writeToFile_fileCannotBeReplaced_throwsIoExceptionAndLeavesNoTemporaryFile() throws IOException {
        // A folder that has a file in it cannot be replaced by a file
        Path folderInTheWay = temporaryFolder.resolve("data.json");
        Files.createDirectory(folderInTheWay);
        Path fileInFolder = folderInTheWay.resolve("keep.txt");
        Files.writeString(fileInFolder, "keep");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(folderInTheWay, "content"));

        assertTrue(Files.isDirectory(folderInTheWay));
        assertEquals("keep", Files.readString(fileInFolder));
        assertEquals(List.of(folderInTheWay.getFileName()), listFileNames(temporaryFolder));
    }

    @Test
    public void writeToFile_folderDoesNotExist_throwsIoException() {
        Path file = temporaryFolder.resolve("missing").resolve("data.json");
        assertThrows(IOException.class, () -> FileUtil.writeToFile(file, "content"));
    }

    /**
     * Returns the names of the files and folders directly in {@code folder}.
     */
    private static List<Path> listFileNames(Path folder) throws IOException {
        try (Stream<Path> entries = Files.list(folder)) {
            return entries.map(Path::getFileName).toList();
        }
    }
}
