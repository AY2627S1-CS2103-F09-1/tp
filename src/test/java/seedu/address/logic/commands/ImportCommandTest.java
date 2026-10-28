package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ImportCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ImportCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ImportCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ImportCommand command = new ImportCommand("students.csv");

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_modelDoesNotStoreContacts_throwsAssertionError() throws IOException {
        Path csv = writeCsv("name,class\nAlice,A1\n");
        ModelManager modelThatDropsContacts = new ModelManager() {
            @Override
            public void addPerson(Person person) {
                // Intentionally does nothing to simulate a model that fails to store an imported contact
            }
        };

        assertThrows(AssertionError.class, () -> new ImportCommand(csv.toString()).execute(modelThatDropsContacts));
    }

    @Test
    public void execute_validCsv_importsAllContacts() throws IOException, CommandException, ParseException {
        Path csv = writeCsv("CLASS,Name,EMAIL\nA1,\"Tan Wei Ming\",wei@example.com\nA2,Jane Doe,\n");
        ModelManager model = new ModelManager();

        CommandResult result = new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals("Imported 2 contacts from " + csv, result.getFeedbackToUser());
        assertEquals(2, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_csvSurroundedByWhitespace_importsContacts()
            throws IOException, CommandException, ParseException {
        Path csv = writeCsv("\r\n\t  name,class\nAlice Smith,A1\n  \t\r\n");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
        assertEquals("Alice Smith", model.getAddressBook().getPersonList().get(0).getName().toString());
    }

    @Test
    public void execute_csvWithTrailingUnicodeWhitespace_importsContacts()
            throws IOException, CommandException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\u2003");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
        assertEquals("A1", model.getAddressBook().getPersonList().get(0).getClassName().toString());
    }

    @Test
    public void execute_validCsv_showsContactsAtTopInFileOrder()
            throws IOException, CommandException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\nBob,A2\nCarl,A3\n");
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Existing Person").build());

        new ImportCommandParser().parse(csv.toString()).execute(model);

        List<String> names = model.getFilteredPersonList().stream()
                .map(person -> person.getName().toString())
                .toList();
        assertEquals(List.of("Alice", "Bob", "Carl", "Existing Person"), names);
    }

    @Test
    public void execute_namesWithAccentsDigitsAndSymbols_importsContacts()
            throws IOException, CommandException, ParseException {
        Path csv = writeCsv("name,class\nJosé Müller,T11/A\nTan s/o Kumar (Raj) #2,Sec 3 (Morning)\n");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        List<String> names = model.getFilteredPersonList().stream()
                .map(person -> person.getName().toString())
                .toList();
        assertEquals(List.of("José Müller", "Tan s/o Kumar (Raj) #2"), names);
    }

    @Test
    public void execute_nameInOtherScript_doesNotImportContacts() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\n\u674E\u660E,A2\n");
        ModelManager model = new ModelManager();
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals("Invalid contact data on row 3", exception.getMessage());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_invalidRow_doesNotImportEarlierRows() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\n\"Bad, Name\",A2\n");
        ModelManager model = new ModelManager();
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals("Invalid contact data on row 3", exception.getMessage());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_invalidEmail_doesNotImportContact() throws IOException, ParseException {
        Path csv = writeCsv("name,class,email\nAlice,A1,not-an-email\n");
        ModelManager model = new ModelManager();
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals("Invalid contact data on row 2", exception.getMessage());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_duplicateRow_failsFast() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\nAlice,A1\nAlice,A2\n");
        ModelManager model = new ModelManager();
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals("Duplicate contact on row 3", exception.getMessage());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_headerOnlyFile_reportsEmpty() throws IOException, ParseException {
        Path csv = writeCsv("name,class,email\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_FILE_EMPTY, exception.getMessage());
    }

    @Test
    public void execute_emptyFile_reportsEmpty() throws IOException, ParseException {
        Path csv = writeCsv("");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_FILE_EMPTY, exception.getMessage());
    }

    @Test
    public void execute_fileLargerThanLimit_reportsFileTooLarge() throws IOException, ParseException {
        Path csv = tempDir.resolve("students.csv");
        Files.write(csv, new byte[ImportCommand.MAX_FILE_SIZE_BYTES + 1]);
        ImportCommand command = new ImportCommandParser().parse(csv.toString());
        ModelManager model = new ModelManager();

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(ImportCommand.MESSAGE_FILE_TOO_LARGE, exception.getMessage());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_moreRowsThanLimit_reportsTooManyRowsAndImportsNothing()
            throws IOException, ParseException {
        Path csv = writeCsv(csvWithRows(ImportCommand.MAX_ROWS + 1));
        ImportCommand command = new ImportCommandParser().parse(csv.toString());
        ModelManager model = new ModelManager();

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(ImportCommand.MESSAGE_TOO_MANY_ROWS, exception.getMessage());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_exactlyLimitRows_isNotRejected() throws IOException, CommandException, ParseException {
        // Blank rows count as rows, and are ignored when the contacts are imported, so this is quick
        Path csv = writeCsv("name,class\nAlice,C1\n" + "\n".repeat(ImportCommand.MAX_ROWS - 1));
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_invalidExtension_reportsCsvOnly() throws IOException, ParseException {
        Path file = tempDir.resolve("students.txt");
        Files.writeString(file, "name,class\nAlice,A1\n");
        ImportCommand command = new ImportCommandParser().parse(file.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_CSV_ONLY, exception.getMessage());
    }

    @Test
    public void execute_missingFileWithInvalidExtension_reportsFileNotFound() throws ParseException {
        Path txt = tempDir.resolve("missing.txt");
        ImportCommand command = new ImportCommandParser().parse(txt.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FILE_NOT_FOUND, txt), exception.getMessage());
    }

    @Test
    public void execute_existingDirectoryWithoutCsvExtension_reportsCsvOnly() throws IOException, ParseException {
        Path directory = Files.createDirectory(tempDir.resolve("folder"));
        ImportCommand command = new ImportCommandParser().parse(directory.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_CSV_ONLY, exception.getMessage());
    }

    @Test
    public void execute_missingFile_reportsFileNotFound() throws ParseException {
        Path csv = tempDir.resolve("missing.csv");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FILE_NOT_FOUND, csv), exception.getMessage());
    }

    @Test
    public void execute_invalidPath_reportsFileNotFound() throws ParseException {
        ImportCommand command = new ImportCommandParser().parse("invalid\0.csv");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FILE_NOT_FOUND, "invalid\0.csv"), exception.getMessage());
    }

    @Test
    public void execute_directoryPath_reportsImportFailure() throws IOException, ParseException {
        Path directory = tempDir.resolve("directory.csv");
        Files.createDirectory(directory);
        ImportCommand command = new ImportCommandParser().parse(directory.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FAILURE, directory), exception.getMessage());
    }

    @Test
    public void readFile_ioError_reportsImportFailure() throws ReflectiveOperationException, IOException {
        Path directory = Files.createDirectory(tempDir.resolve("unreadable.csv"));
        ImportCommand command = new ImportCommand(directory.toString());
        Method readFile = ImportCommand.class.getDeclaredMethod("readFile", Path.class);
        readFile.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                readFile.invoke(command, directory));
        CommandException cause = assertInstanceOf(CommandException.class, exception.getCause());

        assertEquals(String.format(ImportCommand.MESSAGE_FAILURE, directory), cause.getMessage());
    }

    @Test
    public void execute_invalidHeader_reportsHeaderError() throws IOException, ParseException {
        Path csv = writeCsv("name,email\nAlice,alice@example.com\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_HEADER, exception.getMessage());
    }

    @Test
    public void execute_duplicateHeader_reportsHeaderError() throws IOException, ParseException {
        Path csv = writeCsv("name,class,name\nAlice,A1,Alice\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_HEADER, exception.getMessage());
    }

    @Test
    public void execute_invalidFieldCount_reportsRowNumber() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1,alice@example.com\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals("Invalid number of fields on row 2", exception.getMessage());
    }

    @Test
    public void execute_blankRow_ignoresRow() throws IOException, CommandException, ParseException {
        Path csv = writeCsv("name,class\n\nAlice,A1\n");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_existingDuplicate_reportsDuplicateRow() throws IOException, CommandException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\n");
        ModelManager model = new ModelManager();
        ImportCommand command = new ImportCommandParser().parse(csv.toString());
        command.execute(model);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals("Duplicate contact on row 2", exception.getMessage());
    }

    @Test
    public void execute_malformedCsv_reportsImportFailure() throws IOException, ParseException {
        Path csv = writeCsv("name,class\n\"Alice,A1\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FAILURE, csv), exception.getMessage());
    }

    @Test
    public void execute_quoteAfterUnquotedField_reportsImportFailure() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice\",A1\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FAILURE, csv), exception.getMessage());
    }

    @Test
    public void execute_nonWhitespaceAfterClosedQuote_reportsImportFailure() throws IOException, ParseException {
        Path csv = writeCsv("name,class\n\"Alice\"x,A1\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FAILURE, csv), exception.getMessage());
    }

    @Test
    public void execute_quotedFieldsWithCrLf_importsContacts() throws IOException, CommandException, ParseException {
        Path csv = writeCsv("\"name\",\"class\"\r\n\"Alice Smith\",\"A1\"\r\n");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_quotedFieldWithTrailingWhitespace_importsContacts() throws IOException, CommandException,
            ParseException {
        Path csv = writeCsv("\"name\" ,class\n\"Alice Smith\" ,A1");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_relativePath_importsContacts() throws IOException, CommandException, ParseException {
        Path relativePath = Path.of("build", "import-command-test.csv");
        Files.writeString(relativePath, "name,class\nAlice,A1\n");
        try {
            ModelManager model = new ModelManager();

            new ImportCommandParser().parse(relativePath.toString()).execute(model);

            assertEquals(1, model.getAddressBook().getPersonList().size());
        } finally {
            Files.deleteIfExists(relativePath);
        }
    }

    @Test
    public void execute_utf8BomHeader_importsContacts() throws IOException, CommandException, ParseException {
        Path csv = writeCsv("\uFEFFname,class\nAlice,A1\n");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_csvWithoutTrailingNewline_importsContacts() throws IOException, CommandException,
            ParseException {
        Path csv = writeCsv("name,class\nAlice,A1");
        ModelManager model = new ModelManager();

        new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void equals_variousCommands_returnsExpected() {
        ImportCommand command = new ImportCommand("students.csv");
        ImportCommand sameCommand = new ImportCommand("students.csv");
        ImportCommand differentCommand = new ImportCommand("others.csv");

        assertEquals(command, command);
        assertEquals(command, sameCommand);
        assertEquals(command.hashCode(), sameCommand.hashCode());
        assertNotEquals(command, differentCommand);
        assertNotEquals(null, command);
    }

    /**
     * Returns the content of a CSV file with a header and {@code rowCount} rows of different contacts.
     */
    private static String csvWithRows(int rowCount) {
        StringBuilder csv = new StringBuilder("name,class\n");
        for (int i = 1; i <= rowCount; i++) {
            csv.append("Alice,C").append(i).append('\n');
        }
        return csv.toString();
    }

    private Path writeCsv(String content) throws IOException {
        Path csv = tempDir.resolve("students.csv");
        return Files.writeString(csv, content);
    }
}
