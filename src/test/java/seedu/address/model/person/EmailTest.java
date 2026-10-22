package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void constructor_wrongFormat_throwsIllegalArgumentExceptionWithConstraintsMessage() {
        assertThrows(IllegalArgumentException.class, Email.MESSAGE_CONSTRAINTS, () -> new Email("example.com"));
    }

    @Test
    public void constructor_tooLong_throwsIllegalArgumentExceptionWithTooLongMessage() {
        String tooLongEmail = "a".repeat(Email.MAX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, Email.MESSAGE_TOO_LONG, () -> new Email(tooLongEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void isValidEmail_lengthAroundLimit_onlyLongerThanLimitIsInvalid() {
        String domain = "@example.com";
        String atLimit = "a".repeat(Email.MAX_LENGTH - domain.length()) + domain;

        assertEquals(Email.MAX_LENGTH, atLimit.length());
        assertTrue(Email.isValidEmail(atLimit));
        assertFalse(Email.isValidEmail("a" + atLimit));
    }

    @Test
    public void isValidEmail_longInvalidInput_returnsQuickly() {
        // Matching the format against input this long takes several seconds, so it must not be attempted
        String longInvalidEmail = "a@" + "a".repeat(5000) + "!";

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertFalse(Email.isValidEmail(longInvalidEmail)));
    }

    @Test
    public void getConstraintViolation_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Email.getConstraintViolation(null));
    }

    @Test
    public void getConstraintViolation_validEmail_returnsEmpty() {
        assertEquals(Optional.empty(), Email.getConstraintViolation("valid@email"));
    }

    @Test
    public void getConstraintViolation_wrongFormat_returnsConstraintsMessage() {
        assertEquals(Optional.of(Email.MESSAGE_CONSTRAINTS), Email.getConstraintViolation(""));
        assertEquals(Optional.of(Email.MESSAGE_CONSTRAINTS), Email.getConstraintViolation("example.com"));
    }

    @Test
    public void getConstraintViolation_tooLong_returnsTooLongMessage() {
        // A too long email is reported as too long even though its format is also wrong
        assertEquals(Optional.of(Email.MESSAGE_TOO_LONG),
                Email.getConstraintViolation("a".repeat(Email.MAX_LENGTH + 1)));
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
