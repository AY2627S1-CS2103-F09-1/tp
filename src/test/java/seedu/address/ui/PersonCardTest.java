package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void constructor_noAttendance_showsNone() {
        PersonCard card = new PersonCard(new PersonBuilder().build(), 1);

        assertEquals("Recent attendance: none", attendanceLabel(card).getText());
    }

    @Test
    public void constructor_threeAttendanceRecords_showsAllWithoutOlderCount() {
        Person person = new PersonBuilder().build();
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 12), Attendance.Status.PRESENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 13), Attendance.Status.ABSENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 14), Attendance.Status.LATE));

        PersonCard card = new PersonCard(person, 1);

        assertEquals(
                "Recent attendance:\n"
                        + "14-Sep-2026  late\n"
                        + "13-Sep-2026  absent\n"
                        + "12-Sep-2026  present",
                attendanceLabel(card).getText());
    }

    @Test
    public void constructor_moreThanThreeAttendanceRecords_showsLatestThreeAndOlderCount() {
        Person person = new PersonBuilder().build();
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 10), Attendance.Status.PRESENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 12), Attendance.Status.PRESENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 13), Attendance.Status.ABSENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 14), Attendance.Status.LATE));

        PersonCard card = new PersonCard(person, 1);

        assertEquals(
                "Recent attendance:\n"
                        + "14-Sep-2026  late\n"
                        + "13-Sep-2026  absent\n"
                        + "12-Sep-2026  present\n"
                        + "+1 older record(s)",
                attendanceLabel(card).getText());
    }

    private static Label attendanceLabel(PersonCard card) {
        Label label = (Label) card.getRoot().lookup("#attendance");
        assertNotNull(label);
        return label;
    }

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException alreadyStarted) {
        // The toolkit was started by another test.
        }
    }

}
