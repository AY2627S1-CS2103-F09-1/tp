package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Attendance;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final String NL = "\n";

    @Test
    public void formatRecentAttendance_localeWithOtherDigits_olderCountUsesDigitsZeroToNine() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        Locale.setDefault(Locale.Category.FORMAT, Locale.forLanguageTag("th-TH-u-nu-thai"));
        try {
            Person person = new PersonBuilder().build();
            for (int day = 1; day <= 5; day++) {
                person = person.withAttendance(new Attendance(
                        LocalDate.of(2026, 9, day), Attendance.Status.PRESENT));
            }

            assertEquals(
                    "Recent attendance:" + NL
                            + "05-Sep-2026  present" + NL
                            + "04-Sep-2026  present" + NL
                            + "03-Sep-2026  present" + NL
                            + "+2 older record(s)",
                    PersonCard.formatRecentAttendance(person));
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }

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
    public void getSortedTags_numbersInTagNames_comparedByValue() {
        Person person = new PersonBuilder().withTags("Team 10", "Team 2", "team 1", "Group-A", "Group B").build();

        assertEquals(List.of(new Tag("Group-A"), new Tag("Group B"), new Tag("team 1"), new Tag("Team 2"),
                new Tag("Team 10")), PersonCard.getSortedTags(person));
    }

    @Test
    public void getSortedTags_mixedCase_sortsIgnoringCase() {
        Person person = new PersonBuilder().withTags("beta", "Alpha", "gamma").build();

        assertEquals(List.of(new Tag("Alpha"), new Tag("beta"), new Tag("gamma")),
                PersonCard.getSortedTags(person));
    }

    @Test
    public void formatRecentGrades_noGrades_showsNone() {
        Person person = new PersonBuilder().build();

        assertEquals("Recent grades: none", PersonCard.formatRecentGrades(person));
    }

    @Test
    public void formatRecentGrades_threeGrades_showsNewestFirst() {
        Person person = new PersonBuilder().build()
                .withGrade(new Grade("Quiz 1", new BigDecimal("80")))
                .withGrade(new Grade("Quiz 2", new BigDecimal("85")))
                .withGrade(new Grade("Quiz 3", new BigDecimal("90")));

        assertEquals("Recent grades:\nQuiz 3: 90\nQuiz 2: 85\nQuiz 1: 80",
                PersonCard.formatRecentGrades(person));
    }

    @Test
    public void formatRecentGrades_moreThanThree_showsOlderCount() {
        Person person = new PersonBuilder().build()
                .withGrade(new Grade("Quiz 1", new BigDecimal("80")))
                .withGrade(new Grade("Quiz 2", new BigDecimal("85")))
                .withGrade(new Grade("Quiz 3", new BigDecimal("90")))
                .withGrade(new Grade("Quiz 4", new BigDecimal("95")));

        assertEquals("Recent grades:\nQuiz 4: 95\nQuiz 3: 90\nQuiz 2: 85\n+1 older grade(s)",
                PersonCard.formatRecentGrades(person));
    }

}
