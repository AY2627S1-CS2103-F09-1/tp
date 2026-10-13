package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Name;

/** Parses arguments and creates a ViewAttendanceCommand. */
public class ViewAttendanceCommandParser implements Parser<ViewAttendanceCommand> {

    private static final String MESSAGE_MISSING_NAME = "Missing student name.";
    private static final String MESSAGE_MISSING_CLASS = "Missing class.";

    @Override
    public ViewAttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS);
        String nameArgument = argMultimap.getValue(PREFIX_NAME).orElse("").trim();
        String classArgument = argMultimap.getValue(PREFIX_CLASS).orElse("").trim();

        if (nameArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_NAME);
        }
        if (classArgument.isBlank()) {
            throw new ParseException(MESSAGE_MISSING_CLASS);
        }
        if (argMultimap.getAllValues(PREFIX_NAME).size() > 1 || argMultimap.getAllValues(PREFIX_CLASS).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_PARAMETERS);
        }

        Name name = ParserUtil.parseName(nameArgument);
        ClassName className = ParserUtil.parseClassName(classArgument);
        return new ViewAttendanceCommand(name, className);
    }

}
