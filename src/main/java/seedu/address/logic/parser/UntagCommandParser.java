package seedu.address.logic.parser;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new UntagCommand object
 */
public class UntagCommandParser extends GroupMembershipCommandParser<UntagCommand> {

    /**
     * Creates a parser for the untag command.
     */
    public UntagCommandParser() {
        super(UntagCommand.MESSAGE_USAGE);
    }

    @Override
    protected UntagCommand createCommand(Tag group, List<Index> memberIndices) {
        return new UntagCommand(group, memberIndices);
    }

}
