package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

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
        assertThrows(IllegalArgumentException.class, () -> new Name("R@chel"));
        assertThrows(IllegalArgumentException.class, () -> new Name(NAME_OVER_MAX_LENGTH));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains disallowed symbol
        assertFalse(Name.isValidName("peter (jack)")); // contains parentheses
        assertFalse(Name.isValidName("12345")); // numbers only
        assertFalse(Name.isValidName("peter the 2nd")); // contains digits
        assertFalse(Name.isValidName("-peter")); // starts with a hyphen
        assertFalse(Name.isValidName("'peter")); // starts with an apostrophe
        assertFalse(Name.isValidName(".peter")); // starts with a period
        assertFalse(Name.isValidName(" peter")); // starts with a space
        assertFalse(Name.isValidName("José")); // accented letter
        assertFalse(Name.isValidName("李明")); // non-English letters
        assertFalse(Name.isValidName("peter’s")); // typographic apostrophe
        assertFalse(Name.isValidName(NAME_OVER_MAX_LENGTH)); // too long

        // invalid name: slash not between two letters
        assertFalse(Name.isValidName("/tan")); // at the start
        assertFalse(Name.isValidName("tan/")); // at the end
        assertFalse(Name.isValidName("tan s / o kumar")); // surrounded by spaces
        assertFalse(Name.isValidName("tan s/ o kumar")); // followed by a space
        assertFalse(Name.isValidName("tan s /o kumar")); // preceded by a space
        assertFalse(Name.isValidName("tan s//o kumar")); // repeated
        assertFalse(Name.isValidName("tan 1/2")); // next to digits
        assertFalse(Name.isValidName("tan -/o")); // next to other symbols

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
        assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation("R@chel"));
        assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation("Rachel 2nd"));
        assertEquals(Optional.of(Name.MESSAGE_CONSTRAINTS), Name.getConstraintViolation("-Rachel"));
    }

    @Test
    public void getConstraintViolation_tooLongAndInvalidCharacters_returnsTooLongMessage() {
        String name = NAME_OVER_MAX_LENGTH + "&";
        assertEquals(Optional.of(Name.MESSAGE_TOO_LONG), Name.getConstraintViolation(name));
    }

    @Test
    public void toString_keepsTextAsGiven() {
        assertEquals("john   TAN", new Name("john   TAN").toString());
        assertEquals("john   TAN", new Name("john   TAN").fullName);
    }

    @Test
    public void equals() {
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
