package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void getSortedTags_noTags_returnsEmptyList() {
        Person person = new PersonBuilder().withTags().build();
        assertTrue(PersonCard.getSortedTags(person).isEmpty());
    }

    @Test
    public void getSortedTags_mixedCasing_sortsIgnoringCase() {
        Person person = new PersonBuilder().withTags("Zeta", "alpha", "Beta", "gamma").build();

        List<String> sortedNames = PersonCard.getSortedTags(person).stream().map(tag -> tag.tagName).toList();

        assertEquals(List.of("alpha", "Beta", "gamma", "Zeta"), sortedNames);
    }

    @Test
    public void getSortedTags_tagsWithSpacesAndSymbols_sortsByWholeName() {
        Person person = new PersonBuilder().withTags("group b", "Group A", "group-1", "Group 10").build();

        List<Tag> sortedTags = PersonCard.getSortedTags(person);

        assertEquals(List.of(new Tag("Group 10"), new Tag("Group A"), new Tag("group b"), new Tag("group-1")),
                sortedTags);
    }
}
