package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles the Up and Down keys of the command box. The Up key recalls an older command and the Down key
 * recalls a newer one, from a {@code CommandHistory}.
 * It works with functions instead of a text field, so that it can be tested without a GUI.
 */
class HistoryKeyHandler implements EventHandler<KeyEvent> {

    private final CommandHistory commandHistory;
    private final Supplier<String> currentTextSupplier;
    private final Consumer<String> recalledTextConsumer;

    /**
     * Creates a {@code HistoryKeyHandler} that recalls commands from {@code commandHistory}.
     *
     * @param currentTextSupplier gives the text that is in the command box when a key is pressed
     * @param recalledTextConsumer is given the text to show in the command box, when there is a command to
     *     recall
     */
    HistoryKeyHandler(CommandHistory commandHistory, Supplier<String> currentTextSupplier,
            Consumer<String> recalledTextConsumer) {
        requireNonNull(commandHistory);
        requireNonNull(currentTextSupplier);
        requireNonNull(recalledTextConsumer);
        this.commandHistory = commandHistory;
        this.currentTextSupplier = currentTextSupplier;
        this.recalledTextConsumer = recalledTextConsumer;
    }

    /**
     * Recalls a command if {@code event} is for the Up or Down key, and consumes the event so that the text
     * field does not also use the key to move its caret. Any other key is left alone.
     */
    @Override
    public void handle(KeyEvent event) {
        Optional<String> recalledText;
        if (event.getCode() == KeyCode.UP) {
            recalledText = commandHistory.previous(currentTextSupplier.get());
        } else if (event.getCode() == KeyCode.DOWN) {
            recalledText = commandHistory.next();
        } else {
            return;
        }

        recalledText.ifPresent(recalledTextConsumer);
        event.consume();
    }
}
