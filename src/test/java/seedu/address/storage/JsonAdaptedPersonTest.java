package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
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
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_tooLongName_throwsIllegalValueException() {
        String tooLongName = "a".repeat(Name.MAX_LENGTH + 1);
        JsonAdaptedPerson person = new JsonAdaptedPerson(tooLongName, VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS);
        assertThrows(IllegalValueException.class, Name.MESSAGE_TOO_LONG, person::toModelType);
    }

    @Test
    public void toModelType_emptyName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson("", VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS);
        assertThrows(IllegalValueException.class, Name.MESSAGE_EMPTY, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidClassName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_CLASS_NAME, VALID_EMAIL, VALID_TAGS);
        assertThrows(IllegalValueException.class, ClassName.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_tooLongClassName_throwsIllegalValueException() {
        String tooLongClassName = "a".repeat(ClassName.MAX_LENGTH + 1);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, tooLongClassName, VALID_EMAIL, VALID_TAGS);
        assertThrows(IllegalValueException.class, ClassName.MESSAGE_TOO_LONG, person::toModelType);
    }

    @Test
    public void toModelType_emptyClassName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, "", VALID_EMAIL, VALID_TAGS);
        assertThrows(IllegalValueException.class, ClassName.MESSAGE_EMPTY, person::toModelType);
    }

    @Test
    public void toModelType_nullClassName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, ClassName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, INVALID_EMAIL, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_returnsPersonWithoutEmail() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, null, VALID_TAGS);
        assertTrue(person.toModelType().getEmail().isEmpty());
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_CLASS_NAME, VALID_EMAIL, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

}
