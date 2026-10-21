package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/** Displays the grades recorded for a student. */
public class ViewGradeCommand extends Command {

    public static final String COMMAND_WORD = "viewG";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays all grades for a student.\n"
            + "Parameters: /name NAME /class CLASS\n"
            + "Example: " + COMMAND_WORD + " /name John Tan /class A1";

    public static final String MESSAGE_CONTACT_NOT_FOUND = "Contact not found";
    public static final String MESSAGE_NO_GRADES = "No grades recorded.";
    public static final String MESSAGE_SUCCESS = "Here are the grades for %s from class %s\n%s";

    private final String name;
    private final String className;

    /**
     * Creates a command to display grades for the student with the given name and class. Neither needs to be
     * a valid name or class, and case and extra whitespace are ignored when they are matched.
     */
    public ViewGradeCommand(String name, String className) {
        requireAllNonNull(name, className);
        this.name = name;
        this.className = className;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.hasNameAndClass(name, className))
                .findFirst().orElseThrow(() -> new CommandException(MESSAGE_CONTACT_NOT_FOUND));

        List<Grade> gradeRecords = new ArrayList<>(student.getGradeRecords());
        Collections.reverse(gradeRecords);
        String grades = gradeRecords.stream().map(Grade::toString).collect(Collectors.joining("\n"));
        if (grades.isEmpty()) {
            grades = MESSAGE_NO_GRADES;
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), student.getClassName(), grades));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewGradeCommand otherCommand)) {
            return false;
        }
        return name.equals(otherCommand.name) && className.equals(otherCommand.className);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("className", className).toString();
    }

}
