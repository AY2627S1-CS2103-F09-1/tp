package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.commands.CommandTestUtil.CLASS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.CLASS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CLASS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.parser.AddCommandParser.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    private static final String PHONE_DESC = " /phone 91234567";
    private static final String ADDRESS_DESC = " /address Blk 30 Geylang Street 29";
    private static final String TAG_DESC = " " + CliSyntax.PREFIX_TAG + " friends";

    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags().build();

        assertParseSuccess(parser, NAME_DESC_BOB + CLASS_DESC_BOB + EMAIL_DESC_BOB,
                new AddCommand(expectedPerson));

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + CLASS_DESC_BOB + EMAIL_DESC_BOB,
                new AddCommand(expectedPerson));

        // parameters in a different order
        assertParseSuccess(parser, EMAIL_DESC_BOB + CLASS_DESC_BOB + NAME_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_optionalFieldMissing_success() {
        Person expectedPerson = new PersonBuilder(AMY).withTags().withoutEmail().build();
        assertParseSuccess(parser, NAME_DESC_AMY + CLASS_DESC_AMY, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameWithAllowedSymbols_success() {
        Person expectedPerson = new PersonBuilder().withName("Mary-Ann O'Neil Tan s/o Kumar Jr.")
                .withClassName("Sec 3-2").withoutEmail().build();
        assertParseSuccess(parser, " /name Mary-Ann O'Neil Tan s/o Kumar Jr. /class Sec 3-2",
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, CLASS_DESC_BOB + EMAIL_DESC_BOB, MESSAGE_MISSING_NAME);
        assertParseFailure(parser, CLASS_DESC_BOB, MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_missingClass_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB, MESSAGE_MISSING_CLASS);
        assertParseFailure(parser, NAME_DESC_BOB, MESSAGE_MISSING_CLASS);

        // class value without its prefix is joined to the name
        assertParseFailure(parser, NAME_DESC_BOB + VALID_CLASS_BOB, MESSAGE_MISSING_CLASS);
    }

    @Test
    public void parse_missingNameAndClass_reportsMissingNameOnly() {
        assertParseFailure(parser, "", MESSAGE_MISSING_NAME);
        assertParseFailure(parser, EMAIL_DESC_BOB, MESSAGE_MISSING_NAME);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + CLASS_DESC_BOB + EMAIL_DESC_BOB,
                MESSAGE_INVALID_FORMAT);

        // name given without its prefix
        assertParseFailure(parser, " " + VALID_NAME_BOB + CLASS_DESC_BOB, MESSAGE_INVALID_FORMAT);

        // email given without its prefix
        assertParseFailure(parser, " " + VALID_EMAIL_BOB + NAME_DESC_BOB + CLASS_DESC_BOB,
                MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unrecognizedParameter_failure() {
        // parameters that were removed from the contact
        assertParseFailure(parser, NAME_DESC_BOB + CLASS_DESC_BOB + PHONE_DESC, MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, NAME_DESC_BOB + CLASS_DESC_BOB + ADDRESS_DESC, MESSAGE_UNKNOWN_PARAMETER);

        // tags are not accepted when adding a person
        assertParseFailure(parser, NAME_DESC_BOB + CLASS_DESC_BOB + TAG_DESC, MESSAGE_UNKNOWN_PARAMETER);

        // unknown parameter before the known ones
        assertParseFailure(parser, PHONE_DESC + NAME_DESC_BOB + CLASS_DESC_BOB, MESSAGE_UNKNOWN_PARAMETER);

        // matching is case-sensitive and requires the whole token to match
        assertParseFailure(parser, " /Name Bob Choo" + CLASS_DESC_BOB, MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, NAME_DESC_BOB + " /classes A1", MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, " /nameBob" + CLASS_DESC_BOB, MESSAGE_UNKNOWN_PARAMETER);

        // the old short prefixes are not parameters, so they are taken as part of the previous value
        assertParseFailure(parser, NAME_DESC_BOB + " p/91234567" + CLASS_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedParameter_failure() {
        String validString = NAME_DESC_BOB + CLASS_DESC_BOB + EMAIL_DESC_BOB;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validString, MESSAGE_DUPLICATE_PARAMETERS);

        // multiple classes
        assertParseFailure(parser, CLASS_DESC_AMY + validString, MESSAGE_DUPLICATE_PARAMETERS);

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validString, MESSAGE_DUPLICATE_PARAMETERS);

        // multiple fields repeated
        assertParseFailure(parser, validString + validString, MESSAGE_DUPLICATE_PARAMETERS);

        // invalid value followed by valid value, or the other way round
        assertParseFailure(parser, INVALID_NAME_DESC + validString, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, validString + INVALID_NAME_DESC, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, INVALID_CLASS_DESC + validString, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, validString + INVALID_CLASS_DESC, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, INVALID_EMAIL_DESC + validString, MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, validString + INVALID_EMAIL_DESC, MESSAGE_DUPLICATE_PARAMETERS);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, INVALID_NAME_DESC + CLASS_DESC_BOB + EMAIL_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // empty name
        assertParseFailure(parser, " " + PREFIX_NAME + CLASS_DESC_BOB, Name.MESSAGE_EMPTY);

        // too long name
        String tooLongNameDesc = " " + PREFIX_NAME + " " + "a".repeat(Name.MAX_LENGTH + 1);
        assertParseFailure(parser, tooLongNameDesc + CLASS_DESC_BOB, Name.MESSAGE_TOO_LONG);
    }

    @Test
    public void parse_invalidClass_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_CLASS_DESC + EMAIL_DESC_BOB,
                ClassName.MESSAGE_CONSTRAINTS);

        // empty class
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_CLASS + EMAIL_DESC_BOB,
                ClassName.MESSAGE_EMPTY);

        // too long class
        String tooLongClassDesc = " " + PREFIX_CLASS + " " + "a".repeat(ClassName.MAX_LENGTH + 1);
        assertParseFailure(parser, NAME_DESC_BOB + tooLongClassDesc, ClassName.MESSAGE_TOO_LONG);
    }

    @Test
    public void parse_invalidEmail_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + CLASS_DESC_BOB + INVALID_EMAIL_DESC,
                Email.MESSAGE_CONSTRAINTS);

        // an email parameter with no value is invalid, not treated as absent
        assertParseFailure(parser, NAME_DESC_BOB + CLASS_DESC_BOB + " " + PREFIX_EMAIL,
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_multipleInvalidValues_reportsNameThenClassThenEmail() {
        assertParseFailure(parser, INVALID_NAME_DESC + INVALID_CLASS_DESC + INVALID_EMAIL_DESC,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_CLASS_DESC + INVALID_EMAIL_DESC,
                ClassName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_multipleProblems_reportsInDocumentedOrder() {
        // unrecognized parameter comes before repeated parameter
        assertParseFailure(parser, NAME_DESC_BOB + NAME_DESC_AMY + PHONE_DESC, MESSAGE_UNKNOWN_PARAMETER);

        // unrecognized parameter comes before text before the first parameter
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + PHONE_DESC, MESSAGE_UNKNOWN_PARAMETER);

        // repeated parameter comes before text before the first parameter
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + NAME_DESC_AMY,
                MESSAGE_DUPLICATE_PARAMETERS);

        // text before the first parameter comes before a missing parameter
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB, MESSAGE_INVALID_FORMAT);

        // a missing name comes before an invalid class
        assertParseFailure(parser, INVALID_CLASS_DESC, MESSAGE_MISSING_NAME);

        // a missing class comes before an invalid name
        assertParseFailure(parser, INVALID_NAME_DESC, MESSAGE_MISSING_CLASS);
    }
}
