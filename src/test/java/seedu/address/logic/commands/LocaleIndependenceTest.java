package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesKeyword;

/**
 * Tests that the messages of the commands do not depend on the locale of the computer, whose default number
 * format can use other digits than 0-9, such as the Thai digits.
 */
public class LocaleIndependenceTest {

    private static final Locale THAI_DIGITS_LOCALE = Locale.forLanguageTag("th-TH-u-nu-thai");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Locale originalFormatLocale;

    @BeforeEach
    public void useLocaleWithOtherDigits() {
        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
        Locale.setDefault(Locale.Category.FORMAT, THAI_DIGITS_LOCALE);
        assertEquals("๑", String.format("%d", 1), "The test locale must not use the digits 0-9");
    }

    @AfterEach
    public void restoreLocale() {
        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale);
    }

    @Test
    public void execute_listCommand_countUsesDigitsZeroToNine() throws CommandException {
        String message = new ListCommand(new PersonMatchesKeyword("Alice")).execute(model).getFeedbackToUser();

        assertEquals("1 contact(s) listed!", message);
    }

    @Test
    public void execute_attendanceWithUnusualYear_warningUsesDigitsZeroToNine() throws CommandException {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        AttendanceByIndexCommand command = new AttendanceByIndexCommand(
                INDEX_FIRST_PERSON, LocalDate.of(226, 9, 16), Attendance.Status.LATE);

        String message = command.execute(model).getFeedbackToUser();

        assertEquals("Attendance for " + person.getName() + " on 16-09-0226 recorded as late."
                + " Warning: the year 0226 is before 2000. If the date is a typo, use deleteA to remove the "
                + "record and enter it again.", message);
    }
}
