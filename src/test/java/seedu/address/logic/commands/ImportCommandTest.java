package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ImportCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;

public class ImportCommandTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void execute_validCsv_importsAllContacts() throws IOException, CommandException, ParseException {
        Path csv = writeCsv("CLASS,Name,EMAIL\nA1,\"Tan Wei Ming\",wei@example.com\nA2,Jane Doe,\n");
        ModelManager model = new ModelManager();

        CommandResult result = new ImportCommandParser().parse(csv.toString()).execute(model);

        assertEquals("Imported 2 contacts from " + csv, result.getFeedbackToUser());
        assertEquals(2, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_invalidRow_doesNotImportEarlierRows() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1\nBad!,A2\n");
        ModelManager model = new ModelManager();
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals("Invalid contact data on row 3", exception.getMessage());
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
    public void execute_invalidExtension_reportsCsvOnly() throws IOException, ParseException {
        Path file = temporaryDirectory.resolve("students.txt");
        Files.writeString(file, "name,class\nAlice,A1\n");
        ImportCommand command = new ImportCommandParser().parse(file.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_CSV_ONLY, exception.getMessage());
    }

    @Test
    public void execute_missingFile_reportsFileNotFound() throws ParseException {
        Path csv = temporaryDirectory.resolve("missing.csv");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FILE_NOT_FOUND, csv), exception.getMessage());
    }

    @Test
    public void execute_invalidHeader_reportsHeaderError() throws IOException, ParseException {
        Path csv = writeCsv("name,email\nAlice,alice@example.com\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(ImportCommand.MESSAGE_HEADER, exception.getMessage());
    }

    @Test
    public void execute_invalidFieldCount_reportsRowNumber() throws IOException, ParseException {
        Path csv = writeCsv("name,class\nAlice,A1,alice@example.com\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_INVALID_FIELD_COUNT, 2), exception.getMessage());
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

        assertEquals(String.format(ImportCommand.MESSAGE_DUPLICATE, 2), exception.getMessage());
    }

    @Test
    public void execute_malformedCsv_reportsImportFailure() throws IOException, ParseException {
        Path csv = writeCsv("name,class\n\"Alice,A1\n");
        ImportCommand command = new ImportCommandParser().parse(csv.toString());

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(new ModelManager()));

        assertEquals(String.format(ImportCommand.MESSAGE_FAILURE, csv), exception.getMessage());
    }

    private Path writeCsv(String content) throws IOException {
        Path csv = temporaryDirectory.resolve("students.csv");
        return Files.writeString(csv, content);
    }
}
