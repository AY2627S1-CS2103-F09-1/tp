package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteAttendanceCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteGradeCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.logic.commands.ViewGradeCommand;
import seedu.address.logic.parser.CliSyntax;
import seedu.address.logic.parser.Prefix;

public class HelpWindowTest {

    private static final List<String> DOCUMENTED_COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD,
            AttendanceCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            DeleteAttendanceCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            GradeCommand.COMMAND_WORD,
            TagCommand.COMMAND_WORD,
            UntagCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ImportCommand.COMMAND_WORD,
            ViewAttendanceCommand.COMMAND_WORD,
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
    public void helpMessage_deleteAttendanceCommand_hasLineForEachWayToIdentifyContact() {
        assertTrue(HelpWindow.HELP_MESSAGE.lines()
                .anyMatch(line -> line.equals("deleteA INDEX /date DATE")));
        assertTrue(HelpWindow.HELP_MESSAGE.lines()
                .anyMatch(line -> line.equals("deleteA /name NAME /class CLASS /date DATE")));
    }

    @Test
    public void helpMessage_tagAndUntagCommands_haveSummaryLines() {
        assertTrue(HelpWindow.HELP_MESSAGE.lines()
                .anyMatch(line -> line.equals("tag /group GROUP_NAME /members INDEX[,INDEX]...")));
        assertTrue(HelpWindow.HELP_MESSAGE.lines()
                .anyMatch(line -> line.equals("untag /group GROUP_NAME /members INDEX[,INDEX]...")));
    }

    @Test
    public void helpMessage_editCommand_hasSummaryLine() {
        assertTrue(HelpWindow.HELP_MESSAGE.lines().anyMatch(
                line -> line.equals("edit INDEX [/name NAME] [/class CLASS] [/email [EMAIL]]")));
    }

    @Test
    public void helpMessage_prefixAliases_areListedWithTheirPrefixes() throws Exception {
        String shortcuts = shortcutsOfHelpMessage();
        int checkedAliasCount = 0;

        // every alias of every prefix is checked, so that a new alias cannot be left out of the help message
        for (Field field : CliSyntax.class.getFields()) {
            if (field.getType() != Prefix.class) {
                continue;
            }
            Prefix prefix = (Prefix) field.get(null);
            for (String alias : prefix.getAliases()) {
                assertTrue(shortcuts.contains(alias + " = " + prefix.getPrefix()),
                        "Help message has no shortcut for " + alias);
                checkedAliasCount++;
            }
        }

        assertTrue(checkedAliasCount > 0);
    }

    @Test
    public void helpMessage_shortcuts_areAfterAllTheCommands() {
        String shortcuts = shortcutsOfHelpMessage();
        String commands = HelpWindow.HELP_MESSAGE.substring(0, HelpWindow.HELP_MESSAGE.indexOf(shortcuts));

        assertTrue(commands.contains("exit"));
        assertTrue(shortcuts.startsWith("Shortcuts"));
    }

    @Test
    public void helpMessage_commandAliases_areListedWithTheirCommandWords() {
        String shortcuts = shortcutsOfHelpMessage();

        assertTrue(shortcuts.contains(AttendanceCommand.COMMAND_ALIAS + " = " + AttendanceCommand.COMMAND_WORD));
        assertTrue(shortcuts.contains(
                ViewAttendanceCommand.COMMAND_ALIAS + " = " + ViewAttendanceCommand.COMMAND_WORD));
        assertTrue(shortcuts.contains(ViewGradeCommand.COMMAND_ALIAS + " = " + ViewGradeCommand.COMMAND_WORD));
        assertTrue(shortcuts.contains(
                DeleteAttendanceCommand.COMMAND_ALIAS + " = " + DeleteAttendanceCommand.COMMAND_WORD));
        assertTrue(shortcuts.contains(DeleteGradeCommand.COMMAND_ALIAS + " = " + DeleteGradeCommand.COMMAND_WORD));
    }

    @Test
    public void helpMessage_viewGradesCommand_hasSummaryLine() {
        assertTrue(HelpWindow.HELP_MESSAGE.lines()
                .anyMatch(line -> line.equals("viewG /name NAME /class CLASS")));
    }

    @Test
    public void userGuideUrl_isTeamUserGuide() {
        assertEquals("https://ay2627s1-cs2103-f09-1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }

    /**
     * Returns the part of the help message that starts at the heading of the shortcuts.
     */
    private static String shortcutsOfHelpMessage() {
        int headingIndex = HelpWindow.HELP_MESSAGE.indexOf("Shortcuts");
        assertTrue(headingIndex >= 0, "Help message has no shortcuts");
        return HelpWindow.HELP_MESSAGE.substring(headingIndex);
    }
}
