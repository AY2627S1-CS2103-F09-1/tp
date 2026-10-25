package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a person's attendance on one date.
 * Guarantees: immutable; date is not null and status is present, absent, or late.
 */
public class Attendance {

    /** The attendance statuses accepted by the application. */
    public enum Status {
        PRESENT, ABSENT, LATE;

        /**
         * Parses a status ignoring capitalisation and leading/trailing whitespace.
         *
         * @throws IllegalArgumentException if the value is not a supported status.
         */
        public static Status fromString(String value) {
            requireNonNull(value);
            try {
                return Status.valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid attendance status. Use present, absent, or late", e);
            }
        }

        /**
         * Returns the status in lower case, as it is typed by the user and shown in messages.
         */
        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * The format {@code DD-MM-YYYY} of a date that is typed into a command and shown in the messages of a
     * command. Every part has a fixed number of digits, so a sign or a year of more than four digits is not
     * accepted.
     */
    public static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);

    /** Dates before this year are most likely typos, so recording one shows a warning. */
    public static final int EARLIEST_EXPECTED_YEAR = 2000;

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-uuuu", Locale.ENGLISH);

    private final LocalDate date;
    private final Status status;

    /**
     * Creates an attendance record.
     *
     * @param date date of the attendance
     * @param status attendance status
     */
    public Attendance(LocalDate date, Status status) {
        requireNonNull(date);
        requireNonNull(status);
        this.date = date;
        this.status = status;
    }

    /**
     * Returns {@code date} in the format {@link #DATE_FORMAT}, the same format in which a date is typed.
     */
    public static String formatDate(LocalDate date) {
        requireNonNull(date);
        return DATE_FORMAT.format(date);
    }

    /**
     * Returns this attendance record in a format suitable for display.
     */
    public String toDisplayString() {
        return DISPLAY_DATE_FORMAT.format(date) + "  " + status;
    }

    public LocalDate getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return date + ": " + status;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Attendance otherAttendance)) {
            return false;
        }
        return date.equals(otherAttendance.date) && status == otherAttendance.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, status);
    }

}
