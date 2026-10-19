package seedu.address.commons.util;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
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
     * Copies {@code file} to a file in the same folder whose name is the name of {@code file} followed by
     * {@code suffix}, and returns the copy. An existing file is never replaced.
     *
     * @throws IOException if {@code file} cannot be copied, including when the copy already exists.
     */
    public static Path copyWithSuffix(Path file, String suffix) throws IOException {
        requireAllNonNull(file, suffix);
        Path copy = file.resolveSibling(file.getFileName() + suffix);
        Files.copy(file, copy);
        return copy;
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     * The content is first written to a temporary file in the same folder, which then replaces {@code file}, so
     * that a failure while writing never leaves {@code file} half written or empty. Both files are in the same
     * folder, so the replacement is a rename, which the file system does in one step.
     *
     * @throws IOException if the content cannot be written. The old content of {@code file} is kept then.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        requireAllNonNull(file, content);
        Path folder = file.toAbsolutePath().getParent();
        Path temporaryFile = Files.createTempFile(folder, file.getFileName().toString(), TEMPORARY_FILE_SUFFIX);
        try {
            Files.write(temporaryFile, content.getBytes(CHARSET));
            Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

}
