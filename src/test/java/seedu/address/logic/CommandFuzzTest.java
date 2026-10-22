package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Feeds generated commands to the parser and executes the ones that are parsed, to check that a bad command
 * only ever ends in a {@code ParseException} or a {@code CommandException}, and never in another exception or
 * a failed assertion. The input is generated from a fixed seed, so a failure can be repeated.
 */
public class CommandFuzzTest {

    private static final long SEED = 2103;
    private static final int RUNS = 10_000;

    /** The command words are mostly real ones. The commands that read a file are left out. */
    private static final String[] COMMAND_WORDS = {"add", "edit", "delete", "list", "tag", "untag", "attendance",
        "grade", "viewA", "viewG", "deleteG", "clear", "help", "exit", "LIST", "Delete", "frobnicate", ""};

    private static final String[] TOKENS = {"/name", "/class", "/email", "/group", "/members", "/date", "/status",
        "/assessment", "/score", "/tag", "/foo", "/", "//", "/NAME", "/Class", "John", "Amy Bee", "A1", "Sec 3-2",
        "a@b.co", "bad@", "@", "0", "1", "2", "3", "-1", "+1", "99999999999", "2147483647", "2147483648", "1.5",
        "1e3", ".5", "100", "101", "1,2", "1,,2", "1,2,", ",", "1, 2 ,3", "present", "late", "16-09-2026",
        "31-02-2026", "Quiz 1", "Group A", "friends", "Zoë", "名前", " ", " ", "\t", "\r",
        "\n", "\0", "\"", "'", "\\", "%s", "%1$s", "null", "NaN", "  ", " ", "s/o", "O'Neil", "John/class A1",
        "Peter (Pete) Lim", "T11/A", "#1", "﻿", "😀", "\uD800", "a".repeat(81), "a".repeat(300),
        "9".repeat(400), "a@" + "a".repeat(300) + "!"};

    /** In a template, %V is a value, %I is an index and %M is a list of indices. */
    private static final String[] TEMPLATES = {"add /name %V /class %V /email %V", "add /name %V /class %V",
        "edit %I /name %V /class %V /email %V", "edit %I /email", "edit %I /class %V", "delete %I",
        "delete /name %V /class %V", "tag /group %V /members %M", "untag /group %V /members %M",
        "attendance %I /date %V /status %V", "grade %I /assessment %V /score %V", "viewA /name %V /class %V",
        "viewG /name %V /class %V", "deleteG %I /assessment %V", "deleteG /name %V /class %V /assessment %V",
        "list %V", "DELETEG %I /ASSESSMENT %V", "Add /NAME %V /Class %V", "EDIT %I /Email %V",
        "tag /GROUP %V /MEMBERS %M", "grade %I /assessment %V /score %V /score %V"};

    private static final String[] VALUES = {"Alice Pauline", "alice  pauline", "Benson Meier", "A1", "a1", "B2",
        "friends", "Group A", "group   a", "Amy Bee", "X9", "a@b.co", "", " ", "present", "late", "16-09-2026",
        "Quiz 1", "50", "100", "0", "100.5", "-1", "Zoë", "s/o", "O'Neil", "Peter (Pete) Lim", "T11/A"};

    private static final String BASE_LOGGER_NAME = "ab3";

    private static Level originalLogLevel;

    @BeforeAll
    public static void switchOffLogging() {
        // The commands log every change, which would write tens of thousands of lines. Getting a logger first
        // makes sure that LogsCenter has set up the base logger that the loggers inherit their level from.
        LogsCenter.getLogger(CommandFuzzTest.class);
        Logger baseLogger = Logger.getLogger(BASE_LOGGER_NAME);
        originalLogLevel = baseLogger.getLevel();
        baseLogger.setLevel(Level.OFF);
    }

    @AfterAll
    public static void restoreLogging() {
        Logger.getLogger(BASE_LOGGER_NAME).setLevel(originalLogLevel);
    }

    @Test
    public void parseAndExecute_randomInput_throwsOnlyExpectedExceptions() {
        assertOnlyExpectedExceptions(CommandFuzzTest::randomCommand);
    }

    @Test
    public void parseAndExecute_wellFormedCommandsWithHostileValues_throwsOnlyExpectedExceptions() {
        assertOnlyExpectedExceptions(CommandFuzzTest::wellFormedCommand);
    }

    /**
     * Parses and executes {@code RUNS} commands made by {@code generator}, each on a new model, and checks that
     * no throwable other than {@code ParseException} and {@code CommandException} comes out of them.
     */
    private static void assertOnlyExpectedExceptions(Function<Random, String> generator) {
        Random random = new Random(SEED);
        AddressBookParser parser = new AddressBookParser();
        Map<String, String> firstInputOfEachProblem = new LinkedHashMap<>();

        for (int i = 0; i < RUNS; i++) {
            String input = generator.apply(random);
            try {
                Command command = parser.parseCommand(input);
                command.execute(newModel());
            } catch (ParseException | CommandException expected) {
                // A command that is wrong is reported to the user in this way
            } catch (Throwable unexpected) {
                firstInputOfEachProblem.putIfAbsent(unexpected.getClass().getName() + ": "
                        + unexpected.getMessage(), input);
            }
        }

        String message = "Unexpected throwables, with the first command that caused each: "
                + firstInputOfEachProblem;
        assertTrue(firstInputOfEachProblem.isEmpty(), message);
    }

    private static Model newModel() {
        return new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    /**
     * Returns a command word followed by up to seven tokens, mostly separated by a space.
     */
    private static String randomCommand(Random random) {
        StringBuilder command = new StringBuilder(pick(random, COMMAND_WORDS));
        int tokenCount = random.nextInt(8);
        for (int i = 0; i < tokenCount; i++) {
            command.append(random.nextInt(6) == 0 ? "" : " ").append(pick(random, TOKENS));
        }
        return command.toString();
    }

    /**
     * Returns a command that has the form of a real one, with values that may be wrong.
     */
    private static String wellFormedCommand(Random random) {
        String template = pick(random, TEMPLATES);
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < template.length(); i++) {
            char character = template.charAt(i);
            if (character != '%') {
                command.append(character);
                continue;
            }
            i++;
            command.append(fill(random, template.charAt(i)));
        }
        return command.toString();
    }

    /**
     * Returns the text for the placeholder {@code placeholder}, which is V, I or M.
     */
    private static String fill(Random random, char placeholder) {
        if (placeholder == 'V') {
            return random.nextInt(4) == 0 ? pick(random, TOKENS) : pick(random, VALUES);
        }
        if (placeholder == 'I') {
            return String.valueOf(random.nextInt(9) - 1);
        }
        if (placeholder == 'M') {
            return randomIndices(random);
        }
        throw new IllegalArgumentException("Unknown placeholder: " + placeholder);
    }

    /**
     * Returns one to five indices from 0 to 6, separated by commas.
     */
    private static String randomIndices(Random random) {
        StringBuilder indices = new StringBuilder();
        int count = 1 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            indices.append(i > 0 ? "," : "").append(random.nextInt(7));
        }
        return indices.toString();
    }

    private static String pick(Random random, String[] options) {
        return options[random.nextInt(options.length)];
    }
}
