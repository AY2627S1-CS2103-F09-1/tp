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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code UntagCommand}.
 * The typical contacts are used. Alice Pauline is in {@code friends}, Benson Meier is in {@code owesMoney} and
 * {@code friends}, and Carl Kurz is in no group.
 */
public class UntagCommandTest {

    private static final Tag FRIENDS = new Tag("friends");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UntagCommand(null, List.of(INDEX_FIRST_PERSON)));
        assertThrows(NullPointerException.class, () -> new UntagCommand(FRIENDS, null));
    }

    @Test
    public void constructor_duplicateIndices_throwsIllegalArgumentException() {
        List<Index> indicesWithDuplicate =
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, INDEX_FIRST_PERSON);
        assertThrows(IllegalArgumentException.class, () -> new UntagCommand(FRIENDS, indicesWithDuplicate));
    }

    @Test
    public void constructor_noIndices_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new UntagCommand(FRIENDS, List.of()));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));
        assertThrows(NullPointerException.class, () -> untagCommand.execute(null));
    }

    @Test
    public void execute_modelDoesNotStoreChangedContact_throwsAssertionError() {
        Model modelThatKeepsContacts = new ModelManager(getTypicalAddressBook(), new UserPrefs()) {
            @Override
            public void setPerson(Person target, Person editedPerson) {
                // Intentionally does nothing, to simulate a model that fails to store the contact
            }
        };
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));

        // Gradle runs the tests with assertions enabled
        assertThrows(AssertionError.class, () -> untagCommand.execute(modelThatKeepsContacts));
    }

    @Test
    public void execute_singleIndex_success() {
        Person contact = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(contact, contact.withoutTag(FRIENDS));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, "friends", "Alice Pauline (A1)");
        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleIndices_listsContactsInOrderGiven() {
        Person alice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person benson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(alice, alice.withoutTag(FRIENDS));
        expectedModel.setPerson(benson, benson.withoutTag(FRIENDS));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, "friends",
                "Benson Meier (A1), Alice Pauline (A1)");
        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_contactInOtherGroups_keepsOtherGroups() {
        executeSuccessfully(new UntagCommand(FRIENDS, List.of(INDEX_SECOND_PERSON)));

        Person benson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        assertFalse(benson.hasTag(FRIENDS));
        assertTrue(benson.hasTag(new Tag("owesMoney")));
        assertEquals(1, benson.getTags().size());
    }

    @Test
    public void execute_groupNameDiffersInCasingAndSpacing_removesGroup() {
        UntagCommand untagCommand = new UntagCommand(new Tag("  FRIENDS "), List.of(INDEX_FIRST_PERSON));

        executeSuccessfully(untagCommand);

        Person alice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(alice.getTags().isEmpty());
    }

    @Test
    public void execute_typedCasing_isWhatTheMessageShows() {
        UntagCommand untagCommand = new UntagCommand(new Tag("fRIENDS"), List.of(INDEX_FIRST_PERSON));

        assertCommandSuccess(untagCommand, model, String.format(UntagCommand.MESSAGE_SUCCESS, "fRIENDS",
                "Alice Pauline (A1)"), expectedModelWithoutFriends(INDEX_FIRST_PERSON));
    }

    @Test
    public void execute_contactWithRecords_keepsAttendanceAndGrades() {
        Person alice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person aliceWithRecords = alice
                .withAttendance(new Attendance(LocalDate.of(2024, 1, 1), Attendance.Status.PRESENT))
                .withGrade(new Grade("Quiz 1", BigDecimal.valueOf(80)));
        model.setPerson(alice, aliceWithRecords);

        executeSuccessfully(new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON)));

        Person actual = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(aliceWithRecords.getAttendanceRecords(), actual.getAttendanceRecords());
        assertEquals(aliceWithRecords.getGradeRecords(), actual.getGradeRecords());
    }

    @Test
    public void execute_filteredList_usesIndicesOfShownList() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person contact = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals("Benson Meier", contact.getName().fullName);
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(contact, contact.withoutTag(FRIENDS));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, "friends", "Benson Meier (A1)");
        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_tagThenUntagSameGroup_restoresContact() {
        Person carl = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        Tag groupA = new Tag("Group A");

        executeSuccessfully(new TagCommand(groupA, List.of(INDEX_THIRD_PERSON)));
        assertTrue(model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased()).hasTag(groupA));
        executeSuccessfully(new UntagCommand(groupA, List.of(INDEX_THIRD_PERSON)));

        assertEquals(carl, model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased()));
    }

    @Test
    public void execute_indexNotInList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(outOfBoundIndex));

        assertCommandFailure(untagCommand, model,
                String.format(UntagCommand.MESSAGE_INDEX_NOT_FOUND, outOfBoundIndex.getOneBased()));
    }

    @Test
    public void execute_indexNotInFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_SECOND_PERSON));

        // the second contact exists in the address book but is not shown
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());
        assertCommandFailure(untagCommand, model, String.format(UntagCommand.MESSAGE_INDEX_NOT_FOUND, 2));
    }

    @Test
    public void execute_someIndicesNotInList_reportsAllMissingAndChangesNothing() {
        int size = model.getFilteredPersonList().size();
        UntagCommand untagCommand = new UntagCommand(FRIENDS,
                List.of(INDEX_FIRST_PERSON, Index.fromOneBased(size + 2), INDEX_SECOND_PERSON,
                        Index.fromOneBased(size + 1)));

        assertCommandFailure(untagCommand, model,
                String.format(UntagCommand.MESSAGE_INDEX_NOT_FOUND, (size + 2) + ", " + (size + 1)));
    }

    @Test
    public void execute_veryManyIndicesNotInList_messageStaysShort() {
        int size = model.getFilteredPersonList().size();
        int missingCount = 100_000;
        List<Index> missingIndices = IntStream.rangeClosed(size + 1, size + missingCount)
                .mapToObj(Index::fromOneBased).toList();
        String firstIndices = IntStream.rangeClosed(size + 1, size + GroupMembershipCommand.MAX_INDICES_SHOWN)
                .mapToObj(String::valueOf).collect(Collectors.joining(", "));

        assertCommandFailure(new UntagCommand(FRIENDS, missingIndices), model,
                String.format(UntagCommand.MESSAGE_INDEX_NOT_FOUND, firstIndices
                        + String.format(UntagCommand.MESSAGE_MORE_INDICES,
                        missingCount - GroupMembershipCommand.MAX_INDICES_SHOWN)));
    }

    @Test
    public void execute_someContactsNotInGroup_reportsThemAndChangesNothing() {
        // Alice and Benson are in "friends", but Carl is not
        UntagCommand untagCommand = new UntagCommand(FRIENDS,
                List.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON, INDEX_SECOND_PERSON));

        assertCommandFailure(untagCommand, model,
                String.format(UntagCommand.MESSAGE_NOT_IN_GROUP, "Carl Kurz (A1)"));
    }

    @Test
    public void execute_severalContactsNotInGroup_reportsAllInOrderGiven() {
        UntagCommand untagCommand = new UntagCommand(new Tag("owesMoney"),
                List.of(INDEX_THIRD_PERSON, INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));

        assertCommandFailure(untagCommand, model,
                String.format(UntagCommand.MESSAGE_NOT_IN_GROUP, "Carl Kurz (A1), Alice Pauline (A1)"));
    }

    @Test
    public void execute_missingIndexAndNotInGroup_reportsMissingIndexFirst() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_THIRD_PERSON, outOfBoundIndex));

        assertCommandFailure(untagCommand, model,
                String.format(UntagCommand.MESSAGE_INDEX_NOT_FOUND, outOfBoundIndex.getOneBased()));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        assertTrue(untagCommand.equals(untagCommand));
    }

    @Test
    public void equals_sameGroupAndIndices_returnsTrueWithSameHashCode() {
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        UntagCommand sameCommand = new UntagCommand(new Tag("FRIENDS"),
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));

        assertTrue(untagCommand.equals(sameCommand));
        assertEquals(untagCommand.hashCode(), sameCommand.hashCode());
    }

    @Test
    public void equals_nullOrDifferentType_returnsFalse() {
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));
        assertFalse(untagCommand.equals(null));
        assertFalse(untagCommand.equals(1));
    }

    @Test
    public void equals_tagCommandWithSameGroupAndIndices_returnsFalse() {
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));
        TagCommand tagCommand = new TagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON));

        assertFalse(untagCommand.equals(tagCommand));
        assertFalse(tagCommand.equals(untagCommand));
    }

    @Test
    public void equals_differentGroupOrIndices_returnsFalse() {
        UntagCommand untagCommand = new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));

        assertFalse(untagCommand.equals(new UntagCommand(new Tag("Group B"),
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON))));
        assertFalse(untagCommand.equals(new UntagCommand(FRIENDS, List.of(INDEX_FIRST_PERSON))));
        assertFalse(untagCommand.equals(new UntagCommand(FRIENDS,
                List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON))));
    }

    @Test
    public void toString_validCommand_showsGroupAndIndices() {
        List<Index> indices = List.of(INDEX_FIRST_PERSON);
        UntagCommand untagCommand = new UntagCommand(FRIENDS, indices);
        String expected = UntagCommand.class.getCanonicalName() + "{group=" + FRIENDS
                + ", memberIndices=" + indices + "}";
        assertEquals(expected, untagCommand.toString());
    }

    /**
     * Returns a model with the same contacts as {@code model}, except that the contact at {@code index} is
     * not in the {@code friends} group.
     */
    private Model expectedModelWithoutFriends(Index index) {
        Person contact = model.getFilteredPersonList().get(index.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(contact, contact.withoutTag(FRIENDS));
        return expectedModel;
    }

    /**
     * Executes {@code command} on {@code model}, for tests that only inspect the resulting model.
     */
    private void executeSuccessfully(Command command) {
        try {
            command.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }
}
