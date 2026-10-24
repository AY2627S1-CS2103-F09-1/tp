package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.StringUtil;

/** Represents a score attained by a person for an assessment. */
public class Grade {

    public static final int MAX_ASSESSMENT_NAME_LENGTH = 80;
    public static final String MESSAGE_INVALID_ASSESSMENT_NAME = "Assessment name must be 1 to 80 characters "
            + "and can only contain English letters (accented letters are allowed), digits, spaces and the "
            + "symbols on a standard keyboard, except commas.";
    public static final String MESSAGE_INVALID_SCORE = "Score must be a numerical value (0-100).";
    public static final BigDecimal MIN_SCORE = BigDecimal.ZERO;
    public static final BigDecimal MAX_SCORE = BigDecimal.valueOf(100);

    private final String assessmentName;
    private final String assessmentKey;
    private final BigDecimal score;

    /** Creates a grade after validating its assessment name and score. */
    public Grade(String assessmentName, BigDecimal score) {
        requireNonNull(assessmentName);
        requireNonNull(score);
        String normalizedAssessmentName = StringUtil.toNormalizedForm(assessmentName).trim();
        if (getAssessmentNameConstraintViolation(normalizedAssessmentName).isPresent()) {
            throw new IllegalArgumentException(MESSAGE_INVALID_ASSESSMENT_NAME);
        }
        if (!isValidScore(score)) {
            throw new IllegalArgumentException(MESSAGE_INVALID_SCORE);
        }
        this.assessmentName = normalizedAssessmentName;
        this.assessmentKey = StringUtil.toComparisonKey(normalizedAssessmentName);
        this.score = score;
    }

    /**
     * Returns true if {@code score} is between {@link #MIN_SCORE} and {@link #MAX_SCORE}, inclusive.
     */
    public static boolean isValidScore(BigDecimal score) {
        requireNonNull(score);
        return score.compareTo(MIN_SCORE) >= 0 && score.compareTo(MAX_SCORE) <= 0;
    }

    /**
     * Returns a constraint violation for the assessment name, if any.
     * An assessment name can contain the same characters as a {@link Name}.
     */
    public static Optional<String> getAssessmentNameConstraintViolation(String assessmentName) {
        requireNonNull(assessmentName);
        String normalizedAssessmentName = StringUtil.toNormalizedForm(assessmentName);
        boolean isTooLong = normalizedAssessmentName.length() > MAX_ASSESSMENT_NAME_LENGTH;
        if (normalizedAssessmentName.isEmpty() || isTooLong
                || !normalizedAssessmentName.matches(Name.VALIDATION_REGEX)) {
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

    /**
     * Returns whether this record refers to {@code assessmentName}, ignoring case and extra spaces.
     */
    public boolean hasAssessmentName(String assessmentName) {
        return assessmentName != null
                && assessmentKey.equals(StringUtil.toComparisonKey(assessmentName));
    }

    /**
     * Returns whether this record and {@code other} refer to the same assessment, ignoring case and
     * extra spaces.
     */
    public boolean isSameAssessment(Grade other) {
        return other != null && assessmentKey.equals(other.assessmentKey);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Grade otherGrade)) {
            return false;
        }
        return assessmentKey.equals(otherGrade.assessmentKey)
                && score.compareTo(otherGrade.score) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentKey, score.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return assessmentName + ": " + score.stripTrailingZeros().toPlainString();
    }

}
