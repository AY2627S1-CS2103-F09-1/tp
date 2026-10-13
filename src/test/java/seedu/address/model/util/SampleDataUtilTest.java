package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_noTwoPersonsShareIdentity() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();

        for (int i = 0; i < samplePersons.length; i++) {
            for (int j = i + 1; j < samplePersons.length; j++) {
                assertFalse(samplePersons[i].isSamePerson(samplePersons[j]));
            }
        }
    }

    @Test
    public void getSamplePersons_spansSeveralClasses() {
        long classCount = Arrays.stream(SampleDataUtil.getSamplePersons())
                .map(Person::getClassName)
                .distinct()
                .count();

        assertTrue(classCount > 1);
    }

    @Test
    public void getSamplePersons_includesPersonsWithAndWithoutEmail() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();

        assertTrue(Arrays.stream(samplePersons).anyMatch(person -> person.getEmail().isPresent()));
        assertTrue(Arrays.stream(samplePersons).anyMatch(person -> person.getEmail().isEmpty()));
    }

    @Test
    public void getSampleAddressBook_containsAllSamplePersonsInOrder() {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();

        assertEquals(Arrays.asList(SampleDataUtil.getSamplePersons()), sampleAddressBook.getPersonList());
    }

    @Test
    public void getTagSet_noTags_returnsEmptySet() {
        assertTrue(SampleDataUtil.getTagSet().isEmpty());
    }

    @Test
    public void getTagSet_duplicateTags_collapsedIntoOne() {
        assertEquals(Set.of(new Tag("friends")), SampleDataUtil.getTagSet("friends", "friends"));
    }

    @Test
    public void getTagSet_invalidTag_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> SampleDataUtil.getTagSet("not valid!"));
    }
}
