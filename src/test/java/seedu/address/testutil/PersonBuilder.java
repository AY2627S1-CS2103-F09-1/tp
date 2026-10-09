package seedu.address.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.model.person.Attendance;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_CLASS_NAME = "A1";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";

    private Name name;
    private ClassName className;
    private Email email; // null if the person has no email
    private Set<Tag> tags;
    private List<Attendance> attendanceRecords;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        className = new ClassName(DEFAULT_CLASS_NAME);
        email = new Email(DEFAULT_EMAIL);
        tags = new HashSet<>();
        attendanceRecords = new ArrayList<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        className = personToCopy.getClassName();
        email = personToCopy.getEmail().orElse(null);
        tags = new HashSet<>(personToCopy.getTags());
        attendanceRecords = new ArrayList<>(personToCopy.getAttendanceRecords());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code ClassName} of the {@code Person} that we are building.
     */
    public PersonBuilder withClassName(String className) {
        this.className = new ClassName(className);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Removes the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withoutEmail() {
        this.email = null;
        return this;
    }

    public Person build() {
        return new Person(name, className, Optional.ofNullable(email), tags, attendanceRecords);
    }

}
