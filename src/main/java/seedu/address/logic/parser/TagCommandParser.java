package seedu.address.logic.parser;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object
 */
public class TagCommandParser extends GroupMembershipCommandParser<TagCommand> {

    /**
     * Creates a parser for the tag command.
     */
    public TagCommandParser() {
        super(TagCommand.MESSAGE_USAGE);
    }

    @Override
    protected TagCommand createCommand(Tag group, List<Index> memberIndices) {
        return new TagCommand(group, memberIndices);
    }

}
