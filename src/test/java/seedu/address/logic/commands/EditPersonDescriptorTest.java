package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different class -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withClassName(VALID_CLASS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // email removed -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withoutEmail().build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags to add -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTagsToAdd(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags to remove -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTagsToRemove(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        assertEquals(DESC_AMY.hashCode(), new EditPersonDescriptor(DESC_AMY).hashCode());
    }

    @Test
    public void isAnyFieldEdited_noField_returnsFalse() {
        assertFalse(new EditPersonDescriptor().isAnyFieldEdited());
    }

    @Test
    public void isAnyFieldEdited_eachField_returnsTrue() {
        assertTrue(new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withClassName(VALID_CLASS_BOB).build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_BOB).build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withoutEmail().build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withTagsToAdd(VALID_TAG_FRIEND).build().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withTagsToRemove(VALID_TAG_FRIEND).build()
                .isAnyFieldEdited());
    }

    @Test
    public void getTags_noTags_returnsEmptySets() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertTrue(descriptor.getTagsToAdd().isEmpty());
        assertTrue(descriptor.getTagsToRemove().isEmpty());
    }

    @Test
    public void getTags_modifyReturnedSets_throwsUnsupportedOperationException() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTagsToAdd(VALID_TAG_FRIEND).withTagsToRemove(VALID_TAG_HUSBAND).build();

        assertThrows(UnsupportedOperationException.class, () -> descriptor.getTagsToAdd().clear());
        assertThrows(UnsupportedOperationException.class, () -> descriptor.getTagsToRemove().clear());
    }

    @Test
    public void setTags_nullSet_throwsNullPointerException() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertThrows(NullPointerException.class, () -> descriptor.setTagsToAdd(null));
        assertThrows(NullPointerException.class, () -> descriptor.setTagsToRemove(null));
    }

    @Test
    public void copyConstructor_changeOriginalTags_copyIsNotAffected() {
        EditPersonDescriptor original = new EditPersonDescriptorBuilder().withTagsToAdd(VALID_TAG_FRIEND).build();
        EditPersonDescriptor copy = new EditPersonDescriptor(original);

        original.setTagsToAdd(Set.of(new Tag(VALID_TAG_HUSBAND)));

        assertEquals(Set.of(new Tag(VALID_TAG_FRIEND)), copy.getTagsToAdd());
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", className="
                + editPersonDescriptor.getClassName().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", isEmailCleared="
                + editPersonDescriptor.isEmailCleared() + ", tagsToAdd="
                + editPersonDescriptor.getTagsToAdd() + ", tagsToRemove="
                + editPersonDescriptor.getTagsToRemove() + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
