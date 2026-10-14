package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class GradeTest {

    @Test
    public void constructor_validGrade_success() {
        Grade grade = new Grade("Assignment 1", new BigDecimal("97.5"));

        assertEquals("Assignment 1", grade.getAssessmentName());
        assertEquals(0, grade.getScore().compareTo(new BigDecimal("97.5")));
    }

    @Test
    public void constructor_invalidAssessmentName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Grade("", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Grade("Exam_1", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Grade("a".repeat(81), BigDecimal.ONE));
    }

    @Test
    public void constructor_scoreOutsideRange_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Grade("Quiz", new BigDecimal("-0.1")));
        assertThrows(IllegalArgumentException.class, () -> new Grade("Quiz", new BigDecimal("100.1")));
    }

    @Test
    public void equals_assessmentCaseAndScoreScaleInsensitive() {
        Grade first = new Grade("Assignment 1", new BigDecimal("97.0"));
        Grade second = new Grade("assignment 1", new BigDecimal("97"));

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(new Grade("Assignment 2", new BigDecimal("97"))));
        assertFalse(first.equals(new Grade("Assignment 1", new BigDecimal("96"))));
    }

}
