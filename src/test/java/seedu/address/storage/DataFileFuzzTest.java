package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Grade;
import seedu.address.model.person.Person;

/**
 * Damages a valid data file in many random ways, to check that the file is either loaded or rejected with a
 * {@code DataLoadingException}, and never ends in another exception. A file that is loaded must also be saved
 * and read back as the same data. The damage is generated from a fixed seed, so a failure can be repeated.
 */
public class DataFileFuzzTest {

    private static final long SEED = 7;
    private static final int RUNS = 500;

    /** Values that replace a value in the file, and that a careless editor or a failing disk could produce. */
    private static final String[] REPLACEMENTS = {"null", "[]", "{}", "123", "-1", "1e999", "true", "\"\"",
        "\" \"", "\"a\"", "[null]", "[1]", "{\"x\":1}", "\"2026-13-45\"", "\"" + "a".repeat(500) + "\"",
        "[[[[[[[[]]]]]]]]"};

    private static final String JSON_CHARACTERS = "{}[]\",:0\\ x";
    private static final String BASE_LOGGER_NAME = "ab3";

    private static Level originalLogLevel;

    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void switchOffLogging() {
        // A damaged file is logged as a warning each time. Getting a logger first makes sure that LogsCenter
        // has set up the base logger that the loggers inherit their level from.
        LogsCenter.getLogger(DataFileFuzzTest.class);
        Logger baseLogger = Logger.getLogger(BASE_LOGGER_NAME);
        originalLogLevel = baseLogger.getLevel();
        baseLogger.setLevel(Level.OFF);
    }

    @AfterAll
    public static void restoreLogging() {
        Logger.getLogger(BASE_LOGGER_NAME).setLevel(originalLogLevel);
    }

    @Test
    public void readAddressBook_damagedDataFile_isLoadedOrRejectedAndLoadedDataCanBeSavedAgain()
            throws IOException {
        Path dataFile = temporaryFolder.resolve("classmates.json");
        Path copyFile = temporaryFolder.resolve("copy.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        storage.saveAddressBook(addressBookWithRecords());
        String validContent = Files.readString(dataFile, StandardCharsets.UTF_8);

        Random random = new Random(SEED);
        Map<String, String> firstFileOfEachProblem = new LinkedHashMap<>();
        for (int i = 0; i < RUNS; i++) {
            String damagedContent = damage(validContent, random);
            Files.writeString(dataFile, damagedContent, StandardCharsets.UTF_8);
            try {
                Optional<ReadOnlyAddressBook> loaded = storage.readAddressBook();
                storage.saveAddressBook(loaded.orElseThrow(), copyFile);
                assertEquals(new AddressBook(loaded.get()),
                        new AddressBook(storage.readAddressBook(copyFile).orElseThrow()));
            } catch (DataLoadingException expected) {
                // A file that cannot be used is reported in this way
            } catch (Throwable unexpected) {
                firstFileOfEachProblem.putIfAbsent(unexpected.getClass().getName() + ": "
                        + unexpected.getMessage(), damagedContent);
            }
        }

        String message = "Unexpected throwables, with the first file that caused each: " + firstFileOfEachProblem;
        assertTrue(firstFileOfEachProblem.isEmpty(), message);
    }

    private static AddressBook addressBookWithRecords() {
        Person alice = ALICE
                .withAttendance(new Attendance(LocalDate.of(2026, 9, 16), Attendance.Status.PRESENT))
                .withGrade(new Grade("Quiz 1", BigDecimal.valueOf(80)));
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(alice, BENSON));
        return addressBook;
    }

    /**
     * Returns {@code content} after one to three random changes: cutting it short, removing or replacing a
     * character, or replacing a whole value with a value that does not fit.
     */
    private static String damage(String content, Random random) {
        StringBuilder damaged = new StringBuilder(content);
        int changeCount = 1 + random.nextInt(3);
        for (int i = 0; i < changeCount; i++) {
            if (damaged.length() == 0) {
                damaged.append('x');
            }
            applyRandomChange(damaged, random.nextInt(damaged.length()), random);
        }
        return damaged.toString();
    }

    /**
     * Changes {@code json} at or after {@code position} in one of four ways, chosen with {@code random}.
     */
    private static void applyRandomChange(StringBuilder json, int position, Random random) {
        int kind = random.nextInt(5);
        if (kind == 0) {
            json.setLength(Math.max(1, position));
        } else if (kind == 1) {
            json.deleteCharAt(position);
        } else if (kind == 2) {
            json.setCharAt(position, JSON_CHARACTERS.charAt(random.nextInt(JSON_CHARACTERS.length())));
        } else {
            replaceValue(json, position, REPLACEMENTS[random.nextInt(REPLACEMENTS.length)]);
        }
    }

    /**
     * Replaces the first value after {@code position}, which is a quoted text or a number, with
     * {@code replacement}. Does nothing if there is no such value.
     */
    private static void replaceValue(StringBuilder json, int position, String replacement) {
        int start = json.indexOf(": ", position);
        if (start < 0 || start + 2 >= json.length()) {
            return;
        }
        start += 2;
        int end = start;
        if (json.charAt(start) == '"') {
            end = json.indexOf("\"", start + 1) + 1;
        } else {
            while (end < json.length() && ",\n}]".indexOf(json.charAt(end)) < 0) {
                end++;
            }
        }
        if (end > start) {
            json.replace(start, end, replacement);
        }
    }
}
