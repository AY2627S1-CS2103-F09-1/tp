package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.StringUtil;

public class CliSyntaxTest {

    @Test
    public void allPrefixes_everyFormOfEveryPrefix_isDistinctIgnoringCase() {
        List<String> allForms = CliSyntax.ALL_PREFIXES.stream()
                .flatMap(prefix -> prefix.getAllForms().stream())
                .toList();

        long distinctCount = allForms.stream().map(StringUtil::toAsciiLowerCase).distinct().count();

        assertEquals(allForms.size(), distinctCount);
    }

    @Test
    public void allPrefixes_everyFormStartsWithASlash() {
        CliSyntax.ALL_PREFIXES.stream()
                .flatMap(prefix -> prefix.getAllForms().stream())
                .forEach(form -> assertTrue(form.startsWith("/") && form.length() > 1, form));
    }

    @Test
    public void aliases_areTheShortFormsOfTheFrequentlyTypedPrefixes() {
        assertEquals(List.of("/n"), CliSyntax.PREFIX_NAME.getAliases());
        assertEquals(List.of("/c"), CliSyntax.PREFIX_CLASS.getAliases());
        assertEquals(List.of("/m"), CliSyntax.PREFIX_MEMBERS.getAliases());
        assertEquals(List.of("/as"), CliSyntax.PREFIX_ASSESSMENT.getAliases());
        assertEquals(List.of("/st"), CliSyntax.PREFIX_STATUS.getAliases());
        assertEquals(List.of("/sc"), CliSyntax.PREFIX_SCORE.getAliases());
    }
}
