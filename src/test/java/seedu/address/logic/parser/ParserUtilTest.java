package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_CLASS_NAME = "Sec 3/2";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_CLASS_NAME = "Sec 3-2";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void requireNoUnrecognizedPrefixes_onlyKnownPrefixes_doesNotThrow() throws Exception {
        ParserUtil.requireNoUnrecognizedPrefixes("", "error", CliSyntax.PREFIX_NAME);
        ParserUtil.requireNoUnrecognizedPrefixes(" /name John /class A1", "error",
                CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_CLASS);
    }

    @Test
    public void requireNoUnrecognizedPrefixes_unrecognizedPrefix_throwsParseExceptionWithGivenMessage() {
        assertThrows(ParseException.class, "custom error", () ->
                ParserUtil.requireNoUnrecognizedPrefixes(" /name John /phone 123", "custom error",
                        CliSyntax.PREFIX_NAME));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_emptyValue_throwsParseExceptionWithEmptyMessage() {
        assertThrows(ParseException.class, Name.MESSAGE_EMPTY, () -> ParserUtil.parseName(""));
        assertThrows(ParseException.class, Name.MESSAGE_EMPTY, () -> ParserUtil.parseName(WHITESPACE));
    }

    @Test
    public void parseName_tooLongValue_throwsParseExceptionWithTooLongMessage() {
        String tooLongName = "a".repeat(Name.MAX_LENGTH + 1);
        assertThrows(ParseException.class, Name.MESSAGE_TOO_LONG, () -> ParserUtil.parseName(tooLongName));
    }

    @Test
    public void parseName_invalidCharacters_throwsParseExceptionWithConstraintsMessage() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(INVALID_NAME));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName("Rachel 2nd"));
    }

    @Test
    public void parseName_validValueWithAllowedSymbols_returnsName() throws Exception {
        String name = "Rachel O'Neil-Walker Jr. s/o Tan";
        assertEquals(new Name(name), ParserUtil.parseName(name));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseClassName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseClassName((String) null));
    }

    @Test
    public void parseClassName_emptyValue_throwsParseExceptionWithEmptyMessage() {
        assertThrows(ParseException.class, ClassName.MESSAGE_EMPTY, () -> ParserUtil.parseClassName(""));
        assertThrows(ParseException.class, ClassName.MESSAGE_EMPTY, () -> ParserUtil.parseClassName(WHITESPACE));
    }

    @Test
    public void parseClassName_tooLongValue_throwsParseExceptionWithTooLongMessage() {
        String tooLongClassName = "a".repeat(ClassName.MAX_LENGTH + 1);
        assertThrows(ParseException.class, ClassName.MESSAGE_TOO_LONG, () ->
                ParserUtil.parseClassName(tooLongClassName));
    }

    @Test
    public void parseClassName_invalidCharacters_throwsParseExceptionWithConstraintsMessage() {
        assertThrows(ParseException.class, ClassName.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseClassName(INVALID_CLASS_NAME));
    }

    @Test
    public void parseClassName_validValueWithoutWhitespace_returnsClassName() throws Exception {
        ClassName expectedClassName = new ClassName(VALID_CLASS_NAME);
        assertEquals(expectedClassName, ParserUtil.parseClassName(VALID_CLASS_NAME));
    }

    @Test
    public void parseClassName_validValueWithWhitespace_returnsTrimmedClassName() throws Exception {
        String classNameWithWhitespace = WHITESPACE + VALID_CLASS_NAME + WHITESPACE;
        ClassName expectedClassName = new ClassName(VALID_CLASS_NAME);
        assertEquals(expectedClassName, ParserUtil.parseClassName(classNameWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
