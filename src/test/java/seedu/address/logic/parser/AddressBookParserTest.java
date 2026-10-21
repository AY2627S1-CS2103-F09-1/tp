package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteByIndexCommand;
import seedu.address.logic.commands.DeleteByNameAndClassCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.GradeCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.logic.commands.ViewGradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesKeyword;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteByIndexCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deleteByNameAndClass() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " /name John Tan /class A1");
        assertEquals(new DeleteByNameAndClassCommand("John Tan", "A1"), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find_throwsParseException() {
        // the find command was replaced by list KEYWORD
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("find Alice"));
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(), parser.parseCommand(ListCommand.COMMAND_WORD));
        assertEquals(new ListCommand(new PersonMatchesKeyword("Alice Pauline")),
                parser.parseCommand(ListCommand.COMMAND_WORD + " Alice Pauline"));
    }

    @Test
    public void parseCommand_tag() throws Exception {
        TagCommand command = (TagCommand) parser.parseCommand(
                TagCommand.COMMAND_WORD + " /group Group A /members 1, 2");
        assertEquals(new TagCommand(new Tag("Group A"), List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)),
                command);
    }

    @Test
    public void parseCommand_untag() throws Exception {
        UntagCommand command = (UntagCommand) parser.parseCommand(
                UntagCommand.COMMAND_WORD + " /group Group A /members 1, 2");
        assertEquals(new UntagCommand(new Tag("Group A"), List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)),
                command);
    }

    @Test
    public void parseCommand_import() throws Exception {
        ImportCommand command = (ImportCommand) parser.parseCommand(
                ImportCommand.COMMAND_WORD + " students.csv");

        assertEquals(new ImportCommand("students.csv"), command);
    }

    @Test
    public void parseCommand_importWithoutPath_throwsParseException() {
        assertThrows(ParseException.class, "Specify file path", () ->
                parser.parseCommand(ImportCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_commandWordInAnyCase_success() throws Exception {
        assertEquals(new ListCommand(), parser.parseCommand("LIST"));
        assertEquals(parser.parseCommand("list John"), parser.parseCommand("List John"));
        assertEquals(parser.parseCommand("add /name John Tan /class A1"),
                parser.parseCommand("ADD /name John Tan /class A1"));
        assertEquals(parser.parseCommand("viewA /name John Tan /class A1"),
                parser.parseCommand("viewa /name John Tan /class A1"));
        assertEquals(parser.parseCommand("viewG /name John Tan /class A1"),
                parser.parseCommand("VIEWg /name John Tan /class A1"));
        assertTrue(parser.parseCommand("CLEAR") instanceof ClearCommand);
        assertTrue(parser.parseCommand("Exit") instanceof ExitCommand);
        assertTrue(parser.parseCommand("hELP") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_everyCommandWordInUpperAndLowerCase_isNotUnknown() {
        List<String> commandWords = List.of(AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD,
                DeleteCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, ListCommand.COMMAND_WORD,
                TagCommand.COMMAND_WORD, UntagCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD, AttendanceCommand.COMMAND_WORD, ViewAttendanceCommand.COMMAND_WORD,
                ImportCommand.COMMAND_WORD, GradeCommand.COMMAND_WORD, ViewGradeCommand.COMMAND_WORD);

        for (String commandWord : commandWords) {
            for (String variant : List.of(commandWord.toUpperCase(), commandWord.toLowerCase())) {
                try {
                    parser.parseCommand(variant);
                } catch (ParseException pe) {
                    // a command that needs arguments reports them as missing, but never as unknown
                    assertNotEquals(MESSAGE_UNKNOWN_COMMAND, pe.getMessage(), variant);
                }
            }
        }
    }

    @Test
    public void parseCommand_commandWordWithDifferentCase_stillReportsProblemsOfItsArguments() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("DELETE x"));
    }

    @Test
    public void parseCommand_commandWordWithExtraLetters_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("LISTS"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("view /name John Tan /class A1"));
    }

    @Test
    public void parseCommand_attendance() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 16);
        AttendanceCommand expected = new AttendanceCommand(
            INDEX_FIRST_PERSON, date, Attendance.Status.ABSENT);

        AttendanceCommand actual = (AttendanceCommand) parser.parseCommand(
            "attendance 1 /date 16-09-2026 /status absent");

        assertEquals(expected, actual);
    }

    @Test
    public void parseCommand_viewAttendance() throws Exception {
        ViewAttendanceCommand expected = new ViewAttendanceCommand("John Tan", "A1");

        assertEquals(expected, parser.parseCommand("viewA /name John Tan /class A1"));
    }

    @Test
    public void parseCommand_grade() throws Exception {
        GradeCommand expected = new GradeCommand(INDEX_FIRST_PERSON,
                new Grade("Assignment 1", new BigDecimal("97.5")));

        assertEquals(expected, parser.parseCommand("grade 1 /assessment Assignment 1 /score 97.5"));
    }

}
