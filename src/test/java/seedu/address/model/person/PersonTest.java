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
    public void hasTag() {
        Person person = new PersonBuilder().withTags("Group A", "team-1").build();

        // null tag -> throws exception
        assertThrows(NullPointerException.class, () -> person.hasTag(null));

        // tag present -> returns true
        assertTrue(person.hasTag(new Tag("team-1")));

        // tag present with different casing and spacing -> returns true
        assertTrue(person.hasTag(new Tag("  gROUP   a ")));

        // tag absent -> returns false
        assertFalse(person.hasTag(new Tag("Group B")));

        // person without tags -> returns false
        assertFalse(new PersonBuilder().withTags().build().hasTag(new Tag("Group A")));
    }

    @Test
    public void withTag() {
        Person person = new PersonBuilder().withTags("Group A").build();

        // null tag -> throws exception
        assertThrows(NullPointerException.class, () -> person.withTag(null));

        // new tag -> existing tags kept, tag added, other fields unchanged
        Person tagged = person.withTag(new Tag("Group B"));
        assertEquals(new PersonBuilder(person).withTags("Group A", "Group B").build(), tagged);

        // original person is not modified
        assertEquals(Set.of(new Tag("Group A")), person.getTags());

        // person without an email keeps having no email
        Person withoutEmail = new PersonBuilder().withoutEmail().withTags().build();
        assertEquals(new PersonBuilder(withoutEmail).withTags("Group A").build(),
                withoutEmail.withTag(new Tag("Group A")));
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
