package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;

/**
 * Imports persons from a CSV file.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports contacts from a CSV file.\n"
            + "Parameters: FILEPATH\n"
            + "Example: " + COMMAND_WORD + " students.csv";

    public static final String MESSAGE_SUCCESS = "Imported %1$d contacts from %2$s";
    public static final String MESSAGE_FILE_NOT_FOUND = "File not found at %s";
    public static final String MESSAGE_CSV_ONLY = "Only .csv files are supported";
    public static final String MESSAGE_FILE_EMPTY = "File empty";
    public static final String MESSAGE_HEADER = "CSV header must contain 'name' and 'class' columns";
    public static final String MESSAGE_INVALID_FIELD_COUNT = "Invalid number of fields on row %d";
    public static final String MESSAGE_DUPLICATE = "Duplicate contact on row %d";
    public static final String MESSAGE_INVALID_CONTACT = "Invalid contact data on row %d";
    public static final String MESSAGE_FAILURE = "Failed to import from %s";

    private final String filePath;

    /**
     * Creates an import command for the specified CSV file.
     *
     * @param filePath Path to the CSV file to import.
     */
    public ImportCommand(String filePath) {
        this.filePath = requireNonNull(filePath);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!filePath.endsWith(".csv")) {
            throw new CommandException(MESSAGE_CSV_ONLY);
        }

        Path path;
        try {
            path = resolvePath(filePath);
        } catch (InvalidPathException | URISyntaxException e) {
            throw new CommandException(String.format(MESSAGE_FILE_NOT_FOUND, filePath), e);
        }

        if (!Files.exists(path)) {
            throw new CommandException(String.format(MESSAGE_FILE_NOT_FOUND, filePath));
        }
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new CommandException(String.format(MESSAGE_FAILURE, filePath));
        }

        String content;
        try {
            content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_FAILURE, filePath), e);
        }
        if (content.isEmpty()) {
            throw new CommandException(MESSAGE_FILE_EMPTY);
        }

        List<CsvRecord> records;
        try {
            records = parseCsv(content);
        } catch (CsvFormatException e) {
            throw new CommandException(String.format(MESSAGE_FAILURE, filePath), e);
        }
        if (records.isEmpty()) {
            throw new CommandException(MESSAGE_FILE_EMPTY);
        }

        Header header = parseHeader(records.get(0));
        if (header == null) {
            throw new CommandException(MESSAGE_HEADER);
        }

        List<Person> importedPersons = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            CsvRecord record = records.get(i);
            if (record.isBlank()) {
                continue;
            }
            int rowNumber = record.rowNumber;
            if (record.fields.size() != header.columnCount()) {
                throw new CommandException(String.format(MESSAGE_INVALID_FIELD_COUNT, rowNumber));
            }
            Person person;
            try {
                person = toPerson(record.fields, header);
            } catch (ParseException | IllegalArgumentException e) {
                throw new CommandException(String.format(MESSAGE_INVALID_CONTACT, rowNumber), e);
            }
            if (model.hasPerson(person) || importedPersons.stream().anyMatch(person::isSamePerson)) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE, rowNumber));
            }
            importedPersons.add(person);
        }
        if (importedPersons.isEmpty()) {
            throw new CommandException(MESSAGE_FILE_EMPTY);
        }

        for (Person person : importedPersons) {
            model.addPerson(person);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, importedPersons.size(), filePath));
    }

    /**
     * Converts one CSV record into a validated person.
     *
     * @param fields Values from the CSV record.
     * @param header Mapping of contact fields to CSV columns.
     * @return Validated person represented by the record.
     * @throws ParseException If a contact field is invalid.
     */
    private static Person toPerson(List<String> fields, Header header) throws ParseException {
        return new Person(ParserUtil.parseName(fields.get(header.nameIndex)),
                ParserUtil.parseClassName(fields.get(header.classIndex)),
                parseOptionalEmail(fields, header), Collections.emptySet());
    }

    /**
     * Returns the optional email value from a CSV record.
     *
     * @param fields Values from the CSV record.
     * @param header Mapping of contact fields to CSV columns.
     * @return The parsed email, or an empty value when email is absent or blank.
     * @throws ParseException If the email is present but invalid.
     */
    private static Optional<Email> parseOptionalEmail(List<String> fields, Header header) throws ParseException {
        if (header.emailIndex == -1 || fields.get(header.emailIndex).trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseEmail(fields.get(header.emailIndex)));
    }

    /**
     * Returns the column mapping represented by the CSV header.
     *
     * @param record First CSV record containing the header.
     * @return Header mapping, or {@code null} when the header is invalid.
     */
    private static Header parseHeader(CsvRecord record) {
        Set<String> names = new HashSet<>();
        int nameIndex = -1;
        int classIndex = -1;
        int emailIndex = -1;
        for (int i = 0; i < record.fields.size(); i++) {
            String column = record.fields.get(i).trim().toLowerCase(Locale.ROOT);
            if (!Set.of("name", "class", "email").contains(column) || !names.add(column)) {
                return null;
            }
            switch (column) {
                case "name" -> nameIndex = i;
                case "class" -> classIndex = i;
                case "email" -> emailIndex = i;
                default -> throw new AssertionError("Unexpected header column");
            }
        }
        if (nameIndex == -1 || classIndex == -1) {
            return null;
        }
        return new Header(nameIndex, classIndex, emailIndex, record.fields.size());
    }

    /**
     * Resolves a user-provided path against the application base directory.
     * Packaged applications use the JAR directory while local runs use the project working directory.
     *
     * @param filePath User-provided absolute or relative path.
     * @return Normalized path to the requested file.
     * @throws URISyntaxException If the application location cannot be converted to a path.
     */
    private static Path resolvePath(String filePath) throws URISyntaxException {
        Path suppliedPath = Paths.get(filePath);
        if (suppliedPath.isAbsolute()) {
            return suppliedPath.normalize();
        }
        Path location = Paths.get(ImportCommand.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI());
        Path baseDirectory = Files.isRegularFile(location)
                ? location.getParent() : Paths.get(System.getProperty("user.dir"));
        return baseDirectory.resolve(suppliedPath).normalize();
    }

    /**
     * Parses CSV content into logical records while supporting quoted and escaped fields.
     *
     * @param content Complete UTF-8 file content.
     * @return Parsed CSV records in file order.
     * @throws CsvFormatException If the content has malformed quoting.
     */
    private static List<CsvRecord> parseCsv(String content) throws CsvFormatException {
        List<CsvRecord> records = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean closedQuote = false;
        int rowNumber = 1;
        int recordStart = 1;
        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);
            if (inQuotes) {
                if (current == '"') {
                    if (i + 1 < content.length() && content.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                        closedQuote = true;
                    }
                } else {
                    field.append(current);
                }
            } else if (closedQuote) {
                if (current == ',') {
                    fields.add(field.toString());
                    field.setLength(0);
                    closedQuote = false;
                } else if (current == '\n' || current == '\r') {
                    fields.add(field.toString());
                    records.add(new CsvRecord(recordStart, fields));
                    fields = new ArrayList<>();
                    field.setLength(0);
                    closedQuote = false;
                    if (current == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                        i++;
                    }
                    rowNumber++;
                    recordStart = rowNumber;
                } else if (!Character.isWhitespace(current)) {
                    throw new CsvFormatException();
                }
            } else if (current == '"') {
                if (!field.isEmpty()) {
                    throw new CsvFormatException();
                }
                inQuotes = true;
            } else if (current == ',') {
                fields.add(field.toString());
                field.setLength(0);
            } else if (current == '\n' || current == '\r') {
                fields.add(field.toString());
                records.add(new CsvRecord(recordStart, fields));
                fields = new ArrayList<>();
                field.setLength(0);
                if (current == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    i++;
                }
                rowNumber++;
                recordStart = rowNumber;
            } else {
                field.append(current);
            }
        }
        if (inQuotes) {
            throw new CsvFormatException();
        }
        if (closedQuote || !field.isEmpty() || !fields.isEmpty()) {
            fields.add(field.toString());
            records.add(new CsvRecord(recordStart, fields));
        }
        return records;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ImportCommand command && filePath.equals(command.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }

    /** Stores the column positions declared by the CSV header. */
    private record Header(int nameIndex, int classIndex, int emailIndex, int columnCount) {}

    /** Stores one logical CSV record and its one-based row number. */
    private record CsvRecord(int rowNumber, List<String> fields) {
        /**
         * Creates an immutable CSV record and removes a UTF-8 byte-order mark from the first header field.
         *
         * @param rowNumber One-based logical row number.
         * @param fields Parsed field values.
         */
        private CsvRecord {
            fields = List.copyOf(fields);
            if (rowNumber == 1 && !fields.isEmpty() && fields.get(0).startsWith("\uFEFF")) {
                List<String> withoutBom = new ArrayList<>(fields);
                withoutBom.set(0, withoutBom.get(0).substring(1));
                fields = List.copyOf(withoutBom);
            }
        }

        /**
         * Returns whether every field in this record is blank.
         *
         * @return True when this record can be ignored as a blank row.
         */
        private boolean isBlank() {
            return fields.stream().allMatch(String::isBlank);
        }
    }

    /** Indicates that a CSV file contains malformed quoting. */
    private static class CsvFormatException extends Exception {}
}
