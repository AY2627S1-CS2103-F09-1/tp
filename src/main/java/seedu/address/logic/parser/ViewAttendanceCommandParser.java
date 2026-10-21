package seedu.address.logic.parser;

import seedu.address.logic.commands.ViewAttendanceCommand;

/** Parses arguments and creates a ViewAttendanceCommand. */
public class ViewAttendanceCommandParser extends NameAndClassCommandParser<ViewAttendanceCommand> {

    /**
     * Creates a parser for the {@code viewA} command.
     */
    public ViewAttendanceCommandParser() {
        super(ViewAttendanceCommand.MESSAGE_USAGE);
    }

    @Override
    protected ViewAttendanceCommand createCommand(String name, String className) {
        return new ViewAttendanceCommand(name, className);
    }

}
