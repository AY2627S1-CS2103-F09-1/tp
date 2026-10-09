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

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

import java.time.LocalDate;
import java.util.Collections;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
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
                + ALICE.getClassName() + ", email=" + ALICE.getEmail().get() + ", tags=" + ALICE.getTags()
                + ", attendanceRecords=" + ALICE.getAttendanceRecords() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void withAttendance_addsRecord() {
        LocalDate date = LocalDate.of(2026, 9, 16);
        Attendance record = new Attendance(date, Attendance.Status.PRESENT);

        Person updatedPerson = new PersonBuilder().build().withAttendance(record);

        assertEquals(Collections.singletonList(record), updatedPerson.getAttendanceRecords());
    }

    @Test
    public void withAttendance_duplicateDate_throwsIllegalArgumentException() {
        LocalDate date = LocalDate.of(2026, 9, 16);
        Person person = new PersonBuilder().build()
            .withAttendance(new Attendance(date, Attendance.Status.PRESENT));

        assertThrows(IllegalArgumentException.class, () ->
            person.withAttendance(new Attendance(date, Attendance.Status.ABSENT)));
    }

    @Test
    public void attendanceRecords_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build()
            .withAttendance(new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.PRESENT));

        assertThrows(UnsupportedOperationException.class,
            () -> person.getAttendanceRecords().add(
            new Attendance(LocalDate.of(2026, 9, 17), Attendance.Status.ABSENT)));
    }

    @Test
    public void equals_differentAttendanceRecords_returnsFalse() {
        Person personWithoutAttendance = new PersonBuilder().build();
        Person personWithAttendance = personWithoutAttendance.withAttendance(
            new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.PRESENT));

        assertFalse(personWithoutAttendance.equals(personWithAttendance));
    }
}
