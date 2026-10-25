package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StringUtilTest {

    //---------------- Tests for isNonZeroUnsignedInteger --------------------------------------

    @Test
    public void isNonZeroUnsignedInteger_variousInputs_returnsExpected() {

        // EP: empty strings
        assertFalse(StringUtil.isNonZeroUnsignedInteger("")); // Boundary value
        assertFalse(StringUtil.isNonZeroUnsignedInteger("  "));

        // EP: not a number
        assertFalse(StringUtil.isNonZeroUnsignedInteger("a"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("aaa"));

        // EP: zero
        assertFalse(StringUtil.isNonZeroUnsignedInteger("0"));

        // EP: zero as prefix
        assertTrue(StringUtil.isNonZeroUnsignedInteger("01"));

        // EP: signed numbers
        assertFalse(StringUtil.isNonZeroUnsignedInteger("-1"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("+1"));

        // EP: numbers with white space
        assertFalse(StringUtil.isNonZeroUnsignedInteger(" 10 ")); // Leading/trailing spaces
        assertFalse(StringUtil.isNonZeroUnsignedInteger("1 0")); // Spaces in the middle

        // EP: number larger than Integer.MAX_VALUE
        assertFalse(StringUtil.isNonZeroUnsignedInteger(Long.toString(Integer.MAX_VALUE + 1)));

        // EP: valid numbers, should return true
        assertTrue(StringUtil.isNonZeroUnsignedInteger("1")); // Boundary value
        assertTrue(StringUtil.isNonZeroUnsignedInteger("10"));
    }

    //---------------- Tests for getDetails --------------------------------------

    /*
     * Equivalence Partitions: null, valid throwable object
     */

    @Test
    public void toAsciiLowerCase_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.toAsciiLowerCase(null));
    }

    @Test
    public void toAsciiLowerCase_variousInputs_onlyEnglishLettersChange() {
        assertEquals("", StringUtil.toAsciiLowerCase(""));
        assertEquals("viewa /name", StringUtil.toAsciiLowerCase("ViewA /NAME"));
        assertEquals("s/o-1 (a)", StringUtil.toAsciiLowerCase("S/O-1 (A)")); // symbols and digits kept

        // letters of other languages are kept as they are, whatever Unicode says about their case
        assertEquals("ÉİK", StringUtil.toAsciiLowerCase("ÉİK"));
        assertEquals("李明", StringUtil.toAsciiLowerCase("李明"));
    }

    @Test
    public void toAsciiLowerCase_variousInputs_lengthIsKept() {
        String text = "İSTANBUL Kelvin CafÉ";
        assertEquals(text.length(), StringUtil.toAsciiLowerCase(text).length());
    }

    @Test
    public void toComparisonKey_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.toComparisonKey(null));
    }

    @Test
    public void toComparisonKey_variousInputs_correctKey() {
        assertEquals("", StringUtil.toComparisonKey(""));
        assertEquals("", StringUtil.toComparisonKey("   "));
        assertEquals("john tan", StringUtil.toComparisonKey("john tan")); // already a key
        assertEquals("john tan", StringUtil.toComparisonKey("John TAN")); // case
        assertEquals("john tan", StringUtil.toComparisonKey("  john tan  ")); // leading/trailing whitespace
        assertEquals("john tan", StringUtil.toComparisonKey("john   \t tan")); // repeated whitespace
        assertEquals("s/o-1", StringUtil.toComparisonKey("S/O-1")); // symbols kept
    }

    @Test
    public void toComparisonKey_fullWidthLetters_sameKeyAsAsciiLetters() {
        assertEquals("quiz 1", StringUtil.toComparisonKey("\uff31uiz 1")); // full-width Q
        assertEquals("alex yeoh", StringUtil.toComparisonKey("\uff21\uff2c\uff25\uff38 Yeoh")); // full-width ALEX
        assertEquals("a1", StringUtil.toComparisonKey("A\uff11")); // full-width digit 1
        assertEquals("alex yeoh", StringUtil.toComparisonKey("Alex\u00a0Yeoh")); // no-break space
    }

    @Test
    public void compareNaturally_numbersInText_comparedByValue() {
        assertTrue(StringUtil.compareNaturally("Team 2", "Team 10") < 0);
        assertTrue(StringUtil.compareNaturally("Week 11", "Week 9") > 0);
        assertTrue(StringUtil.compareNaturally("Team 02", "Team 10") < 0);
        assertTrue(StringUtil.compareNaturally("A9", "A10") < 0);
        assertTrue(StringUtil.compareNaturally("9" + "0".repeat(30), "1" + "0".repeat(31)) < 0);
    }

    @Test
    public void compareNaturally_separatorsAndCase_doNotChangeTheLetterOrder() {
        assertTrue(StringUtil.compareNaturally("Group A", "Group B") < 0);
        assertTrue(StringUtil.compareNaturally("Group-A", "Group B") < 0);
        assertTrue(StringUtil.compareNaturally("Group_A", "Group B") < 0);
        assertTrue(StringUtil.compareNaturally("group a", "Group B") < 0);
        assertTrue(StringUtil.compareNaturally("Team", "Team 1") < 0);
    }

    @Test
    public void compareNaturally_sameUnderTheRules_stillHaveAFixedOrder() {
        assertEquals(0, StringUtil.compareNaturally("Team 1", "Team 1"));
        assertNotEquals(0, StringUtil.compareNaturally("team 1", "Team 1"));
        assertNotEquals(0, StringUtil.compareNaturally("Group-A", "Group A"));
        assertEquals(-Integer.signum(StringUtil.compareNaturally("Team 07", "Team 7")),
                Integer.signum(StringUtil.compareNaturally("Team 7", "Team 07")));
    }

    @Test
    public void compareNaturally_mixedLabels_sortsAsAPersonWould() {
        List<String> labels = new ArrayList<>(List.of("Team 10", "Group_A", "Team 2", "team 1", "Group B",
                "Group-A", "Group A", "Week 9", "Week 11"));

        labels.sort(StringUtil::compareNaturally);

        assertEquals(List.of("Group A", "Group-A", "Group_A", "Group B", "team 1", "Team 2", "Team 10", "Week 9",
                "Week 11"), labels);
    }

    @Test
    public void compareNaturally_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.compareNaturally(null, "a"));
        assertThrows(NullPointerException.class, () -> StringUtil.compareNaturally("a", null));
    }

    @Test
    public void getDetails_exceptionGiven_containsExceptionAndMessage() {
        assertTrue(StringUtil.getDetails(new FileNotFoundException("file not found"))
            .contains("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.getDetails(null));
    }

}
