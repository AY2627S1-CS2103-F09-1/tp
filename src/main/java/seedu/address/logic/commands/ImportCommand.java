package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.CsvParser;
import seedu.address.commons.util.FileUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;

/** Imports persons from a CSV file. */
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

    private static final String CSV_EXTENSION = ".csv";

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

        Path path = resolvePath(filePath);
        validatePath(path);
        String content = readFile(path);
        CsvParser.ParsedCsv csv = parseCsv(content);

        List<Person> persons = parsePersons(csv, model);
        for (Person person : persons) {
            model.addPerson(person);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, persons.size(), filePath));
    }

    /**
     * Resolves a user-provided path against the application base directory.
     * Packaged applications use the JAR directory; local Gradle runs use the project working directory.
     *
     * @param filePath User-provided absolute or relative path.
     * @return Normalized path.
     * @throws CommandException If the path cannot be resolved.
     */
    private Path resolvePath(String filePath) throws CommandException {
        try {
            Path suppliedPath = Paths.get(filePath);
            if (suppliedPath.isAbsolute()) {
                return suppliedPath.normalize();
            }
            Path location = Paths.get(ImportCommand.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI());
            Path baseDirectory = Files.isRegularFile(location)
                    ? location.getParent() : Paths.get(System.getProperty("user.dir"));
            return baseDirectory.resolve(suppliedPath).normalize();
        } catch (InvalidPathException | URISyntaxException e) {
            throw new CommandException(String.format(MESSAGE_FILE_NOT_FOUND, filePath), e);
        }
    }

    /**
     * Validates that a resolved path refers to a readable CSV file.
     *
     * @param path Resolved path to validate.
     * @throws CommandException If the path is not a readable CSV file.
     */
    private void validatePath(Path path) throws CommandException {
        if (!path.toString().endsWith(CSV_EXTENSION)) {
            throw new CommandException(MESSAGE_CSV_ONLY);
        }
        if (!Files.exists(path)) {
            throw new CommandException(String.format(MESSAGE_FILE_NOT_FOUND, filePath));
        }
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new CommandException(String.format(MESSAGE_FAILURE, filePath));
        }
    }

    /**
     * Reads the requested file as UTF-8 content.
     *
     * @param path Path to the readable CSV file.
     * @return File content.
     * @throws CommandException If the file is empty or cannot be read.
     */
    private String readFile(Path path) throws CommandException {
        String content;
        try {
            content = FileUtil.readFromFile(path);
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_FAILURE, filePath), e);
        }
        if (content.isEmpty()) {
            throw new CommandException(MESSAGE_FILE_EMPTY);
        }
        return content;
    }

    /**
     * Parses file content into validated CSV data.
     *
     * @param content Complete CSV content.
     * @return Parsed header and data records.
     * @throws CommandException If the CSV syntax or header is invalid.
     */
    private CsvParser.ParsedCsv parseCsv(String content) throws CommandException {
        try {
            return CsvParser.parse(content);
        } catch (CsvParser.CsvParseException e) {
            throw new CommandException(String.format(MESSAGE_FAILURE, filePath), e);
        } catch (CsvParser.CsvHeaderException e) {
            throw new CommandException(MESSAGE_HEADER);
        }
    }

    /**
     * Converts and validates all contact rows before changing the model.
     *
     * @param csv Parsed CSV data.
     * @param model Model used to check existing contacts.
     * @return Validated contacts ready for insertion.
     * @throws CommandException If a row is invalid or duplicated.
     */
    private List<Person> parsePersons(CsvParser.ParsedCsv csv, Model model)
            throws CommandException {
        List<Person> persons = new ArrayList<>();
        for (CsvParser.CsvRecord record : csv.records()) {
            if (record.isBlank()) {
                continue;
            }
            Person person = parsePerson(record, csv.header());
            if (model.hasPerson(person) || persons.stream().anyMatch(person::isSamePerson)) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE, record.rowNumber()));
            }
            persons.add(person);
        }
        if (persons.isEmpty()) {
            throw new CommandException(MESSAGE_FILE_EMPTY);
        }
        return persons;
    }

    /**
     * Converts one CSV record into a validated person.
     *
     * @param record CSV record to convert.
     * @param header Mapping of contact fields to CSV columns.
     * @return Validated person represented by the record.
     * @throws CommandException If the record has an invalid field count or contact value.
     */
    private Person parsePerson(CsvParser.CsvRecord record, CsvParser.CsvHeader header) throws CommandException {
        if (record.fields().size() != header.columnCount()) {
            throw new CommandException(String.format(MESSAGE_INVALID_FIELD_COUNT, record.rowNumber()));
        }
        try {
            return new Person(ParserUtil.parseName(record.fields().get(header.nameIndex())),
                    ParserUtil.parseClassName(record.fields().get(header.classIndex())),
                    parseOptionalEmail(record.fields(), header), Collections.emptySet());
        } catch (ParseException | IllegalArgumentException e) {
            throw new CommandException(String.format(MESSAGE_INVALID_CONTACT, record.rowNumber()), e);
        }
    }

    /**
     * Returns the optional email value from a CSV record.
     *
     * @param fields Values from the CSV record.
     * @param header Mapping of contact fields to CSV columns.
     * @return The parsed email, or an empty value when email is absent or blank.
     * @throws ParseException If the email is present but invalid.
     */
    private static Optional<Email> parseOptionalEmail(List<String> fields, CsvParser.CsvHeader header)
            throws ParseException {
        if (header.emailIndex() == CsvParser.CsvHeader.COLUMN_NOT_FOUND
                || fields.get(header.emailIndex()).trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseEmail(fields.get(header.emailIndex())));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ImportCommand command && filePath.equals(command.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }

}
