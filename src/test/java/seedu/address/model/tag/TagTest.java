package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Tag(""));
        assertThrows(IllegalArgumentException.class, () -> new Tag("   "));
        assertThrows(IllegalArgumentException.class, () -> new Tag("Group!"));
    }

    @Test
    public void constructor_untrimmedTagName_keepsTrimmedNameWithOriginalCasing() {
        assertEquals("Group A", new Tag("  Group A  ").tagName);
    }

    @Test
    public void constructor_repeatedSpaces_collapsesIntoSingleSpace() {
        assertEquals("Group A B", new Tag("Group   A    B").tagName);
    }

    @Test
    public void isValidTagName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

    @Test
    public void isValidTagName_invalidInputs_returnsFalse() {
        assertFalse(Tag.isValidTagName("")); // empty
        assertFalse(Tag.isValidTagName(" ")); // only spaces
        assertFalse(Tag.isValidTagName("\t")); // only a tab
        assertFalse(Tag.isValidTagName("Group!")); // symbol not allowed
        assertFalse(Tag.isValidTagName("Group/A")); // slash would clash with prefixes
        assertFalse(Tag.isValidTagName("Group\tA")); // tab is not a space
        assertFalse(Tag.isValidTagName("Gröup")); // non-ASCII letter
        assertFalse(Tag.isValidTagName("a".repeat(Tag.MAX_LENGTH + 1))); // too long
    }

    @Test
    public void isValidTagName_validInputs_returnsTrue() {
        assertTrue(Tag.isValidTagName("a")); // shortest
        assertTrue(Tag.isValidTagName("GroupA"));
        assertTrue(Tag.isValidTagName("Group A")); // internal space
        assertTrue(Tag.isValidTagName("team-1_alpha")); // hyphen, underscore and digit
        assertTrue(Tag.isValidTagName("12345")); // digits only
        assertTrue(Tag.isValidTagName("a".repeat(Tag.MAX_LENGTH))); // longest
    }

    @Test
    public void isValidTagName_surroundingAndRepeatedSpaces_ignoredWhenCheckingLength() {
        assertTrue(Tag.isValidTagName("  " + "a".repeat(Tag.MAX_LENGTH) + "  "));
        assertTrue(Tag.isValidTagName("a" + " ".repeat(5) + "b"));
    }

    @Test
    public void equals() {
        Tag tag = new Tag("Group A");

        // same object -> returns true
        assertTrue(tag.equals(tag));

        // same values -> returns true
        assertTrue(tag.equals(new Tag("Group A")));

        // different casing -> returns true
        assertTrue(tag.equals(new Tag("gROUP a")));

        // different surrounding or repeated spaces -> returns true
        assertTrue(tag.equals(new Tag("  Group    A ")));

        // null -> returns false
        assertFalse(tag.equals(null));

        // different type -> returns false
        assertFalse(tag.equals(5));

        // different name -> returns false
        assertFalse(tag.equals(new Tag("Group B")));

        // no space versus a space -> returns false
        assertFalse(tag.equals(new Tag("GroupA")));
    }

    @Test
    public void hashCode_equalTags_haveSameHashCode() {
        assertEquals(new Tag("Group A").hashCode(), new Tag("  gROUP   a ").hashCode());
        assertNotEquals(new Tag("Group A").hashCode(), new Tag("Group B").hashCode());
    }

    @Test
    public void toString_keepsOriginalCasing() {
        assertEquals("[GroupA]", new Tag("GroupA").toString());
        assertEquals("[groupa]", new Tag("groupa").toString());
    }

}
