package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ClassNameTest {

    private static final String CLASS_NAME_AT_MAX_LENGTH = "a".repeat(ClassName.MAX_LENGTH);
    private static final String CLASS_NAME_OVER_MAX_LENGTH = "a".repeat(ClassName.MAX_LENGTH + 1);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClassName(null));
    }

    @Test
    public void constructor_invalidClassName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ClassName(""));
        assertThrows(IllegalArgumentException.class, () -> new ClassName("A1/B2"));
        assertThrows(IllegalArgumentException.class, () -> new ClassName(CLASS_NAME_OVER_MAX_LENGTH));
    }

    @Test
    public void isValidClassName() {
        // null class name
        assertThrows(NullPointerException.class, () -> ClassName.isValidClassName(null));

        // invalid class name
        assertFalse(ClassName.isValidClassName("")); // empty string
        assertFalse(ClassName.isValidClassName(" ")); // spaces only
        assertFalse(ClassName.isValidClassName("^")); // only symbols
        assertFalse(ClassName.isValidClassName("A1/B2")); // slash
        assertFalse(ClassName.isValidClassName("Math (Adv)")); // parentheses
        assertFalse(ClassName.isValidClassName("Arts & Humanities")); // ampersand
        assertFalse(ClassName.isValidClassName("Class 3A, Group 2")); // comma
        assertFalse(ClassName.isValidClassName("-A1")); // starts with a hyphen
        assertFalse(ClassName.isValidClassName(".A1")); // starts with a period
        assertFalse(ClassName.isValidClassName("_A1")); // starts with an underscore
        assertFalse(ClassName.isValidClassName(" A1")); // starts with a space
        assertFalse(ClassName.isValidClassName("3É")); // accented letter
        assertFalse(ClassName.isValidClassName("甲班")); // non-English letters
        assertFalse(ClassName.isValidClassName(CLASS_NAME_OVER_MAX_LENGTH)); // too long

        // valid class name
        assertTrue(ClassName.isValidClassName("A")); // single letter
        assertTrue(ClassName.isValidClassName("1")); // single digit
        assertTrue(ClassName.isValidClassName("A1")); // letter and digit
        assertTrue(ClassName.isValidClassName("3A")); // starts with a digit
        assertTrue(ClassName.isValidClassName("a1")); // lower case
        assertTrue(ClassName.isValidClassName("Sec 3-2")); // space and hyphen
        assertTrue(ClassName.isValidClassName("P5.2")); // period
        assertTrue(ClassName.isValidClassName("CS2103_T11")); // underscore
        assertTrue(ClassName.isValidClassName("Math 101")); // letters, space and digits
        assertTrue(ClassName.isValidClassName(CLASS_NAME_AT_MAX_LENGTH)); // exactly the maximum length
    }

    @Test
    public void getConstraintViolation_validClassName_returnsEmpty() {
        assertEquals(Optional.empty(), ClassName.getConstraintViolation("Sec 3-2"));
        assertEquals(Optional.empty(), ClassName.getConstraintViolation(CLASS_NAME_AT_MAX_LENGTH));
    }

    @Test
    public void getConstraintViolation_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ClassName.getConstraintViolation(null));
    }

    @Test
    public void getConstraintViolation_emptyClassName_returnsEmptyMessage() {
        assertEquals(Optional.of(ClassName.MESSAGE_EMPTY), ClassName.getConstraintViolation(""));
        assertEquals(Optional.of(ClassName.MESSAGE_EMPTY), ClassName.getConstraintViolation("   "));
    }

    @Test
    public void getConstraintViolation_tooLongClassName_returnsTooLongMessage() {
        assertEquals(Optional.of(ClassName.MESSAGE_TOO_LONG),
                ClassName.getConstraintViolation(CLASS_NAME_OVER_MAX_LENGTH));
    }

    @Test
    public void getConstraintViolation_invalidCharacters_returnsConstraintsMessage() {
        assertEquals(Optional.of(ClassName.MESSAGE_CONSTRAINTS), ClassName.getConstraintViolation("A1/B2"));
        assertEquals(Optional.of(ClassName.MESSAGE_CONSTRAINTS), ClassName.getConstraintViolation("-A1"));
    }

    @Test
    public void getConstraintViolation_tooLongAndInvalidCharacters_returnsTooLongMessage() {
        String className = CLASS_NAME_OVER_MAX_LENGTH + "&";
        assertEquals(Optional.of(ClassName.MESSAGE_TOO_LONG), ClassName.getConstraintViolation(className));
    }

    @Test
    public void matches_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClassName("Sec 3-2").matches(null));
    }

    @Test
    public void matches() {
        ClassName className = new ClassName("Sec 3-2");

        // same text -> returns true
        assertTrue(className.matches("Sec 3-2"));

        // different case -> returns true
        assertTrue(className.matches("sec 3-2"));
        assertTrue(new ClassName("A1").matches("a1"));

        // extra whitespace -> returns true
        assertTrue(className.matches("  sec    3-2 "));

        // different text -> returns false
        assertFalse(className.matches("Sec 3-3"));
        assertFalse(className.matches("Sec"));
        assertFalse(className.matches("Sec3-2"));

        // empty or blank text -> returns false
        assertFalse(className.matches(""));
        assertFalse(className.matches("   "));

        // text that is not a valid class name does not match and does not throw
        assertFalse(className.matches("A1/B2"));
        assertFalse(className.matches("a".repeat(ClassName.MAX_LENGTH + 1)));
    }

    @Test
    public void toString_keepsTextAsGiven() {
        assertEquals("sec   3-2", new ClassName("sec   3-2").toString());
        assertEquals("sec   3-2", new ClassName("sec   3-2").value);
    }

    @Test
    public void equals() {
        ClassName className = new ClassName("Sec 3-2");

        // same values -> returns true
        assertTrue(className.equals(new ClassName("Sec 3-2")));

        // same object -> returns true
        assertTrue(className.equals(className));

        // null -> returns false
        assertFalse(className.equals(null));

        // different types -> returns false
        assertFalse(className.equals(5.0f));

        // different values -> returns false
        assertFalse(className.equals(new ClassName("Sec 3-3")));

        // different case -> returns true
        assertTrue(new ClassName("A1").equals(new ClassName("a1")));
        assertTrue(className.equals(new ClassName("SEC 3-2")));

        // repeated and trailing spaces -> returns true
        assertTrue(className.equals(new ClassName("Sec    3-2  ")));

        // different punctuation -> returns false
        assertFalse(new ClassName("Sec 3-2").equals(new ClassName("Sec 3.2")));
        assertFalse(new ClassName("Sec 3-2").equals(new ClassName("Sec3-2")));
    }

    @Test
    public void hashCode_equalClassNames_haveSameHashCode() {
        ClassName className = new ClassName("Sec 3-2");

        assertEquals(className.hashCode(), new ClassName("Sec 3-2").hashCode());
        assertEquals(className.hashCode(), new ClassName("sec   3-2").hashCode());
    }
}
