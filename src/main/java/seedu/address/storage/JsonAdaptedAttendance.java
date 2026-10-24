package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Attendance;

/**
 * Jackson-friendly version of {@link Attendance}.
 */
class JsonAdaptedAttendance {

    private final String date;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedAttendance} with the given {@code date}.
     */
    @JsonCreator
    public JsonAdaptedAttendance(@JsonProperty("date") String date,
            @JsonProperty("status") String status) {
        this.date = date;
        this.status = status;
    }

    /**
     * Converts a given {@code Attendance} into this class for Jackson use.
     */
    public JsonAdaptedAttendance(Attendance source) {
        date = source.getDate().toString();
        status = source.getStatus().toString();
    }

    /**
     * Converts this Jackson-friendly adapted attendance object into the model's {@code Attendance} object.
     *
     * @throws IllegalValueException if date or status is invalid.
     */
    public Attendance toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException("Attendance date is missing.");
        }
        if (status == null) {
            throw new IllegalValueException("Attendance status is missing.");
        }

        try {
            LocalDate modelDate = LocalDate.parse(date);
            Attendance.Status modelStatus = Attendance.Status.fromString(status);
            return new Attendance(modelDate, modelStatus);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new IllegalValueException("Attendance record has an invalid date or status.");
        }
    }
}
