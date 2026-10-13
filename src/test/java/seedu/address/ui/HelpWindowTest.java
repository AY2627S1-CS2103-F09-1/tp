package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.TagCommand;

public class HelpWindowTest {

    private static final List<String> DOCUMENTED_COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD,
            AttendanceCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            TagCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ImportCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD);

    @Test
    public void helpMessage_documentedCommandWord_hasSummaryLine() {
        for (String commandWord : DOCUMENTED_COMMAND_WORDS) {
            boolean hasSummaryLine = HelpWindow.HELP_MESSAGE.lines()
                    .anyMatch(line -> line.equals(commandWord) || line.startsWith(commandWord + " "));
            assertTrue(hasSummaryLine, "Help message has no summary line for the command: " + commandWord);
        }
    }

    @Test
    public void helpMessage_deleteCommand_hasLineForEachWayToIdentifyContact() {
        assertTrue(HelpWindow.HELP_MESSAGE.lines().anyMatch(line -> line.equals("delete INDEX")));
        assertTrue(HelpWindow.HELP_MESSAGE.lines()
                .anyMatch(line -> line.equals("delete /name NAME /class CLASS")));
    }

    @Test
    public void userGuideUrl_isTeamUserGuide() {
        assertEquals("https://ay2627s1-cs2103-f09-1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }
}
