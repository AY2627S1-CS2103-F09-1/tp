package seedu.address.logic.parser;

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

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Maps each command word, in lower case, to the parser of its arguments. The commands that take no
     * arguments ignore them. Two command words must not differ only in case, as the lookup ignores case.
     */
    private static final Map<String, Parser<? extends Command>> COMMAND_PARSERS = Map.ofEntries(
            entry(AddCommand.COMMAND_WORD, new AddCommandParser()),
            entry(EditCommand.COMMAND_WORD, new EditCommandParser()),
            entry(DeleteCommand.COMMAND_WORD, new DeleteCommandParser()),
            entry(ClearCommand.COMMAND_WORD, arguments -> new ClearCommand()),
            entry(ListCommand.COMMAND_WORD, new ListCommandParser()),
            entry(TagCommand.COMMAND_WORD, new TagCommandParser()),
            entry(UntagCommand.COMMAND_WORD, new UntagCommandParser()),
            entry(ExitCommand.COMMAND_WORD, arguments -> new ExitCommand()),
            entry(HelpCommand.COMMAND_WORD, arguments -> new HelpCommand()),
            entry(AttendanceCommand.COMMAND_WORD, new AttendanceCommandParser()),
            entry(ViewAttendanceCommand.COMMAND_WORD, new ViewAttendanceCommandParser()),
            entry(ImportCommand.COMMAND_WORD, new ImportCommandParser()),
            entry(GradeCommand.COMMAND_WORD, new GradeCommandParser()),
            entry(ViewGradeCommand.COMMAND_WORD, new ViewGradeCommandParser()),
            entry(DeleteGradeCommand.COMMAND_WORD, new DeleteGradeCommandParser())
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
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

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

}
