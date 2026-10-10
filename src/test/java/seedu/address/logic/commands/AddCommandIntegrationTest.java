package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, validPerson.getName()),
                expectedModel);
    }

    @Test
    public void execute_newPerson_showsNewPersonAtTopOfList() throws Exception {
        Person previousFirstPerson = model.getFilteredPersonList().get(0);
        Person validPerson = new PersonBuilder().build();

        new AddCommand(validPerson).execute(model);

        assertEquals(validPerson, model.getFilteredPersonList().get(0));
        assertEquals(previousFirstPerson, model.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_newPersonWhileListIsFiltered_showsNewPersonAtTopOfFullList() throws Exception {
        Person previousFirstPerson = model.getFilteredPersonList().get(0);
        Person validPerson = new PersonBuilder().build();
        model.updateFilteredPersonList(person -> false);

        new AddCommand(validPerson).execute(model);

        assertEquals(validPerson, model.getFilteredPersonList().get(0));
        assertEquals(previousFirstPerson, model.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_newPersonWithoutEmail_success() {
        Person validPerson = new PersonBuilder().withoutEmail().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, validPerson.getName()),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        String expectedMessage = String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, personInList.getName(),
                personInList.getClassName());
        assertCommandFailure(new AddCommand(personInList), model, expectedMessage);
    }

    @Test
    public void execute_sameNameAndClassInDifferentCase_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person duplicatePerson = new PersonBuilder(personInList)
                .withName(personInList.getName().toString().toUpperCase())
                .withClassName(personInList.getClassName().toString().toLowerCase())
                .build();
        String expectedMessage = String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, duplicatePerson.getName(),
                duplicatePerson.getClassName());
        assertCommandFailure(new AddCommand(duplicatePerson), model, expectedMessage);
    }

    @Test
    public void execute_sameNameDifferentClass_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person samePersonInOtherClass = new PersonBuilder(personInList).withClassName(VALID_CLASS_BOB).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(samePersonInOtherClass);

        assertCommandSuccess(new AddCommand(samePersonInOtherClass), model,
                String.format(AddCommand.MESSAGE_SUCCESS, samePersonInOtherClass.getName()),
                expectedModel);
    }

}
