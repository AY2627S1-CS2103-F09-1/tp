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
     * Problems are reported in this order: unrecognized parameters, repeated parameters, an invalid index,
     * an invalid name, class or email, and finally that no detail is given. A name or class without a value
     * is invalid, and an email without a value removes the email.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER, EDIT_PREFIXES);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, EDIT_PREFIXES);
        argMultimap.verifyNoDuplicatePrefixesFor(EDIT_PREFIXES);

        Index index = parseIndex(argMultimap.getPreamble());
        EditPersonDescriptor editPersonDescriptor = parseDescriptor(argMultimap);
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Returns the index in {@code preamble}.
     * @throws ParseException if {@code preamble} is empty, with the invalid command format message, or is
     *         not a positive integer
     */
    private static Index parseIndex(String preamble) throws ParseException {
        if (preamble.isBlank()) {
            throw new ParseException(INVALID_FORMAT_MESSAGE);
        }
        return ParserUtil.parseIndex(preamble);
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
