package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void hasTag_nullTag_throwsNullPointerException() {
        Person person = new PersonBuilder().build();
        assertThrows(NullPointerException.class, () -> person.hasTag(null));
    }

    @Test
    public void hasTag_tagPresent_returnsTrue() {
        Person person = new PersonBuilder().withTags("Group A", "team-1").build();
        assertTrue(person.hasTag(new Tag("team-1")));
    }

    @Test
    public void hasTag_tagPresentWithDifferentCasingAndSpacing_returnsTrue() {
        Person person = new PersonBuilder().withTags("Group A", "team-1").build();
        assertTrue(person.hasTag(new Tag("  gROUP   a ")));
    }

    @Test
    public void hasTag_tagAbsent_returnsFalse() {
        Person person = new PersonBuilder().withTags("Group A", "team-1").build();
        assertFalse(person.hasTag(new Tag("Group B")));
    }

    @Test
    public void hasTag_personWithoutTags_returnsFalse() {
        Person person = new PersonBuilder().withTags().build();
        assertFalse(person.hasTag(new Tag("Group A")));
    }

    @Test
    public void withTag_nullTag_throwsNullPointerException() {
        Person person = new PersonBuilder().build();
        assertThrows(NullPointerException.class, () -> person.withTag(null));
    }

    @Test
    public void withTag_newTag_keepsExistingTagsAndOtherFields() {
        Person person = new PersonBuilder().withTags("Group A").build();

        Person tagged = person.withTag(new Tag("Group B"));

        assertEquals(new PersonBuilder(person).withTags("Group A", "Group B").build(), tagged);
    }

    @Test
    public void withTag_newTag_doesNotModifyOriginalPerson() {
        Person person = new PersonBuilder().withTags("Group A").build();

        person.withTag(new Tag("Group B"));

        assertEquals(Set.of(new Tag("Group A")), person.getTags());
    }

    @Test
    public void withTag_personWithoutEmail_returnsPersonWithoutEmail() {
        Person person = new PersonBuilder().withoutEmail().withTags().build();

        Person tagged = person.withTag(new Tag("Group A"));

        assertEquals(new PersonBuilder(person).withTags("Group A").build(), tagged);
        assertTrue(tagged.getEmail().isEmpty());
    }

    @Test
    public void withTag_tagAlreadyPresent_keepsExistingTag() {
        Person person = new PersonBuilder().withTags("Group A").build();

        Person tagged = person.withTag(new Tag("group a"));

        assertEquals(1, tagged.getTags().size());
        assertEquals("Group A", tagged.getTags().iterator().next().tagName);
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name and class, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has repeated spaces, all other attributes same -> returns true
        String nameWithRepeatedSpaces = VALID_NAME_BOB.replace(" ", "   ");
        editedBob = new PersonBuilder(BOB).withName(nameWithRepeatedSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // same name, different class, all other attributes same -> returns false
        editedBob = new PersonBuilder(BOB).withClassName(VALID_CLASS_AMY).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // same name, class differs in case, all other attributes same -> returns true
        editedBob = new PersonBuilder(BOB).withClassName(VALID_CLASS_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // same name and class, no email, all other attributes same -> returns true
        editedBob = new PersonBuilder(BOB).withoutEmail().build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void getEmail_personWithoutEmail_returnsEmptyOptional() {
        Person personWithoutEmail = new PersonBuilder().withoutEmail().build();
        assertTrue(personWithoutEmail.getEmail().isEmpty());
        assertTrue(new PersonBuilder().build().getEmail().isPresent());
    }

    @Test
    public void constructor_nullEmailOptional_throwsNullPointerException() {
        Person person = new PersonBuilder().build();
        assertThrows(NullPointerException.class, () ->
                new Person(person.getName(), person.getClassName(), null, person.getTags()));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different class -> returns false
        editedAlice = new PersonBuilder(ALICE).withClassName(VALID_CLASS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // email removed -> returns false
        editedAlice = new PersonBuilder(ALICE).withoutEmail().build();
        assertFalse(ALICE.equals(editedAlice));

        // both without email -> returns true
        Person aliceWithoutEmail = new PersonBuilder(ALICE).withoutEmail().build();
        assertTrue(aliceWithoutEmail.equals(new PersonBuilder(ALICE).withoutEmail().build()));
        assertEquals(aliceWithoutEmail.hashCode(), new PersonBuilder(ALICE).withoutEmail().build().hashCode());

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", className="
                + ALICE.getClassName() + ", email=" + ALICE.getEmail().get() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
