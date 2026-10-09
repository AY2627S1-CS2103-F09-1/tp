package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null (except the email, which is optional), field values are
 * validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final ClassName className;

    // Data fields
    private final Email email; // null if the person has no email
    private final Set<Tag> tags = new HashSet<>();
    private final List<Attendance> attendanceRecords;

    /**
     * Every field must be present and not null. The {@code email} may be an empty {@code Optional}.
     */
    public Person(Name name, ClassName className, Optional<Email> email, Set<Tag> tags) {
        this(name, className, email, tags, Collections.emptyList());
    }

    /**
     * Creates a Person with the given name, className, email, tags, and attendance records.
     * @param name
     * @param className
     * @param email
     * @param tags
     * @param attendanceRecords
     */
    public Person(Name name, ClassName className, Optional<Email> email, Set<Tag> tags,
        List<Attendance> attendanceRecords) {
        requireAllNonNull(name, className, email, tags, attendanceRecords);
        this.name = name;
        this.className = className;
        this.email = email.orElse(null);
        this.tags.addAll(tags);
        this.attendanceRecords = new ArrayList<>(attendanceRecords);
    }

    public Name getName() {
        return name;
    }

    public ClassName getClassName() {
        return className;
    }

    /**
     * Returns the email of this person, or an empty {@code Optional} if this person has no email.
     */
    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable view of the attendance records.
     */
    public List<Attendance> getAttendanceRecords() {
        return Collections.unmodifiableList(attendanceRecords);
    }

    /**
     * Returns a new Person with the given attendance record added.
     *
     * @param attendance
     * @return a new Person with the given attendance record added
     * @throws IllegalArgumentException if a record already exists for that date
     */
    public Person withAttendance(Attendance attendance) {
        requireNonNull(attendance);
        boolean alreadyRecorded = attendanceRecords.stream()
                .anyMatch(record -> record.getDate().equals(attendance.getDate()));
        if (alreadyRecorded) {
            throw new IllegalArgumentException("Attendance record already exists");
        }
        List<Attendance> newAttendanceRecords = new ArrayList<>(attendanceRecords);
        newAttendanceRecords.add(attendance);
        return new Person(name, className, Optional.ofNullable(email), tags, newAttendanceRecords);
    }

    /**
     * Returns true if both persons have the same name and class.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName())
                && otherPerson.getClassName().equals(getClassName());
    }

    /**
     * Returns true if this person has the given name and class, ignoring case and extra whitespace.
     * The given strings do not need to be valid, in which case they simply do not match.
     */
    public boolean hasNameAndClass(String nameToMatch, String classNameToMatch) {
        return name.matches(nameToMatch) && className.matches(classNameToMatch);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && className.equals(otherPerson.className)
                && Objects.equals(email, otherPerson.email)
                && tags.equals(otherPerson.tags)
                && attendanceRecords.equals(otherPerson.attendanceRecords);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, className, email, tags, attendanceRecords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("email", email)
                .add("tags", tags)
                .add("attendanceRecords", attendanceRecords)
                .toString();
    }

}
