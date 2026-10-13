package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.testutil.PersonBuilder;

public class PersonMatchesKeywordTest {

    private final Person person = new PersonBuilder().withName("John Doe").withClassName("CS2103 T05")
            .withTags("friend", "owesMoney").build();

    @Test
    public void test_keywordInName_returnsTrue() {
        // whole name
        assertTrue(new PersonMatchesKeyword("John Doe").test(person));

        // start, middle and end of the name
        assertTrue(new PersonMatchesKeyword("jo").test(person));
        assertTrue(new PersonMatchesKeyword("hn d").test(person));
        assertTrue(new PersonMatchesKeyword("oe").test(person));
    }

    @Test
    public void test_keywordInClassName_returnsTrue() {
        assertTrue(new PersonMatchesKeyword("2103").test(person));
        assertTrue(new PersonMatchesKeyword("cs2103 t05").test(person));
    }

    @Test
    public void test_keywordInTagName_returnsTrue() {
        assertTrue(new PersonMatchesKeyword("friend").test(person));
        assertTrue(new PersonMatchesKeyword("rie").test(person));
        assertTrue(new PersonMatchesKeyword("money").test(person));
    }

    @Test
    public void test_keywordInDifferentCase_returnsTrue() {
        assertTrue(new PersonMatchesKeyword("JOHN").test(person));
        assertTrue(new PersonMatchesKeyword("cs2103").test(person));
        assertTrue(new PersonMatchesKeyword("FRIEND").test(person));
        assertTrue(new PersonMatchesKeyword("OWESMONEY").test(person));
    }

    @Test
    public void test_keywordWithExtraWhitespace_returnsTrue() {
        assertTrue(new PersonMatchesKeyword("  john    doe ").test(person));
        assertTrue(new PersonMatchesKeyword(" \t cs2103 \n t05 ").test(person));
    }

    @Test
    public void test_keywordNotContiguousSubstring_returnsFalse() {
        // letters appear in the same order but not next to each other
        assertFalse(new PersonMatchesKeyword("rid").test(person));
        assertFalse(new PersonMatchesKeyword("jhn").test(person));
        assertFalse(new PersonMatchesKeyword("cs t05").test(person));
    }

    @Test
    public void test_keywordNotPresent_returnsFalse() {
        assertFalse(new PersonMatchesKeyword("Alice").test(person));
        assertFalse(new PersonMatchesKeyword("A1").test(person));
        assertFalse(new PersonMatchesKeyword("colleague").test(person));
    }

    @Test
    public void test_keywordSpansDifferentFields_returnsFalse() {
        // end of the name followed by the start of the class name
        assertFalse(new PersonMatchesKeyword("doe cs2103").test(person));
    }

    @Test
    public void test_personWithoutTags_matchesOnNameAndClassName() {
        Person personWithoutTags = new PersonBuilder().withName("Amy Bee").withClassName("A1").build();

        assertTrue(new PersonMatchesKeyword("amy").test(personWithoutTags));
        assertTrue(new PersonMatchesKeyword("a1").test(personWithoutTags));
        assertFalse(new PersonMatchesKeyword("friend").test(personWithoutTags));
    }

    @Test
    public void test_keywordInEmail_returnsFalse() {
        Person personWithEmail = new PersonBuilder().withName("Amy Bee").withClassName("A1")
                .withEmail("secret@example.com").build();

        assertFalse(new PersonMatchesKeyword("secret").test(personWithEmail));
        assertFalse(new PersonMatchesKeyword("example.com").test(personWithEmail));
        assertFalse(new PersonMatchesKeyword("secret@example.com").test(personWithEmail));
    }

    @Test
    public void test_keywordWithSpecialCharacters_matchedLiterally() {
        Person personWithSlash = new PersonBuilder().withName("Tan s/o Kumar").withClassName("A1").build();
        Person personWithDot = new PersonBuilder().withName("Dr. Lee").withClassName("A1").build();

        // characters that are allowed in names are matched as typed
        assertTrue(new PersonMatchesKeyword("s/o").test(personWithSlash));
        assertTrue(new PersonMatchesKeyword("dr.").test(personWithDot));

        // regular expression symbols have no special meaning
        assertFalse(new PersonMatchesKeyword(".").test(personWithSlash));
        assertFalse(new PersonMatchesKeyword("t.*r").test(personWithSlash));
        assertFalse(new PersonMatchesKeyword("r.l").test(personWithDot));
    }

    @Test
    public void test_multiWordKeywordInTagName_returnsTrue() {
        Person personInGroup = new PersonBuilder().withName("Amy Bee").withClassName("A1")
                .withTags("Group A", "owesMoney").build();

        assertTrue(new PersonMatchesKeyword("group a").test(personInGroup));
        assertTrue(new PersonMatchesKeyword("  GROUP    A ").test(personInGroup));
        assertTrue(new PersonMatchesKeyword("oup").test(personInGroup));
    }

    @Test
    public void test_keywordSpansDifferentTags_returnsFalse() {
        Person personInGroup = new PersonBuilder().withName("Amy Bee").withClassName("A1")
                .withTags("Group A", "owesMoney").build();

        assertFalse(new PersonMatchesKeyword("group a owesmoney").test(personInGroup));
        assertFalse(new PersonMatchesKeyword("a owesmoney").test(personInGroup));
    }

    @Test
    public void test_blankKeyword_matchesEveryone() {
        // the list command never builds this, because it treats a blank argument as no keyword
        assertTrue(new PersonMatchesKeyword("").test(person));
        assertTrue(new PersonMatchesKeyword("   ").test(person));
    }

    @Test
    public void constructor_nullKeyword_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesKeyword(null));
    }

    @Test
    public void equals() {
        PersonMatchesKeyword firstPredicate = new PersonMatchesKeyword("first");
        PersonMatchesKeyword secondPredicate = new PersonMatchesKeyword("second");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new PersonMatchesKeyword("first")));

        // same values apart from case and whitespace -> returns true
        assertTrue(firstPredicate.equals(new PersonMatchesKeyword("  FIRST ")));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keyword -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void hashCode_equalPredicates_haveSameHashCode() {
        assertEquals(new PersonMatchesKeyword("first").hashCode(),
                new PersonMatchesKeyword(" First ").hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("First  Second");
        String expected = new ToStringBuilder(predicate).add("keyword", "first second").toString();
        assertEquals(expected, predicate.toString());
    }
}
