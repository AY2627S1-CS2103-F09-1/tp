package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String className;
    private final String email; // null if the person has no email
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedAttendance> attendanceRecords = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("className") String className,
            @JsonProperty("email") String email, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("attendanceRecords") List<JsonAdaptedAttendance> attendanceRecords) {
        this.name = name;
        this.className = className;
        this.email = email;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (attendanceRecords != null) {
            this.attendanceRecords.addAll(attendanceRecords);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        className = source.getClassName().value;
        email = source.getEmail().map(sourceEmail -> sourceEmail.value).orElse(null);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        attendanceRecords.addAll(source.getAttendanceRecords().stream()
                .map(JsonAdaptedAttendance::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }
        List<Attendance> modelAttendanceRecords = new ArrayList<>();
        for (JsonAdaptedAttendance attendance : attendanceRecords) {
            modelAttendanceRecords.add(attendance.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        Optional<String> nameConstraintViolation = Name.getConstraintViolation(name);
        if (nameConstraintViolation.isPresent()) {
            throw new IllegalValueException(nameConstraintViolation.get());
        }
        final Name modelName = new Name(name);

        final ClassName modelClassName = toModelClassName();

        final Optional<Email> modelEmail = toModelEmail();

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelClassName, modelEmail, modelTags, modelAttendanceRecords);
    }

    /**
     * Converts the class name of this adapted person into the model's {@code ClassName} object.
     *
     * @throws IllegalValueException if the class name is missing or invalid.
     */
    private ClassName toModelClassName() throws IllegalValueException {
        if (className == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, ClassName.class.getSimpleName()));
        }
        Optional<String> classNameConstraintViolation = ClassName.getConstraintViolation(className);
        if (classNameConstraintViolation.isPresent()) {
            throw new IllegalValueException(classNameConstraintViolation.get());
        }
        return new ClassName(className);
    }

    /**
     * Converts the email of this adapted person into the model's {@code Email} object. The email is optional,
     * so an empty {@code Optional} is returned if it is missing.
     *
     * @throws IllegalValueException if the email is present but invalid.
     */
    private Optional<Email> toModelEmail() throws IllegalValueException {
        if (email == null) {
            return Optional.empty();
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        return Optional.of(new Email(email));
    }

}
