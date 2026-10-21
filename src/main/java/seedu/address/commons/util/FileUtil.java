package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final String TEMPORARY_FILE_SUFFIX = ".tmp";
    private static final String BACKUP_FILE_INFIX = ".bad-";
    private static final DateTimeFormatter BACKUP_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

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
     * The content is first written to a temporary file next to {@code file}, which then replaces {@code file}.
     * If the writing fails half way, for example because the disk is full, the old content of {@code file}
     * is kept instead of being left half written.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        Path temporaryFile = file.resolveSibling(file.getFileName() + TEMPORARY_FILE_SUFFIX);
        try {
            Files.write(temporaryFile, content.getBytes(CHARSET));
            replaceFile(temporaryFile, file);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Renames {@code file} to a file next to it whose name is the name of {@code file} followed by
     * {@code .bad-} and the current time, so that the content of a file that can not be used is kept.
     *
     * @return The path of the renamed file.
     * @throws IOException if {@code file} does not exist or can not be renamed.
     */
    public static Path moveToBackup(Path file) throws IOException {
        requireNonNull(file);
        String time = LocalDateTime.now().format(BACKUP_TIME_FORMAT);
        Path backupFile = file.resolveSibling(file.getFileName() + BACKUP_FILE_INFIX + time);
        return Files.move(file, backupFile);
    }

    /**
     * Moves {@code source} to {@code target}, replacing {@code target} in one step when the file system
     * can do that.
     */
    private static void replaceFile(Path source, Path target) throws IOException {
        replaceFile(source, target, Files::move);
    }

    /**
     * Moves {@code source} to {@code target} with {@code fileMover}, replacing {@code target}. The move is
     * made in one step if the file system supports that, and otherwise without that guarantee.
     * A different {@code fileMover} than {@link Files#move(Path, Path, CopyOption...)} is only given in tests,
     * as the file system of a test can not be made to refuse an atomic move.
     */
    static void replaceFile(Path source, Path target, FileMover fileMover) throws IOException {
        try {
            fileMover.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            fileMover.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Moves a file, in the same way as {@link Files#move(Path, Path, CopyOption...)}. */
    @FunctionalInterface
    interface FileMover {
        void move(Path source, Path target, CopyOption... options) throws IOException;
    }

}
