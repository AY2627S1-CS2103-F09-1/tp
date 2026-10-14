package seedu.address.storage;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Grade;

/**
 * Jackson-friendly version of {@link Grade}.
 */
class JsonAdaptedGrade {

    private final String assessmentName;
    private final BigDecimal score;

    /**
     * Constructs a {@code JsonAdaptedGrade} with the given assessment name and score.
     */
    @JsonCreator
    public JsonAdaptedGrade(@JsonProperty("assessmentName") String assessmentName,
            @JsonProperty("score") BigDecimal score) {
        this.assessmentName = assessmentName;
        this.score = score;
    }

    /**
     * Converts a given {@code Grade} into this class for Jackson use.
     */
    public JsonAdaptedGrade(Grade source) {
        assessmentName = source.getAssessmentName();
        score = source.getScore();
    }

    /**
     * Converts this adapted grade into the model's {@code Grade} object.
     *
     * @throws IllegalValueException if the assessment name or score is missing or invalid
     */
    public Grade toModelType() throws IllegalValueException {
        if (assessmentName == null) {
            throw new IllegalValueException("Grade assessment name is missing.");
        }
        if (score == null) {
            throw new IllegalValueException("Grade score is missing.");
        }

        try {
            return new Grade(assessmentName, score);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException("Grade assessment name or score is invalid.", e);
        }
    }

}
