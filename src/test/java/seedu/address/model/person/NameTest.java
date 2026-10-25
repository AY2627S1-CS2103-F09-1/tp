package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class NameTest {

    private static final String NAME_AT_MAX_LENGTH = "a".repeat(Name.MAX_LENGTH);
    private static final String NAME_OVER_MAX_LENGTH = "a".repeat(Name.MAX_LENGTH + 1);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Name(""));
        assertThrows(IllegalArgumentException.class, () -> new Name("Tan, John"));
        assertThrows(IllegalArgumentException.class, () -> new Name("李明"));
        assertThrows(IllegalArgumentException.class, () -> new Name(NAME_OVER_MAX_LENGTH));
    }

    @Test
    public void isValidName_variousInputs_returnsExpected() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("Tan, John")); // comma
        assertFalse(Name.isValidName(",")); // only a comma
        assertFalse(Name.isValidName("李明")); // Chinese letters
        assertFalse(Name.isValidName("Иван")); // Cyrillic letters
        assertFalse(Name.isValidName("محمد")); // Arabic letters
        assertFalse(Name.isValidName("Tan 李明")); // mixed scripts
        assertFalse(Name.isValidName("peter\tjack")); // tab
        assertFalse(Name.isValidName("peter\njack")); // new line
        assertFalse(Name.isValidName("peter\u200Bjack")); // zero-width space
        assertFalse(Name.isValidName("peter\u00A0jack")); // no-break space
        assertFalse(Name.isValidName("peter😀")); // emoji
        assertFalse(Name.isValidName("\u0301")); // an accent with no letter
        assertFalse(Name.isValidName("1\u0301")); // an accent on a digit
        assertFalse(Name.isValidName(NAME_OVER_MAX_LENGTH)); // too long
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH) + "李")); // too long and non-English

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("a")); // single letter
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr")); // long names
        assertTrue(Name.isValidName("Mary-Ann Lim")); // hyphen
        assertTrue(Name.isValidName("Peter O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Muhd. Ali")); // period
        assertTrue(Name.isValidName("Tan s/o Kumar")); // slash between letters
        assertTrue(Name.isValidName("Lim d/o Chen")); // slash between letters
        assertTrue(Name.isValidName(NAME_AT_MAX_LENGTH)); // exactly the maximum length

        // valid name: symbols and digits
        assertTrue(Name.isValidName("peter (jack)")); // parentheses
        assertTrue(Name.isValidName("Peter the 2nd")); // digits
        assertTrue(Name.isValidName("12345")); // digits only
        assertTrue(Name.isValidName("R@chel")); // symbol
        assertTrue(Name.isValidName("Tan & Lim")); // ampersand
        assertTrue(Name.isValidName("-peter")); // starts with a hyphen
        assertTrue(Name.isValidName("'peter")); // starts with an apostrophe
        assertTrue(Name.isValidName("/tan")); // slash at the start
        assertTrue(Name.isValidName("tan s / o kumar")); // slash surrounded by spaces
        assertTrue(Name.isValidName("tan 1/2")); // slash next to digits
        assertTrue(Name.isValidName("peter\"jack\"")); // quotation marks

        // valid name: accented letters
        assertTrue(Name.isValidName("José")); // accented letter
        assertTrue(Name.isValidName("Zoë Müller")); // accents on several letters
        assertTrue(Name.isValidName("Nguyễn Văn An")); // stacked accents
        assertTrue(Name.isValidName("Søren Đặng Łukasz")); // Latin letters without a plain base letter
        assertTrue(Name.isValidName("Jose\u0301")); // letter followed by a separate accent
        assertTrue(Name.isValidName("Peter O’Brien")); // typographic apostrophe
        assertTrue(Name.isValidName("Peter O‘Brien")); // typographic opening apostrophe
    }

    @Test
    public void getConstraintViolation_validName_returnsEmpty() {
        assertEquals(Optional.empty(), Name.getConstraintViolation("Tan s/o Kumar"));
        assertEquals(Optional.empty(), Name.getConstraintViolation(NAME_AT_MAX_LENGTH));
    }

    @Test
    public void getConstraintViolation_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Name.getConstraintViolation(null));
    }

    @Test
    public void getConstraintViolation_emptyName_returnsEmptyMessage() {
        assertEquals(Optional.of(Name.MESSAGE_EMPTY), Name.getConstraintViolation(""));
        assertEquals(Optional.of(Name.MESSAGE_EMPTY), Name.getConstraintViolation("   "));
    }

    @Test
    public void getConstraintViolation_tooLongName_returnsTooLongMessage() {
        assertEquals(Optional.of(Name.MESSAGE_TOO_LONG), Name.getConstraintViolation(NAME_OVER_MAX_LENGTH));
    }

    @Test
    public void getConstraintViolation_invalidCharacters_returnsConstraintsMessage() {
        assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation("Tan, John"));
        assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation("李明"));
        assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation("Rachel\n"));
    }

    @Test
    public void getConstraintViolation_longInvalidName_returnsQuickly() {
        String almostValidName = "a".repeat(Name.MAX_LENGTH - 1) + "李";
        assertTimeoutPreemptively(Duration.ofSeconds(1), () ->
                assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation(almostValidName)));
    }

    @Test
    public void getConstraintViolation_tooLongAndInvalidCharacters_returnsTooLongMessage() {
        String name = NAME_OVER_MAX_LENGTH + "&";
        assertEquals(Optional.of(Name.MESSAGE_TOO_LONG), Name.getConstraintViolation(name));
    }

    @Test
    public void matches_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name("Valid Name").matches(null));
    }

    @Test
    public void matches_variousTexts_returnsExpected() {
        Name name = new Name("Valid Name");

        // same text -> returns true
        assertTrue(name.matches("Valid Name"));

        // different case -> returns true
        assertTrue(name.matches("valid name"));
        assertTrue(name.matches("VALID NAME"));

        // extra whitespace -> returns true
        assertTrue(name.matches("  valid    name "));

        // name constructed with different case and spaces -> returns true
        assertTrue(new Name("vAlId   nAmE").matches("Valid Name"));

        // different text -> returns false
        assertFalse(name.matches("Other Valid Name"));
        assertFalse(name.matches("Valid"));
        assertFalse(name.matches("Valid Name 2"));

        // empty or blank text -> returns false
        assertFalse(name.matches(""));
        assertFalse(name.matches("   "));

        // text that is not a valid name does not match and does not throw
        assertFalse(name.matches("R@chel"));
        assertFalse(name.matches("a".repeat(Name.MAX_LENGTH + 1)));

        // different punctuation -> returns false
        assertFalse(new Name("Mary-Ann").matches("Mary Ann"));
    }

    @Test
    public void toString_extraSpaces_keepsTextAsGiven() {
        assertEquals("john   TAN", new Name("john   TAN").toString());
        assertEquals("john   TAN", new Name("john   TAN").fullName);
    }

    @Test
    public void constructor_decomposedAccent_storedInNormalizedForm() {
        Name decomposed = new Name("Jose\u0301");

        assertEquals("José", decomposed.fullName);
        assertEquals(new Name("José"), decomposed);
        assertEquals(new Name("José").hashCode(), decomposed.hashCode());
    }

    @Test
    public void matches_accentedLetters_ignoresCaseAndNormalizationForm() {
        Name name = new Name("José Mu\u0308ller");

        assertTrue(name.matches("JOSÉ MÜLLER"));
        assertTrue(name.matches("Jose\u0301 Müller"));
        assertFalse(name.matches("Jose Muller")); // an unaccented letter is a different letter
    }

    @Test
    public void equals_fullWidthLetters_returnsTrue() {
        assertEquals(new Name("Alex Yeoh"), new Name("\uff21lex Yeoh"));
        assertTrue(new Name("Alex Yeoh").matches("\uff21\uff2c\uff25\uff38 yeoh"));
    }

    @Test
    public void equals_variousNames_returnsExpected() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));

        // different case -> returns true
        assertTrue(name.equals(new Name("valid name")));
        assertTrue(name.equals(new Name("VALID NAME")));
        assertTrue(name.equals(new Name("vAlId nAmE")));

        // repeated spaces -> returns true
        assertTrue(name.equals(new Name("Valid    Name")));

        // trailing spaces -> returns true
        assertTrue(name.equals(new Name("Valid Name  ")));

        // different punctuation -> returns false
        assertFalse(new Name("Mary-Ann").equals(new Name("Mary Ann")));
        assertFalse(new Name("Tan s/o Kumar").equals(new Name("Tan s o Kumar")));
    }

    @Test
    public void hashCode_equalNames_haveSameHashCode() {
        Name name = new Name("Valid Name");

        assertEquals(name.hashCode(), new Name("Valid Name").hashCode());
        assertEquals(name.hashCode(), new Name("valid   NAME").hashCode());
    }
}
