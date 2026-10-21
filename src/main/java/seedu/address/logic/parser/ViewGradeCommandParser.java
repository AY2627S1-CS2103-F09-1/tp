package seedu.address.logic.parser;

import seedu.address.logic.commands.ViewGradeCommand;

/** Parses arguments and creates a ViewGradeCommand. */
public class ViewGradeCommandParser extends NameAndClassCommandParser<ViewGradeCommand> {

    /**
     * Creates a parser for the {@code viewG} command.
     */
    public ViewGradeCommandParser() {
        super(ViewGradeCommand.MESSAGE_USAGE);
    }

    @Override
    protected ViewGradeCommand createCommand(String name, String className) {
        return new ViewGradeCommand(name, className);
    }

}
