package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithAllFields_showsAllFields() {
        Person person = new PersonBuilder().withName("Amy Bee").withClassName("A1").withPhone("85355255")
                .withEmail("amy@gmail.com").withAddress("123 Jurong West").withTags("friends").build();

        String expected = "Amy Bee; Class: A1; Phone: 85355255; Email: amy@gmail.com; Address: 123 Jurong West; "
                + "Tags: [friends]";
        assertEquals(expected, Messages.format(person));
    }

    @Test
    public void format_personWithoutEmail_omitsEmail() {
        Person person = new PersonBuilder().withName("Amy Bee").withClassName("A1").withPhone("85355255")
                .withoutEmail().withAddress("123 Jurong West").build();

        String expected = "Amy Bee; Class: A1; Phone: 85355255; Address: 123 Jurong West; Tags: ";
        assertEquals(expected, Messages.format(person));
    }
}
