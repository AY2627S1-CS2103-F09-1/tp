package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Remembers the commands that the user has entered, so that they can be recalled one by one.
 * The history does not depend on JavaFX, so that it can be tested without a GUI.
 */
public class CommandHistory {

    /** The maximum number of commands that are remembered. The oldest command is dropped when it is exceeded. */
    public static final int MAX_SIZE = 100;

    private final List<String> commands = new ArrayList<>();

    /** The index of the command being shown, or {@code commands.size()} if the user is not browsing. */
    private int position = 0;

    /** The text that the user had typed before browsing started. It is shown again when browsing ends. */
    private String draft = "";

    /**
     * Adds {@code command} as the most recent command and stops browsing.
     * A blank command, or a command identical to the most recent one, is not added.
     */
    public void add(String command) {
        requireNonNull(command);
        if (!command.isBlank() && !isMostRecent(command)) {
            commands.add(command);
            dropOldestIfOverLimit();
        }
        stopBrowsing();
    }

    /**
     * Returns the next older command, or an empty {@code Optional} if there is no command to recall.
     * The first call after {@link #add(String)} remembers {@code currentText} so that {@link #next()} can
     * give it back. If {@code currentText} is the most recent command, as it is after a command fails and
     * stays in the command box, that command is skipped. At the oldest command, the oldest command is
     * returned again.
     */
    public Optional<String> previous(String currentText) {
        requireNonNull(currentText);
        if (commands.isEmpty()) {
            return Optional.empty();
        }
        if (!isBrowsing()) {
            draft = currentText;
            position = isMostRecent(currentText) ? commands.size() - 1 : commands.size();
        }
        position = Math.max(position - 1, 0);
        return Optional.of(commands.get(position));
    }

    /**
     * Returns the next newer command, or the text that was remembered by {@link #previous(String)} after the
     * newest command. Returns an empty {@code Optional} if the user is not browsing.
     */
    public Optional<String> next() {
        if (!isBrowsing()) {
            return Optional.empty();
        }
        position++;
        return Optional.of(isBrowsing() ? commands.get(position) : draft);
    }

    private boolean isBrowsing() {
        return position < commands.size();
    }

    private boolean isMostRecent(String command) {
        return !commands.isEmpty() && commands.get(commands.size() - 1).equals(command);
    }

    private void dropOldestIfOverLimit() {
        if (commands.size() > MAX_SIZE) {
            commands.remove(0);
        }
    }

    private void stopBrowsing() {
        position = commands.size();
        draft = "";
    }
}
