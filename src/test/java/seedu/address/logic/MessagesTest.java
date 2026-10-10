package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithAllFields_showsAllFields() {
        Person person = new PersonBuilder().withName("Amy Bee").withClassName("A1")
                .withEmail("amy@gmail.com").withTags("friends").build();

        String expected = "Amy Bee; Class: A1; Email: amy@gmail.com; Tags: [friends]";
        assertEquals(expected, Messages.format(person));
    }

    @Test
    public void format_personWithoutEmail_omitsEmail() {
        Person person = new PersonBuilder().withName("Amy Bee").withClassName("A1").withoutEmail().build();

        String expected = "Amy Bee; Class: A1; Tags: ";
        assertEquals(expected, Messages.format(person));
    }
}
