package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
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
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /** The most characters that a command can have. A command that adds a contact needs about 450. */
    public static final int MAX_INPUT_LENGTH = 5000;

    public static final String MESSAGE_INPUT_TOO_LONG = "The command is too long. It can have at most "
            + MAX_INPUT_LENGTH + " characters";

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT =
            Pattern.compile("(?<commandWord>[^\\s\\p{Zs}]+)(?<arguments>.*)");

    /**
     * Matches the kinds of space, other than the space itself, that can be typed or pasted by mistake: a tab, a
     * non-breaking space (which is common in text copied from a web page or a document), or a wide space.
     */
    private static final Pattern UNUSUAL_SPACES = Pattern.compile("[\\p{Zs}\\t]");
    private static final Pattern LEADING_OR_TRAILING_SPACES = Pattern.compile("^[\\s\\p{Zs}]+|[\\s\\p{Zs}]+$");
    private static final Pattern LEADING_SPACES = Pattern.compile("^[\\s\\p{Zs}]+");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Maps each command word, in lower case, to the parser of its arguments. The commands that take no
     * arguments ignore them, except {@code clear}, which rejects them because it deletes all contacts. Some
     * commands also have a shorter alias, which is mapped to the same kind of parser. Two command words or
     * aliases must not differ only in case, as the lookup ignores case.
     */
    private static final Map<String, Parser<? extends Command>> COMMAND_PARSERS = Map.ofEntries(
            entry(AddCommand.COMMAND_WORD, new AddCommandParser()),
            entry(EditCommand.COMMAND_WORD, new EditCommandParser()),
            entry(DeleteCommand.COMMAND_WORD, new DeleteCommandParser()),
            entry(ClearCommand.COMMAND_WORD, new ClearCommandParser()),
            entry(ListCommand.COMMAND_WORD, new ListCommandParser()),
            entry(TagCommand.COMMAND_WORD, new TagCommandParser()),
            entry(UntagCommand.COMMAND_WORD, new UntagCommandParser()),
            entry(ExitCommand.COMMAND_WORD, arguments -> new ExitCommand()),
            entry(HelpCommand.COMMAND_WORD, arguments -> new HelpCommand()),
            entry(AttendanceCommand.COMMAND_WORD, new AttendanceCommandParser()),
            entry(AttendanceCommand.COMMAND_ALIAS, new AttendanceCommandParser()),
            entry(ViewAttendanceCommand.COMMAND_WORD, new ViewAttendanceCommandParser()),
            entry(ViewAttendanceCommand.COMMAND_ALIAS, new ViewAttendanceCommandParser()),
            entry(ImportCommand.COMMAND_WORD, new ImportCommandParser()),
            entry(GradeCommand.COMMAND_WORD, new GradeCommandParser()),
            entry(ViewGradeCommand.COMMAND_WORD, new ViewGradeCommandParser()),
            entry(ViewGradeCommand.COMMAND_ALIAS, new ViewGradeCommandParser()),
            entry(DeleteAttendanceCommand.COMMAND_WORD, new DeleteAttendanceCommandParser()),
            entry(DeleteAttendanceCommand.COMMAND_ALIAS, new DeleteAttendanceCommandParser()),
            entry(DeleteGradeCommand.COMMAND_WORD, new DeleteGradeCommandParser()),
            entry(DeleteGradeCommand.COMMAND_ALIAS, new DeleteGradeCommandParser())
    );

    private static Map.Entry<String, Parser<? extends Command>> entry(String commandWord,
            Parser<? extends Command> parser) {
        return Map.entry(StringUtil.toAsciiLowerCase(commandWord), parser);
    }

    /**
     * Parses user input into command for execution. The command word is matched ignoring the case of its
     * English letters, so {@code LIST} and {@code viewa} are the commands {@code list} and {@code viewA}.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input is longer than {@code MAX_INPUT_LENGTH} or does not conform to
     *         the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        requireNonNull(userInput);
        if (userInput.length() > MAX_INPUT_LENGTH) {
            throw new ParseException(MESSAGE_INPUT_TOO_LONG);
        }
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(
                LEADING_OR_TRAILING_SPACES.matcher(userInput).replaceAll(""));
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = normalizeSpaces(commandWord, matcher.group("arguments"));

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        Parser<? extends Command> parser = COMMAND_PARSERS.get(StringUtil.toAsciiLowerCase(commandWord));
        if (parser == null) {
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
        return parser.parse(arguments);
    }

    /**
     * Returns {@code arguments} with every tab and every other kind of space replaced by a space, so that the
     * parsers only have to recognize the space. The arguments of {@code import} are the path of a file, whose
     * name can contain such a character, so only the spaces that separate it from the command word are
     * replaced.
     */
    private static String normalizeSpaces(String commandWord, String arguments) {
        boolean isImport = StringUtil.toAsciiLowerCase(commandWord)
                .equals(StringUtil.toAsciiLowerCase(ImportCommand.COMMAND_WORD));
        if (isImport) {
            return " " + LEADING_SPACES.matcher(arguments).replaceAll("");
        }
        return UNUSUAL_SPACES.matcher(arguments).replaceAll(" ");
    }

}
