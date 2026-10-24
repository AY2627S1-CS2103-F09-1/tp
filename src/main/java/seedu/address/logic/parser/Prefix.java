package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A prefix that marks the beginning of an argument in an arguments string.
 * E.g. '/name' in 'add /name James /class A1'.
 * A prefix can have shorter aliases, such as '/n' for '/name', which mark the same argument. Two prefixes are
 * equal if their full forms are equal, whatever their aliases, so that a value given with an alias is
 * stored under the same prefix as a value given with the full form.
 */
public class Prefix {
    private final String prefix;
    private final List<String> aliases;

    /**
     * Creates a prefix with the given full form and aliases.
     */
    public Prefix(String prefix, String... aliases) {
        this.prefix = prefix;
        this.aliases = List.of(aliases);
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the aliases of this prefix, which is empty if it has none.
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Returns the full form of this prefix followed by its aliases.
     */
    public List<String> getAllForms() {
        List<String> allForms = new ArrayList<>();
        allForms.add(prefix);
        allForms.addAll(aliases);
        return allForms;
    }

    @Override
    public String toString() {
        return getPrefix();
    }

    @Override
    public int hashCode() {
        return prefix == null ? 0 : prefix.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Prefix otherPrefix)) {
            return false;
        }

        return prefix.equals(otherPrefix.prefix);
    }
}
