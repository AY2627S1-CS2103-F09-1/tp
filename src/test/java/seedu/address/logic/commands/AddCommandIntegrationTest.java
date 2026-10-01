package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
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
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_newPersonWithoutEmail_success() {
        Person validPerson = new PersonBuilder().withoutEmail().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameAndClassInDifferentCase_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person duplicatePerson = new PersonBuilder(personInList)
                .withName(personInList.getName().toString().toUpperCase())
                .withClassName(personInList.getClassName().toString().toLowerCase())
                .build();
        assertCommandFailure(new AddCommand(duplicatePerson), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameDifferentClass_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person samePersonInOtherClass = new PersonBuilder(personInList).withClassName(VALID_CLASS_BOB).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(samePersonInOtherClass);

        assertCommandSuccess(new AddCommand(samePersonInOtherClass), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(samePersonInOtherClass)),
                expectedModel);
    }

}
