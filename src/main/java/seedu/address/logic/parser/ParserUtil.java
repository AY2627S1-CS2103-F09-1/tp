package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_GROUP_NAME = "Invalid group name. Group names must be 1 to "
            + Tag.MAX_LENGTH + " characters long and can only contain letters, digits, spaces, hyphens, "
            + "and underscores";
    public static final String MESSAGE_EMPTY_MEMBER = "Members cannot contain empty entries";
    public static final String MESSAGE_INVALID_MEMBER_INDEX =
            "Contact index must be a positive integer: %1$s";
    public static final String MESSAGE_DUPLICATE_MEMBER_INDEX = "Duplicate contact index: %1$s";

    private static final String MEMBER_SEPARATOR = ",";

    /** Passed to {@code String#split} so that empty entries at the end, such as in "1,3,", are kept. */
    private static final int KEEP_TRAILING_EMPTY_ENTRIES = -1;

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Throws a {@code ParseException} with {@code errorMessage} if {@code args} contains a prefix-like token
     * that is not one of {@code knownPrefixes}. See {@link ArgumentTokenizer#findUnrecognizedPrefixes}.
     */
    public static void requireNoUnrecognizedPrefixes(
            String args, String errorMessage, Prefix... knownPrefixes) throws ParseException {
        requireNonNull(args);
        requireNonNull(errorMessage);
        if (!ArgumentTokenizer.findUnrecognizedPrefixes(args, knownPrefixes).isEmpty()) {
            throw new ParseException(errorMessage);
        }
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        Optional<String> constraintViolation = Name.getConstraintViolation(trimmedName);
        if (constraintViolation.isPresent()) {
            throw new ParseException(constraintViolation.get());
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String className} into a {@code ClassName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code className} is invalid.
     */
    public static ClassName parseClassName(String className) throws ParseException {
        requireNonNull(className);
        String trimmedClassName = className.trim();
        Optional<String> constraintViolation = ClassName.getConstraintViolation(trimmedClassName);
        if (constraintViolation.isPresent()) {
            throw new ParseException(constraintViolation.get());
        }
        return new ClassName(trimmedClassName);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String groupName} into a {@code Tag} that represents the group.
     * Leading and trailing whitespaces will be trimmed and repeated spaces will be collapsed.
     *
     * @throws ParseException if the given {@code groupName} is invalid.
     */
    public static Tag parseGroupName(String groupName) throws ParseException {
        requireNonNull(groupName);
        if (!Tag.isValidTagName(groupName)) {
            throw new ParseException(MESSAGE_INVALID_GROUP_NAME);
        }
        return new Tag(groupName);
    }

    /**
     * Parses a comma-separated {@code String members} into a list of {@code Index} in the order given.
     * Whitespaces around each index will be trimmed.
     * Problems are reported in this order: an empty entry or an index that is not a positive integer,
     * checked entry by entry, and then an index that is given more than once.
     *
     * @throws ParseException if {@code members} has an invalid entry or an index more than once.
     */
    public static List<Index> parseMemberIndices(String members) throws ParseException {
        requireNonNull(members);
        List<Index> indices = new ArrayList<>();
        for (String member : members.split(MEMBER_SEPARATOR, KEEP_TRAILING_EMPTY_ENTRIES)) {
            indices.add(parseMemberIndex(member.trim()));
        }
        requireNoDuplicateIndices(indices);
        return indices;
    }

    /**
     * Parses a single, already trimmed {@code String member} into an {@code Index}.
     *
     * @throws ParseException if {@code member} is empty or is not a positive integer.
     */
    private static Index parseMemberIndex(String member) throws ParseException {
        if (member.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_MEMBER);
        }
        if (!StringUtil.isNonZeroUnsignedInteger(member)) {
            throw new ParseException(String.format(MESSAGE_INVALID_MEMBER_INDEX, member));
        }
        return Index.fromOneBased(Integer.parseInt(member));
    }

    /**
     * Throws a {@code ParseException} naming every index that appears more than once in {@code indices}.
     * Each repeated index is named once, in the order in which it is first found to be repeated.
     */
    private static void requireNoDuplicateIndices(List<Index> indices) throws ParseException {
        Set<Integer> seenIndices = new HashSet<>();
        Set<Integer> duplicateIndices = new LinkedHashSet<>();
        for (Index index : indices) {
            if (!seenIndices.add(index.getOneBased())) {
                duplicateIndices.add(index.getOneBased());
            }
        }
        if (!duplicateIndices.isEmpty()) {
            String duplicates = duplicateIndices.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new ParseException(String.format(MESSAGE_DUPLICATE_MEMBER_INDEX, duplicates));
        }
    }
}
