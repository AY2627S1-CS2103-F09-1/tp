package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentTokenizerTest {

    private final Prefix unknownPrefix = new Prefix("--u");
    private final Prefix pSlash = new Prefix("p/");
    private final Prefix dashT = new Prefix("-t");
    private final Prefix hatQ = new Prefix("^Q");

    @Test
    public void tokenize_emptyArgsString_noValues() {
        String argsString = "  ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);

        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);
    }

    private void assertPreamblePresent(ArgumentMultimap argMultimap, String expectedPreamble) {
        assertEquals(expectedPreamble, argMultimap.getPreamble());
    }

    private void assertPreambleEmpty(ArgumentMultimap argMultimap) {
        assertTrue(argMultimap.getPreamble().isEmpty());
    }

    /**
     * Asserts all the arguments in {@code argMultimap} with {@code prefix} match the {@code expectedValues}
     * and only the last value is returned upon calling {@code ArgumentMultimap#getValue(Prefix)}.
     */
    private void assertArgumentPresent(ArgumentMultimap argMultimap, Prefix prefix, String... expectedValues) {

        // Verify the last value is returned
        assertEquals(expectedValues[expectedValues.length - 1], argMultimap.getValue(prefix).get());

        // Verify the number of values returned is as expected
        assertEquals(expectedValues.length, argMultimap.getAllValues(prefix).size());

        // Verify all values returned are as expected and in order
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], argMultimap.getAllValues(prefix).get(i));
        }
    }

    private void assertArgumentAbsent(ArgumentMultimap argMultimap, Prefix prefix) {
        assertFalse(argMultimap.getValue(prefix).isPresent());
    }

    @Test
    public void tokenize_noPrefixes_allTakenAsPreamble() {
        String argsString = "  some random string /t tag with leading and trailing spaces ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString);

        // Same string expected as preamble, but leading/trailing spaces should be trimmed
        assertPreamblePresent(argMultimap, argsString.trim());

    }

    @Test
    public void tokenize_oneArgument() {
        // Preamble present
        String argsString = "  Some preamble string p/ Argument value ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreamblePresent(argMultimap, "Some preamble string");
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

        // No preamble
        argsString = " p/   Argument value ";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash);
        assertPreambleEmpty(argMultimap);
        assertArgumentPresent(argMultimap, pSlash, "Argument value");

    }

    @Test
    public void tokenize_multipleArguments() {
        // Only two arguments are present
        String argsString = "SomePreambleString -t dashT-Value p/ pSlash value";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentAbsent(argMultimap, hatQ);

        // All three arguments are present
        argsString = "Different Preamble String ^Q 111 -t dashT-Value p/ pSlash value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "Different Preamble String");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value");
        assertArgumentPresent(argMultimap, hatQ, "111");

        /* Also covers: Reusing of the tokenizer multiple times */

        // Reuse tokenizer on an empty string to ensure ArgumentMultimap is correctly reset
        // (i.e. no stale values from the previous tokenizing remain)
        argsString = "";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreambleEmpty(argMultimap);
        assertArgumentAbsent(argMultimap, pSlash);

        /* Also covers: testing for prefixes not specified as a prefix */

        // Prefixes not previously given to the tokenizer should not return any values
        argsString = unknownPrefix + "some value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertArgumentAbsent(argMultimap, unknownPrefix);
        assertPreamblePresent(argMultimap, argsString); // Unknown prefix is taken as part of preamble
    }

    @Test
    public void tokenize_multipleArgumentsWithRepeats() {
        // Two arguments repeated, some have empty values
        String argsString = "SomePreambleString -t dashT-Value ^Q ^Q -t another dashT value p/ pSlash value -t";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value", "another dashT value", "");
        assertArgumentPresent(argMultimap, hatQ, "", "");
    }

    @Test
    public void tokenize_multipleArgumentsJoined() {
        String argsString = "SomePreambleStringp/ pSlash joined-tjoined -t not joined^Qjoined";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleStringp/ pSlash joined-tjoined");
        assertArgumentAbsent(argMultimap, pSlash);
        assertArgumentPresent(argMultimap, dashT, "not joined^Qjoined");
        assertArgumentAbsent(argMultimap, hatQ);
    }

    @Test
    public void tokenize_prefixNotFollowedByWhitespace_notRecognized() {
        Prefix slashName = new Prefix("/name");

        // Prefix is only the start of a longer token
        String argsString = " /names John";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, slashName);
        assertPreamblePresent(argMultimap, "/names John");
        assertArgumentAbsent(argMultimap, slashName);

        // Value joined directly to the prefix
        argsString = " /nameJohn";
        argMultimap = ArgumentTokenizer.tokenize(argsString, slashName);
        assertPreamblePresent(argMultimap, "/nameJohn");
        assertArgumentAbsent(argMultimap, slashName);
    }

    @Test
    public void tokenize_invalidOccurrenceBeforeValidOccurrence_validOccurrenceRecognized() {
        Prefix slashName = new Prefix("/name");

        String argsString = " /names Ignored /name John";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, slashName);
        assertPreamblePresent(argMultimap, "/names Ignored");
        assertArgumentPresent(argMultimap, slashName, "John");
    }

    @Test
    public void tokenize_prefixFollowedByWhitespaceOrEnd_recognized() {
        Prefix slashName = new Prefix("/name");

        // Followed by a tab
        String argsString = " /name\tJohn";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, slashName);
        assertPreambleEmpty(argMultimap);
        assertArgumentPresent(argMultimap, slashName, "John");

        // At the end of the arguments string, with an empty value
        argsString = " /name";
        argMultimap = ArgumentTokenizer.tokenize(argsString, slashName);
        assertPreambleEmpty(argMultimap);
        assertArgumentPresent(argMultimap, slashName, "");
    }

    @Test
    public void findUnrecognizedPrefixes_onlyRecognizedPrefixes_returnsEmpty() {
        Prefix slashName = new Prefix("/name");
        Prefix slashClass = new Prefix("/class");

        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes("", slashName, slashClass).isEmpty());
        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes(" /name John /class A1", slashName, slashClass)
                .isEmpty());
    }

    @Test
    public void findUnrecognizedPrefixes_unknownPrefix_returnsUnknownPrefixes() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /name John /phone 123", slashName));

        // No known prefixes given, so every prefix-like token is unrecognized
        assertEquals(List.of("/name", "/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /name John /phone 123"));
    }

    @Test
    public void findUnrecognizedPrefixes_multipleUnknownPrefixes_returnsInOrderWithRepeats() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/phone", "/address", "/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(
                        " /phone 1 /name John /address x /phone 2", slashName));
    }

    @Test
    public void findUnrecognizedPrefixes_prefixJoinedToOtherText_returnsWholeToken() {
        Prefix slashName = new Prefix("/name");

        // Longer token that merely starts with a known prefix
        assertEquals(List.of("/names"), ArgumentTokenizer.findUnrecognizedPrefixes(" /names John", slashName));

        // Value joined directly to the prefix
        assertEquals(List.of("/nameJohn"), ArgumentTokenizer.findUnrecognizedPrefixes(" /nameJohn", slashName));
    }

    @Test
    public void findUnrecognizedPrefixes_caseDiffers_returnsEmptyList() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of(), ArgumentTokenizer.findUnrecognizedPrefixes(" /Name John /NAME Tan", slashName));
    }

    @Test
    public void findUnrecognizedPrefixes_caseDiffersInTokenWithOtherText_returnsWholeToken() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/NAMES"), ArgumentTokenizer.findUnrecognizedPrefixes(" /NAMES John", slashName));
    }

    @Test
    public void findUnrecognizedPrefixes_letterOfAnotherLanguage_returnsToken() {
        // the Kelvin sign and the dotless i fold to English letters in some Unicode rules, but are not matched
        assertEquals(List.of("/mar\u212A"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /mar\u212A", new Prefix("/mark")));
        assertEquals(List.of("/t\u0131p"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /t\u0131p", new Prefix("/tip")));
    }

    @Test
    public void tokenize_prefixInAnyCase_valueIsStoredUnderGivenPrefix() {
        Prefix slashName = new Prefix("/name");
        Prefix slashClass = new Prefix("/class");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("pre /NAME John /Class A1 /nAmE Tan",
                slashName, slashClass);

        assertPreamblePresent(argMultimap, "pre");
        assertArgumentPresent(argMultimap, slashName, "John", "Tan");
        assertArgumentPresent(argMultimap, slashClass, "A1");
    }

    @Test
    public void tokenize_prefixInDifferentCaseButJoinedToText_isNotAPrefix() {
        Prefix slashName = new Prefix("/name");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /NAMEJohn /Names Tan", slashName);

        assertArgumentAbsent(argMultimap, slashName);
        assertPreamblePresent(argMultimap, "/NAMEJohn /Names Tan");
    }

    @Test
    public void findUnrecognizedPrefixes_loneSlash_returnsToken() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/"), ArgumentTokenizer.findUnrecognizedPrefixes(" /name Tan / Kumar", slashName));
    }

    @Test
    public void findUnrecognizedPrefixes_slashNotAtStartOfToken_returnsEmpty() {
        Prefix slashName = new Prefix("/name");

        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes(" /name Tan s/o Kumar", slashName).isEmpty());
        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes(" /name John/Doe", slashName).isEmpty());
    }

    @Test
    public void findUnrecognizedPrefixes_nonSpaceWhitespaceBeforeToken_returnsToken() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /name John\t/phone 123", slashName));
    }

    @Test
    public void tokenize_aliasOfPrefix_valueIsStoredUnderFullPrefix() {
        Prefix slashName = new Prefix("/name", "/n");
        Prefix slashClass = new Prefix("/class", "/c");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("pre /n John Tan /c A1", slashName, slashClass);

        assertPreamblePresent(argMultimap, "pre");
        assertArgumentPresent(argMultimap, slashName, "John Tan");
        assertArgumentPresent(argMultimap, slashClass, "A1");
    }

    @Test
    public void tokenize_fullFormAndAliasOfSamePrefix_valuesAreCollectedTogether() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /name John /n Tan", slashName);

        assertArgumentPresent(argMultimap, slashName, "John", "Tan");
        assertThrows(ParseException.class, () -> argMultimap.verifyNoDuplicatePrefixesFor(slashName));
    }

    @Test
    public void tokenize_aliasInAnyCase_valueIsStoredUnderFullPrefix() {
        Prefix slashName = new Prefix("/name", "/n");
        Prefix slashClass = new Prefix("/class", "/c");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /N John /C A1", slashName, slashClass);

        assertArgumentPresent(argMultimap, slashName, "John");
        assertArgumentPresent(argMultimap, slashClass, "A1");
    }

    @Test
    public void tokenize_aliasWithoutValue_valueIsEmpty() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /n", slashName);

        assertArgumentPresent(argMultimap, slashName, "");
    }

    @Test
    public void tokenize_fullFormOfAnotherPrefix_isNotReadAsAlias() {
        Prefix slashClass = new Prefix("/class", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /name John", slashClass);

        assertPreamblePresent(argMultimap, "/name John");
        assertArgumentAbsent(argMultimap, slashClass);
    }

    @Test
    public void tokenize_aliasJoinedToText_isNotAPrefix() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("x/n John", slashName);

        assertPreamblePresent(argMultimap, "x/n John");
        assertArgumentAbsent(argMultimap, slashName);
    }

    @Test
    public void tokenize_aliasOfPrefixThatIsNotGiven_isNotRecognized() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /c A1", slashName);

        assertPreamblePresent(argMultimap, "/c A1");
        assertArgumentAbsent(argMultimap, slashName);
    }

    @Test
    public void findUnrecognizedPrefixes_aliasOfKnownPrefix_isKnown() {
        Prefix slashName = new Prefix("/name", "/n");
        Prefix slashClass = new Prefix("/class", "/c");

        assertEquals(List.of("/x"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /n John /C A1 /x 1", slashName, slashClass));
    }

    @Test
    public void findUnrecognizedPrefixes_aliasOfPrefixThatIsNotKnown_returnsToken() {
        Prefix slashName = new Prefix("/name", "/n");

        assertEquals(List.of("/c"), ArgumentTokenizer.findUnrecognizedPrefixes(" /n John /c A1", slashName));
    }

    @Test
    public void equalsMethod() {
        Prefix aaa = new Prefix("aaa");

        assertEquals(aaa, aaa);
        assertEquals(aaa, new Prefix("aaa"));

        assertNotEquals(aaa, "aaa");
        assertNotEquals(aaa, new Prefix("aab"));
    }

}
