package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Parses CSV content and validates its header. */
public final class CsvParser {

    private static final String NAME_COLUMN = "name";
    private static final String CLASS_COLUMN = "class";
    private static final String EMAIL_COLUMN = "email";
    private static final Set<String> SUPPORTED_COLUMNS = Set.of(NAME_COLUMN, CLASS_COLUMN, EMAIL_COLUMN);

    private CsvParser() {
    }

    /**
     * Parses CSV content into a validated header and data records.
     *
     * @param content Complete CSV content.
     * @return Parsed CSV data.
     * @throws CsvParseException If the content has malformed quoting.
     * @throws CsvHeaderException If the header is missing or invalid.
     */
    public static ParsedCsv parse(String content) throws CsvParseException, CsvHeaderException {
        List<CsvRecord> records = parseRecords(content);
        if (records.isEmpty()) {
            throw new CsvHeaderException();
        }
        CsvHeader header = parseHeader(records.get(0));
        List<CsvRecord> dataRecords = records.subList(1, records.size());
        return new ParsedCsv(header, dataRecords);
    }

    /**
     * Parses CSV syntax into logical records.
     *
     * @param content Complete CSV content.
     * @return Parsed records in file order.
     * @throws CsvParseException If the content has malformed quoting.
     */
    static List<CsvRecord> parseRecords(String content) throws CsvParseException {
        requireNonNull(content);

        ParserState state = new ParserState();
        for (int i = 0; i < content.length(); i++) {
            if (consumeCharacter(content, i, state)) {
                i++;
            }
        }
        finishContent(state);
        return List.copyOf(state.records);
    }

    /**
     * Validates a CSV header and returns the positions of its supported columns.
     *
     * @param record Header record to validate.
     * @return Validated header mapping.
     * @throws CsvHeaderException If a column is unknown, repeated, or required columns are missing.
     */
    static CsvHeader parseHeader(CsvRecord record) throws CsvHeaderException {
        requireNonNull(record);

        Set<String> columns = new HashSet<>();
        int nameIndex = CsvHeader.COLUMN_NOT_FOUND;
        int classIndex = CsvHeader.COLUMN_NOT_FOUND;
        int emailIndex = CsvHeader.COLUMN_NOT_FOUND;
        for (int i = 0; i < record.fields().size(); i++) {
            String column = record.fields().get(i).trim().toLowerCase(Locale.ROOT);
            if (!SUPPORTED_COLUMNS.contains(column) || !columns.add(column)) {
                throw new CsvHeaderException();
            }
            if (column.equals(NAME_COLUMN)) {
                nameIndex = i;
            } else if (column.equals(CLASS_COLUMN)) {
                classIndex = i;
            } else {
                emailIndex = i;
            }
        }
        if (nameIndex == CsvHeader.COLUMN_NOT_FOUND || classIndex == CsvHeader.COLUMN_NOT_FOUND) {
            throw new CsvHeaderException();
        }
        return new CsvHeader(nameIndex, classIndex, emailIndex, record.fields().size());
    }

    /** Processes one character according to the current parser state. */
    private static boolean consumeCharacter(String content, int index, ParserState state)
            throws CsvParseException {
        if (state.inQuotes) {
            return consumeQuotedCharacter(content, index, state);
        }
        if (state.closedQuote) {
            return consumeClosedQuoteCharacter(content, index, state);
        }
        return consumeUnquotedCharacter(content, index, state);
    }

    /** Processes one character inside a quoted field. */
    private static boolean consumeQuotedCharacter(String content, int index, ParserState state) {
        char current = content.charAt(index);
        if (current != '"') {
            state.field.append(current);
            return false;
        }
        if (index + 1 < content.length() && content.charAt(index + 1) == '"') {
            state.field.append('"');
            return true;
        }
        state.inQuotes = false;
        state.closedQuote = true;
        return false;
    }

