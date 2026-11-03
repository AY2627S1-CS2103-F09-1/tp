package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + ", " + PREFIX_CLASS + " or " + PREFIX_EMAIL
            + ParserUtil.MESSAGE_SLASH_WORD_HINT;

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private static final Prefix[] EDIT_PREFIXES = {
        PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL
    };

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * Problems are reported in this order: no arguments, unrecognized parameters, repeated parameters, parameter
     * missing a space before it, missing index, no detail to edit, invalid index and invalid name, class or email.
     * A name or class without a value is invalid, and an email without a value removes the email.
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER, EDIT_PREFIXES);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, EDIT_PREFIXES);
        argMultimap.verifyNoDuplicatePrefixesFor(EDIT_PREFIXES);
        ParserUtil.requireNoStuckPrefixes(args);

        String preamble = argMultimap.getPreamble();
        if (preamble.isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        if (!hasDetail(argMultimap)) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        Index index = ParserUtil.parseIndex(preamble);
        EditPersonDescriptor editPersonDescriptor = parseDescriptor(argMultimap);
        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Returns whether at least one editable parameter was given. A parameter without a value still counts as
     * given, since an empty email removes it and an empty name or class has its own validation message.
     */
    private static boolean hasDetail(ArgumentMultimap argMultimap) {
        return argMultimap.getValue(PREFIX_NAME).isPresent()
                || argMultimap.getValue(PREFIX_CLASS).isPresent()
                || argMultimap.getValue(PREFIX_EMAIL).isPresent();
    }

    /**
     * Returns an {@code EditPersonDescriptor} with the details given in {@code argMultimap}.
     * @throws ParseException if a detail is invalid
     */
    private static EditPersonDescriptor parseDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_CLASS).isPresent()) {
            editPersonDescriptor.setClassName(
                    ParserUtil.parseClassName(argMultimap.getValue(PREFIX_CLASS).get()));
        }
        parseEmail(argMultimap, editPersonDescriptor);

        return editPersonDescriptor;
    }

    /**
     * Sets the email of {@code editPersonDescriptor} to the email given in {@code argMultimap}, or marks the
     * email as removed if the email is given without a value. Does nothing if no email is given.
     * @throws ParseException if the email is invalid
     */
    private static void parseEmail(ArgumentMultimap argMultimap, EditPersonDescriptor editPersonDescriptor)
            throws ParseException {
        Optional<String> email = argMultimap.getValue(PREFIX_EMAIL);
        if (email.isEmpty()) {
            return;
        }
        if (email.get().isBlank()) {
            editPersonDescriptor.setEmailCleared(true);
        } else {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(email.get()));
        }
    }

}
