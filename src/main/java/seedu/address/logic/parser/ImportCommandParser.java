package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ImportCommand object
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    @Override
    public ImportCommand parse(String args) throws ParseException {
        String filePath = args.trim();
        if (filePath.isEmpty()) {
            throw new ParseException("Specify file path");
        }
        if (filePath.contains("\n") || filePath.contains("\r")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
        }
        return new ImportCommand(filePath);
    }
}
