package seedu.address.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final int MAX_RECENT_RECORDS = 3;
    private static final String MESSAGE_NO_ATTENDANCE = "Recent attendance: none";
    private static final String MESSAGE_OLDER_RECORDS = "+%d older record(s)";
    private static final String HEADER_RECENT_ATTENDANCE = "Recent attendance:\n";
    private static final String MESSAGE_NO_GRADES = "Recent grades: none";
    private static final String MESSAGE_OLDER_GRADES = "+%d older grade(s)";
    private static final String HEADER_RECENT_GRADES = "Recent grades:\n";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label className;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Label attendance;
    @FXML
    private Label grades;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        className.setText(person.getClassName().value);
        person.getEmail().ifPresentOrElse(personEmail -> email.setText(personEmail.value), () -> hide(email));
        attendance.setText(formatRecentAttendance(person));
        grades.setText(formatRecentGrades(person));
        getSortedTags(person).forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Returns the tags of {@code person} in alphabetical order, ignoring case.
     */
    static List<Tag> getSortedTags(Person person) {
        return person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Hides the given {@code label} and stops it from taking up space in the card.
     */
    private static void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    static String formatRecentAttendance(Person person) {
        List<Attendance> attRecords = person.getAttendanceRecords();

        if (attRecords.isEmpty()) {
            return MESSAGE_NO_ATTENDANCE;
        }

        List<Attendance> newestFirst = new ArrayList<>(attRecords);
        Collections.reverse(newestFirst);
        List<Attendance> recentAttendance = newestFirst.stream().limit(MAX_RECENT_RECORDS).toList();
        String recordLines =
            recentAttendance.stream().map(Attendance::toDisplayString).collect(Collectors.joining("\n"));

        int olderRecordCount = attRecords.size() - recentAttendance.size();
        if (olderRecordCount > 0) {
            recordLines += "\n" + String.format(MESSAGE_OLDER_RECORDS, olderRecordCount);
        }

        return HEADER_RECENT_ATTENDANCE + recordLines;
    }

    static String formatRecentGrades(Person person) {
        List<Grade> gradeRecords = person.getGradeRecords();

        if (gradeRecords.isEmpty()) {
            return MESSAGE_NO_GRADES;
        }

        List<Grade> newestFirst = new ArrayList<>(gradeRecords);
        Collections.reverse(newestFirst);
        List<Grade> recentGrades = newestFirst.stream().limit(MAX_RECENT_RECORDS).toList();
        String gradeLines = recentGrades.stream().map(Grade::toString).collect(Collectors.joining("\n"));
        int olderGradeCount = gradeRecords.size() - recentGrades.size();
        if (olderGradeCount > 0) {
            gradeLines += "\n" + String.format(MESSAGE_OLDER_GRADES, olderGradeCount);
        }

        return HEADER_RECENT_GRADES + gradeLines;
    }

}
