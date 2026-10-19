package seedu.address.storage;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.StringUtil;

/**
 * Makes a copy of a data file that could not be loaded, so that it is not lost when the next save replaces it.
 */
public class DataFileBackup {

    public static final String SUFFIX_PREFIX = ".unreadable-";
    public static final String MESSAGE_BACKED_UP = "The data file could not be read, so ClassMates started "
            + "with no contacts.\nA copy of the file was saved as %s in the same folder.";
    public static final String MESSAGE_NOT_BACKED_UP = "The data file could not be read, so ClassMates started "
            + "with no contacts.\nA copy of the file could not be made, so the file will be replaced when you "
            + "make your next change.";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Logger logger = LogsCenter.getLogger(DataFileBackup.class);

    /**
     * Copies {@code unreadableFile} to a file next to it, whose name is the name of {@code unreadableFile}
     * followed by {@code SUFFIX_PREFIX} and {@code time}. An existing file is never replaced by the copy.
     *
     * @return the message that tells the user that the data file could not be read, and where the copy is, or
     *         that the copy could not be made.
     */
    public static String backUpUnreadableFile(Path unreadableFile, LocalDateTime time) {
        requireAllNonNull(unreadableFile, time);
        String suffix = SUFFIX_PREFIX + time.format(TIME_FORMAT);
        try {
            Path backupFile = FileUtil.copyWithSuffix(unreadableFile, suffix);
            logger.warning("The unreadable data file was copied to " + backupFile);
            return String.format(MESSAGE_BACKED_UP, backupFile.getFileName());
        } catch (IOException ioe) {
            logger.warning("The unreadable data file could not be copied: " + StringUtil.getDetails(ioe));
            return MESSAGE_NOT_BACKED_UP;
        }
    }

}