    /** Processes one character after a quoted field has been closed. */
    private static boolean consumeClosedQuoteCharacter(String content, int index, ParserState state)
            throws CsvParseException {
        char current = content.charAt(index);
        if (current == ',') {
            state.finishField();
            state.closedQuote = false;
            return false;
        }
        if (isLineEnding(current)) {
            return finishRecord(content, index, state);
        }
        if (!Character.isWhitespace(current)) {
            throw new CsvParseException();
        }
        return false;
    }

    /** Processes one character in an unquoted field. */
    private static boolean consumeUnquotedCharacter(String content, int index, ParserState state)
            throws CsvParseException {
        char current = content.charAt(index);
        if (current == '"') {
            if (!state.field.isEmpty()) {
                throw new CsvParseException();
            }
            state.inQuotes = true;
        } else if (current == ',') {
            state.finishField();
        } else if (isLineEnding(current)) {
            return finishRecord(content, index, state);
        } else {
            state.field.append(current);
        }
        return false;
    }

    /** Finishes a record and reports whether the LF in a CRLF pair was consumed. */
    private static boolean finishRecord(String content, int index, ParserState state) {
        state.finishRecord();
        return content.charAt(index) == '\r'
                && index + 1 < content.length() && content.charAt(index + 1) == '\n';
    }

    /** Finishes parsing and validates that no quoted field was left open. */
    private static void finishContent(ParserState state) throws CsvParseException {
        if (state.inQuotes) {
            throw new CsvParseException();
        }
        if (state.hasPendingRecord()) {
            state.finishRecord();
        }
    }

    /** Returns whether the character terminates a CSV record. */
    private static boolean isLineEnding(char c) {
        return c == '\r' || c == '\n';
    }

    /** Holds mutable state while one CSV string is being parsed. */
    private static class ParserState {
        private final List<CsvRecord> records = new ArrayList<>();
        private List<String> fields = new ArrayList<>();
        private final StringBuilder field = new StringBuilder();
        private boolean inQuotes;
        private boolean closedQuote;

        /** Adds the current field to the current record. */
        private void finishField() {
            fields.add(field.toString());
            field.setLength(0);
        }

        /** Adds the current record and prepares the state for the next one. */
        private void finishRecord() {
            finishField();
            records.add(new CsvRecord(records.size() + 1, fields));
            fields = new ArrayList<>();
            closedQuote = false;
        }

        /** Returns whether there is an unfinished record at the end of the content. */
        private boolean hasPendingRecord() {
            return closedQuote || !field.isEmpty() || !fields.isEmpty();
        }
    }

    /** Stores parsed CSV content after header validation. */
    public record ParsedCsv(CsvHeader header, List<CsvRecord> records) {
        /** Creates immutable parsed CSV content. */
        public ParsedCsv {
            requireNonNull(header);
            records = List.copyOf(records);
        }
    }

    /** Stores one logical CSV record and its one-based row number. */
    public record CsvRecord(int rowNumber, List<String> fields) {
        /** Creates an immutable record and removes a UTF-8 byte-order mark from the first header field. */
        public CsvRecord {
            fields = List.copyOf(fields);
            if (rowNumber == 1 && !fields.isEmpty() && fields.get(0).startsWith("\uFEFF")) {
                List<String> withoutBom = new ArrayList<>(fields);
                withoutBom.set(0, withoutBom.get(0).substring(1));
                fields = List.copyOf(withoutBom);
            }
        }

        /** Returns whether every field in this record is blank. */
        public boolean isBlank() {
            return fields.stream().allMatch(String::isBlank);
        }
    }

    /** Stores the column positions declared by a valid CSV header. */
    public record CsvHeader(int nameIndex, int classIndex, int emailIndex, int columnCount) {
        /** Indicates that a CSV column has not been found. */
        public static final int COLUMN_NOT_FOUND = -1;
    }

    /** Indicates that CSV content contains malformed quoting. */
    public static class CsvParseException extends Exception {
    }

    /** Indicates that a CSV header is missing or contains unsupported columns. */
    public static class CsvHeaderException extends Exception {
    }
}
