package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETERS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.CLASS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CLASS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);

        // text before the index
        assertParseFailure(parser, "text 1" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownParameter_failure() {
        assertParseFailure(parser, "1 /phone 91234567", EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, "1" + NAME_DESC_AMY + " /phone 91234567",
                EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);

        // groups are changed with the tag and untag commands, and not with the edit command
        assertParseFailure(parser, "1 /tag friend", EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, "1 /untag friend", EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);
        assertParseFailure(parser, "1 /group Group A", EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);

        // unknown parameters are reported before an invalid index
        assertParseFailure(parser, "x /phone 91234567", EditCommandParser.MESSAGE_UNKNOWN_PARAMETER);
    }

    @Test
    public void parse_unknownParameterMessage_listsAllParameters() {
        String expected = "Unknown parameter. Use " + PREFIX_NAME + ", " + PREFIX_CLASS + " or " + PREFIX_EMAIL;
        assertParseFailure(parser, "1 /phone 91234567", expected);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_CLASS_DESC, ClassName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);

        // a name or class without a value
        assertParseFailure(parser, "1 " + PREFIX_NAME, Name.MESSAGE_EMPTY);
        assertParseFailure(parser, "1 " + PREFIX_CLASS, ClassName.MESSAGE_EMPTY);

        // invalid name followed by valid email
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + EMAIL_DESC_AMY, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_prefixesInAnyCase_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withClassName(VALID_CLASS_AMY).withEmail(VALID_EMAIL_AMY).build();

        assertParseSuccess(parser, "1 /NAME " + VALID_NAME_AMY + " /Class " + VALID_CLASS_AMY
                + " /eMail " + VALID_EMAIL_AMY, new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_prefixWithoutSpace_failure() {
        // the stuck parameter must not silently become part of the name or class
        assertParseFailure(parser, "1 /name John/class B",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "name", "/class"));
        assertParseFailure(parser, "1 /class A1/name Bob",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "class", "/name"));
        assertParseFailure(parser, "1 /class A1/NAME Bob",
                String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "class", "/NAME"));
    }

    @Test
    public void parse_nameAndClassWithSymbolsAndAccents_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("José Müller (Joe) #2")
                .withClassName("T11/A").build();
        assertParseSuccess(parser, "1 /name José Müller (Joe) #2 /class T11/A",
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_multipleInvalidValues_reportsFirstInOrder() {
        // name, then class, then email
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC + INVALID_CLASS_DESC, ClassName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_CLASS_DESC + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY + NAME_DESC_AMY + CLASS_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withClassName(VALID_CLASS_AMY).withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        Index targetIndex = INDEX_THIRD_PERSON;

        // name
        assertParseSuccess(parser, targetIndex.getOneBased() + NAME_DESC_AMY, new EditCommand(targetIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build()));

        // class
        assertParseSuccess(parser, targetIndex.getOneBased() + CLASS_DESC_AMY, new EditCommand(targetIndex,
                new EditPersonDescriptorBuilder().withClassName(VALID_CLASS_AMY).build()));

        // email
        assertParseSuccess(parser, targetIndex.getOneBased() + EMAIL_DESC_AMY, new EditCommand(targetIndex,
                new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build()));
    }

    @Test
    public void parse_emailWithoutValue_removesEmail() {
        Index targetIndex = INDEX_THIRD_PERSON;
        EditCommand expectedCommand = new EditCommand(targetIndex,
                new EditPersonDescriptorBuilder().withoutEmail().build());

        assertParseSuccess(parser, targetIndex.getOneBased() + " " + PREFIX_EMAIL, expectedCommand);
        assertParseSuccess(parser, targetIndex.getOneBased() + " " + PREFIX_EMAIL + "   ", expectedCommand);
    }

    @Test
    public void parse_emailWithoutValueAndOtherFields_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        EditCommand expectedCommand = new EditCommand(targetIndex, new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_AMY).withoutEmail().build());

        assertParseSuccess(parser, targetIndex.getOneBased() + " " + PREFIX_EMAIL + NAME_DESC_AMY, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        Index targetIndex = INDEX_FIRST_PERSON;

        // valid followed by invalid
        String userInput = targetIndex.getOneBased() + INVALID_EMAIL_DESC + EMAIL_DESC_BOB;
        assertParseFailure(parser, userInput, MESSAGE_DUPLICATE_PARAMETERS);

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + EMAIL_DESC_BOB + INVALID_EMAIL_DESC;
        assertParseFailure(parser, userInput, MESSAGE_DUPLICATE_PARAMETERS);

        // every parameter can only be given once
        assertParseFailure(parser, targetIndex.getOneBased() + NAME_DESC_AMY + NAME_DESC_AMY,
                MESSAGE_DUPLICATE_PARAMETERS);
        assertParseFailure(parser, targetIndex.getOneBased() + CLASS_DESC_AMY + CLASS_DESC_AMY,
                MESSAGE_DUPLICATE_PARAMETERS);

        // repeated parameters are reported before an invalid index
        assertParseFailure(parser, "x" + NAME_DESC_AMY + NAME_DESC_AMY, MESSAGE_DUPLICATE_PARAMETERS);
    }
}
