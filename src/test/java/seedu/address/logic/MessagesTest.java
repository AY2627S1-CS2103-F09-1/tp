package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void getInvalidDisplayedIndexMessage_noContactsDisplayed_saysSo() {
        assertEquals("The contact index provided is invalid. No contacts are displayed.",
                Messages.getInvalidDisplayedIndexMessage(0));
    }

    @Test
    public void getInvalidDisplayedIndexMessage_contactsDisplayed_givesTheRangeOfIndices() {
        assertEquals("The contact index provided is invalid. The displayed list has 1 contact(s), "
                + "so the index must be from 1 to 1.", Messages.getInvalidDisplayedIndexMessage(1));
        assertEquals("The contact index provided is invalid. The displayed list has 6 contact(s), "
                + "so the index must be from 1 to 6.", Messages.getInvalidDisplayedIndexMessage(6));
    }

    @Test
    public void getErrorMessageForDuplicatePrefixes_repeatedPrefixes_namesThem() {
        assertEquals("Each parameter can only be specified once. Repeated: /date",
                Messages.getErrorMessageForDuplicatePrefixes("/date"));
        assertEquals("Each parameter can only be specified once. Repeated: /date, /status",
                Messages.getErrorMessageForDuplicatePrefixes("/date", "/status"));
    }

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
