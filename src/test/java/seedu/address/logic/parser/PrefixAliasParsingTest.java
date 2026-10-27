package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests that the aliases of the prefixes, such as {@code /n} for {@code /name}, work through the parsers of the
 * commands that take them. The aliases are the same as the full prefixes, so the commands are compared with the
 * commands that are parsed from the full prefixes.
 */
public class PrefixAliasParsingTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_aliasesOfNameAndClass_giveSameCommandAsFullPrefixes() throws Exception {
        assertSameCommand("add /name John Doe /class A1 /email johnd@example.com",
                "add /n John Doe /c A1 /email johnd@example.com");
        assertSameCommand("edit 1 /name John Tann /class A2", "edit 1 /n John Tann /c A2");
        assertSameCommand("delete /name John Tan /class A1", "delete /n John Tan /c A1");
        assertSameCommand("viewA /name John Tan /class A1", "viewA /n John Tan /c A1");
        assertSameCommand("viewG /name John Tan /class A1", "viewG /n John Tan /c A1");
    }

    @Test
    public void parseCommand_aliasesOfAttendancePrefixes_giveSameCommandAsFullPrefixes() throws Exception {
        assertSameCommand("attendance 1 /date 16-09-2026 /status absent",
                "attendance 1 /date 16-09-2026 /st absent");
        assertSameCommand("attendance /name John Tan /class A1 /date 16-09-2026 /status late",
                "attendance /n John Tan /c A1 /date 16-09-2026 /st late");
        assertSameCommand("deleteA /name John Tan /class A1 /date 16-09-2026",
                "deleteA /n John Tan /c A1 /date 16-09-2026");
    }

    @Test
    public void parseCommand_aliasesOfGradePrefixes_giveSameCommandAsFullPrefixes() throws Exception {
        assertSameCommand("grade 1 /assessment WA2 /score 97", "grade 1 /as WA2 /sc 97");
        assertSameCommand("grade /name John Tan /class A1 /assessment WA2 /score 97",
                "grade /n John Tan /c A1 /as WA2 /sc 97");
        assertSameCommand("deleteG 1 /assessment WA2", "deleteG 1 /as WA2");
        assertSameCommand("deleteG /name John Tan /class A1 /assessment WA2",
                "deleteG /n John Tan /c A1 /as WA2");
    }

    @Test
    public void parseCommand_aliasOfMembers_givesSameCommandAsFullPrefix() throws Exception {
        assertSameCommand("tag /group Group A /members 1,2", "tag /group Group A /m 1,2");
        assertSameCommand("untag /group Group A /members 1,2", "untag /group Group A /m 1,2");
    }

    @Test
    public void parseCommand_aliasesWithCommandAliases_giveSameCommandAsFullForms() throws Exception {
        assertSameCommand("attendance /name John Tan /class A1 /date 16-09-2026 /status late",
                "att /n John Tan /c A1 /date 16-09-2026 /st late");
        assertSameCommand("deleteG /name John Tan /class A1 /assessment WA2", "dg /n John Tan /c A1 /as WA2");
    }

    @Test
    public void parseCommand_fullPrefixesAndAliasesTogether_giveSameCommand() throws Exception {
        assertSameCommand("add /name John Doe /class A1", "add /n John Doe /class A1");
        assertSameCommand("grade 1 /assessment WA2 /score 97", "grade 1 /as WA2 /score 97");
    }

    @Test
    public void parseCommand_aliasesInOtherCase_giveSameCommand() throws Exception {
        assertSameCommand("add /name John Doe /class A1", "ADD /N John Doe /C A1");
        assertSameCommand("grade 1 /assessment WA2 /score 97", "grade 1 /As WA2 /SC 97");
    }

    @Test
    public void parseCommand_prefixGivenInBothForms_throwsParseExceptionForRepeatedParameter() {
        assertRepeatedParameter("add /name John /n Jane /class A1", "/name");
        assertRepeatedParameter("add /name John /class A1 /c A2", "/class");
        assertRepeatedParameter("delete /n John /name Jane /class A1", "/name");
        assertRepeatedParameter("attendance 1 /date 16-09-2026 /status absent /st late", "/status");
        assertRepeatedParameter("grade 1 /assessment WA2 /as WA3 /score 97", "/assessment");
        assertRepeatedParameter("grade 1 /assessment WA2 /score 97 /sc 98", "/score");
        assertRepeatedParameter("tag /group Group A /members 1 /m 2", "/members");
        assertRepeatedParameter("deleteG 1 /as WA2 /assessment WA3", "/assessment");
    }

    @Test
    public void parseCommand_aliasGivenToCommandThatDoesNotTakeIt_showsFullPrefixesInMessage() {
        assertUnknownParameter("add /n John /c A1 /m 1", AddCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertUnknownParameter("edit 1 /as WA2", EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertUnknownParameter("tag /group A /m 1 /n John",
                GroupMembershipCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertUnknownParameter("viewA /n John /c A1 /st late", NameAndClassCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertEquals("Unknown parameter. Use /name, /class or /email" + ParserUtil.MESSAGE_SLASH_WORD_HINT,
                AddCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parseCommand_valueThatLooksLikeAnAlias_isNotAPrefix() throws Exception {
        assertSameCommand("add /name Tan s/o Kumar /class C1", "add /n Tan s/o Kumar /c C1");
        assertSameCommand("add /name Tan a/c Lim /class C1", "add /n Tan a/c Lim /c C1");
        assertSameCommand("add /name Lee x/n Wei /class C1", "add /n Lee x/n Wei /c C1");
    }

    /**
     * Asserts that {@code shortForm} is parsed into the same command as {@code fullForm}.
     */
    private void assertSameCommand(String fullForm, String shortForm) throws Exception {
        Command expectedCommand = parser.parseCommand(fullForm);

        assertEquals(expectedCommand, parser.parseCommand(shortForm));
    }

    private void assertRepeatedParameter(String userInput, String repeatedPrefix) {
        String expectedMessage = getErrorMessageForDuplicatePrefixes(repeatedPrefix);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(userInput));
    }

    private void assertUnknownParameter(String userInput, String expectedMessage) {
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(userInput));
    }
}
