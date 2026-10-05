package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Collections;
import java.util.Optional;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_MISSING_NAME = "Command requires a name";
    public static final String MESSAGE_MISSING_CLASS = "Command requires a class";
    public static final String MESSAGE_UNKNOWN_PARAMETER =
            "Unknown parameter. Use " + PREFIX_NAME + ", " + PREFIX_CLASS + " or " + PREFIX_EMAIL;

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * Problems are reported in this order: unrecognized parameters, repeated parameters, text before the
     * first parameter, missing name, missing class, and finally invalid values.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ParserUtil.requireNoUnrecognizedPrefixes(args, MESSAGE_UNKNOWN_PARAMETER,
                PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        String nameValue = argMultimap.getValue(PREFIX_NAME)
                .orElseThrow(() -> new ParseException(MESSAGE_MISSING_NAME));
        String classValue = argMultimap.getValue(PREFIX_CLASS)
                .orElseThrow(() -> new ParseException(MESSAGE_MISSING_CLASS));

        Name name = ParserUtil.parseName(nameValue);
        ClassName className = ParserUtil.parseClassName(classValue);
        Optional<Email> email = parseOptionalEmail(argMultimap);

        // Tags cannot be given when adding a person for now
        Person person = new Person(name, className, email, Collections.emptySet());

        return new AddCommand(person);
    }

    /**
     * Returns the parsed email in {@code argMultimap}, or an empty {@code Optional} if no email was given.
     * @throws ParseException if an email was given but is invalid
     */
    private static Optional<Email> parseOptionalEmail(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> email = argMultimap.getValue(PREFIX_EMAIL);
        if (email.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseEmail(email.get()));
    }

}
