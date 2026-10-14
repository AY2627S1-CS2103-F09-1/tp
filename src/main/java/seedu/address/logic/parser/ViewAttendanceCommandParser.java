package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;

/** Parses arguments and creates a ViewAttendanceCommand. */
public class ViewAttendanceCommandParser implements Parser<ViewAttendanceCommand> {

    @Override
    public ViewAttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, ViewAttendanceCommand.MESSAGE_USAGE));
        }

        String nameArgument = argMultimap.getValue(PREFIX_NAME).orElse("").trim();
        String classArgument = argMultimap.getValue(PREFIX_CLASS).orElse("").trim();

        if (nameArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_NAME);
        }

        if (classArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_CLASS);
        }

        Name name = ParserUtil.parseName(nameArgument);
        ClassName className = ParserUtil.parseClassName(classArgument);
        return new ViewAttendanceCommand(name, className);
    }

}
