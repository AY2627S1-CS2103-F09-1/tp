package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void formatRecentAttendance_noRecords_showsNone() {
        Person person = new PersonBuilder().build();

        assertEquals("Recent attendance: none", PersonCard.formatRecentAttendance(person));
    }

    @Test
    public void formatRecentAttendance_threeRecords_showsAllWithoutOlderCount() {
        Person person = new PersonBuilder().build();
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 12), Attendance.Status.PRESENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 13), Attendance.Status.ABSENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 14), Attendance.Status.LATE));

        assertEquals(
                "Recent attendance:\n"
                        + "14-Sep-2026  late\n"
                        + "13-Sep-2026  absent\n"
                        + "12-Sep-2026  present",
                PersonCard.formatRecentAttendance(person));
    }

    @Test
    public void formatRecentAttendance_moreThanThreeRecords_showsLatestThreeAndOlderCount() {
        Person person = new PersonBuilder().build();
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 10), Attendance.Status.PRESENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 12), Attendance.Status.PRESENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 13), Attendance.Status.ABSENT));
        person = person.withAttendance(new Attendance(
                LocalDate.of(2026, 9, 14), Attendance.Status.LATE));

        assertEquals(
                "Recent attendance:\n"
                        + "14-Sep-2026  late\n"
                        + "13-Sep-2026  absent\n"
                        + "12-Sep-2026  present\n"
                        + "+1 older record(s)",
                PersonCard.formatRecentAttendance(person));
    }

    @Test
    public void getSortedTags_sortsIgnoringCase() {
        Person person = new PersonBuilder().withTags("beta", "Alpha", "gamma").build();

        assertEquals(List.of(new Tag("Alpha"), new Tag("beta"), new Tag("gamma")), PersonCard.getSortedTags(person));
    }

}
