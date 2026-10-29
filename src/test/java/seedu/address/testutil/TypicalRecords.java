package seedu.address.testutil;

import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;
import java.time.LocalDate;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Contains the attendance records and grades, and the contacts that have them, that the tests of the commands
 * that delete attendance records and grades have in common.
 */
public class TypicalRecords {

    public static final LocalDate DATE = LocalDate.of(2026, 9, 16);
    public static final LocalDate OTHER_DATE = LocalDate.of(2026, 9, 17);
    public static final Attendance ATTENDANCE = new Attendance(DATE, Attendance.Status.ABSENT);
    public static final Attendance OTHER_ATTENDANCE = new Attendance(OTHER_DATE, Attendance.Status.PRESENT);

    public static final Grade GRADE = new Grade("Quiz 1", new BigDecimal("90.50"));
    public static final Grade OTHER_GRADE = new Grade("Exam", new BigDecimal("80"));

    public static final Person ALICE_WITH_ATTENDANCE = ALICE.withAttendance(ATTENDANCE)
            .withAttendance(OTHER_ATTENDANCE);
    public static final Person ALICE_WITH_GRADES = ALICE.withGrade(GRADE).withGrade(OTHER_GRADE);

    private TypicalRecords() {} // prevents instantiation

    /**
     * Returns a model with the typical contacts, in which Alice has {@link #ATTENDANCE} and
     * {@link #OTHER_ATTENDANCE}.
     */
    public static Model getModelWithAliceAttendance() {
        return getModelWithAliceReplacedBy(ALICE_WITH_ATTENDANCE);
    }

    /**
     * Returns a model with the typical contacts, in which Alice has {@link #GRADE} and {@link #OTHER_GRADE}.
     */
    public static Model getModelWithAliceGrades() {
        return getModelWithAliceReplacedBy(ALICE_WITH_GRADES);
    }

    private static Model getModelWithAliceReplacedBy(Person alice) {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPerson(ALICE, alice);
        return model;
    }
}
