package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
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
    }

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

    public LocalDate getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return date + ": " + status.name().toLowerCase(Locale.ROOT);
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