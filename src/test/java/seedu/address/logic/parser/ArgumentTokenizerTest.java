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

    // EP_1: Empty or whitespace only argument string produces no values
    // Boundary Value: empty string / whitespace only string
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

    // EP_2: No recognized prefixes are supplied
    @Test
    public void tokenize_noPrefixes_allTakenAsPreamble() {
        String argsString = "  some random string /t tag with leading and trailing spaces ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString);

        // Same string expected as preamble, but leading/trailing spaces should be trimmed
        assertPreamblePresent(argMultimap, argsString.trim());

    }

    // EP_3_1: One recognized argument is extracted after a preamble
    // EP_3_2: One recognized argument is extracted with no preamble
    @Test
    public void tokenize_oneArgument_extractsPreambleAndArgument() {
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

    // EP_4_1: Multiple different recognized arguments are extracted
    // EP_4_2: An omitted prefix has no value
    // EP_4_3: Reusing the tokenizer does not retain values from the previous call
    // EP_4_4: A prefix not supplied to the tokenizer remains part of the preamble
    @Test
    public void tokenize_multipleArguments_extractsEachArgument() {
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

    // EP_5: Repeated prefixes retain every value, including empty values
    @Test
    public void tokenize_repeatedArguments_keepsEveryValue() {
        // Two arguments repeated, some have empty values
        String argsString = "SomePreambleString -t dashT-Value ^Q ^Q -t another dashT value p/ pSlash value -t";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleString");
        assertArgumentPresent(argMultimap, pSlash, "pSlash value");
        assertArgumentPresent(argMultimap, dashT, "dashT-Value", "another dashT value", "");
        assertArgumentPresent(argMultimap, hatQ, "", "");
    }

    // EP_6: A prefix joined to surrounding text is not recognized as a separate argument
    @Test
    public void tokenize_joinedArguments_doesNotRecognizeThem() {
        String argsString = "SomePreambleStringp/ pSlash joined-tjoined -t not joined^Qjoined";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, pSlash, dashT, hatQ);
        assertPreamblePresent(argMultimap, "SomePreambleStringp/ pSlash joined-tjoined");
        assertArgumentAbsent(argMultimap, pSlash);
        assertArgumentPresent(argMultimap, dashT, "not joined^Qjoined");
        assertArgumentAbsent(argMultimap, hatQ);
    }

    // EP_7_1: A longer token beginning with a known prefix is not recognized
    // EP_7_2: A value joined directly to a known prefix is not recognized
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

    // EP_7_3: An invalid prefix like token before a valid prefix does not prevent the valid one from being recognized
    @Test
    public void tokenize_invalidOccurrenceBeforeValidOccurrence_validOccurrenceRecognized() {
        Prefix slashName = new Prefix("/name");

        String argsString = " /names Ignored /name John";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, slashName);
        assertPreamblePresent(argMultimap, "/names Ignored");
        assertArgumentPresent(argMultimap, slashName, "John");
    }

    // EP_8_1: A prefix followed by a tab is recognized
    // EP_8_2: A prefix at the end of the input is recognized with an empty value
    // Boundary Value: prefix followed by whitespace / prefix at end of input
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

    // EP_9: Input containing only recognized prefixes has no unrecognized prefixes
    @Test
    public void findUnrecognizedPrefixes_onlyRecognizedPrefixes_returnsEmpty() {
        Prefix slashName = new Prefix("/name");
        Prefix slashClass = new Prefix("/class");

        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes("", slashName, slashClass).isEmpty());
        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes(" /name John /class A1", slashName, slashClass)
                .isEmpty());
    }

    // EP_10: Unknown prefix like tokens are returned as unrecognized
    @Test
    public void findUnrecognizedPrefixes_unknownPrefix_returnsUnknownPrefixes() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /name John /phone 123", slashName));

        // No known prefixes given, so every prefix-like token is unrecognized
        assertEquals(List.of("/name", "/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /name John /phone 123"));
    }

    // EP_11: Multiple unknown prefixes are returned in encounter order, including repeats
    @Test
    public void findUnrecognizedPrefixes_multipleUnknownPrefixes_returnsInOrderWithRepeats() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/phone", "/address", "/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(
                        " /phone 1 /name John /address x /phone 2", slashName));
    }

    // EP_12_1: A longer token beginning with a known prefix is returned as a whole unknown token
    // EP_12_2: A value joined directly to a known prefix is returned as a whole unknown token
    @Test
    public void findUnrecognizedPrefixes_prefixJoinedToOtherText_returnsWholeToken() {
        Prefix slashName = new Prefix("/name");

        // Longer token that merely starts with a known prefix
        assertEquals(List.of("/names"), ArgumentTokenizer.findUnrecognizedPrefixes(" /names John", slashName));

        // Value joined directly to the prefix
        assertEquals(List.of("/nameJohn"), ArgumentTokenizer.findUnrecognizedPrefixes(" /nameJohn", slashName));
    }

    // EP_13: Known prefixes are matched without regard to capitalization
    @Test
    public void findUnrecognizedPrefixes_caseDiffers_returnsEmptyList() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of(), ArgumentTokenizer.findUnrecognizedPrefixes(" /Name John /NAME Tan", slashName));
    }

    // EP_14: A token with extra characters after a differently capitalized prefix is still unrecognized
    @Test
    public void findUnrecognizedPrefixes_caseDiffersInTokenWithOtherText_returnsWholeToken() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/NAMES"), ArgumentTokenizer.findUnrecognizedPrefixes(" /NAMES John", slashName));
    }

    // EP_15: Prefix matching does not treat visually similar letters from other languages as English letters
    @Test
    public void findUnrecognizedPrefixes_letterOfAnotherLanguage_returnsToken() {
        // the Kelvin sign and the dotless i fold to English letters in some Unicode rules, but are not matched
        assertEquals(List.of("/mar\u212A"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /mar\u212A", new Prefix("/mark")));
        assertEquals(List.of("/t\u0131p"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /t\u0131p", new Prefix("/tip")));
    }

    // EP_16: Prefix capitalisation variants are stored under the supplied prefix
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

    // EP_17: A differently capitalised prefix joined to text is not recognised
    @Test
    public void tokenize_prefixInDifferentCaseButJoinedToText_isNotAPrefix() {
        Prefix slashName = new Prefix("/name");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /NAMEJohn /Names Tan", slashName);

        assertArgumentAbsent(argMultimap, slashName);
        assertPreamblePresent(argMultimap, "/NAMEJohn /Names Tan");
    }

    // EP_18: A lone slash is an unrecognized prefix like token
    @Test
    public void findUnrecognizedPrefixes_loneSlash_returnsToken() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/"), ArgumentTokenizer.findUnrecognizedPrefixes(" /name Tan / Kumar", slashName));
    }

    // EP_19_1: A slash inside a value is not treated as a prefix
    // EP_19_2: A slash inside a token is not treated as a prefix
    @Test
    public void findUnrecognizedPrefixes_slashNotAtStartOfToken_returnsEmpty() {
        Prefix slashName = new Prefix("/name");

        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes(" /name Tan s/o Kumar", slashName).isEmpty());
        assertTrue(ArgumentTokenizer.findUnrecognizedPrefixes(" /name John/Doe", slashName).isEmpty());
    }

    // EP_20: A prefix like token after non space whitespace is detected as unrecognised
    @Test
    public void findUnrecognizedPrefixes_nonSpaceWhitespaceBeforeToken_returnsToken() {
        Prefix slashName = new Prefix("/name");

        assertEquals(List.of("/phone"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /name John\t/phone 123", slashName));
    }

    // EP_21: A recognised alias is stored under its full prefix
    @Test
    public void tokenize_aliasOfPrefix_valueIsStoredUnderFullPrefix() {
        Prefix slashName = new Prefix("/name", "/n");
        Prefix slashClass = new Prefix("/class", "/c");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("pre /n John Tan /c A1", slashName, slashClass);

        assertPreamblePresent(argMultimap, "pre");
        assertArgumentPresent(argMultimap, slashName, "John Tan");
        assertArgumentPresent(argMultimap, slashClass, "A1");
    }

    // EP_22: Full prefix and alias values are collected together and treated as duplicates
    @Test
    public void tokenize_fullFormAndAliasOfSamePrefix_valuesAreCollectedTogether() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /name John /n Tan", slashName);

        assertArgumentPresent(argMultimap, slashName, "John", "Tan");
        assertThrows(ParseException.class, () -> argMultimap.verifyNoDuplicatePrefixesFor(slashName));
    }

    // EP_23: Prefix aliases are matched without regard to capitalisation
    @Test
    public void tokenize_aliasInAnyCase_valueIsStoredUnderFullPrefix() {
        Prefix slashName = new Prefix("/name", "/n");
        Prefix slashClass = new Prefix("/class", "/c");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /N John /C A1", slashName, slashClass);

        assertArgumentPresent(argMultimap, slashName, "John");
        assertArgumentPresent(argMultimap, slashClass, "A1");
    }

    // EP_24: An alias at the end of the input is recognised with an empty value
    // Boundary Value: alias at end of input
    @Test
    public void tokenize_aliasWithoutValue_valueIsEmpty() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /n", slashName);

        assertArgumentPresent(argMultimap, slashName, "");
    }

    // EP_25: The full form of another prefix is not treated as this prefix's alias
    @Test
    public void tokenize_fullFormOfAnotherPrefix_isNotReadAsAlias() {
        Prefix slashClass = new Prefix("/class", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /name John", slashClass);

        assertPreamblePresent(argMultimap, "/name John");
        assertArgumentAbsent(argMultimap, slashClass);
    }

    // EP_26: An alias joined to preceding text is not recognised
    @Test
    public void tokenize_aliasJoinedToText_isNotAPrefix() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("x/n John", slashName);

        assertPreamblePresent(argMultimap, "x/n John");
        assertArgumentAbsent(argMultimap, slashName);
    }

    // EP_27: An alias not supplied to the tokenizer is not recognised
    @Test
    public void tokenize_aliasOfPrefixThatIsNotGiven_isNotRecognized() {
        Prefix slashName = new Prefix("/name", "/n");

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" /c A1", slashName);

        assertPreamblePresent(argMultimap, "/c A1");
        assertArgumentAbsent(argMultimap, slashName);
    }

    // EP_28: A known alias is accepted, while an unknown prefix remains unrecognised
    @Test
    public void findUnrecognizedPrefixes_aliasOfKnownPrefix_isKnown() {
        Prefix slashName = new Prefix("/name", "/n");
        Prefix slashClass = new Prefix("/class", "/c");

        assertEquals(List.of("/x"),
                ArgumentTokenizer.findUnrecognizedPrefixes(" /n John /C A1 /x 1", slashName, slashClass));
    }

    // EP_29: An alias whose prefix was not supplied is returned as unrecognised
    @Test
    public void findUnrecognizedPrefixes_aliasOfPrefixThatIsNotKnown_returnsToken() {
        Prefix slashName = new Prefix("/name", "/n");

        assertEquals(List.of("/c"), ArgumentTokenizer.findUnrecognizedPrefixes(" /n John /c A1", slashName));
    }

    // EP_30: Prefix equality is based on matching values, not object identity or type
    @Test
    public void equals_variousPrefixes_returnsExpected() {
        Prefix aaa = new Prefix("aaa");

        assertEquals(aaa, aaa);
        assertEquals(aaa, new Prefix("aaa"));

        assertNotEquals(aaa, "aaa");
        assertNotEquals(aaa, new Prefix("aab"));
    }

}
