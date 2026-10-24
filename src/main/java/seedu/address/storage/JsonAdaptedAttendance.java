package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Attendance;

/**
 * Jackson-friendly version of {@link Attendance}.
 */
class JsonAdaptedAttendance {

    /**
     * The format {@code YYYY-MM-DD} with exactly four digits in the year, which is what
     * {@link LocalDate#toString()} writes for the dates that a command accepts. A sign or a year of more than
     * four digits is not accepted.
     */
    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);

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
            LocalDate modelDate = LocalDate.parse(date, DATE_FORMAT);
            Attendance.Status modelStatus = Attendance.Status.fromString(status);
            return new Attendance(modelDate, modelStatus);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new IllegalValueException("Attendance record has an invalid date or status.");
        }
    }
}
