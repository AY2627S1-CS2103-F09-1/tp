package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CsvParserTest {

    @Test
    public void constructor_isPrivate() throws ReflectiveOperationException {
        Constructor<CsvParser> constructor = CsvParser.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void parse_emptyContent_throwsCsvHeaderException() {
        assertThrows(CsvParser.CsvHeaderException.class, () -> CsvParser.parse(""));
    }

    @Test
    public void parseRecords_emptyContent_returnsNoRecords() throws CsvParser.CsvParseException {
        assertTrue(CsvParser.parseRecords("").isEmpty());
    }

    @Test
    public void parse_quotedFieldsAndCrLf_returnsParsedCsv()
            throws CsvParser.CsvParseException, CsvParser.CsvHeaderException {
        CsvParser.ParsedCsv csv = CsvParser.parse("\"name\",\"class\"\r\n\"Alice Smith\",\"A1\"\r\n");

        assertEquals(0, csv.header().nameIndex());
        assertEquals(1, csv.header().classIndex());
        assertEquals(1, csv.records().size());
        assertEquals(2, csv.records().get(0).rowNumber());
        assertEquals(List.of("Alice Smith", "A1"), csv.records().get(0).fields());
    }

    @Test
    public void parse_escapedQuotes_decodesQuotes()
            throws CsvParser.CsvParseException, CsvParser.CsvHeaderException {
        CsvParser.ParsedCsv csv = CsvParser.parse("name,class\n\"Alice \"\"Ace\"\"\",A1");

        assertEquals(List.of("Alice \"Ace\"", "A1"), csv.records().get(0).fields());
    }

    @Test
    public void parseRecords_bomOnFirstRecord_removesBom() throws CsvParser.CsvParseException {
        List<CsvParser.CsvRecord> records = CsvParser.parseRecords("\uFEFFname,class\nAlice,A1\n");

        assertEquals("name", records.get(0).fields().get(0));
    }

    @Test
    public void parseRecords_emptyQuotedField_returnsRecord() throws CsvParser.CsvParseException {
        List<CsvParser.CsvRecord> records = CsvParser.parseRecords("\"\"");

        assertEquals(List.of(""), records.get(0).fields());
    }

    @Test
    public void parseRecords_trailingEmptyField_returnsRecord() throws CsvParser.CsvParseException {
        List<CsvParser.CsvRecord> records = CsvParser.parseRecords("name,");

        assertEquals(List.of("name", ""), records.get(0).fields());
    }

    @Test
    public void parseRecords_standaloneCarriageReturn_finishesRecord() throws CsvParser.CsvParseException {
        List<CsvParser.CsvRecord> records = CsvParser.parseRecords("name,class\r");

        assertEquals(1, records.size());
        assertEquals(List.of("name", "class"), records.get(0).fields());
    }

    @Test
    public void parseRecords_carriageReturnWithoutLineFeed_startsNextRecord() throws CsvParser.CsvParseException {
        List<CsvParser.CsvRecord> records = CsvParser.parseRecords("name,class\rAlice,A1");

        assertEquals(2, records.size());
        assertEquals(List.of("Alice", "A1"), records.get(1).fields());
    }

    @Test
    public void parse_blankRecord_isBlank()
            throws CsvParser.CsvParseException, CsvParser.CsvHeaderException {
        List<CsvParser.CsvRecord> records = CsvParser.parse("name,class\n \t\nAlice,A1").records();

        assertTrue(records.get(0).isBlank());
        assertFalse(records.get(1).isBlank());
    }

    @Test
    public void parseHeader_reorderedOptionalEmail_returnsColumnMapping()
            throws CsvParser.CsvParseException, CsvParser.CsvHeaderException {
        CsvParser.CsvRecord record = CsvParser.parseRecords("EMAIL,Class,Name\n").get(0);

        CsvParser.CsvHeader header = CsvParser.parseHeader(record);

        assertEquals(2, header.nameIndex());
        assertEquals(1, header.classIndex());
        assertEquals(0, header.emailIndex());
        assertEquals(3, header.columnCount());
    }

    @Test
    public void parseHeader_missingRequiredColumn_throwsCsvHeaderException() throws CsvParser.CsvParseException {
        CsvParser.CsvRecord record = CsvParser.parseRecords("name,email\n").get(0);

        assertThrows(CsvParser.CsvHeaderException.class, () -> CsvParser.parseHeader(record));
    }

    @Test
    public void parseHeader_missingNameColumn_throwsCsvHeaderException() throws CsvParser.CsvParseException {
        CsvParser.CsvRecord record = CsvParser.parseRecords("class,email\n").get(0);

        assertThrows(CsvParser.CsvHeaderException.class, () -> CsvParser.parseHeader(record));
    }

    @Test
    public void parseHeader_unknownColumn_throwsCsvHeaderException() throws CsvParser.CsvParseException {
        CsvParser.CsvRecord record = CsvParser.parseRecords("name,class,phone\n").get(0);

        assertThrows(CsvParser.CsvHeaderException.class, () -> CsvParser.parseHeader(record));
    }

    @Test
    public void parseHeader_duplicateColumn_throwsCsvHeaderException() throws CsvParser.CsvParseException {
        CsvParser.CsvRecord record = CsvParser.parseRecords("name,class,name\n").get(0);

        assertThrows(CsvParser.CsvHeaderException.class, () -> CsvParser.parseHeader(record));
    }

    @Test
    public void parse_unclosedQuote_throwsCsvParseException() {
        assertThrows(CsvParser.CsvParseException.class, () -> CsvParser.parse("name,class\n\"Alice,A1"));
    }

    @Test
    public void parse_quoteAfterUnquotedField_throwsCsvParseException() {
        assertThrows(CsvParser.CsvParseException.class, () -> CsvParser.parse("name,class\nAlice\",A1"));
    }

    @Test
    public void parse_nonWhitespaceAfterClosedQuote_throwsCsvParseException() {
        assertThrows(CsvParser.CsvParseException.class, () -> CsvParser.parse("name,class\n\"Alice\"x,A1"));
    }

    @Test
    public void csvRecord_bomOnLaterRecord_preservesBom() {
        CsvParser.CsvRecord record = new CsvParser.CsvRecord(2, List.of("\uFEFFAlice", "A1"));

        assertEquals("\uFEFFAlice", record.fields().get(0));
    }

    @Test
    public void csvRecord_emptyFirstRecord_hasNoFields() {
        CsvParser.CsvRecord record = new CsvParser.CsvRecord(1, List.of());

        assertTrue(record.fields().isEmpty());
    }

    @Test
    public void parsedCsv_nullHeader_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CsvParser.ParsedCsv(null, List.of()));
    }
}
