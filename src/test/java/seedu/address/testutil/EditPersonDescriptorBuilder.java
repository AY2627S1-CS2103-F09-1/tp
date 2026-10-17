package seedu.address.testutil;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details.
     * The tags of {@code person} are the tags to add.
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setClassName(person.getClassName());
        person.getEmail().ifPresent(descriptor::setEmail);
        descriptor.setTagsToAdd(person.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code ClassName} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withClassName(String className) {
        descriptor.setClassName(new ClassName(className));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Marks the email of the {@code EditPersonDescriptor} that we are building as removed.
     */
    public EditPersonDescriptorBuilder withoutEmail() {
        descriptor.setEmailCleared(true);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it as the tags to add of the
     * {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withTagsToAdd(String... tags) {
        descriptor.setTagsToAdd(toTagSet(tags));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it as the tags to remove of the
     * {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withTagsToRemove(String... tags) {
        descriptor.setTagsToRemove(toTagSet(tags));
        return this;
    }

    private static Set<Tag> toTagSet(String... tags) {
        return Stream.of(tags).map(Tag::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
