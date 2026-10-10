package seedu.address.model.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    /**
     * Returns the sample persons, spread over a few classes. One of them has no email.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new ClassName("A1"),
                Optional.of(new Email("alexyeoh@example.com")),
                getTagSet("friends")),
            new Person(new Name("Bernice Yu"), new ClassName("A1"),
                Optional.of(new Email("berniceyu@example.com")),
                getTagSet("colleagues", "friends")),
            new Person(new Name("Charlotte Oliveiro"), new ClassName("A2"),
                Optional.of(new Email("charlotte@example.com")),
                getTagSet("neighbours")),
            new Person(new Name("David Li"), new ClassName("A2"),
                Optional.of(new Email("lidavid@example.com")),
                getTagSet("family")),
            new Person(new Name("Irfan Ibrahim"), new ClassName("B1"),
                Optional.of(new Email("irfan@example.com")),
                getTagSet("classmates")),
            new Person(new Name("Roy Balakrishnan"), new ClassName("B1"),
                Optional.empty(),
                getTagSet("colleagues"))
        };
    }

    /**
     * Returns an address book containing the sample persons.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
