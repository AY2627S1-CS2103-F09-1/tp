package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PrefixTest {

    @Test
    public void getAllForms_prefixWithAliases_listsFullFormFirst() {
        Prefix prefix = new Prefix("/status", "/st", "/s");

        assertEquals(List.of("/status", "/st", "/s"), prefix.getAllForms());
        assertEquals(List.of("/st", "/s"), prefix.getAliases());
    }

    @Test
    public void getAllForms_prefixWithoutAliases_listsOnlyFullForm() {
        Prefix prefix = new Prefix("/email");

        assertEquals(List.of("/email"), prefix.getAllForms());
        assertTrue(prefix.getAliases().isEmpty());
    }

    @Test
    public void getAliases_modifyReturnedList_throwsUnsupportedOperationException() {
        Prefix prefix = new Prefix("/name", "/n");

        assertThrows(UnsupportedOperationException.class, () -> prefix.getAliases().add("/x"));
    }

    @Test
    public void getAllForms_modifyReturnedList_doesNotChangePrefix() {
        Prefix prefix = new Prefix("/name", "/n");

        prefix.getAllForms().add("/x");

        assertEquals(List.of("/name", "/n"), prefix.getAllForms());
    }

    @Test
    public void equals_aliasesAreIgnored() {
        Prefix withAlias = new Prefix("/name", "/n");

        assertEquals(withAlias, new Prefix("/name"));
        assertEquals(withAlias, new Prefix("/name", "/other"));
        assertEquals(withAlias.hashCode(), new Prefix("/name").hashCode());
        assertNotEquals(withAlias, new Prefix("/class", "/n"));
    }

    @Test
    public void toString_showsFullForm() {
        assertEquals("/name", new Prefix("/name", "/n").toString());
    }
}
