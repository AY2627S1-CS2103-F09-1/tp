package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ClassName;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Edits the details of an existing contact in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the contact identified "
            + "by the index number used in the displayed contact list. "
            + "Only the details that are given are changed. "
            + "Use " + PREFIX_EMAIL + " with no value to remove the email.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_CLASS + " CLASS] "
            + "[" + PREFIX_EMAIL + " EMAIL]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NAME + " John Tann "
            + PREFIX_CLASS + " A2";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "%1$s in class %2$s has been edited";
    public static final String MESSAGE_NOT_EDITED = "At least one detail to edit must be provided";
    public static final String MESSAGE_DUPLICATE_PERSON = AddCommand.MESSAGE_DUPLICATE_PERSON;

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * Creates an EditCommand to edit the contact at {@code index} of the displayed list.
     *
     * @param index of the contact in the filtered person list to edit
     * @param editPersonDescriptor details to edit the contact with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON,
                    editedPerson.getName(), editedPerson.getClassName()));
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS,
                editedPerson.getName(), editedPerson.getClassName()));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}. The tags, the attendance records and the grade records are
     * kept.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        ClassName updatedClassName = editPersonDescriptor.getClassName().orElse(personToEdit.getClassName());
        Optional<Email> updatedEmail = editPersonDescriptor.isEmailCleared()
                ? Optional.empty()
                : editPersonDescriptor.getEmail().or(personToEdit::getEmail);

        return new Person(updatedName, updatedClassName, updatedEmail, personToEdit.getTags(),
                personToEdit.getAttendanceRecords(), personToEdit.getGradeRecords());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the contact with. A detail that is set replaces the corresponding detail of
     * the contact.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private ClassName className;
        private Email email;
        private boolean isEmailCleared;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setClassName(toCopy.className);
            setEmail(toCopy.email);
            setEmailCleared(toCopy.isEmailCleared);
        }

        /**
         * Returns true if at least one detail is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, className, email) || isEmailCleared;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setClassName(ClassName className) {
            this.className = className;
        }

        public Optional<ClassName> getClassName() {
            return Optional.ofNullable(className);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        /**
         * Sets whether the email of the contact is removed. This takes priority over the {@code Email} that
         * is set, if any.
         */
        public void setEmailCleared(boolean isEmailCleared) {
            this.isEmailCleared = isEmailCleared;
        }

        public boolean isEmailCleared() {
            return isEmailCleared;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(className, otherEditPersonDescriptor.className)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && isEmailCleared == otherEditPersonDescriptor.isEmailCleared;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, className, email, isEmailCleared);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("className", className)
                    .add("email", email)
                    .add("isEmailCleared", isEmailCleared)
                    .toString();
        }
    }
}
