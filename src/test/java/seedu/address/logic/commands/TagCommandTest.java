package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code TagCommand}.
 */
public class TagCommandTest {

    private static final Tag GROUP_A = new Tag("Group A");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TagCommand(null, List.of(INDEX_FIRST_PERSON)));
        assertThrows(NullPointerException.class, () -> new TagCommand(GROUP_A, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        TagCommand tagCommand = new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON));
        assertThrows(NullPointerException.class, () -> tagCommand.execute(null));
    }

    @Test
    public void execute_singleIndex_success() {
        Person contact = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        TagCommand tagCommand = new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(contact, contact.withTag(GROUP_A));

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, "Group A", "Alice Pauline (A1)");
        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleIndices_listsContactsInOrderGiven() {
        Person first = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person third = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        TagCommand tagCommand = new TagCommand(GROUP_A, List.of(INDEX_THIRD_PERSON, INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(first, first.withTag(GROUP_A));
        expectedModel.setPerson(third, third.withTag(GROUP_A));

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, "Group A",
                "Carl Kurz (A1), Alice Pauline (A1)");
        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_contactAlreadyInOtherGroup_keepsExistingTags() {
        Person alice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(alice.hasTag(new Tag("friends")));

        executeSuccessfully(new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON)));

        Person taggedAlice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(taggedAlice.hasTag(new Tag("friends")));
        assertTrue(taggedAlice.hasTag(GROUP_A));
        assertEquals(2, taggedAlice.getTags().size());
    }

    @Test
    public void execute_typedCasing_isWhatTheContactShows() {
        executeSuccessfully(new TagCommand(new Tag("gROUP a"), List.of(INDEX_FIRST_PERSON)));

        Person taggedAlice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(taggedAlice.getTags().stream().anyMatch(tag -> tag.tagName.equals("gROUP a")));
    }

    @Test
    public void execute_filteredList_usesIndicesOfShownList() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person contact = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals("Benson Meier", contact.getName().fullName);
        TagCommand tagCommand = new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(contact, contact.withTag(GROUP_A));

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, "Group A", "Benson Meier (A1)");
        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_indexNotInList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        TagCommand tagCommand = new TagCommand(GROUP_A, List.of(outOfBoundIndex));

        assertCommandFailure(tagCommand, model,
                String.format(TagCommand.MESSAGE_INDEX_NOT_FOUND, outOfBoundIndex.getOneBased()));
    }

    @Test
    public void execute_indexNotInFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        TagCommand tagCommand = new TagCommand(GROUP_A, List.of(INDEX_SECOND_PERSON));

        // the second contact exists in the address book but is not shown
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());
        assertCommandFailure(tagCommand, model, String.format(TagCommand.MESSAGE_INDEX_NOT_FOUND, 2));
    }

    @Test
    public void execute_someIndicesNotInList_reportsAllMissingAndChangesNothing() {
        int size = model.getFilteredPersonList().size();
        TagCommand tagCommand = new TagCommand(GROUP_A,
                List.of(INDEX_FIRST_PERSON, Index.fromOneBased(size + 2), INDEX_THIRD_PERSON,
                        Index.fromOneBased(size + 1)));

        assertCommandFailure(tagCommand, model,
                String.format(TagCommand.MESSAGE_INDEX_NOT_FOUND, (size + 2) + ", " + (size + 1)));
    }

    @Test
    public void execute_someContactsAlreadyInGroup_reportsThemAndChangesNothing() {
        // Alice already belongs to "friends"; Carl does not
        TagCommand tagCommand = new TagCommand(new Tag("friends"),
                List.of(INDEX_THIRD_PERSON, INDEX_FIRST_PERSON));

        assertCommandFailure(tagCommand, model,
                String.format(TagCommand.MESSAGE_ALREADY_IN_GROUP, "Alice Pauline (A1)"));
    }

    @Test
    public void execute_groupNameDiffersInCasingAndSpacing_stillAlreadyInGroup() {
        TagCommand tagCommand = new TagCommand(new Tag("  FRIENDS "), List.of(INDEX_FIRST_PERSON));

        assertCommandFailure(tagCommand, model,
                String.format(TagCommand.MESSAGE_ALREADY_IN_GROUP, "Alice Pauline (A1)"));
    }

    @Test
    public void execute_missingIndexAndAlreadyInGroup_reportsMissingIndexFirst() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        TagCommand tagCommand = new TagCommand(new Tag("friends"), List.of(INDEX_FIRST_PERSON, outOfBoundIndex));

        assertCommandFailure(tagCommand, model,
                String.format(TagCommand.MESSAGE_INDEX_NOT_FOUND, outOfBoundIndex.getOneBased()));
    }

    @Test
    public void equals() {
        TagCommand tagFirstCommand = new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));

        // same object -> returns true
        assertTrue(tagFirstCommand.equals(tagFirstCommand));

        // same values -> returns true
        assertTrue(tagFirstCommand.equals(
                new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON))));

        // group differing only in casing -> returns true
        assertTrue(tagFirstCommand.equals(
                new TagCommand(new Tag("group a"), List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON))));

        // different types -> returns false
        assertFalse(tagFirstCommand.equals(1));

        // null -> returns false
        assertFalse(tagFirstCommand.equals(null));

        // different group -> returns false
        assertFalse(tagFirstCommand.equals(
                new TagCommand(new Tag("Group B"), List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON))));

        // different indices -> returns false
        assertFalse(tagFirstCommand.equals(new TagCommand(GROUP_A, List.of(INDEX_FIRST_PERSON))));

        // same indices in a different order -> returns false
        assertFalse(tagFirstCommand.equals(
                new TagCommand(GROUP_A, List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON))));
    }

    @Test
    public void toStringMethod() {
        List<Index> indices = List.of(INDEX_FIRST_PERSON);
        TagCommand tagCommand = new TagCommand(GROUP_A, indices);
        String expected = TagCommand.class.getCanonicalName() + "{group=" + GROUP_A
                + ", memberIndices=" + indices + "}";
        assertEquals(expected, tagCommand.toString());
    }

    /**
     * Executes {@code tagCommand} on {@code model}, for tests that only inspect the resulting model.
     */
    private void executeSuccessfully(TagCommand tagCommand) {
        try {
            tagCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }
}
