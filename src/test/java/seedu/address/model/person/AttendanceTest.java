package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class AttendanceTest {

    @Test
    public void toString_validAttendance_returnsDateAndStatus() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);

        assertEquals("2026-09-16: present", attendance.toString());
    }

    @Test
    public void statusToString_allStatuses_returnsLowerCaseName() {
        assertEquals("present", Attendance.Status.PRESENT.toString());
        assertEquals("absent", Attendance.Status.ABSENT.toString());
        assertEquals("late", Attendance.Status.LATE.toString());
    }

    @Test
    public void formatDate_variousDates_returnsDayMonthYearWithFourDigitYear() {
        assertEquals("16-09-2026", Attendance.formatDate(LocalDate.of(2026, 9, 16)));
        assertEquals("01-01-0001", Attendance.formatDate(LocalDate.of(1, 1, 1)));
        assertEquals("01-01-0000", Attendance.formatDate(LocalDate.of(0, 1, 1)));
    }

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

    @Test
    public void equals_differentType_returnsFalse() {
        Attendance attendance = new Attendance(
                LocalDate.of(2026, 9, 16),
                Attendance.Status.PRESENT);

        assertFalse(attendance.equals("not an Attendance"));
    }

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
