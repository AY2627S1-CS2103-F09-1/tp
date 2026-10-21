package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class HistoryKeyHandlerTest {

    private final CommandHistory commandHistory = new CommandHistory();
    private final List<String> shownTexts = new ArrayList<>();
    private String textInCommandBox = "";
    private final HistoryKeyHandler handler =
            new HistoryKeyHandler(commandHistory, () -> textInCommandBox, shownTexts::add);

    private static KeyEvent keyPressed(KeyCode keyCode) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", keyCode, false, false, false, false);
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HistoryKeyHandler(null, () -> "", text -> { }));
        assertThrows(NullPointerException.class, () ->
                new HistoryKeyHandler(commandHistory, null, text -> { }));
        assertThrows(NullPointerException.class, () ->
                new HistoryKeyHandler(commandHistory, () -> "", null));
    }

    @Test
    public void handle_upKeyWithHistory_showsMostRecentCommandAndConsumesEvent() {
        commandHistory.add("list");
        commandHistory.add("list a");
        KeyEvent event = keyPressed(KeyCode.UP);

        handler.handle(event);

        assertEquals(List.of("list a"), shownTexts);
        assertTrue(event.isConsumed());
    }

    @Test
    public void handle_upKeyRepeatedly_walksBackToOldestCommand() {
        commandHistory.add("list");
        commandHistory.add("list a");

        handler.handle(keyPressed(KeyCode.UP));
        handler.handle(keyPressed(KeyCode.UP));
        handler.handle(keyPressed(KeyCode.UP));

        assertEquals(List.of("list a", "list", "list"), shownTexts);
    }

    @Test
    public void handle_upKeyWithoutHistory_showsNothingButConsumesEvent() {
        KeyEvent event = keyPressed(KeyCode.UP);

        handler.handle(event);

        assertTrue(shownTexts.isEmpty());
        assertTrue(event.isConsumed());
    }

    @Test
    public void handle_downKeyAfterUpKey_showsNewerCommandThenTextBeingTyped() {
        commandHistory.add("list");
        commandHistory.add("list a");
        textInCommandBox = "abc";

        handler.handle(keyPressed(KeyCode.UP));
        handler.handle(keyPressed(KeyCode.UP));
        handler.handle(keyPressed(KeyCode.DOWN));
        handler.handle(keyPressed(KeyCode.DOWN));

        assertEquals(List.of("list a", "list", "list a", "abc"), shownTexts);
    }

    @Test
    public void handle_downKeyWhileNotBrowsing_showsNothingButConsumesEvent() {
        commandHistory.add("list");
        KeyEvent event = keyPressed(KeyCode.DOWN);

        handler.handle(event);

        assertTrue(shownTexts.isEmpty());
        assertTrue(event.isConsumed());
    }

    @Test
    public void handle_upKeyWhenCommandBoxHasMostRecentCommand_showsOlderCommand() {
        commandHistory.add("list");
        commandHistory.add("delete");
        textInCommandBox = "delete";

        handler.handle(keyPressed(KeyCode.UP));

        assertEquals(List.of("list"), shownTexts);
    }

    @Test
    public void handle_otherKeys_doNothingAndAreNotConsumed() {
        commandHistory.add("list");

        for (KeyCode keyCode : List.of(KeyCode.A, KeyCode.ENTER, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.TAB)) {
            KeyEvent event = keyPressed(keyCode);
            handler.handle(event);
            assertFalse(event.isConsumed(), "Key should not be consumed: " + keyCode);
        }

        assertTrue(shownTexts.isEmpty());
        // an unrelated key does not start browsing either
        handler.handle(keyPressed(KeyCode.DOWN));
        assertTrue(shownTexts.isEmpty());
    }
}
