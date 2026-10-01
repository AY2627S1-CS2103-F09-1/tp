package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
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

    /**
     * Every field must be present and not null. The {@code email} may be an empty {@code Optional}.
     */
    public Person(Name name, ClassName className, Optional<Email> email, Set<Tag> tags) {
        requireAllNonNull(name, className, email, tags);
        this.name = name;
        this.className = className;
        this.email = email.orElse(null);
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public ClassName getClassName() {
        return className;
    }

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
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, className, email, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("email", email)
                .add("tags", tags)
                .toString();
    }

}
