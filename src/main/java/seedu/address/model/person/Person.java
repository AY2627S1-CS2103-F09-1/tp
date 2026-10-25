package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null (except the email, which is optional), field values are
 * validated, no two attendance records have the same date, no two grade records are for the same assessment,
 * immutable.
 */
public class Person {

    public static final String MESSAGE_DUPLICATE_ATTENDANCE_DATE = "Attendance records must have different dates";
    public static final String MESSAGE_DUPLICATE_ASSESSMENT = "Grade records must be for different assessments";

    // Identity fields
    private final Name name;
    private final ClassName className;

    // Data fields
    private final Email email; // null if the person has no email
    private final Set<Tag> tags = new HashSet<>();
    private final List<Attendance> attendanceRecords;
    private final List<Grade> gradeRecords;

    /**
     * Every field must be present and not null. The {@code email} may be an empty {@code Optional}.
     */
    public Person(Name name, ClassName className, Optional<Email> email, Set<Tag> tags) {
        this(name, className, email, tags, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Creates a Person with the given name, className, email, tags, and attendance records.
     *
     * @throws IllegalArgumentException if two of the {@code attendanceRecords} have the same date.
     */
    public Person(Name name, ClassName className, Optional<Email> email, Set<Tag> tags,
            List<Attendance> attendanceRecords) {
        this(name, className, email, tags, attendanceRecords, Collections.emptyList());
    }

    /**
     * Creates a Person with the given name, className, email, tags, attendance records and grade records.
     *
     * @throws IllegalArgumentException if two of the {@code attendanceRecords} have the same date, or two of
     *         the {@code gradeRecords} are for the same assessment.
     */
    public Person(Name name, ClassName className, Optional<Email> email, Set<Tag> tags,
            List<Attendance> attendanceRecords, List<Grade> gradeRecords) {
        requireAllNonNull(name, className, email, tags, attendanceRecords, gradeRecords);
        checkArgument(hasUniqueAttendanceDates(attendanceRecords), MESSAGE_DUPLICATE_ATTENDANCE_DATE);
        checkArgument(hasUniqueAssessments(gradeRecords), MESSAGE_DUPLICATE_ASSESSMENT);
        this.name = name;
        this.className = className;
        this.email = email.orElse(null);
        this.tags.addAll(tags);
        this.attendanceRecords = new ArrayList<>(attendanceRecords);
        this.gradeRecords = new ArrayList<>(gradeRecords);
    }

    /**
     * Returns true if no two of {@code attendanceRecords} have the same date.
     */
    public static boolean hasUniqueAttendanceDates(List<Attendance> attendanceRecords) {
        requireNonNull(attendanceRecords);
        return attendanceRecords.stream()
                .map(Attendance::getDate)
                .distinct()
                .count() == attendanceRecords.size();
    }

    /**
     * Returns true if no two of {@code gradeRecords} are for the same assessment. Assessment names are
     * compared ignoring case and extra spaces, as in {@link Grade#isSameAssessment(Grade)}.
     */
    public static boolean hasUniqueAssessments(List<Grade> gradeRecords) {
        requireNonNull(gradeRecords);
        return gradeRecords.stream()
                .map(grade -> StringUtil.toComparisonKey(grade.getAssessmentName()))
                .distinct()
                .count() == gradeRecords.size();
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
     * Returns true if this person has {@code tag}. Tags are compared ignoring case.
     */
    public boolean hasTag(Tag tag) {
        requireNonNull(tag);
        return tags.contains(tag);
    }

    /**
     * Returns a copy of this person that also has {@code tag}.
     * If this person already has a tag equal to {@code tag}, the existing tag is kept.
     */
    public Person withTag(Tag tag) {
        requireNonNull(tag);
        Set<Tag> updatedTags = new HashSet<>(tags);
        updatedTags.add(tag);
        return new Person(name, className, getEmail(), updatedTags, attendanceRecords, gradeRecords);
    }

    /**
     * Returns a copy of this person that does not have {@code tag}. Tags are compared ignoring case.
     * If this person does not have {@code tag}, the copy has the same tags as this person.
     */
    public Person withoutTag(Tag tag) {
        requireNonNull(tag);
        Set<Tag> updatedTags = new HashSet<>(tags);
        updatedTags.remove(tag);
        return new Person(name, className, getEmail(), updatedTags, attendanceRecords, gradeRecords);
    }

    /**
     * Returns an immutable view of the attendance records.
     */
    public List<Attendance> getAttendanceRecords() {
        return Collections.unmodifiableList(attendanceRecords);
    }

    /**
     * Returns an immutable view of the grade records.
     */
    public List<Grade> getGradeRecords() {
        return Collections.unmodifiableList(gradeRecords);
    }

    /**
     * Returns true if this person has an attendance record for {@code date}.
     */
    public boolean hasAttendanceOn(LocalDate date) {
        return getAttendanceOn(date).isPresent();
    }

    /**
     * Returns the attendance record of this person for {@code date}, or an empty {@code Optional} if there is
     * none.
     */
    public Optional<Attendance> getAttendanceOn(LocalDate date) {
        requireNonNull(date);
        return attendanceRecords.stream().filter(record -> record.getDate().equals(date)).findFirst();
    }

    /**
     * Returns true if this person has a grade for the same assessment as {@code grade}, ignoring case and
     * extra whitespace in the assessment name.
     */
    public boolean hasGradeFor(Grade grade) {
        return getGradeFor(grade).isPresent();
    }

    /**
     * Returns the grade of this person for the same assessment as {@code grade}, ignoring case and extra
     * whitespace in the assessment name, or an empty {@code Optional} if there is none.
     */
    public Optional<Grade> getGradeFor(Grade grade) {
        requireNonNull(grade);
        return gradeRecords.stream().filter(record -> record.isSameAssessment(grade)).findFirst();
    }

    /**
     * Returns a new Person with the given attendance record added.
     *
     * @param attendance Attendance record to add.
     * @return A new Person with the given attendance record added.
     * @throws IllegalArgumentException If a record already exists for that date.
     */
    public Person withAttendance(Attendance attendance) {
        requireNonNull(attendance);
        if (hasAttendanceOn(attendance.getDate())) {
            throw new IllegalArgumentException("Attendance record already exists");
        }
        List<Attendance> newAttendanceRecords = new ArrayList<>(attendanceRecords);
        newAttendanceRecords.add(attendance);
        return new Person(name, className, Optional.ofNullable(email), tags, newAttendanceRecords, gradeRecords);
    }

    /**
     * Returns a new Person without the given attendance record.
     * The original person and all other records are unchanged.
     *
     * @param attendance Attendance record to remove.
     * @return A new Person with the given attendance record removed.
     * @throws IllegalArgumentException If the attendance record does not exist.
     */
    public Person withoutAttendance(Attendance attendance) {
        requireNonNull(attendance);
        List<Attendance> newAttendanceRecords = new ArrayList<>(attendanceRecords);
        if (!newAttendanceRecords.remove(attendance)) {
            throw new IllegalArgumentException("Attendance record does not exist");
        }
        return new Person(name, className, Optional.ofNullable(email), tags, newAttendanceRecords, gradeRecords);
    }

    /**
     * Returns a new Person with the given grade record added.
     *
     * @param grade Grade record to add.
     * @return A new Person with the given grade record added.
     * @throws IllegalArgumentException If a record already exists for that assessment.
     */
    public Person withGrade(Grade grade) {
        requireNonNull(grade);
        if (hasGradeFor(grade)) {
            throw new IllegalArgumentException("Grade record already exists");
        }
        List<Grade> newGradeRecords = new ArrayList<>(gradeRecords);
        newGradeRecords.add(grade);
        return new Person(name, className, Optional.ofNullable(email), tags, attendanceRecords, newGradeRecords);
    }

    /**
     * Returns a new Person without the given grade record.
     * The original person and all other records are unchanged.
     *
     * @param grade Grade to remove.
     * @return A new Person with the given grade record removed.
     * @throws IllegalArgumentException If a record does not exist for that assessment.
     */
    public Person withoutGrade(Grade grade) {
        requireNonNull(grade);
        List<Grade> newGradeRecords = new ArrayList<>(gradeRecords);
        if (!newGradeRecords.remove(grade)) {
            throw new IllegalArgumentException("Grade record does not exist");
        }
        return new Person(name, className, Optional.ofNullable(email), tags, attendanceRecords, newGradeRecords);
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
                && attendanceRecords.equals(otherPerson.attendanceRecords)
                && gradeRecords.equals(otherPerson.gradeRecords);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, className, email, tags, attendanceRecords, gradeRecords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("email", email)
                .add("tags", tags)
                .add("attendanceRecords", attendanceRecords)
                .add("gradeRecords", gradeRecords)
                .toString();
    }

}
