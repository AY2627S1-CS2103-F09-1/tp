package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Grade;

public class JsonAdaptedGradeTest {

    @Test
    public void constructor_validFields_convertsToModelGrade() throws Exception {
        JsonAdaptedGrade adaptedGrade = new JsonAdaptedGrade("Assignment 1", new BigDecimal("97.5"));

        assertEquals(new Grade("Assignment 1", new BigDecimal("97.5")), adaptedGrade.toModelType());
    }

    @Test
    public void toModelType_missingAssessmentName_throwsIllegalValueException() {
        JsonAdaptedGrade adaptedGrade = new JsonAdaptedGrade(null, BigDecimal.TEN);

        IllegalValueException exception = assertThrows(
                IllegalValueException.class, adaptedGrade::toModelType);

        assertEquals("Grade assessment name is missing.", exception.getMessage());
    }

    @Test
    public void toModelType_missingScore_throwsIllegalValueException() {
        JsonAdaptedGrade adaptedGrade = new JsonAdaptedGrade("Quiz", null);

        IllegalValueException exception = assertThrows(
                IllegalValueException.class, adaptedGrade::toModelType);

        assertEquals("Grade score is missing.", exception.getMessage());
    }

    @Test
    public void toModelType_invalidGrade_throwsIllegalValueException() {
        JsonAdaptedGrade adaptedGrade = new JsonAdaptedGrade("Exam,1", BigDecimal.TEN);

        IllegalValueException exception = assertThrows(
                IllegalValueException.class, adaptedGrade::toModelType);

        assertEquals("Grade assessment name or score is invalid.", exception.getMessage());
    }

}
