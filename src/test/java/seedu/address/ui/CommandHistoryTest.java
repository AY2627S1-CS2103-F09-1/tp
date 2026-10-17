package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private static final String DRAFT = "half-typed";

    private static CommandHistory historyOf(String... commands) {
        CommandHistory history = new CommandHistory();
        for (String command : commands) {
            history.add(command);
        }
        return history;
    }

    @Test
    public void add_nullCommand_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandHistory().add(null));
    }

    @Test
    public void previous_nullCurrentText_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandHistory().previous(null));
    }

    @Test
    public void previous_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), new CommandHistory().previous(DRAFT));
    }

    @Test
    public void next_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), new CommandHistory().next());
    }

    @Test
    public void next_notBrowsing_returnsEmpty() {
        assertEquals(Optional.empty(), historyOf("list", "help").next());
    }

    @Test
    public void previous_firstCall_returnsMostRecentCommand() {
        assertEquals(Optional.of("help"), historyOf("list", "help").previous(""));
    }

    @Test
    public void previous_repeatedCalls_walksBackToOldestAndStays() {
        CommandHistory history = historyOf("list", "help", "clear");

        assertEquals(Optional.of("clear"), history.previous(""));
        assertEquals(Optional.of("help"), history.previous(""));
        assertEquals(Optional.of("list"), history.previous(""));
        assertEquals(Optional.of("list"), history.previous(""));
    }

    @Test
    public void next_afterPrevious_walksForwardAndRestoresDraft() {
        CommandHistory history = historyOf("list", "help");
        history.previous(DRAFT);
        history.previous(DRAFT);

        assertEquals(Optional.of("help"), history.next());
        assertEquals(Optional.of(DRAFT), history.next());
        assertEquals(Optional.empty(), history.next());
    }

    @Test
    public void previous_afterReturningToDraft_startsFromMostRecentAgain() {
        CommandHistory history = historyOf("list", "help");
        history.previous(DRAFT);
        history.next();

        assertEquals(Optional.of("help"), history.previous("other draft"));
        assertEquals(Optional.of("other draft"), history.next());
    }

    @Test
    public void previous_whileBrowsing_keepsFirstDraft() {
        CommandHistory history = historyOf("list", "help");
        history.previous(DRAFT);

        history.previous("help");

        assertEquals(Optional.of("help"), history.next());
        assertEquals(Optional.of(DRAFT), history.next());
    }

    @Test
    public void add_whileBrowsing_stopsBrowsing() {
        CommandHistory history = historyOf("list", "help");
        history.previous(DRAFT);
        history.previous(DRAFT);

        history.add("clear");

        assertEquals(Optional.empty(), history.next());
        assertEquals(Optional.of("clear"), history.previous(""));
    }

    @Test
    public void add_consecutiveDuplicate_addedOnce() {
        CommandHistory history = historyOf("list", "list");

        assertEquals(Optional.of("list"), history.previous(DRAFT));
        // Had "list" been added twice, the next command would be "list" again and not the draft
        assertEquals(Optional.of(DRAFT), history.next());
    }

    @Test
    public void add_nonConsecutiveDuplicate_bothKept() {
        CommandHistory history = historyOf("list", "help", "list");

        assertEquals(Optional.of("list"), history.previous(""));
        assertEquals(Optional.of("help"), history.previous(""));
        assertEquals(Optional.of("list"), history.previous(""));
    }

    @Test
    public void add_blankCommand_notAdded() {
        CommandHistory history = historyOf("   ", "");

        assertEquals(Optional.empty(), history.previous(DRAFT));
    }

    @Test
    public void add_blankCommandWhileBrowsing_stopsBrowsingWithoutAdding() {
        CommandHistory history = historyOf("list");
        history.previous(DRAFT);

        history.add("");

        assertEquals(Optional.empty(), history.next());
        assertEquals(Optional.of("list"), history.previous(""));
    }

    @Test
    public void add_moreThanMaxSize_dropsOldestCommands() {
        CommandHistory history = new CommandHistory();
        int extraCommands = 3;
        for (int i = 0; i < CommandHistory.MAX_SIZE + extraCommands; i++) {
            history.add("command " + i);
        }

        Optional<String> oldest = Optional.empty();
        for (int i = 0; i < CommandHistory.MAX_SIZE + extraCommands; i++) {
            oldest = history.previous("");
        }

        assertEquals(Optional.of("command " + extraCommands), oldest);
    }

    @Test
    public void add_exactlyMaxSize_keepsAllCommands() {
        CommandHistory history = new CommandHistory();
        for (int i = 0; i < CommandHistory.MAX_SIZE; i++) {
            history.add("command " + i);
        }

        Optional<String> oldest = Optional.empty();
        for (int i = 0; i < CommandHistory.MAX_SIZE; i++) {
            oldest = history.previous("");
        }

        assertEquals(Optional.of("command 0"), oldest);
    }
}
