package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final DateTimeFormatter ATTENDANCE_DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd-MMM-uuuu");

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
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        attendance.setText(formatRecentAttendance(person));
    }

    /**
     * Hides the given {@code label} and stops it from taking up space in the card.
     */
    private static void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    private static String formatRecentAttendance(Person person) {
        List<Attendance> attRecords = person.getAttendanceRecords();

        if (attRecords.isEmpty()) {
            return "Recent attendance: none";
        }

        List<Attendance> recentRecords = attRecords.stream()
            .sorted(Comparator.comparing(Attendance::getDate).reversed()).limit(3).toList();

        String recordLines = recentRecords.stream()
            .map(record -> ATTENDANCE_DATE_FORMAT.format(record.getDate())
            + "  " + record.getStatus().name().toLowerCase(Locale.ROOT)).collect(Collectors.joining("\n"));

        int olderRecordCount = attRecords.size() - recentRecords.size();
        if (olderRecordCount > 0) {
            recordLines += "\n+" + olderRecordCount + " older record(s)";
        }

        return "Recent attendance:\n" + recordLines;
    }

}
