package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Attendance;

public class JsonAdaptedAttendanceTest {

    @Test
    public void toModelType_missingDate_throwsIllegalValueException() {
        JsonAdaptedAttendance adaptedAttendance =
            new JsonAdaptedAttendance(null, "present");

        assertThrows(IllegalValueException.class, "Attendance date is missing.", adaptedAttendance::toModelType);
    }

    @Test
    public void toModelType_validYearZero_success() throws Exception {
        Attendance attendance = new JsonAdaptedAttendance("0000-01-01", "late").toModelType();

        assertEquals(new Attendance(LocalDate.of(0, 1, 1), Attendance.Status.LATE), attendance);
    }

    @Test
    public void toModelType_signedOrLongYear_throwsIllegalValueException() {
        for (String invalidDate : List.of("+12026-01-01", "-0001-01-01", "12026-01-01", "+2026-01-01")) {
            JsonAdaptedAttendance adaptedAttendance = new JsonAdaptedAttendance(invalidDate, "late");

            assertThrows(IllegalValueException.class, "Attendance record has an invalid date or status.",
                    adaptedAttendance::toModelType);
        }
    }

    @Test
    public void toModelType_missingStatus_throwsIllegalValueException() {
        JsonAdaptedAttendance adaptedAttendance =
            new JsonAdaptedAttendance("2026-09-16", null);

        assertThrows(IllegalValueException.class, "Attendance status is missing.", adaptedAttendance::toModelType);
    }

}
