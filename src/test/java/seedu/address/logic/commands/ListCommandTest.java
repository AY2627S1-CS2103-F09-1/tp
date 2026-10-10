package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonMatchesKeyword;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_keywordMatchesName_showsMatchingPersons() {
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("meier");
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);

        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_keywordMatchesTag_showsMatchingPersons() {
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("frien");
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);

        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_keywordMatchesClassName_showsMatchingPersons() {
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("a1");
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size());

        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_keywordMatchesNobody_showsEmptyList() {
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("zzz");
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);

        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_keywordWhenListIsFiltered_searchesWholeAddressBook() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("meier");
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);

        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_noKeywordAfterKeyword_showsEverything() {
        model.updateFilteredPersonList(new PersonMatchesKeyword("meier"));

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand().execute(null));
        assertThrows(NullPointerException.class, () ->
                new ListCommand(new PersonMatchesKeyword("meier")).execute(null));
    }

    @Test
    public void equals() {
        ListCommand listAllCommand = new ListCommand();
        ListCommand listMeierCommand = new ListCommand(new PersonMatchesKeyword("meier"));
        ListCommand listFriendCommand = new ListCommand(new PersonMatchesKeyword("friend"));

        // same object -> returns true
        assertTrue(listAllCommand.equals(listAllCommand));
        assertTrue(listMeierCommand.equals(listMeierCommand));

        // same values -> returns true
        assertTrue(listAllCommand.equals(new ListCommand()));
        assertTrue(listMeierCommand.equals(new ListCommand(new PersonMatchesKeyword("meier"))));

        // same keyword apart from case and whitespace -> returns true
        assertTrue(listMeierCommand.equals(new ListCommand(new PersonMatchesKeyword(" MEIER "))));

        // different types -> returns false
        assertFalse(listAllCommand.equals(1));

        // null -> returns false
        assertFalse(listAllCommand.equals(null));

        // list all vs list with keyword -> returns false
        assertFalse(listAllCommand.equals(listMeierCommand));

        // different keyword -> returns false
        assertFalse(listMeierCommand.equals(listFriendCommand));
    }

    @Test
    public void hashCode_equalCommands_haveSameHashCode() {
        assertEquals(new ListCommand().hashCode(), new ListCommand().hashCode());
        assertEquals(new ListCommand(new PersonMatchesKeyword("meier")).hashCode(),
                new ListCommand(new PersonMatchesKeyword(" MEIER ")).hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesKeyword predicate = new PersonMatchesKeyword("meier");
        ListCommand listCommand = new ListCommand(predicate);
        String expected = ListCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, listCommand.toString());
    }
}
