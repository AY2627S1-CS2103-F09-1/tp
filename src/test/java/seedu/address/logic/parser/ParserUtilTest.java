package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.Messages.MESSAGE_MISSING_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_BOTH_CONTACT_IDENTIFIERS;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_DUPLICATE_MEMBER_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_MEMBER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_DATE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_GROUP_NAME;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_MEMBER_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_MISSING_CONTACT_IDENTIFIER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_NONEXISTENT_DATE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "Rachel,";
    private static final String INVALID_CLASS_NAME = "Sec 3&2";
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
    public void parseDate_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDate(null));
    }

    @Test
    public void parseDate_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_DATE, () -> ParserUtil.parseDate("2026-09-16"));
        assertThrows(ParseException.class, MESSAGE_INVALID_DATE, () -> ParserUtil.parseDate("1-9-2026"));
    }

    @Test
    public void parseDate_dayOrMonthOutOfRange_throwsParseExceptionForFormat() {
        for (String invalidDate : List.of("00-01-2026", "32-01-2026", "16-00-2026", "16-13-2026")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_DATE, () -> ParserUtil.parseDate(invalidDate));
        }
    }

    @Test
    public void parseDate_nonexistentDate_throwsParseExceptionNamingTheDate() {
        for (String nonexistentDate : List.of("31-02-2026", "29-02-2025", "31-04-2026", "30-02-2024")) {
            assertThrows(ParseException.class, String.format(MESSAGE_NONEXISTENT_DATE, nonexistentDate), ()
                    -> ParserUtil.parseDate(nonexistentDate));
        }
    }

    @Test
    public void parseDate_leapDay_success() throws Exception {
        assertEquals(LocalDate.of(2024, 2, 29), ParserUtil.parseDate("29-02-2024"));
    }

    @Test
    public void parseDate_validInput_success() throws Exception {
        LocalDate expectedDate = LocalDate.of(2026, 9, 16);

        assertEquals(expectedDate, ParserUtil.parseDate("16-09-2026"));
        assertEquals(expectedDate, ParserUtil.parseDate("  16-09-2026  "));
    }

    @Test
    public void parseDate_yearZero_success() throws Exception {
        assertEquals(LocalDate.of(0, 1, 1), ParserUtil.parseDate("01-01-0000"));
    }

    @Test
    public void parseDate_signedOrLongYear_throwsParseException() {
        for (String invalidDate : List.of("16-09-+2026", "16-09--0001", "16-09-12026", "16-09-+12026")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_DATE, () -> ParserUtil.parseDate(invalidDate));
        }
    }

    @Test
    public void parseScore_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseScore(null));
    }

    @Test
    public void parseScore_validInput_returnsScore() throws Exception {
        assertEquals(new BigDecimal("0"), ParserUtil.parseScore("0"));
        assertEquals(new BigDecimal("100"), ParserUtil.parseScore("100"));
        assertEquals(new BigDecimal("100.00"), ParserUtil.parseScore("100.00"));
        assertEquals(new BigDecimal("97.5"), ParserUtil.parseScore("  97.5  "));
        assertEquals(new BigDecimal(".5"), ParserUtil.parseScore(".5"));
        assertEquals(new BigDecimal("7"), ParserUtil.parseScore("007"));
    }

    @Test
    public void parseScore_invalidInput_throwsParseException() {
        for (String invalidScore : List.of("", "abc", "-1", "+5", "5.", "1e2", "5%", "1 2", "100.01", "101")) {
            assertThrows(ParseException.class, Grade.MESSAGE_INVALID_SCORE, ()
                    -> ParserUtil.parseScore(invalidScore));
        }
    }

    @Test
    public void parseScore_decimalPlaces_acceptedUpToTheLimit() throws Exception {
        assertEquals(new BigDecimal("97.1234"), ParserUtil.parseScore("97.1234"));
        assertEquals(new BigDecimal("97.12340"), ParserUtil.parseScore("97.12340"));
        assertEquals(new BigDecimal("100.0000000"), ParserUtil.parseScore("100.0000000"));
    }

    @Test
    public void parseScore_tooManyDecimalPlaces_throwsParseException() {
        for (String invalidScore : List.of("97.12345", "0.00001", "0.0000000000000000000001")) {
            assertThrows(ParseException.class, Grade.MESSAGE_TOO_MANY_DECIMAL_PLACES, ()
                    -> ParserUtil.parseScore(invalidScore));
        }
    }

    @Test
    public void parseScore_outOfRangeWithTooManyDecimalPlaces_reportsTheRange() {
        assertThrows(ParseException.class, Grade.MESSAGE_INVALID_SCORE, () -> ParserUtil.parseScore("100.00001"));
    }

    @Test
    public void parseAssessmentName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAssessmentName(null));
    }

    @Test
    public void parseAssessmentName_validInput_returnsTrimmedName() throws Exception {
        assertEquals("Quiz #1", ParserUtil.parseAssessmentName("  Quiz #1  "));
        assertEquals("Test 1, Part A", ParserUtil.parseAssessmentName("Test 1, Part A"));
        assertEquals("a".repeat(Grade.MAX_ASSESSMENT_NAME_LENGTH),
                ParserUtil.parseAssessmentName("a".repeat(Grade.MAX_ASSESSMENT_NAME_LENGTH)));
    }

    @Test
    public void parseAssessmentName_invalidInput_throwsParseException() {
        for (String invalidName : List.of("", "Quiz \u671f\u672b", "a".repeat(Grade.MAX_ASSESSMENT_NAME_LENGTH + 1))) {
            assertThrows(ParseException.class, Grade.MESSAGE_INVALID_ASSESSMENT_NAME, ()
                    -> ParserUtil.parseAssessmentName(invalidName));
        }
    }

    @Test
    public void requireNoStuckPrefixes_noStuckPrefix_doesNotThrow() throws Exception {
        ParserUtil.requireNoStuckPrefixes("");
        ParserUtil.requireNoStuckPrefixes(" 1 /date 16-09-2026 /status late");
        ParserUtil.requireNoStuckPrefixes(" /NAME Ravi s/o Kumar /class T11/A");
    }

    @Test
    public void requireNoStuckPrefixes_stuckPrefix_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/status"), ()
                -> ParserUtil.requireNoStuckPrefixes(" 1 /date 16-09-2026/status late"));
        assertThrows(ParseException.class, String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/date"), ()
                -> ParserUtil.requireNoStuckPrefixes(" 1/date 16-09-2026"));
        assertThrows(ParseException.class, String.format(MESSAGE_PREFIX_WITHOUT_SPACE, "command", "/CLASS"), ()
                -> ParserUtil.requireNoStuckPrefixes(" /name Alex/CLASS A1"));
    }

    @Test
    public void parseAssessmentName_embeddedPrefix_throwsParseException() {
        assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE,
                "assessment name", "/score"), () -> ParserUtil.parseAssessmentName("Quiz/score"));
    }

    @Test
    public void isIdentifiedByIndex_indexOnly_returnsTrue() throws Exception {
        assertTrue(ParserUtil.isIdentifiedByIndex(tokenize(" 1 /date 16-09-2026")));
    }

    @Test
    public void isIdentifiedByIndex_nameAndClass_returnsFalse() throws Exception {
        assertFalse(ParserUtil.isIdentifiedByIndex(tokenize(" /name Alex /class A1 /date 16-09-2026")));
    }

    @Test
    public void isIdentifiedByIndex_indexAndNameOrClass_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_BOTH_CONTACT_IDENTIFIERS, ()
                -> ParserUtil.isIdentifiedByIndex(tokenize(" 1 /name Alex /class A1")));
        assertThrows(ParseException.class, MESSAGE_BOTH_CONTACT_IDENTIFIERS, ()
                -> ParserUtil.isIdentifiedByIndex(tokenize(" 1 /class A1")));
    }

    @Test
    public void isIdentifiedByIndex_noIdentifier_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_MISSING_CONTACT_IDENTIFIER, ()
                -> ParserUtil.isIdentifiedByIndex(tokenize(" /date 16-09-2026")));
        assertThrows(ParseException.class, MESSAGE_MISSING_CONTACT_IDENTIFIER, ()
                -> ParserUtil.isIdentifiedByIndex(tokenize(" /name /class /date 16-09-2026")));
    }

    @Test
    public void isIdentifiedByIndex_missingNameOrClass_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_MISSING_NAME, ()
                -> ParserUtil.isIdentifiedByIndex(tokenize(" /class A1")));
        assertThrows(ParseException.class, MESSAGE_MISSING_CLASS, ()
                -> ParserUtil.isIdentifiedByIndex(tokenize(" /name Alex")));
    }

    private static ArgumentMultimap tokenize(String args) {
        return ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS, PREFIX_DATE);
    }

    @Test
    public void requireNoUnrecognizedPrefixes_null_throwsNullPointerException() {
        Prefix slashName = new Prefix("/name");
        assertThrows(NullPointerException.class, () ->
                ParserUtil.requireNoUnrecognizedPrefixes(null, "error", slashName));
        assertThrows(NullPointerException.class, () ->
                ParserUtil.requireNoUnrecognizedPrefixes(" /name John", null, slashName));
    }

    @Test
    public void requireNoUnrecognizedPrefixes_onlyKnownPrefixes_doesNotThrow() throws Exception {
        Prefix slashName = new Prefix("/name");
        Prefix slashClass = new Prefix("/class");

        ParserUtil.requireNoUnrecognizedPrefixes("", "error", slashName, slashClass);
        ParserUtil.requireNoUnrecognizedPrefixes(" /name John /class A1", "error", slashName, slashClass);

        // a slash that is not at the start of a token is not a prefix
        ParserUtil.requireNoUnrecognizedPrefixes(" /name Tan s/o Kumar", "error", slashName);
    }

    @Test
    public void requireNoUnrecognizedPrefixes_unknownPrefix_throwsParseExceptionWithGivenMessage() {
        Prefix slashName = new Prefix("/name");

        assertThrows(ParseException.class, "error", () ->
                ParserUtil.requireNoUnrecognizedPrefixes(" /name John /phone 123", "error", slashName));

        // matching needs the whole token to match
        assertThrows(ParseException.class, "error", () ->
                ParserUtil.requireNoUnrecognizedPrefixes(" /names John", "error", slashName));
        assertThrows(ParseException.class, "error", () ->
                ParserUtil.requireNoUnrecognizedPrefixes(" /NAMES John", "error", slashName));

        // no known prefixes means that every prefix-like token is unknown
        assertThrows(ParseException.class, "error", () ->
                ParserUtil.requireNoUnrecognizedPrefixes(" /name John", "error"));
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
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName("李明"));
    }

    @Test
    public void parseName_validValueWithDigitsSymbolsAndAccents_returnsName() throws Exception {
        for (String name : List.of("Rachel 2nd", "R@chel (Rae)", "José Müller", "Nguyễn Văn An", "O’Brien")) {
            assertEquals(new Name(name), ParserUtil.parseName(name));
        }
    }

    @Test
    public void parseName_prefixWithoutSpace_throwsParseExceptionWithMissingSpaceMessage() {
        String expectedMessage = String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "name", "/class");
        assertThrows(ParseException.class, expectedMessage, () -> ParserUtil.parseName("John/class B"));
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
    public void parseClassName_validValueWithSlashAndParentheses_returnsClassName() throws Exception {
        assertEquals(new ClassName("T11/A"), ParserUtil.parseClassName("T11/A"));
        assertEquals(new ClassName("Sec 3 (Morning)"), ParserUtil.parseClassName("Sec 3 (Morning)"));
    }

    @Test
    public void parseClassName_prefixWithoutSpace_throwsParseExceptionWithMissingSpaceMessage() {
        String expectedMessage = String.format(ParserUtil.MESSAGE_PREFIX_WITHOUT_SPACE, "class", "/name");
        assertThrows(ParseException.class, expectedMessage, () -> ParserUtil.parseClassName("A1/name Bob"));
    }

    @Test
    public void requireNoEmbeddedPrefix_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.requireNoEmbeddedPrefix(null, "name"));
        assertThrows(NullPointerException.class, () -> ParserUtil.requireNoEmbeddedPrefix("John", null));
    }

    @Test
    public void requireNoEmbeddedPrefix_prefixEndingTheValueOrFollowedBySpace_throwsParseException() {
        // every prefix is detected, whatever its case
        for (Prefix prefix : CliSyntax.ALL_PREFIXES) {
            String text = "John" + prefix.getPrefix();
            assertThrows(ParseException.class, () -> ParserUtil.requireNoEmbeddedPrefix(text, "name"));
            assertThrows(ParseException.class, () -> ParserUtil.requireNoEmbeddedPrefix(text.toUpperCase(), "name"));
            assertThrows(ParseException.class, () -> ParserUtil.requireNoEmbeddedPrefix(text + " B", "name"));
        }
    }

    @Test
    public void requireNoEmbeddedPrefix_textThatEndsInAnAlias_doesNotThrow() throws Exception {
        // only the full prefixes are detected, so that a name that ends in the letter of an alias is accepted
        ParserUtil.requireNoEmbeddedPrefix("Tan a/c", "name");
        ParserUtil.requireNoEmbeddedPrefix("Lee x/n", "name");
        ParserUtil.requireNoEmbeddedPrefix("Lim b/m", "name");
        ParserUtil.requireNoEmbeddedPrefix("Quiz a/as", "assessment name");
        ParserUtil.requireNoEmbeddedPrefix("Quiz a/st", "assessment name");
        ParserUtil.requireNoEmbeddedPrefix("Quiz a/sc", "assessment name");
    }

    @Test
    public void requireNoEmbeddedPrefix_slashThatIsNotAPrefix_doesNotThrow() throws Exception {
        ParserUtil.requireNoEmbeddedPrefix("Tan s/o Kumar", "name");
        ParserUtil.requireNoEmbeddedPrefix("T11/A", "class");
        ParserUtil.requireNoEmbeddedPrefix("A1/names", "class"); // the whole token is not a prefix
        ParserUtil.requireNoEmbeddedPrefix("A1/classroom B", "class");
        ParserUtil.requireNoEmbeddedPrefix("Quiz 1/2", "assessment name");
        ParserUtil.requireNoEmbeddedPrefix("name class", "name"); // no slash
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
    public void parseEmail_invalidFormat_throwsParseExceptionWithConstraintsMessage() {
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_tooLongValue_throwsParseExceptionWithTooLongMessage() {
        String tooLongEmail = "a".repeat(Email.MAX_LENGTH + 1);
        assertThrows(ParseException.class, Email.MESSAGE_TOO_LONG, () -> ParserUtil.parseEmail(tooLongEmail));
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

    @Test
    public void parseGroupName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseGroupName(null));
    }

    @Test
    public void parseGroupName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_GROUP_NAME, () -> ParserUtil.parseGroupName(""));
        assertThrows(ParseException.class, MESSAGE_INVALID_GROUP_NAME, () ->
                ParserUtil.parseGroupName(WHITESPACE));
        assertThrows(ParseException.class, MESSAGE_INVALID_GROUP_NAME, () ->
                ParserUtil.parseGroupName("Group!"));
        assertThrows(ParseException.class, MESSAGE_INVALID_GROUP_NAME, () ->
                ParserUtil.parseGroupName("Group\tA"));
        assertThrows(ParseException.class, MESSAGE_INVALID_GROUP_NAME, () ->
                ParserUtil.parseGroupName("a".repeat(Tag.MAX_LENGTH + 1)));
    }

    @Test
    public void parseGroupName_validValue_returnsTagWithTypedCasing() throws Exception {
        assertEquals("Group A", ParserUtil.parseGroupName("Group A").tagName);
        String longestName = "a".repeat(Tag.MAX_LENGTH);
        assertEquals(longestName, ParserUtil.parseGroupName(longestName).tagName);

        // a name made only of digits is fine since the group is introduced by its prefix
        assertEquals("123", ParserUtil.parseGroupName("123").tagName);
    }

    @Test
    public void parseGroupName_valueWithExtraWhitespace_returnsTrimmedAndCollapsedTag() throws Exception {
        Tag actual = ParserUtil.parseGroupName(WHITESPACE + "Group    A" + WHITESPACE);
        assertEquals("Group A", actual.tagName);
        assertEquals(new Tag("group a"), actual);
    }

    @Test
    public void parseMemberIndices_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMemberIndices(null));
    }

    @Test
    public void parseMemberIndices_validValues_returnsIndicesInOrderGiven() throws Exception {
        assertEquals(List.of(Index.fromOneBased(1)), ParserUtil.parseMemberIndices("1"));
        assertEquals(List.of(Index.fromOneBased(5), Index.fromOneBased(1), Index.fromOneBased(3)),
                ParserUtil.parseMemberIndices("5,1,3"));
    }

    @Test
    public void parseMemberIndices_whitespaceAroundEntries_ignored() throws Exception {
        List<Index> expected = List.of(Index.fromOneBased(1), Index.fromOneBased(3), Index.fromOneBased(5));
        assertEquals(expected, ParserUtil.parseMemberIndices("1, 3 ,5"));
        assertEquals(expected, ParserUtil.parseMemberIndices(WHITESPACE + "1 ,\t3,  5" + WHITESPACE));
    }

    @Test
    public void parseMemberIndices_emptyEntry_throwsParseException() {
        // blank, leading comma, trailing comma, and consecutive commas
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () -> ParserUtil.parseMemberIndices(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () ->
                ParserUtil.parseMemberIndices(WHITESPACE));
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () -> ParserUtil.parseMemberIndices(",1"));
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () -> ParserUtil.parseMemberIndices("1,3,"));
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () -> ParserUtil.parseMemberIndices("1,,3"));
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () ->
                ParserUtil.parseMemberIndices("1, ,3"));
    }

    @Test
    public void parseMemberIndices_entryNotPositiveInteger_throwsParseExceptionNamingEntry() {
        for (String invalid : List.of("0", "-1", "1.5", "abc", "+1", "1 3", "99999999999")) {
            assertThrows(ParseException.class, String.format(MESSAGE_INVALID_MEMBER_INDEX, invalid), () ->
                    ParserUtil.parseMemberIndices("2," + invalid + ",4"));
        }
    }

    @Test
    public void parseMemberIndices_multipleInvalidEntries_reportsFirstInvalidEntry() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_MEMBER_INDEX, "x"), () ->
                ParserUtil.parseMemberIndices("1,x,,y"));
        assertThrows(ParseException.class, MESSAGE_EMPTY_MEMBER, () -> ParserUtil.parseMemberIndices("1,,x"));
    }

    @Test
    public void parseMemberIndices_repeatedIndex_throwsParseExceptionNamingIndex() {
        assertThrows(ParseException.class, String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, "1"), () ->
                ParserUtil.parseMemberIndices("1,1"));
        assertThrows(ParseException.class, String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, "3"), () ->
                ParserUtil.parseMemberIndices("3,1,3 , 3"));

        // every repeated index is reported once, in the order it is first repeated
        assertThrows(ParseException.class, String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, "2, 1"), () ->
                ParserUtil.parseMemberIndices("1,2,2,1,1"));
    }

    @Test
    public void parseMemberIndices_invalidEntryAndRepeatedIndex_reportsInvalidEntryFirst() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_MEMBER_INDEX, "x"), () ->
                ParserUtil.parseMemberIndices("1,1,x"));
    }
}
