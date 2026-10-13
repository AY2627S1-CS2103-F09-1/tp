package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name}, {@code ClassName} or any {@code Tag} contains the keyword given.
 * Matching is a contiguous substring match that ignores case and extra whitespace.
 */
public class PersonMatchesKeyword implements Predicate<Person> {
    private final String keyword;

    /**
     * Constructs a {@code PersonMatchesKeyword}.
     *
     * @param keyword The text to look for. It may contain several words.
     */
    public PersonMatchesKeyword(String keyword) {
        requireNonNull(keyword);
        this.keyword = StringUtil.toComparisonKey(keyword);
    }

    @Override
    public boolean test(Person person) {
        return containsKeyword(person.getName().fullName)
                || containsKeyword(person.getClassName().value)
                || person.getTags().stream().map(tag -> tag.tagName).anyMatch(this::containsKeyword);
    }

    private boolean containsKeyword(String text) {
        return StringUtil.toComparisonKey(text).contains(keyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonMatchesKeyword otherPersonMatchesKeyword)) {
            return false;
        }

        return keyword.equals(otherPersonMatchesKeyword.keyword);
    }

    @Override
    public int hashCode() {
        return keyword.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keyword", keyword).toString();
    }
}
