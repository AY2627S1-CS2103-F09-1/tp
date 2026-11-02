package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandResult.DisplayTarget.ATTENDANCE_HISTORY;
import static seedu.address.logic.commands.CommandResult.DisplayTarget.GRADE_HISTORY;
import static seedu.address.logic.commands.CommandResult.DisplayTarget.RESULT_DISPLAY;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandResultTest {
    @Test
    public void constructor_showHelpAndExit_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, CommandResult.MESSAGE_HELP_AND_EXIT, () ->
                new CommandResult("feedback", true, true));
    }

    @Test
    public void constructor_nullFeedback_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult(null));
        assertThrows(NullPointerException.class, () -> new CommandResult(null, true, false));
    }

    @Test
    public void constructor_nullDisplayTarget_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", null));
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", false, false, null));
    }

    @Test
    public void equals_variousResults_returnsExpected() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false, RESULT_DISPLAY)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // different displayTarget value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", ATTENDANCE_HISTORY)));
    }

    @Test
    public void hashCode_variousResults_matchesEquality() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // different displayTarget value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", GRADE_HISTORY).hashCode());
    }

    @Test
    public void toString_commandResult_returnsExpectedFormat() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", displayTarget=" + commandResult.getDisplayTarget() + "}";
        assertEquals(expected, commandResult.toString());
    }
}
