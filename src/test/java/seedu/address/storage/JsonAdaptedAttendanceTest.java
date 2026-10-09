package seedu.address.storage;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedAttendanceTest {

    @Test
    public void toModelType_missingDate_throwsIllegalValueException() {
        JsonAdaptedAttendance adaptedAttendance =
            new JsonAdaptedAttendance(null, "present");

        assertThrows(IllegalValueException.class, "Attendance date is missing.", adaptedAttendance::toModelType);
    }

    @Test
    public void toModelType_missingStatus_throwsIllegalValueException() {
        JsonAdaptedAttendance adaptedAttendance =
            new JsonAdaptedAttendance("2026-09-16", null);

        assertThrows(IllegalValueException.class, "Attendance status is missing.", adaptedAttendance::toModelType);
    }

}
