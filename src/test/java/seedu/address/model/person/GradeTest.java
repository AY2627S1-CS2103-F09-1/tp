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
        assertThrows(IllegalArgumentException.class, () -> new Grade("Exam,1", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Grade("a".repeat(81), BigDecimal.ONE));
    }

    @Test
    public void constructor_assessmentNameWithSymbolsAndAccents_success() {
        assertEquals("Quiz #1 (Part A)", new Grade("Quiz #1 (Part A)", BigDecimal.ONE).getAssessmentName());
        assertEquals("Lab 1/2 - Mid-term", new Grade("Lab 1/2 - Mid-term", BigDecimal.ONE).getAssessmentName());
        assertEquals("Exam_1", new Grade("Exam_1", BigDecimal.ONE).getAssessmentName());
        assertEquals("Examen Finé", new Grade("Examen Fine\u0301", BigDecimal.ONE).getAssessmentName());
    }

    @Test
    public void constructor_assessmentNameWithOtherScripts_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Grade("期中考试", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Grade("Quiz\n1", BigDecimal.ONE));
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
        assertTrue(first.equals(first));
        assertFalse(first.equals(null));
        assertFalse(first.equals("not a Grade"));
    }

    @Test
    public void isSameAssessment_extraSpaces_isSameAssessment() {
        Grade grade = new Grade("Quiz 1", new BigDecimal("97"));

        assertTrue(grade.isSameAssessment(new Grade("quiz   1", new BigDecimal("80"))));
        assertTrue(grade.isSameAssessment(new Grade("  QUIZ 1 ", new BigDecimal("80"))));
        assertFalse(grade.isSameAssessment(new Grade("Quiz1", new BigDecimal("80"))));
    }

    @Test
    public void equalsAndHashCode_extraSpacesInAssessmentName_equal() {
        Grade first = new Grade("Quiz 1", new BigDecimal("97"));
        Grade second = new Grade("quiz    1", new BigDecimal("97.0"));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void toString_assessmentNameWithExtraSpaces_keepsNameAsGiven() {
        assertEquals("quiz   1: 97", new Grade("quiz   1", new BigDecimal("97")).toString());
    }

    @Test
    public void isSameAssessment_caseInsensitiveAndNull_safe() {
        Grade grade = new Grade("Assignment 1", new BigDecimal("97"));
        Grade sameAssessment = new Grade("assignment 1", new BigDecimal("80"));
        Grade differentAssessment = new Grade("Assignment 2", new BigDecimal("97"));

        assertTrue(grade.isSameAssessment(sameAssessment));
        assertFalse(grade.isSameAssessment(differentAssessment));
        assertFalse(grade.isSameAssessment(null));
    }

    @Test
    public void toString_scoreWithTrailingZeros_omitsTrailingZeros() {
        assertEquals("Assignment 1: 97.5", new Grade("Assignment 1", new BigDecimal("97.50")).toString());
        assertEquals("Quiz: 100", new Grade("Quiz", new BigDecimal("100")).toString());
    }

}
