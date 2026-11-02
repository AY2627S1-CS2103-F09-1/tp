package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class AttendanceTest {

    // EP_1: Attendance string representation includes its date and status
    @Test
    public void toString_validAttendance_returnsDateAndStatus() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);

        assertEquals("2026-09-16: present", attendance.toString());
    }

    // EP_2_1: Present status is displayed in lower case
    // EP_2_2: Absent status is displayed in lower case
    // EP_2_3: Late status is displayed in lower case
    @Test
    public void statusToString_allStatuses_returnsLowerCaseName() {
        assertEquals("present", Attendance.Status.PRESENT.toString());
        assertEquals("absent", Attendance.Status.ABSENT.toString());
        assertEquals("late", Attendance.Status.LATE.toString());
    }

    // EP_3_1: Date is formatted as day, month, and four digit year
    // EP_3_2: Single digit year is padded to four digits
    // EP_3_3: Year zero is formatted with four digits
    // Boundary Value: Year 0000
    @Test
    public void formatDate_variousDates_returnsDayMonthYearWithFourDigitYear() {
        assertEquals("16-09-2026", Attendance.formatDate(LocalDate.of(2026, 9, 16)));
        assertEquals("01-01-0001", Attendance.formatDate(LocalDate.of(1, 1, 1)));
        assertEquals("01-01-0000", Attendance.formatDate(LocalDate.of(0, 1, 1)));
    }

    // EP_4_1: An attendance object equals itself
    // EP_4_2: Attendance objects with the same date and status are equal
    @Test
    public void equals_sameValues_returnsTrue() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);
        Attendance sameAttendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);

        assertTrue(attendance.equals(attendance));
        assertTrue(attendance.equals(sameAttendance));
    }

    // EP_5: Attendance object is compared with a different type
    @Test
    public void equals_differentType_returnsFalse() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);

        assertFalse(attendance.equals("not an Attendance"));
    }

    // EP_6: Attendance objects have different dates
    @Test
    public void equals_differentDate_returnsFalse() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);
        Attendance attendanceOnDifferentDate = new Attendance(
                LocalDate.of(2026, 9, 17),
                Attendance.Status.PRESENT);

        assertFalse(attendance.equals(attendanceOnDifferentDate));
    }

    // EP_7: Attendance objects have different statuses
    @Test
    public void equals_differentStatus_returnsFalse() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);
        Attendance absentAttendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.ABSENT);

        assertFalse(attendance.equals(absentAttendance));
    }

    // EP_8: Equal attendance objects have the same hash code
    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        Attendance first = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);
        Attendance second = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);

        assertEquals(first.hashCode(), second.hashCode());
    }

}
