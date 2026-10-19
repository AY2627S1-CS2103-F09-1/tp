package seedu.address.commons.util;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final String TEMPORARY_FILE_SUFFIX = ".tmp";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     * The content is first written to a temporary file in the same folder, which then replaces {@code file}, so
     * that a failure while writing never leaves {@code file} half written or empty.
     *
     * @throws IOException if the content cannot be written. The old content of {@code file} is kept then.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        requireAllNonNull(file, content);
        Path folder = file.toAbsolutePath().getParent();
        Path temporaryFile = Files.createTempFile(folder, file.getFileName().toString(), TEMPORARY_FILE_SUFFIX);
        try {
            Files.write(temporaryFile, content.getBytes(CHARSET));
            replaceFile(temporaryFile, file);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Moves {@code source} to {@code target}, replacing {@code target} if it exists. The move is atomic if the
     * file system supports it.
     */
    private static void replaceFile(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
