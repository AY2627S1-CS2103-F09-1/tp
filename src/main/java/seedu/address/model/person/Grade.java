package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/** Represents a score attained by a person for an assessment. */
public class Grade {

    public static final int MAX_ASSESSMENT_NAME_LENGTH = 80;
    public static final String MESSAGE_INVALID_ASSESSMENT_NAME =
            "Assessment name must be 1 to 80 characters and contain only English letters, digits, spaces and hyphens.";
    public static final String MESSAGE_INVALID_SCORE = "Score must be a numerical value (0-100).";

    private final String assessmentName;
    private final BigDecimal score;

    /** Creates a grade after validating its assessment name and score. */
    public Grade(String assessmentName, BigDecimal score) {
        requireNonNull(assessmentName);
        requireNonNull(score);
        String trimmedAssessmentName = assessmentName.trim();
        if (getAssessmentNameConstraintViolation(trimmedAssessmentName).isPresent()) {
            throw new IllegalArgumentException(MESSAGE_INVALID_ASSESSMENT_NAME);
        }
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(MESSAGE_INVALID_SCORE);
        }
        this.assessmentName = trimmedAssessmentName;
        this.score = score;
    }

    /** Returns a constraint violation for the assessment name, if any. */
    public static Optional<String> getAssessmentNameConstraintViolation(String assessmentName) {
        requireNonNull(assessmentName);
        if (assessmentName.isEmpty() || assessmentName.length() > MAX_ASSESSMENT_NAME_LENGTH
                || !assessmentName.matches("[A-Za-z0-9 -]+")) {
            return Optional.of(MESSAGE_INVALID_ASSESSMENT_NAME);
        }
        return Optional.empty();
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public BigDecimal getScore() {
        return score;
    }

    /** Returns whether this record and {@code other} refer to the same assessment, ignoring case. */
    public boolean isSameAssessment(Grade other) {
        return other != null && assessmentName.equalsIgnoreCase(other.assessmentName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Grade otherGrade)) {
            return false;
        }
        return assessmentName.equalsIgnoreCase(otherGrade.assessmentName)
                && score.compareTo(otherGrade.score) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentName.toLowerCase(Locale.ROOT), score.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return assessmentName + ": " + score.stripTrailingZeros().toPlainString();
    }

}
