package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_CLASS_NAME = "A1&";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_CLASS_NAME = BENSON.getClassName().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().get().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_personWithoutEmail_returnsPersonWithoutEmail() throws Exception {
        Person personWithoutEmail = new PersonBuilder(BENSON).withoutEmail().build();
        JsonAdaptedPerson person = new JsonAdaptedPerson(personWithoutEmail);
        assertEquals(personWithoutEmail, person.toModelType());
        assertTrue(person.toModelType().getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS,
            List.of());
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_tooLongName_throwsIllegalValueException() {
        String tooLongName = "a".repeat(Name.MAX_LENGTH + 1);
        JsonAdaptedPerson person = new JsonAdaptedPerson(tooLongName, VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS,
            List.of());
        assertThrows(IllegalValueException.class, Name.MESSAGE_TOO_LONG, person::toModelType);
    }

    @Test
    public void toModelType_emptyName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson("", VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS, List.of());
        assertThrows(IllegalValueException.class, Name.MESSAGE_EMPTY, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidClassName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS,
            List.of());
        assertThrows(IllegalValueException.class, ClassName.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_tooLongClassName_throwsIllegalValueException() {
        String tooLongClassName = "a".repeat(ClassName.MAX_LENGTH + 1);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, tooLongClassName, VALID_EMAIL, VALID_TAGS,
            List.of());
        assertThrows(IllegalValueException.class, ClassName.MESSAGE_TOO_LONG, person::toModelType);
    }

    @Test
    public void toModelType_emptyClassName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, "", VALID_EMAIL, VALID_TAGS, List.of());
        assertThrows(IllegalValueException.class, ClassName.MESSAGE_EMPTY, person::toModelType);
    }

    @Test
    public void toModelType_nullClassName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_TAGS, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, ClassName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, INVALID_EMAIL, VALID_TAGS,
            List.of());
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_returnsPersonWithoutEmail() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, null, VALID_TAGS, List.of());
        assertTrue(person.toModelType().getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, VALID_EMAIL, invalidTags,
            List.of());
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_tooLongTag_throwsIllegalValueException() {
        List<JsonAdaptedTag> tooLongTags = List.of(new JsonAdaptedTag("a".repeat(Tag.MAX_LENGTH + 1)));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, VALID_EMAIL, tooLongTags);
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_tagsWithSpacesAndMixedCasing_returnsPersonWithTagsAsTyped() throws Exception {
        Person personWithGroups =
                new PersonBuilder(BENSON).withTags("Group A", "team-1_Alpha", "gROUP b").build();

        Person restoredPerson = new JsonAdaptedPerson(personWithGroups).toModelType();

        assertEquals(personWithGroups, restoredPerson);
        Set<String> restoredTagNames = restoredPerson.getTags().stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.toSet());
        assertEquals(Set.of("Group A", "team-1_Alpha", "gROUP b"), restoredTagNames);
    }

    @Test
    public void toModelType_tagWithExtraSpacesInFile_returnsNormalizedTag() throws Exception {
        List<JsonAdaptedTag> tagsWithExtraSpaces = List.of(new JsonAdaptedTag("  Group    A "));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, VALID_EMAIL, tagsWithExtraSpaces);

        Set<Tag> tags = person.toModelType().getTags();

        assertEquals(1, tags.size());
        assertEquals("Group A", tags.iterator().next().tagName);
    public void toModelType_personWithAttendance_returnsPersonWithAttendance() throws Exception {
        Attendance record = new Attendance(
            LocalDate.of(2026, 9, 16),
            Attendance.Status.PRESENT);
        Person personWithAttendance = BENSON.withAttendance(record);

        JsonAdaptedPerson adaptedPerson = new JsonAdaptedPerson(personWithAttendance);

        assertEquals(personWithAttendance, adaptedPerson.toModelType());
        assertEquals(List.of(record), adaptedPerson.toModelType().getAttendanceRecords());
    }

    @Test
    public void toModelType_missingAttendanceRecords_returnsPersonWithEmptyAttendance() throws Exception {
        JsonAdaptedPerson adaptedPerson = new JsonAdaptedPerson(
            VALID_NAME,
            VALID_CLASS_NAME,
            VALID_EMAIL,
            VALID_TAGS,
            null);

        assertTrue(adaptedPerson.toModelType().getAttendanceRecords().isEmpty());
    }

    @Test
    public void toModelType_invalidAttendanceRecord_throwsIllegalValueException() {
        JsonAdaptedAttendance invalidRecord =
            new JsonAdaptedAttendance("31-02-2026", "present");
        JsonAdaptedPerson adaptedPerson = new JsonAdaptedPerson(
            VALID_NAME,
            VALID_CLASS_NAME,
            VALID_EMAIL,
            VALID_TAGS,
            List.of(invalidRecord));

        assertThrows(IllegalValueException.class, adaptedPerson::toModelType);
    }

}
