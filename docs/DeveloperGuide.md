---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# ClassMates Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads and writes the app's saved data (the address book and the user preferences) on the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**Note:** The `import` command reads the CSV file that the user names directly in `Logic`, using `FileUtil` and `CsvParser` from `Commons`, and does not go through `Storage`. The file is input to a command and not saved data of the app, so `Storage` is only used afterwards to save the updated address book (see [Importing contacts from a file](#importing-contacts-from-a-file-feature)).

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. `MainWindow` also creates a `HelpWindow`, which is only shown when the user asks for help. All of these, including the `PersonCard` objects that `PersonListPanel` creates, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layout of each part is defined in a matching `.fxml` file in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* shows the contacts by binding `PersonListPanel` to the `ObservableList<Person>` returned by `Logic#getFilteredPersonList()`. JavaFX redraws the list whenever that list changes, so the UI does not have to be told to refresh after a command.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.
* remembers the commands that the user has entered, so that they can be recalled with the arrow keys (see [Command history](#command-history-feature)).

**How the UI handles a command**

1. `MainWindow` passes its `executeCommand` method to `CommandBox` as a `CommandBox.CommandExecutor`. When the user presses Enter on a non-empty command, `CommandBox` calls it.
2. `MainWindow#executeCommand(String)` calls `Logic#execute(String)` and shows the feedback of the returned `CommandResult` in `ResultDisplay`, unless the command word is `viewA` or `viewG`, in which case the feedback is shown in a pop-up window instead (see [Viewing attendance](#viewing-attendance-feature)).
3. If `Logic` throws a `ParseException` or a `CommandException`, `MainWindow` shows the message of the exception in `ResultDisplay`, and `CommandBox` keeps its text so that the user can correct it.
4. If the `CommandResult` asks for help, `MainWindow#handleHelp()` opens the `HelpWindow`, or focuses it if it is already open. If it asks to exit, `MainWindow#handleExit()` saves the size and position of the window with `Logic#setGuiSettings(GuiSettings)` and hides the window.

**What the parts show**

* `ResultDisplay` shows the feedback or the error message of the last command, and `StatusBarFooter` shows the location of the data file.
* `HelpWindow` shows a summary of the format of every command, and a button that copies the link to the User Guide.
* A separate pop-up window, which `MainWindow` creates for each `viewA` or `viewG` command, shows the history of a contact. It is not a `UiPart`, since it is built in code from a `Stage` with a heading, a read-only text area and a `Close` button.
* Each `PersonCard` shows the displayed index, the name, the class, the email (if the contact has one), the groups of the contact in alphabetical order, and the 3 most recent attendance records and grades, followed by a line such as `+2 older record(s)` if there are more (see `PersonCard`).
* `UiManager` shows an error dialog and then exits the app if the window fails to start.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, `LogicManager` keeps a copy of the address book as it is before the command and passes the command text to an `AddressBookParser` object. This rejects a command that is too long (see [Limits on the size of the input](#limits-on-the-size-of-the-input)), splits the command word from the arguments, replaces every tab and other kind of space in the arguments by a plain space (except in the path of `import`, which can contain them), creates a parser that matches the command word (e.g., `DeleteCommandParser`) and uses it to parse the arguments.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteByIndexCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a contact).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation. In the `delete 1` example, `DeleteByIndexCommand` first asks the `Model` for the displayed list to find the contact at the index, and then deletes it.
1. After the command has been executed, `LogicManager` saves the whole address book with `Storage#saveAddressBook(ReadOnlyAddressBook)`, whether or not the command changed it. If the file cannot be saved, or if parsing or executing the command throws a `RuntimeException` (which is a bug, not a mistake of the user), `LogicManager` sets the data of the `Model` back to the copy that it made before the command, and throws a `CommandException` with a message that says that the change was undone. See [Saving and loading data](#saving-and-loading-data) and [Unexpected errors](#unexpected-errors).
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class finds the `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`) of the command word in a map. The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`), which the `AddressBookParser` returns as a `Command` object. Command words are matched ignoring case (only the English letters `A-Z` are converted, by `StringUtil#toAsciiLowerCase(String)`), so `LIST` and `viewa` are the commands `list` and `viewA`.
* All `XYZCommandParser` classes implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing. `ExitCommand` and `HelpCommand` take no arguments, so their entries in the map are lambda expressions that ignore the arguments and create the command. `ClearCommand` has a `ClearCommandParser`, because `clear` deletes all contacts and therefore does not ignore extra text (see [Clearing all contacts](#clearing-all-contacts)).
* Parameters are introduced by prefixes such as `/name`. `ArgumentTokenizer` recognizes a prefix that is preceded by a whitespace and followed by a whitespace or the end of the input, whatever the case of its letters, and stores the values in an `ArgumentMultimap`, which names every repeated prefix in its error message. `ParserUtil#requireNoUnrecognizedPrefixes(String, String, Prefix...)` rejects a word that looks like a prefix that the command does not accept, and `ParserUtil#requireNoStuckPrefixes(String)` rejects a prefix that is stuck to the text before it (see [Validation of names, classes and assessment names](#validation-of-names-classes-and-assessment-names-feature)). `ListCommandParser` and `ImportCommandParser` do neither, because they take the whole argument as one text, which is a keyword or a file path.
* A parser reports the first problem that it finds. Unrecognized and repeated parameters are always checked first.
* `delete`, `attendance`, `grade`, `deleteA` and `deleteG` identify the contact either by an index or by `/name` and `/class`. A command word therefore does not always map to a single `Command` class: for example, `DeleteCommandParser` returns a `DeleteByIndexCommand` when given an index, and a `DeleteByNameAndClassCommand` when given `/name` and `/class`. Both extend the abstract `DeleteCommand`, which holds the shared messages and operations. `AttendanceCommand`, `GradeCommand`, `DeleteAttendanceCommand` and `DeleteGradeCommand` are structured in the same way. For `attendance` and `grade`, `ParserUtil#isIdentifiedByIndex(ArgumentMultimap)` tells the parser which way was used. The two ways are not allowed together.
* `viewA` and `viewG` identify the contact by `/name` and `/class` only. `ViewAttendanceCommandParser` and `ViewGradeCommandParser` extend `NameAndClassCommandParser`, which does the parsing that they share. `tag` and `untag` take the same arguments, so `TagCommandParser` and `UntagCommandParser` extend `GroupMembershipCommandParser`, which does the parsing, and a subclass only passes its usage message and creates its command.
* `ListCommandParser` returns a `ListCommand` that lists all contacts when the argument is empty, and otherwise a `ListCommand` that holds a `PersonMatchesKeyword` predicate. `EditCommandParser` creates an `EditCommand` that holds an `EditPersonDescriptor` with the details to change. `ImportCommandParser` takes the whole trimmed argument as the file path, and `ImportCommand` checks the file when it runs.
* The name and class that `viewA`, `viewG`, `delete`, `attendance`, `grade`, `deleteA` and `deleteG` receive in the name-and-class form are plain strings that are not checked against the rules of `Name` and `ClassName`, so a name or class that no contact has is reported when the command runs.

**Design considerations**

**Aspect: How a command that identifies a contact in two ways is structured**

* **Alternative 1 (current choice):** An abstract command (such as `DeleteCommand`) with one subclass for each way of identifying the contact, and the shared operation in the abstract class.
  * Pros: Each `execute` method does one thing and can be tested on its own, an invalid combination, such as an index together with a name, cannot be represented, and the shared logic and messages are written once.
  * Cons: One command word maps to two classes, so the parser must choose between them.
* **Alternative 2:** A single command with an optional index and an optional name and class.
  * Pros: Only one command class.
  * Cons: `execute` must branch on which fields are set, and an invalid combination of fields is possible. This is how `attendance` and `grade` were first written.

**Aspect: How the contact is identified**

* **Alternative 1 (current choice):** `delete`, `attendance`, `grade`, `deleteA` and `deleteG` accept either the index in the displayed list or `/name NAME /class CLASS`. `edit`, `tag` and `untag` accept only the index, and `viewA` and `viewG` only the name and class.
  * Pros: The index is short to type and is convenient after a `list`, and the name and class do not depend on what is displayed, which suits a contact that is hidden by a search. `edit` leaves `/name` and `/class` free to mean the new values, and `tag` and `untag` need only the index for many members at once.
  * Cons: The index depends on the displayed list, so the wrong contact is changed if the list is not what the user expects. The name and class are long to type, and two forms must be parsed and explained.
* **Alternative 2:** Name and class only.
  * Pros: Does not depend on the displayed list.
  * Cons: A long command for several members, and `edit` would need other parameters such as `/newname` for the new values.

**Aspect: Which contacts a search by name and class covers**

* **Alternative 1 (current choice):** The whole address book.
  * Pros: Works whatever is displayed, so the user does not have to run `list` first.
  * Cons: Can change a contact that is not currently displayed.
* **Alternative 2:** Only the displayed contacts.
  * Pros: Consistent with the index, and the affected contact is visible.
  * Cons: Fails after a search that hides the contact, which defeats the purpose of using a name.

**Aspect: Whether the name and class are validated**

* **Alternative 1 (current choice):** They are only compared with the stored contacts, ignoring case and extra spaces.
  * Pros: The same mistake always gives the same message, `Contact not found`, and the rules for `add` can change without affecting other commands.
  * Cons: A name with a typo in a character that is not allowed gets no hint about the allowed format.
* **Alternative 2:** Validate them with the rules for `add`.
  * Pros: A name that can never exist is reported as a format error.
  * Cons: Duplicates the validation rules, and the commands gave different messages for the same mistake, as `attendance` and `grade` did before.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* is used through the `Model` interface, which `ModelManager` implements. `ModelManager` holds one `AddressBook`, one `UserPrefs` and the filtered list of `Person` objects. Commands use it to check for a duplicate (`hasPerson(Person)`), to add, delete and replace a contact (`addPerson(Person)`, `deletePerson(Person)` and `setPerson(Person, Person)`), to find a contact by name and class (`findPerson(String, String)`), and to change which contacts are displayed (`updateFilteredPersonList(Predicate<Person>)`). `addPerson` puts the contact at the top of the list and shows all contacts again (see [Order of contacts](#order-of-contacts-feature)), while `setPerson` replaces a contact in place, so it keeps its position. These methods have preconditions, for example that the contact to add is not already in the address book. The commands check them first, so that a problem is reported to the user as a `CommandException`, and not as the unchecked `DuplicatePersonException` or `PersonNotFoundException` that `UniquePersonList` throws.
* identifies a `Person` by its `Name` and `ClassName` (see `Person#isSamePerson(Person)`). Both are compared ignoring case and extra whitespace, so the same name may appear in different classes but not twice in the same class.
* keeps `Person` immutable. A change returns a copy, such as `Person#withTag(Tag)` (adds a contact to a group), `Person#withoutTag(Tag)`, `Person#withAttendance(Attendance)` and `Person#withGrade(Grade)`.
* compares `Tag` objects ignoring case, leading and trailing whitespace, and repeated spaces, so `GroupA` and `  groupa ` are the same tag. A `Tag` is 1 to 80 characters long and can contain letters, digits, spaces, hyphens, and underscores. It keeps the casing it was created with, so that is what the UI shows.
* stores each contact's attendance as a list of `Attendance` objects, each with a date and a status (`PRESENT`, `ABSENT` or `LATE`), and each contact's grades as a list of `Grade` objects, each with an assessment name and a score from `Grade.MIN_SCORE` (0) to `Grade.MAX_SCORE` (100). A contact has at most one attendance record for a date and one grade for an assessment (assessment names are compared ignoring case). `Person#withAttendance(Attendance)` and `Person#withGrade(Grade)` throw an `IllegalArgumentException` for a duplicate, but only as a safeguard, since the commands check first.
* treats the `Email` of a `Person` as optional. `Person#getEmail()` returns an `Optional<Email>`, which is empty if the contact has no email.
* keeps the `Person` objects in the order they are displayed, which is from the most recently added to the oldest (see [Order of contacts](#order-of-contacts-feature)).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is used through the `Storage` interface, which has a read method, a save method and a file path method for each of the two data files: `readAddressBook()`, `saveAddressBook(ReadOnlyAddressBook)` and `getAddressBookFilePath()`, and `readUserPrefs()`, `saveUserPrefs(ReadOnlyUserPrefs)` and `getUserPrefsFilePath()`.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file). `MainApp` creates them with the files `data/classmates.json` for the address book and `preferences.json` for the user preferences.
* converts the data with the Jackson library, using `JsonUtil` and `FileUtil` from `Commons`. `JsonSerializableAddressBook` holds a list of `JsonAdaptedPerson`, and each `JsonAdaptedPerson` holds the name, the class name, the email, and lists of `JsonAdaptedTag`, `JsonAdaptedAttendance` and `JsonAdaptedGrade`. Each adapter has a `toModelType()` method that rebuilds the model object, and throws an `IllegalValueException` if a value breaks the rules of the model. The contacts are saved in the order they are displayed, and that order is kept when they are read back.
* writes a file by writing a temporary file first, which then replaces the file in one step (see `FileUtil#writeToFile(Path, String)`), so that a failure while writing keeps the old content of the file.
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

**What is checked when the address book is loaded**

* The name, the class name, the email, every tag, every attendance record and every grade record are checked against the rules of the model classes. A contact must have a name and a class name. The email, and the lists of tags, attendance records and grade records, are optional, so a data file that was saved before attendance or grades existed can still be loaded. Properties in the file that the app does not know are ignored.
* The date of an attendance record must be written as `YYYY-MM-DD` with exactly four digits in the year. A date in the future is accepted, because the file is not checked against the clock.
* A value that breaks a rule, a missing name or class name, or two contacts with the same name and class makes the whole file invalid. No contact from an invalid file is loaded.

**What happens when the files cannot be loaded or saved**

* `readAddressBook()` returns an empty `Optional` if the data file does not exist, and throws a `DataLoadingException` if the file cannot be read as JSON or is invalid as described above. `readUserPrefs()` behaves the same way for the preferences file.
* At start-up, `MainApp` uses the sample address book if the data file does not exist. If the data file cannot be loaded, it keeps the unreadable file as a backup and starts with an empty address book (see [Protecting the data file](#protecting-the-data-file-feature)). If the preferences file does not exist or cannot be loaded, it uses the default preferences, and saves them at every start-up, which creates a missing preferences file and replaces an unreadable one.
* `saveAddressBook(ReadOnlyAddressBook)` writes the whole address book each time. It creates the data file and the folders above it if they are missing. An `IOException` is passed on to `LogicManager`, which undoes the command and reports the problem to the user as a `CommandException` (see [Logic component](#logic-component)).

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Listing and searching contacts feature

#### Implementation

The `list` command shows all contacts, or, when it is given a keyword, only the contacts that match the keyword. It is implemented by `ListCommandParser`, `ListCommand` and `PersonMatchesKeyword`.

* `ListCommandParser#parse(String)` takes the whole trimmed argument as one keyword, so a full name or class with spaces is one keyword. If the argument is empty, it creates a `ListCommand` that shows all contacts.
* `PersonMatchesKeyword` implements `Predicate<Person>`. `PersonMatchesKeyword#test(Person)` returns true if the keyword is a substring of the name, class name, or any tag name of the contact. Both are first normalized with `StringUtil#toComparisonKey(String)`, which ignores case and extra spaces and the different ways of typing an accented letter. The fields, and each tag, are checked separately, so a keyword cannot match across two fields or two tags. The email is not checked, and the keyword is matched as plain text and not as a regular expression, so symbols such as `.` and `/` are matched as typed.
* `ListCommand#execute(Model)` calls `Model#updateFilteredPersonList(Predicate)` with the predicate, which is `Model#PREDICATE_SHOW_ALL_PERSONS` when no keyword is given. This replaces any earlier filter, so `list` always searches the whole address book and a plain `list` clears a previous search. It never modifies the address book. With a keyword, the success message shows the number of contacts in the filtered list, and an empty result is not an error.

The contacts that match a keyword are displayed in the same order as in the whole list, from the most recently added to the oldest, because a filter only hides contacts (see [Order of contacts](#order-of-contacts-feature)).

#### Design considerations

**Aspect: How a keyword is matched**

* **Alternative 1 (current choice):** Case-insensitive substring match on the name, class and tags.
  * Pros: Supports partial matches, such as `jo` for `John` or `2103` for `CS2103`, with one simple predicate.
  * Cons: A short keyword, such as `a`, matches many contacts.
* **Alternative 2:** Whole-word match, so that `Han` does not match `Hans`.
  * Pros: More precise results.
  * Cons: Cannot find a contact from part of a name or class, which the search is meant to support.

**Aspect: How the argument is split into keywords**

* **Alternative 1 (current choice):** The whole argument is one keyword.
  * Pros: A full name or a class with spaces, such as `Tutorial 1`, can be searched.
  * Cons: Cannot search for several separate keywords at once. A keyword with several words cannot match across two tags, such as `Group A owesMoney`.
* **Alternative 2:** Split the argument on whitespace and show the contacts that match any of the words.
  * Pros: Allows searching for several keywords at once.
  * Cons: A full name or a class with spaces can no longer be searched as a phrase.

**Aspect: What the search is applied to**

* **Alternative 1 (current choice):** Always search the whole address book.
  * Pros: Predictable, as the result does not depend on the earlier command.
  * Cons: Cannot narrow down the previous result by searching again.
* **Alternative 2:** Search only the contacts currently displayed.
  * Pros: Allows narrowing down a result step by step.
  * Cons: The result depends on what is displayed, and a contact can be missed after an earlier search.

### Deleting a contact feature

#### Implementation

The `delete` command removes one contact, identified either by its index in the displayed list or by its name and class. `DeleteCommandParser` returns a `DeleteByIndexCommand` or a `DeleteByNameAndClassCommand`, which both extend the abstract `DeleteCommand` (see [Logic component](#logic-component), which also explains why the command is structured in this way).

* `DeleteByIndexCommand#execute(Model)` rejects an index that is larger than the displayed list, then deletes the contact at that index.
* `DeleteByNameAndClassCommand#execute(Model)` searches the whole address book, not the displayed list, using `Person#hasNameAndClass(String, String)`. It reports `Contact not found` if no contact matches.

### Order of contacts feature

#### Implementation

The contacts are displayed from the most recently added to the oldest. The order is kept in the list itself, so the displayed order, the order in memory and the order in the data file are all the same.

* `UniquePersonList#add(Person)` inserts the contact at index 0 instead of at the end. `AddCommand#execute(Model)` reaches it through `Model#addPerson(Person)` and `AddressBook#addPerson(Person)`.
* `ModelManager#addPerson(Person)` also calls `Model#updateFilteredPersonList(Predicate)` with `Model#PREDICATE_SHOW_ALL_PERSONS`, so the new contact is visible at the top even if a search was displayed.
* A filter only hides contacts and never reorders them, so search results are also shown from the most recently added to the oldest.
* `Model#setPerson(Person, Person)` replaces a contact in place, so `edit`, `tag` and `untag` do not change the position of a contact.
* `ImportCommand#execute(Model)` adds the contacts of the file from the last row to the first, so that the first row ends up at the top and the imported contacts appear in the order of the file.
* Code that builds an address book from a whole list must not add the contacts one by one, as that would reverse their order. `JsonSerializableAddressBook#toModelType()`, `SampleDataUtil#getSampleAddressBook()` and `TypicalPersons#getTypicalAddressBook()` use `AddressBook#setPersons(List)`, which keeps the given order.

#### Design considerations

**Aspect: Where the order is kept**

* **Alternative 1 (current choice):** The list is stored in the displayed order, and `add` inserts at the front.
  * Pros: Simple, with no extra state. Indices, filters, saving and loading all see the same order.
  * Cons: Every place that builds a list from several contacts must keep the given order, as described above.
* **Alternative 2:** Store the contacts in the order they were added and display them in reverse.
  * Pros: `add` keeps appending, and loading needs no change.
  * Cons: JavaFX has no reversed `ObservableList`, so the displayed list would need its own sorting or mapping. Indices would then refer to a different order from the stored list.
* **Alternative 3:** Give each `Person` a timestamp and sort the displayed list by it.
  * Pros: The order survives any change to the stored order.
  * Cons: Adds a field to `Person` and to the data file, and data files from before the change have no timestamps.

### Validation of names, classes and assessment names feature

#### Implementation

A name, a class name and an assessment name are validated when they are created, so that a value from a command, a CSV file or the data file is held to the same rule. The rules reject only what causes a problem, so that names that real students have are not rejected.

* `Name#getConstraintViolation(String)` returns the first problem of a name: it is blank, longer than `Name#MAX_LENGTH` characters, or has a character that is not allowed. `Name#VALIDATION_REGEX` allows a Latin letter followed by any accents, a printable ASCII character other than a comma, and the typographic apostrophes `‘` and `’`. This allows digits and symbols such as `(`, `#` and `/`, and rejects control characters, invisible characters, and letters of other scripts such as Chinese or Cyrillic. The regex is written so that a long invalid name is rejected without backtracking.
* `StringUtil#toNormalizedForm(String)` converts text to Unicode normalization form C before a name is validated and stored, and `StringUtil#toComparisonKey(String)` converts it to form KC before it is compared. This way, two texts that look the same, such as `é` typed as one character or as `e` followed by an accent, or a full-width `Ａ` and `A`, do not become two names or two assessments.
* `ClassName#VALIDATION_REGEX` allows letters, digits, spaces and `- . _ / ( )`, and the first character must be a letter or a digit. A class name is ASCII only, as class codes are.
* `Grade#getAssessmentNameConstraintViolation(String)` applies the same rule as a name after normalizing it, except that it also allows a comma. A comma is only kept out of a name because it separates the values of a CSV file, and an assessment name is never written to one. `CsvParser` can read a quoted value that contains a comma, but a name that contains one is then rejected, with `Invalid contact data on row N`.
* `ParserUtil#requireNoEmbeddedPrefix(String, String)` rejects a value that contains a prefix, such as `/class`, that is followed by a space or the end of the value. `ArgumentTokenizer` only reads a prefix that has a space before it, so without this check, `edit 1 /name John/class B` would make `/class B` part of the name without any error. `ParserUtil#parseName(String)`, `ParserUtil#parseClassName(String)` and `ParserUtil#parseAssessmentName(String)` call it, with the prefixes in `CliSyntax#ALL_PREFIXES`. `ParserUtil#requireNoStuckPrefixes(String)` makes the same check for a whole command, such as the `/status` in `attendance 1 /date 16-09-2026/status late`, which would otherwise be reported as a missing status.
* The check is in `ParserUtil` and not in `Name` or `ClassName`, as the model must not depend on the command syntax. The values that are read from the data file are therefore only checked by the model rules, while `ImportCommand` reads the values of a CSV file with `ParserUtil`, so they are also checked for a missing space.

#### Design considerations

**Aspect: Which characters a name can contain**

* **Alternative 1 (current choice):** Latin letters with accents, and printable ASCII other than a comma.
  * Pros: Covers the names in languages that use Latin letters, such as Spanish or Vietnamese, and names with digits or symbols that are used in practice, such as `Tan (Jun Wei)`. The text can be shown with the fonts of the application.
  * Cons: A name in another script, such as `李明`, must be written with Latin letters.
* **Alternative 2:** Any letter of Unicode.
  * Pros: A name can be written as it is.
  * Cons: The fonts of the target computers are not known, matching the letters of some scripts is not simple, and the same text can have several forms that look alike.
* **Alternative 3:** English letters only, as before.
  * Pros: Simplest.
  * Cons: Rejects `José`, `O’Brien` and `Tan (Jun Wei)`, so a CSV file of a class with such a name cannot be imported at all.

**Aspect: How a missing space before a parameter is found**

* **Alternative 1 (current choice):** Reject a value that contains a prefix, in `ParserUtil`.
  * Pros: Small, and the parsing of the other values does not change. The message names the parameter.
  * Cons: A value that really contains such text, such as the class `A1/name`, is rejected.
* **Alternative 2:** Do not allow `/` in a class or an assessment name, as before.
  * Pros: Needs no check.
  * Cons: Rejects classes such as `T11/A`.
* **Alternative 3:** Make `ArgumentTokenizer` recognize a prefix that has no space before it.
  * Pros: One place for all the commands.
  * Cons: It would split `s/o` and other text that has a `/` in it.

### Protecting the data file feature

#### Implementation

The data file holds everything that a user has entered, and users are allowed to edit it by hand. The following makes sure that a file that cannot be read, or a failure while saving, does not lose their contacts.

* `JsonUtil#deserializeObjectFromJsonFile(Path, Class)` removes the byte order mark that some editors put at the start of a UTF-8 file, as the JSON parser rejects it. `CsvParser` does the same for the files that `import` reads. `JsonSerializableAddressBook#toModelType()` and `JsonAdaptedPerson#toModelType()` throw an `IllegalValueException` for a `null` entry in a list of the data file, as the `NullPointerException` that it caused was not caught by `MainApp`, and the application stopped before it showed a window.
* `MainApp#initModelManager(Storage, ReadOnlyUserPrefs)` catches the `DataLoadingException` of a file that cannot be read, and calls `MainApp#keepUnreadableDataFile(Storage)`. This renames the file with `FileUtil#moveToBackup(Path)`, to its name followed by `.bad-` and the time, and then saves an empty address book to the original path, because the sample data is only used when there is no data file, and the next run would otherwise show it again.
* `MainApp#keepUnreadableDataFile(Storage)` returns a message for the user, which `MainApp` gives to `UiManager`, which gives it to `MainWindow` to show in the `ResultDisplay`. If the file cannot be renamed, for example because another program has locked it, the file is left as it is, nothing is saved, and the message says that the next command replaces the file.
* `FileUtil#writeToFile(Path, String)` writes the text to a temporary file in the same folder, so that it is on the same file system, and then moves it over the target in one step (`ATOMIC_MOVE`, or a plain move if the file system does not support it). The temporary file is deleted afterwards. A test can make the atomic move fail with `FileUtil#replaceFile(Path, Path, FileMover)`, as the file system of a test cannot be made to refuse it.

#### Design considerations

**Aspect: What to do with a data file that cannot be read**

* **Alternative 1 (current choice):** Rename the file, start with an empty list, save the empty list, and tell the user.
  * Pros: The content is never lost, the next run is clean, and the user knows what happened and where the file is.
  * Cons: The user has to rename the file back by hand, and each failure leaves another file in the folder.
* **Alternative 2:** Start with an empty list and leave the file, as before.
  * Pros: Nothing to implement.
  * Cons: The next command saves the empty list over the file, and the content is lost.
* **Alternative 3:** Show an error and do not start.
  * Pros: The file is never touched.
  * Cons: The user cannot use the application until the file is fixed, which is hard for someone who does not know JSON.

**Aspect: How a file is written**

* **Alternative 1 (current choice):** Write a temporary file, then replace the target with it.
  * Pros: The target is either the old content or the new content, whatever happens while writing.
  * Cons: A temporary file is left behind if the process is killed between the two steps, and the folder must allow new files.
* **Alternative 2:** Write the target directly, as before.
  * Pros: Simplest.
  * Cons: The target is cut to zero length before the new content is written, so a crash or a full disk leaves it empty or half written.

### Linking contacts to a group feature

#### Implementation

The `tag` command (`tag /group GROUP_NAME /members INDEX[,INDEX]...`) links the contacts at the given indices of the displayed list to a group. A group is represented as a `Tag` on each of its members, so a contact can be in several groups. It is implemented by `TagCommandParser` and `TagCommand`. Most of their work is done by the abstract classes `GroupMembershipCommandParser` and `GroupMembershipCommand`, which they share with the [`untag` command](#removing-contacts-from-a-group-feature).

* `GroupMembershipCommandParser#parse(String)` parses the group name and the members with `ParserUtil#parseGroupName(String)` and `ParserUtil#parseMemberIndices(String)`, which checks the entries from left to right and reports an empty entry or an entry that is not a positive integer before an index that appears more than once. `TagCommandParser` only passes the usage message of `tag` and creates the `TagCommand`.
* `GroupMembershipCommand#execute(Model)` is atomic. It first finds every contact, and reports the indices that are not in the displayed list, naming at most the first `GroupMembershipCommand.MAX_INDICES_SHOWN` (10) of them and then how many more there are. It then calls `GroupMembershipCommand#requireMembersCanBeChanged(List<Person>, Tag)`. `TagCommand` implements it by checking that none of the contacts is already in the group (`Person#hasTag(Tag)`), and it names every contact that is. Only after both checks pass does the command replace each contact with `GroupMembershipCommand#changeMembership(Person, Tag)`, which is `Person#withTag(Tag)` for `tag`, using `Model#setPerson(Person, Person)`. The displayed list is left as it was, so the indices stay valid.
* The constructor rejects an empty list of indices, and a list in which an index is repeated, because a contact that was already replaced could not be replaced a second time. The parser guarantees both by rejecting empty and repeated members.
* The success message lists the linked contacts in the order the indices were given. It shows the group name with the casing it was typed in, after leading and trailing spaces are removed and repeated spaces are collapsed. Group names are compared ignoring case, but each contact keeps the casing it was linked with.

#### Design considerations

**Aspect: How a group is represented**

* **Alternative 1 (current choice):** A group is a `Tag` on each of its members.
  * Pros: Reuses the existing `Tag` class, storage and `list` search, and a contact can be in several groups without any new data structure.
  * Cons: A group has no identity of its own. It exists only while a contact has the tag, and it cannot hold group-level data.
* **Alternative 2:** A separate `Group` class that stores its members.
  * Pros: Allows group-level data and operations.
  * Cons: Adds a new entity to `Model` and `Storage`, and must be kept in sync when contacts are deleted.

**Aspect: What happens when some contacts cannot be linked**

* **Alternative 1 (current choice):** All or nothing. If any index is invalid or any contact is already in the group, no contact is linked.
  * Pros: The result is predictable, and the user can correct the command and run it again without working out who was linked.
  * Cons: One bad index blocks the other contacts.
* **Alternative 2:** Link the contacts that can be linked and report the rest.
  * Pros: Less retyping.
  * Cons: The user must work out which contacts were linked, and the data is left half-changed.

### Removing contacts from a group feature

#### Implementation

The `untag` command (`untag /group GROUP_NAME /members INDEX[,INDEX]...`) removes the contacts at the given indices of the displayed list from a group. It is the opposite of [`tag`](#linking-contacts-to-a-group-feature), and it is implemented by `UntagCommandParser` and `UntagCommand`.

* `UntagCommandParser` extends `GroupMembershipCommandParser`, as `TagCommandParser` does. It accepts the same arguments, in the same way and with the same problems reported in the same order. It passes the usage message of `untag` and creates the `UntagCommand`.
* `UntagCommand` extends `GroupMembershipCommand`, as `TagCommand` does, so it also finds every contact first, and is atomic. `UntagCommand#requireMembersCanBeChanged(List<Person>, Tag)` checks that every contact is in the group, with `Person#hasTag(Tag)`, and names every contact that is not. `UntagCommand#changeMembership(Person, Tag)` returns `Person#withoutTag(Tag)`.
* `Person#withoutTag(Tag)` returns a copy of the contact without the tag. Tags are compared ignoring case, so `group a` removes `Group A`. The other tags, the email, the attendance records and the grade records are kept.
* The success message lists the contacts in the order the indices were given, and shows the group name with the casing it was typed in. A group is not stored as an object of its own, so it stops existing when the last contact is removed from it.

#### Design considerations

**Aspect: Where contacts are removed from a group**

* **Alternative 1 (current choice):** A separate `untag` command, with the same parameters as `tag`.
  * Pros: Removes several contacts from a group in one command, which suits groups such as the members of a team. The prefixes, messages and rules are the same as for `tag`, and `edit` stays limited to the details of one contact.
  * Cons: Moving a contact from one group to another takes two commands, and the second can fail after the first has worked. It is one more command to learn.
* **Alternative 2:** A `/untag` parameter of `edit`, which removes the groups of one contact.
  * Pros: One command can change all the details of a contact, and can add and remove groups at the same time.
  * Cons: Two commands change groups, with different prefixes and messages, and the error messages of `edit` double. Removing many contacts from one group takes one command for each contact.

**Aspect: What happens when a contact is not in the group**

* **Alternative 1 (current choice):** It is an error, and no contact is changed.
  * Pros: Consistent with `tag`, which rejects a contact that is already in the group. A typo in the name of the group is noticed.
  * Cons: The same command cannot be run twice, as the second run fails.
* **Alternative 2:** Ignore the contacts that are not in the group.
  * Pros: Simpler, and a command can safely be repeated.
  * Cons: A typo in the name of the group does nothing, and the user is told that the contacts were removed.

**Aspect: How the code of `tag` and `untag` is shared**

* **Alternative 1 (current choice):** Abstract classes `GroupMembershipCommand` and `GroupMembershipCommandParser`, with a few methods that `TagCommand` and `UntagCommand` implement, and `TagCommandParser` and `UntagCommandParser` supply.
  * Pros: The finding of the contacts, the all-or-nothing rule and the parsing are written once. A change to one command's rules cannot make the two commands differ by accident.
  * Cons: A reader must look at the base class to see what a command does.
* **Alternative 2:** One `TagCommand` with a flag that tells whether to add or remove.
  * Pros: Fewer classes.
  * Cons: The flag appears in every step, and two commands share a name, a usage message and an error message that should differ.
* **Alternative 3:** Copy `TagCommand` and `TagCommandParser` for `untag`.
  * Pros: Each class is simple to read.
  * Cons: The same code exists twice, and a fix has to be made in both places.

### Editing a contact feature

#### Implementation

The `edit` command (`edit INDEX [/name NAME] [/class CLASS] [/email [EMAIL]]`) changes the name, class or email of the contact at the given index of the displayed list. It is implemented by `EditCommandParser`, `EditCommand` and its nested class `EditPersonDescriptor`.

* `EditPersonDescriptor` stores the details to change. The name, class and email replace the current values when they are set. `EditPersonDescriptor#isEmailCleared()` means that the email is removed, and it takes priority over the email that is set. `EditPersonDescriptor#isAnyFieldEdited()` tells whether anything is to be changed.
* `EditCommandParser#parse(String)` takes the index from the text before the first parameter and rejects a command in which no detail is given. A name or class without a value is invalid, but an email without a value marks the email as removed.
* `EditCommand#execute(Model)` first checks that the index is in the displayed list. `EditCommand#createEditedPerson(Person, EditPersonDescriptor)` then creates a new `Person`, because `Person` is immutable. A detail that is not set is taken from the contact, as are its tags, attendance records and grade records, which is why an edit never loses them, even if the name or the class changes. The groups of a contact are changed with the `tag` and `untag` commands (see [Linking contacts to a group](#linking-contacts-to-a-group-feature) and [Removing contacts from a group](#removing-contacts-from-a-group-feature)).
* A contact is identified by its name and class (`Person#isSamePerson(Person)`). If the edited contact is not the same contact as the one before the edit, and `Model#hasPerson(Person)` is true for it, the command fails with the same message as `add`. Because `Person#isSamePerson(Person)` ignores case and extra spaces, changing only the capitalization of a name is allowed, and a contact is never compared with itself.
* `Model#setPerson(Person, Person)` is called last, after every check has passed, so a failed edit changes nothing. It replaces the contact in place, so the order of the contacts does not change (see [Order of contacts](#order-of-contacts-feature)). The command then calls `Model#updateFilteredPersonList(Predicate)` with `Model#PREDICATE_SHOW_ALL_PERSONS`, as `add` does, because an edited name or class may no longer match a search that is displayed, and the user would otherwise see the contact disappear.

#### Design considerations

**Aspect: Where the groups of a contact are changed**

* **Alternative 1 (current choice):** Not in `edit`. Groups are changed with the `tag` and `untag` commands, which work on several contacts at once.
  * Pros: `edit` stays limited to three details of one contact. Groups are usually changed for many contacts, such as a whole team, so one command does it. The two group commands use the same prefixes and messages.
  * Cons: Moving a contact from one group to another takes two commands. Changing the details and the groups of a contact at once takes several commands.
* **Alternative 2:** `/tag` and `/untag` parameters of `edit`.
  * Pros: One command can change everything about one contact, including moving it between groups.
  * Cons: Two commands change groups, with different prefixes and messages. The errors of `edit` double, as every group problem also applies to it. Removing many contacts from a group takes one command for each contact.
* **Alternative 3:** `/tag` replaces all the tags of the contact, as in the AB3 template.
  * Pros: Only one parameter.
  * Cons: Removing one group means typing all the other groups again, and a group that is left out is removed without a warning.

**Aspect: How the email of a contact is removed**

* **Alternative 1 (current choice):** `/email` with no value removes the email.
  * Pros: The email is optional, so removing it needs no extra parameter.
  * Cons: Differs from `add`, where `/email` with no value is an error, and a user who forgets the value removes the email instead of getting an error.
* **Alternative 2:** `/email` with no value is an error, and another parameter, such as `/noemail`, removes the email.
  * Pros: An email cannot be removed by accident.
  * Cons: One more parameter to learn and to keep consistent.

### Importing contacts from a file feature

#### Implementation

The `import` command adds multiple contacts from a CSV file. It is implemented by `ImportCommandParser`, `ImportCommand` and `CsvParser`.

* `ImportCommandParser#parse(String)` removes leading and trailing whitespace from the path while preserving spaces within it. It rejects empty paths and paths containing line breaks.
* `ImportCommand#resolvePath(String)` uses an absolute path directly, and resolves a relative path from the working directory, or, if the application is run as a JAR file, from the folder containing the JAR file. `ImportCommand#validatePath(Path)` then checks that the path refers to an existing, readable regular file with the `.csv` extension, and reports the first check that fails. `FileUtil#readFromFile(Path)` reads the complete file as UTF-8 text.
* `CsvParser#parse(String)` parses the CSV syntax (comma-separated fields, quoted fields, escaped double quotes, embedded line endings, `LF` and `CRLF` record endings, and a UTF-8 byte-order mark) and validates the header. The header is converted into a `CsvHeader`, which records the index of each column. Column names are compared ignoring case and can appear in any order. The `name` and `class` columns are mandatory, while `email` is optional. Unknown and repeated columns are rejected.
* Each data row is represented by a `CsvRecord`, which keeps its row number for error reporting. Blank rows are ignored. `ImportCommand#parsePerson(CsvRecord, CsvHeader)` checks the number of fields and uses the same `ParserUtil` methods as the `add` command to validate the name, class and optional email.
* `ImportCommand#parsePersons(ParsedCsv, Model)` validates every contact before modifying the model. It checks for duplicates against both the existing address book and the contacts of earlier rows, and stops at the first invalid row.
* Only after every non-blank row has been successfully converted does `ImportCommand#execute(Model)` add the contacts, which makes the operation atomic. Since `Model#addPerson(Person)` inserts a contact at the top of the list, the contacts are added from the last row to the first, so that they appear at the top in the same order as in the CSV file.
* CSV syntax and I/O failures are reported as a general import failure, while header, field-count, contact-validation and duplicate errors receive more specific messages.

#### Design considerations

**Aspect: When contacts are added to the model**

* **Alternative 1 (current choice):** Validate every row before adding any contact.
  * Pros: The operation is atomic. After an error, the user can correct the file and try again without removing contacts from a partially completed import.
  * Cons: All parsed contacts must be kept in memory until validation finishes, and one invalid row prevents every other row from being imported.
* **Alternative 2:** Add each contact immediately after validating its row.
  * Pros: Uses less temporary memory and preserves contacts from valid rows before an error.
  * Cons: Leaves the address book partially modified, making the result harder for the user to understand or safely retry.

**Aspect: How CSV responsibilities are separated**

* **Alternative 1 (current choice):** Keep CSV syntax and header validation in the dedicated `CsvParser` utility.
  * Pros: Keeps `ImportCommand` focused on file handling, contact validation and model updates. CSV behavior can also be tested independently.
  * Cons: Requires additional record classes and CSV-specific exceptions to pass parsed data back to the command.
* **Alternative 2:** Parse the CSV directly inside `ImportCommand`.
  * Pros: Fewer classes are required.
  * Cons: Mixes filesystem, CSV and model responsibilities in one class, producing longer methods that are harder to test independently.

**Aspect: How CSV columns are identified**

* **Alternative 1 (current choice):** Read column positions from a required header row.
  * Pros: Columns can appear in any order, and the optional email column can be omitted without creating ambiguous rows.
  * Cons: The header requires additional validation and can itself contain errors.
* **Alternative 2:** Require a fixed column order without a header.
  * Pros: Simpler parsing and shorter files.
  * Cons: Users must remember the column order, and adding or omitting an optional column becomes ambiguous.

### Recording attendance feature

#### Implementation

The `attendance` command records one attendance entry for a contact on a specified date. It is implemented by `AttendanceCommandParser`, the abstract `AttendanceCommand` with its subclasses `AttendanceByIndexCommand` and `AttendanceByNameAndClassCommand`, and the `Attendance` model class.

* `AttendanceCommandParser#parse(String)` supports `attendance INDEX /date DD-MM-YYYY /status STATUS` and `attendance /name NAME /class CLASS /date DD-MM-YYYY /status STATUS`. It verifies that the user provides either an index or both a name and class, but not both, and that the date and the status are present.
* The date is parsed strictly in `DD-MM-YYYY` format, with a four-digit year and no sign, by `ParserUtil#parseDate(String)` (which uses `Attendance#DATE_FORMAT`). A text that is not of the shape DD-MM-YYYY, or whose day is not 01-31 or whose month is not 01-12, is reported with `ParserUtil#MESSAGE_INVALID_DATE`, but a text of that shape that is not a real date, such as 31-02-2026, is reported with `ParserUtil#MESSAGE_NONEXISTENT_DATE`, which names the date.
* Both subclasses first reject a date in the future with `AttendanceCommand#requireNotInFuture(LocalDate)`. They then find the contact: `AttendanceByIndexCommand#execute(Model)` from `Model#getFilteredPersonList()` using the displayed index, and `AttendanceByNameAndClassCommand#execute(Model)` from the full address book with `Model#findPerson(String, String)`. If no contact matches, the command throws a `CommandException`.
* `AttendanceCommand#recordAttendance(Model, Person, LocalDate, Attendance.Status)` contains the recording that is shared by both subclasses. It first asks `Person#getAttendanceOn(LocalDate)` and, if the contact already has an attendance record for that date, throws a `CommandException` that names the existing record and says that `deleteA` removes it. Otherwise, it calls `Person#withAttendance(Attendance)`, which returns an updated `Person`, and replaces the old contact in the model using `Model#setPerson(Person, Person)`.
* If the year of the date is before `Attendance.EARLIEST_EXPECTED_YEAR` (2000), the record is still saved, but the success message ends with a warning (`AttendanceCommand#MESSAGE_UNUSUAL_YEAR`), because such a date is most likely a typo. A warning is used instead of an error, since a user may have a reason to record an old date, and the record can be removed with `deleteA`.

#### Design considerations

**Aspect: How a duplicate record is detected**

* **Alternative 1 (current choice):** Ask the model first with `Person#getAttendanceOn(LocalDate)`. The `grade` command does the same with `Person#getGradeFor(Grade)`.
  * Pros: The command states the rule that it enforces, and no other `IllegalArgumentException` can be mistaken for a duplicate.
* **Alternative 2:** Catch the `IllegalArgumentException` that `Person#withAttendance(Attendance)` throws.
  * Cons: Uses an exception for an expected case, and the `try` block also covers unrelated code.

### Recording grades feature

#### Implementation

The `grade` command records one grade for a contact for a specified assessment. It is implemented by `GradeCommandParser`, the abstract `GradeCommand` with its subclasses `GradeByIndexCommand` and `GradeByNameAndClassCommand`, and the `Grade` model class. It is structured and parsed in the same way as `attendance`.

* `GradeCommandParser#parse(String)` supports `grade INDEX /assessment ASSESSMENT_NAME /score SCORE` and `grade /name NAME /class CLASS /assessment ASSESSMENT_NAME /score SCORE`. It verifies that the user provides either an index or both a name and class, but not both, and that the assessment name and the score are present.
* The assessment name is validated by `ParserUtil#parseAssessmentName(String)` (see [Validation of names, classes and assessment names](#validation-of-names-classes-and-assessment-names-feature)). The score is validated by `ParserUtil#parseScore(String)` as a number from 0 to 100 with at most `Grade.MAX_SCORE_DECIMAL_PLACES` (4) decimal places. The limit on the decimal places is not checked by `Grade` itself, so that a data file with a longer score still loads.
* The subclasses find the contact in the same way as for `attendance`.
* `GradeCommand#recordGrade(Model, Person, Grade)` contains the recording that is shared by both subclasses. A contact can have only one grade for each assessment, so it first asks `Person#getGradeFor(Grade)` and, if the contact already has a grade for the assessment, throws a `CommandException` that names the existing grade and says that `deleteG` removes it. Assessment names are compared ignoring case and extra spaces. Otherwise, it calls `Person#withGrade(Grade)`, which returns an updated `Person`, and replaces the old contact in the model using `Model#setPerson(Person, Person)`.

### Viewing attendance feature

#### Implementation

The `viewA` command displays all attendance records for a contact. It is implemented by `ViewAttendanceCommandParser` and `ViewAttendanceCommand`.

* `ViewAttendanceCommandParser` extends `NameAndClassCommandParser` (see [Logic component](#logic-component)), which uses `ArgumentTokenizer` with the `/name` and `/class` prefixes, and creates a `ViewAttendanceCommand` with the name and class as plain strings.
* `ViewAttendanceCommand#execute(Model)` searches the whole address book for a contact using `Model#findPerson(String, String)`, which ignores case and extra whitespace. If no contact matches, it throws a `CommandException` with the contact-not-found message. Otherwise, it formats all of the attendance records of the contact from the most recent date to the earliest and returns them in the command result, or returns the no-attendance-records message if there are none.
* The UI displays the result of a successful `viewA` command in a separate pop-up window rather than in the main result display. `MainWindow#executeCommand(String)` recognizes a `viewA` command by the command word that the user typed and passes the returned command result to a dedicated history pop-up. The pop-up contains a heading, a scrollable text area containing the attendance records, and a close button. The text area expands when the window is resized.

#### Design considerations

**Aspect: How attendance records are displayed**

* **Alternative 1 (current choice):** Display the attendance records in a separate pop-up window. The `viewG` command does the same for grades.
  * Pros: Provides more space for a full history, and allows the user to scroll through a long history without affecting the main contact list.
  * Cons: Requires the user to close the pop-up before returning to the main interface.
* **Alternative 2:** Display the records in the main result display.
  * Pros: Simpler to implement and keeps the user in one window.
  * Cons: The result display has limited space, so a long history is difficult to read, and the records may be overwritten by the next command.

### Deleting attendance feature

#### Implementation

The `deleteA` command removes one attendance record, identified by its date, from a contact. The contact can be identified either by an index in the displayed list or by a name and class. `DeleteAttendanceCommandParser` returns a `DeleteAttendanceByIndexCommand` or a `DeleteAttendanceByNameAndClassCommand` which both extend the abstract `DeleteAttendanceCommand`.

* `DeleteAttendanceCommandParser#parse(String)` first rejects unrecognized parameters, a parameter that is stuck to the text before it, and repeated parameters. If neither `/name` nor `/class` is present, it reads the contact index from the text before the first parameter and creates a `DeleteAttendanceByIndexCommand`. If either is present, it rejects any text before the first parameter, requires both parameters and creates a `DeleteAttendanceByNameAndClassCommand`.
* The required `/date` value is parsed by `ParserUtil#parseDate(String)`, as for `attendance`. The success messages of `attendance` and `deleteA` write the date with `Attendance#formatDate(LocalDate)`, which uses the same format, so that the date in the message is the date that was typed.
* The subclasses find the contact in the same way as for `attendance`.
* `DeleteAttendanceCommand#deleteAttendance(Model, Person, LocalDate)` contains the deletion shared by both subclasses. It finds the stored `Attendance` record by date and reports that the record was not found if there is no match. Otherwise, it passes the stored record to `Person#withoutAttendance(Attendance)`, which returns a new `Person` with only that record removed, and replaces the original contact using `Model#setPerson(Person, Person)`. The success message uses the status and date from the stored record.
* The constructors and the shared operation reject null arguments, and an assertion after the deletion checks that no attendance record remains for the date. These checks document and enforce assumptions between the parser, command and model layers.

#### Design considerations

**Aspect: How the record to remove is represented**

* **Alternative 1 (current choice):** Find the stored `Attendance` by date, then pass that object to `Person#withoutAttendance(Attendance)`. The `deleteG` command does the same with a stored `Grade`.
  * Pros: The user does not need to supply the status (or the score), and the stored values are available for the success message.
  * Cons: Finding the record and removing it are separate operations over the list.
* **Alternative 2:** Add a `Person#withoutAttendance(LocalDate)` method that finds and removes a record by date.
  * Pros: Encapsulates the lookup and removal in the model class.
  * Cons: The command would need another way to obtain the removed record for its success message, or the model method would need a more complex return type.

### Viewing grades feature

#### Implementation

The `viewG` command displays all grades of a contact. It is implemented by `ViewGradeCommandParser` and `ViewGradeCommand`, and it works in the same way as the `viewA` command.

* `ViewGradeCommandParser` extends `NameAndClassCommandParser`, as `ViewAttendanceCommandParser` does.
* `ViewGradeCommand#execute(Model)` searches the whole address book for a contact using `Model#findPerson(String, String)`. If no contact matches, it throws a `CommandException` with the contact-not-found message. Otherwise, it lists the grades of the contact from the newest to the oldest, as `Grade#toString()`, or returns the no-grades message if there are none.
* `Grade#isSameAssessment(Grade)` and `Grade#equals(Object)` compare the assessment names with `StringUtil#toComparisonKey(String)`, so `Quiz 1` and ` quiz   1` are the same assessment, in the same way as names and classes. `Person#withGrade(Grade)` uses it to reject a second grade for the same assessment.
* The UI displays the result in a separate pop-up window, in the same way as for `viewA` (see [Viewing attendance](#viewing-attendance-feature)).

### Deleting grades feature

#### Implementation

The `deleteG` command removes one grade, identified by its assessment name, from a contact. The contact can be identified either by an index in the displayed list or by a name and class. `DeleteGradeCommandParser` returns a `DeleteGradeByIndexCommand` or a `DeleteGradeByNameAndClassCommand` which both extend the abstract `DeleteGradeCommand`. It works in the same way as `deleteA` (see [Deleting attendance](#deleting-attendance-feature)).

* `DeleteGradeCommandParser#parse(String)` first rejects unrecognized and repeated parameters, and then identifies the contact in the same way as `DeleteAttendanceCommandParser`.
* The parser validates the assessment name with `ParserUtil#parseAssessmentName(String)`, which uses `Grade#getAssessmentNameConstraintViolation(String)` and does not construct a `Grade`, since deleting a grade does not require a score. The method also uses `ParserUtil#requireNoEmbeddedPrefix(String, String)` to detect a parameter that may be missing a space before it.
* `DeleteGradeCommand#deleteGrade(Model, Person, String)` contains the deletion shared by both subclasses. It finds the stored grade using `Grade#hasAssessmentName(String)`, which ignores case and extra whitespace, and if there is no match, reports that the grade was not found. Otherwise, it passes the stored `Grade` to `Person#withoutGrade(Grade)` and replaces the contact using `Model#setPerson(Person, Person)`. The success message uses the score and assessment name from the stored grade.
* The constructors reject null arguments and assert that the assessment name has already been validated. After deletion, an assertion checks that the grade is absent from the new contact.

### Command history feature

#### Implementation

The user can press the Up and Down keys in the command box to recall the commands that were entered earlier. It is implemented by `CommandHistory`, `HistoryKeyHandler` and `CommandBox`, and it is only part of the `UI` component. `Logic`, `Model` and `Storage` are not involved.

* `CommandHistory` does not depend on JavaFX, so that it can be tested without a GUI. It keeps the commands in the order they were entered, a position that is used while the user is browsing, and a _draft_, which is the text that the user had typed before browsing started.
* `CommandHistory#add(String)` adds a command and stops browsing. A blank command, or a command identical to the most recent one, is not added. When there are more than `CommandHistory#MAX_SIZE` (100) commands, the oldest one is dropped.
* `CommandHistory#previous(String)` is called with the text of the command box. It saves that text as the draft if the user was not browsing, moves to the next older command, and returns it. At the oldest command, it returns the oldest command again. `CommandHistory#next()` moves to the next newer command, and after the newest command, it returns the draft. Both return an empty `Optional` when there is nothing to recall, so that the text is left as it is.
* A command that fails stays in the command box, and it is also the most recent command. If the text given to `CommandHistory#previous(String)` when browsing starts is the most recent command, that command is skipped. Otherwise, the first press of the Up key would seem to do nothing.
* `HistoryKeyHandler` handles the Up and Down keys with a function that gives the text of the command box and a function that shows a recalled text, and not with a text field, so that it can be tested without a GUI. It consumes the event, so that the text field does not also use the key to move its caret, and leaves any other key alone.
* `CommandBox` adds a `HistoryKeyHandler` to its text field as an event filter for `KeyEvent.KEY_PRESSED`, because a filter runs before the text field handles the key itself. `CommandBox#handleCommandEntered()` calls `CommandHistory#add(String)` before it executes the command, so the commands that fail are remembered as well.

#### Design considerations

**Aspect: Where the history is kept**

* **Alternative 1 (current choice):** In the `UI` component, in a `CommandHistory` used by `CommandBox`.
  * Pros: Recalling text for the command box is a convenience of the input field, so `Logic`, `Model` and `Storage` stay unchanged. The class can be tested without JavaFX.
  * Cons: `Logic` cannot use the history, for example for a `history` command.
* **Alternative 2:** In the `Logic` component, exposed through the `Logic` API.
  * Pros: Independent of the GUI, and available to any command or other `UI`.
  * Cons: Adds to the `Logic` API for a feature that only the command box needs.

**Aspect: Which commands are remembered**

* **Alternative 1 (current choice):** Every command that is not blank, including the ones that fail.
  * Pros: The user can recall a command with a typo and correct it, even after the box was cleared or other commands were entered.
  * Cons: The history contains commands that did not work.
* **Alternative 2:** Only the commands that succeed.
  * Pros: Every recalled command is a valid one.
  * Cons: A failed command can no longer be recalled once its text is replaced, which is when it is most useful.

**Aspect: Whether the history is kept after ClassMates exits**

* **Alternative 1 (current choice):** The history is kept for the current session only.
  * Pros: Simple, and there is no new file or storage format to keep correct.
  * Cons: The user starts every session with an empty history.
* **Alternative 2:** Save the history to a file and load it at the next launch.
  * Pros: Commands from earlier sessions can be recalled.
  * Cons: Needs a file and a format, with handling for a missing or invalid file. The commands also contain contact details, so the file would hold data that the user has not chosen to save.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th contact in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add /name David …​` to add a new contact. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the contact was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add /name David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the contact being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

### Defensive programming

The code defends itself against bad input and against its own mistakes in four ways. Each is used for a different kind of problem.

* **Exceptions** are for problems that the user can cause. `ParseException` reports invalid input and `CommandException` reports a command that cannot be carried out. Both carry a message that is shown to the user.
* **`requireNonNull`** is for a caller that passes `null` to a method that does not accept it. It fails fast with a `NullPointerException` at the point of the mistake instead of later and elsewhere.
* **Assertions** (`assert`) are for conditions that can only be false because of a bug in the code, never because of user input. Gradle enables them when the tests run and when the app is started with `gradle run`, so they catch such bugs during development. They are not enabled when the packaged jar is started with `java -jar`, so they cannot be the only protection of the user (see [Unexpected errors](#unexpected-errors)).
* **Logging** records what happened for the developers. Each command logs at `INFO` level when it changes the data or rejects a change, and `LogicManager` already logs every command that the user enters.

#### Repeating the guarantees of the parser

* A public constructor that the parser already protects still rejects a wrong argument with an `IllegalArgumentException` (`AppUtil#checkArgument`), and not with an assertion, because the assertion would not run in the packaged jar. `GroupMembershipCommand` rejects an empty list of indices and a repeated index, which would otherwise make the second replacement of a contact fail with an unchecked `PersonNotFoundException`, or report success without a change. `EditCommand` rejects an `EditPersonDescriptor` with no detail to edit, which would otherwise report that a contact was edited when nothing changed. `CommandResult` rejects a result that both shows help and exits, as the UI would open the help window and then close the app.
* The commands that change the data (`add`, `edit`, `delete`, `deleteA`, `deleteG`, `tag`, `untag` and `clear`), and `list`, assert after they change the model that the change took effect, for example that a new contact is in the address book. The assertion would fail if a model silently lost or dropped a contact.

#### Clearing all contacts

* `clear` is the only command without arguments that does not ignore extra text, because it deletes all contacts. `ClearCommandParser#parse(String)` throws a `ParseException` with the usage of `clear` if the arguments are not blank, so a user who types `clear 3`, perhaps expecting to delete one contact, deletes nothing. `AddressBookParser` still creates `HelpCommand` and `ExitCommand` itself and ignores what follows them, as described in the User Guide.

#### Saving and loading data

* `LogicManager#execute(String)` copies the address book before it executes a command. If the address book cannot be saved afterwards, `LogicManager` sets the data of the `Model` back to that copy and throws a `CommandException` that says that the change was undone (`LogicManager#MESSAGE_CHANGE_UNDONE`). Without this, the user would be told that the command failed while the contact was changed on the screen, and the next command that saves would store the change anyway. Only the data is restored. The filter of the displayed list is kept as the command left it.
* `Person` does not accept two attendance records with the same date, or two grade records for the same assessment (compared ignoring case and extra spaces). The commands already refused a second record, but the constructors did not, so a data file that was edited by hand, or damaged, could create a contact that the commands cannot produce. `JsonAdaptedPerson#toModelType()` reports the `IllegalArgumentException` of the constructor as an `IllegalValueException`, which makes the data file invalid like any other broken rule, so that the rule is written in one place.

#### Unexpected errors

* `LogicManager#execute(String)` catches any `RuntimeException` thrown while it parses or executes a command. Such an exception is a bug, because the expected problems are reported as a `ParseException` or a `CommandException`. `LogicManager` logs it with its stack trace at `SEVERE` level, sets the data of the `Model` back to what it was before the command, and throws a `CommandException` with `LogicManager#MESSAGE_UNEXPECTED_ERROR`. Without this, the exception would reach the UI, which does not handle it, so the user would see nothing happen while the data may have been changed halfway. An `Error`, such as a failed assertion, is not caught, so that the bugs that assertions find are not hidden during development.

#### Limits on the size of the input

An input without a limit lets one pasted text or file freeze the app, so the following inputs have one.

* `AddressBookParser#parseCommand(String)` rejects a command with more than `AddressBookParser.MAX_INPUT_LENGTH` (5,000) characters with a `ParseException`, before it does anything else. The longest real commands, such as one that links a class of 300 students to a group, have well under 5,000 characters. `LogicManager#execute(String)` writes at most `LogicManager.MAX_LOGGED_COMMAND_LENGTH` (500) characters of a command to the log, so that a long command does not flood it.
* `Email` is limited to `Email.MAX_LENGTH` (254) characters, and the length is checked before the format. Matching the format regex takes time that grows quickly with the length of an invalid input (about 4 seconds for 5,000 characters), so without the limit a pasted text froze the app.
* `ImportCommand` rejects a file with more than `ImportCommand.MAX_FILE_SIZE_BYTES` (5 MiB) before it reads the file, because the whole file is read into memory. It also rejects a file with more than `ImportCommand.MAX_ROWS` (5,000) rows before it checks any row, because each contact is compared with all contacts before it, so the time to import grows with the square of the number of rows. Nothing is imported in either case.
* `GroupMembershipCommand#execute(Model)` names at most `GroupMembershipCommand.MAX_INDICES_SHOWN` (10) of the indices that are not in the displayed list, followed by how many more there are, so that a command with 100,000 missing indices does not produce a message that names all of them.

#### Fuzz tests

* `CommandFuzzTest` parses and executes 10,000 random commands and 10,000 commands that have the form of a real one with values that may be wrong, such as an index that is too large or a repeated parameter, each on a new model. It checks that the only exceptions that come out are `ParseException` and `CommandException`, and it fails when an assertion fails. `DataFileFuzzTest` damages a valid data file in 500 random ways, such as cutting it short or replacing a value with `null`, and checks that the file is either loaded or rejected with a `DataLoadingException`.
* Both tests use a fixed seed, so a failure can be repeated.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: Teaching assistants in universities who prefer desktop apps over other types of applications. They can type fast, prefer typing to mouse interactions, and are reasonably comfortable using CLI apps.

**Value proposition**: TAs that handle multiple courses and multiple tutorial groups will have many students to manage at the same time, which can make organization of information difficult. The product can provide fast and organized contact retrieval for TAs.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …                 | So that I can…                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | TA                                         | add new student contacts with name, email, class | keep track of who is in each of my classes                |
| `* * *`  | TA                                         | search for contacts by different keywords (e.g. name/class) | quickly find the information I need         |
| `* * *`  | TA setting up a new class                  | import my initial students from a file | don't have to manually enter every student                     |
| `* * *`  | TA                                         | remove contacts easily       | keep my contact list from being cluttered                              |
| `* * *`  | TA coordinating group assignments          | link students belonging to the same group activity together | handle team-based queries faster            |
| `* * *`  | TA                                         | record a student's attendance status | keep track of students who attended a tutorial                 |
| `* * *`  | TA                                         | view all grades recorded for a student | keep track of the progress of my students                       |
| `* * *`  | TA                                         | view a list of all my contacts | see everyone at a glance                                              |
| `* * *`  | TA                                         | record grades for a specific student | monitor their progress                                          |
| `* * *`  | TA                                         | view a student's attendance  | review their attendance history                                        |
| `* * *`  | TA                                         | delete an attendance record that I entered by mistake | correct a student's attendance history                |
| `* * *`  | TA                                         | delete a grade that I entered by mistake | correct a student's grades                                 |
| `* *`    | new user                                   | view a help screen           | learn about the app without going to an external website               |
| `* *`    | TA working with other TAs                  | store colleagues' contacts but clearly distinguished | keep my "work" contacts all in one place but not mix them up |
| `* *`    | TA                                         | edit the information under any contact | keep my information up to date                                |
| `* *`    | TA coordinating group assignments          | remove students from a group | keep each group correct when the teams change                          |
| `* *`    | TA who might make mistakes                 | receive a clear error message | correct my command                                                    |
| `* *`    | experienced user                           | recall my earlier commands   | run or correct a command again without typing it out again             |
| `* *`    | TA (tracking student support)               | attach and edit notes to student profiles | review past consultation details                          |
| `* *`    | experienced user                           | delete contacts in bulk based on tags | clean up my contacts quickly                                  |
| `* *`    | TA                                         | undo an accidental deletion  | restore a contact I removed by mistake                                 |
| `* *`    | TA                                         | sort students consistently   | scan through them quickly                                              |
| `* *`    | TA                                         | favorite certain contacts   | quickly access my most frequent contacts                               |
| `*`      | TA                                         | distinguish between students of similar names | not use the wrong student's information                |
| `*`      | TA                                         | view total number of students across my classes | understand my workload                               |
| `*`      | TA (managing high volume of active contacts) | highlight frequently contacted people | retrieve their details faster                             |
| `*`      | TA                                         | search through archived contact records separately | quickly recall info relating to past students     |
| `*`      | TA                                         | generate a breakdown of current students | balance my time commitments                              |
| `*`      | TA with partner TAs                        | export student records based on tags | maintain student contacts in sync with my partner              |
| `*`      | TA                                         | identify records with incomplete info | know which record needs attention                             |
| `*`      | New user                                   | remove sample or experimental data | start with a clean contact list                                  |
| `*`      | TA                                         | export selected students' records | use the required information                                     |
| `*`      | TA                                         | archive students from previous semesters and restore them | old records do not clutter my contacts and I can reuse them if I want to |


### Use cases

(For all use cases below, the **System** is `ClassMates` (CM) and the **Actor** is the `user`, unless specified otherwise)

A use case that needs another use case includes it. An inclusion is written as <u>underlined text</u> followed by the number of the included use case, such as <u>list or search student contacts</u> (UC02).

The use cases describe what the application does now. Commands with similar interactions share one use case, such as the commands for attendance and grades.

**Use case: UC01 - Add a new student contact**

**MSS**
1. User requests to add a contact, giving a name, a class and optionally an email.
2. CM adds the contact and displays a success message.

   Use case ends.

**Extensions**

* 1a. The command format is invalid, e.g. a parameter is unknown or repeated, there is text before the first parameter, or the name or class is missing.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.
* 1b. The name, class or email is invalid, e.g. it is empty, too long or has characters that are not allowed.
  * 1b1. CM displays an error message.

    Use case resumes at step 1.
* 1c. A contact with the same name and class already exists. Names and classes are compared ignoring case and extra spaces, so the same name can be added to a different class.
  * 1c1. CM displays an error message.

    Use case resumes at step 1.


**Use case: UC02 - List or search student contacts**

**Guarantees**
* The search always covers all contacts, whether or not an earlier search is displayed.

**MSS**
1. User requests to list contacts, giving a keyword.
2. CM displays the contacts whose name, class or tags contain the keyword, and the number of contacts displayed.

   Use case ends.

**Extensions**

* 1a. User does not give a keyword.
  * 1a1. CM displays all contacts.

    Use case ends.
* 2a. No contact matches the keyword.
  * 2a1. CM displays an empty list.

    Use case ends.


**Use case: UC03 - Edit a contact**

**Guarantees**
* The contact is only changed if the whole command can be carried out. If any problem is found, nothing about the contact is changed.
* The attendance records and the grades of the contact are kept.

**MSS**
1. User requests to <u>list or search student contacts</u> (UC02) to find the contact.
2. CM displays the matching contacts.
3. User requests to edit a contact in the displayed list, giving the index of the contact and the details to change. These are any of the name, the class and the email.
4. CM changes the details of the contact, shows all contacts and displays a success message.

   Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The command format is invalid, e.g. the index is missing or is not a positive integer, no detail to change is given, or a parameter is unknown or repeated.
  * 3a1. CM displays an error message.

    Use case resumes at step 3.
* 3b. A detail is invalid, i.e. the name, class or email has characters that are not allowed, or is empty or too long.
  * 3b1. CM displays an error message.

    Use case resumes at step 3.
* 3c. The index is larger than the number of displayed contacts.
  * 3c1. CM displays an error message.

    Use case resumes at step 3.
* 3d. The new name and class are the same as those of another contact. Names and classes are compared ignoring case and extra spaces, so changing only the capitalization of the name of the contact is allowed.
  * 3d1. CM displays an error message.

    Use case resumes at step 3.
* 3e. User gives the email without a value, to remove the email of the contact.
  * 3e1. CM removes the email, together with any other change in the command.

    Use case resumes at step 4.


**Use case: UC04 - Delete a student**

**MSS**
1. User requests to <u>list or search student contacts</u> (UC02).
2. CM displays the matching contacts.
3. User requests to delete a contact in the displayed list, giving the index of the contact.
4. CM deletes the contact and displays a success message.

   Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The index is not a positive integer, or is larger than the number of displayed contacts.
  * 3a1. CM displays an error message.

    Use case resumes at step 3.
* 3b. User knows the name and class of the contact, and requests to delete the contact by its name and class instead of its index. This does not need steps 1 and 2.
  * 3b1. CM searches all contacts, including those that are not displayed, deletes the contact with that name and class, and displays a success message.

    Use case ends.
* 3c. No contact has the given name and class.
  * 3c1. CM displays an error message.

    Use case resumes at step 3.
* 3d. The command format is invalid, e.g. an index is combined with a name or class, or a name or class is missing or repeated.
  * 3d1. CM displays an error message.

    Use case resumes at step 3.


**Use case: UC05 - Set up new classes using bulk load**

**Guarantees**
* Contacts are only added if the file and every row in it are valid. If any problem is found, no contact is added.

**MSS**
1. User requests to import contacts from a CSV file, giving the path of the file.
2. CM checks the file, adds all the contacts in it and displays the number of contacts imported.
3. User requests to <u>list or search student contacts</u> (UC02), using a class that was added as the keyword, to check the imported contacts.
4. CM displays the contacts of that class.

   Use case ends.

**Extensions**

* 1a. The path is missing, the file does not exist, the file is not a `.csv` file or the file cannot be read.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.
* 2a. The file is empty or has no contact rows.
  * 2a1. CM displays an error message and adds no contact.
  * 2a2. User externally modifies the CSV file.

    Use case resumes at step 1.
* 2b. The header row is invalid, i.e. the `name` or `class` column is missing, or a column is unknown or repeated.
  * 2b1. CM displays an error message and adds no contact.
  * 2b2. User externally modifies the CSV file.

    Use case resumes at step 1.
* 2c. A row has the wrong number of values, has invalid contact data, or duplicates an existing contact or an earlier row.
  * 2c1. CM stops at the first such row, displays an error message with the row number and adds no contact.
  * 2c2. User externally modifies the CSV file.

    Use case resumes at step 1.


**Use case: UC06 - Add students to groups, or remove students from groups (by tagging or untagging)**

**Guarantees**
* The contacts are only linked to the group (or removed from it) if all of them can be. If any problem is found, none of them is changed.
* When contacts are removed from a group, their other details, attendance records and grades are kept.

**MSS**
1. User requests to <u>list or search student contacts</u> (UC02) to find the students to group.
2. CM displays the matching contacts.
3. User requests to link the contacts at the given indices of the displayed list to a group, giving the name of the group.
4. CM links the contacts to the group and displays a success message.

   Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The command format is invalid, the group name is invalid, or an index is not a positive integer.
  * 3a1. CM displays an error message.

    Use case resumes at step 3.
* 3b. An index is repeated, or does not match a contact in the displayed list.
  * 3b1. CM displays an error message.

    Use case resumes at step 3.
* 3c. One or more of the contacts are already in the group.
  * 3c1. CM displays an error message.

    Use case resumes at step 3.
* 3d. User requests to remove the contacts at the given indices from the group instead. Extensions 3a and 3b apply to this request as well.
  * 3d1. CM removes the contacts from the group and displays a success message.

    Use case ends.
* 3e. In 3d, one or more of the contacts are not in the group.
  * 3e1. CM displays an error message.

    Use case resumes at step 3.


**Use case: UC07 - Record attendance or a grade for a known student**

**MSS**
1. User requests to record attendance (a date and a status) or a grade (an assessment name and a score) for a student, identifying the student either by the index of the student in the displayed list or by the name and class of the student.
2. CM records the attendance or grade and displays a success message.

   Use case ends.

**Extensions**

* 1a. The command format is invalid, e.g. a detail is missing or given more than once, an index is given together with a name or class, only one of the name and class is given, or the command has a parameter that it does not accept.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.
* 1b. The index is not a positive integer, or no student is displayed at that index.
  * 1b1. CM displays an error message.

    Use case resumes at step 1.
* 1c. No student has the given name and class. Case and extra spaces are ignored.
  * 1c1. CM displays an error message.

    Use case resumes at step 1.
* 1d. For attendance, the date does not follow the format DD-MM-YYYY, is not a real date or is in the future, or the status is not present, absent or late. The error message tells which of these it is.
  * 1d1. CM displays an error message.

    Use case resumes at step 1.
* 1e. For a grade, the assessment name is invalid, e.g. it is empty, longer than 80 characters, has a letter of another language, or contains a parameter such as `/score` stuck to the text before it, or the score is not a number from 0 to 100.
  * 1e1. CM displays an error message.

    Use case resumes at step 1.
* 1f. The student already has an attendance entry for the date, or a grade for the assessment. Assessment names are compared ignoring case and extra spaces.
  * 1f1. CM keeps the existing entry and displays an error message.

    Use case resumes at step 1.


**Use case: UC08 - View the attendance or grades of a known student**

**MSS**
1. User requests to view the attendance or the grades of a student, giving the name and class of the student.
2. CM displays all the attendance records or grades of the student, from the newest to the oldest.

   Use case ends.

**Extensions**

* 1a. The command format is invalid, e.g. the name or class is missing or given more than once, or the command has a parameter that it does not accept.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.
* 1b. No student has the name and class. Case and extra spaces are ignored.
  * 1b1. CM displays an error message.

    Use case resumes at step 1.
* 2a. The student has no attendance records or no grades.
  * 2a1. CM displays a message that none are recorded.

    Use case ends.


**Use case: UC09 - Delete an attendance record or a grade of a student**

**MSS**
1. User requests to delete the attendance record for a date, or the grade for an assessment, of a student, identifying the student either by the index of the student in the displayed list or by the name and class of the student.
2. CM deletes the record or grade and displays a success message.

   Use case ends.

**Extensions**

* 1a. The command format is invalid, e.g. the date or assessment name is missing or given more than once, an index is given together with a name or class, only one of the name and class is given, or the command has a parameter that it does not accept.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.
* 1b. The index is not a positive integer, or no student is displayed at that index.
  * 1b1. CM displays an error message.

    Use case resumes at step 1.
* 1c. No student has the given name and class. Case and extra spaces are ignored.
  * 1c1. CM displays an error message.

    Use case resumes at step 1.
* 1d. The date is not a real date in the format DD-MM-YYYY, or the assessment name is invalid, e.g. it is empty or longer than 80 characters.
  * 1d1. CM displays an error message.

    Use case resumes at step 1.
* 1e. The student has no attendance record for the date, or no grade for the assessment. Assessment names are compared ignoring case and extra spaces.
  * 1e1. CM keeps the records and grades and displays an error message.

    Use case resumes at step 1.


**Use case: UC10 - Clear all contacts**

**MSS**
1. User requests to clear all contacts.
2. CM deletes all contacts and displays a success message.

   Use case ends.

**Extensions**

* 1a. User writes anything after the command word, such as `clear 3`.
  * 1a1. CM displays an error message and deletes nothing.

    Use case resumes at step 1.


**Use case: UC11 - Recall earlier commands**

**Guarantees**
* The last 100 commands of the current session are remembered, including the commands that failed. A blank command, and a command that is the same as the one before it, are not remembered.

**MSS**
1. User presses the Up key in the command box.
2. CM replaces the text in the command box with the command that the user entered before it.
3. User edits the recalled command if needed and enters it.
4. CM executes the command.

   Use case ends.

**Extensions**

* 1a. The user has not entered any command since ClassMates started.
  * 1a1. CM leaves the text in the command box as it is.

    Use case ends.
* 2a. User presses the Up key again.
  * 2a1. CM shows the next older command. At the oldest command, CM shows that command again.

    Use case resumes at step 2.
* 2b. User presses the Down key.
  * 2b1. CM shows the next newer command. After the newest command, CM shows the text that the user had typed before pressing the Up key.

    Use case resumes at step 2.


**Use case: UC12 - View help**

**MSS**
1. User requests to view help.
2. CM shows a summary of the format of every command, with a link to the user guide.

   Use case ends.


**Use case: UC13 - Exit the application**

**MSS**
1. User requests to exit the application.
2. CM closes all its windows and exits.

   Use case ends.


### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2. Should be usable without an installer, and be distributed as a single JAR file, or as a single ZIP file containing the JAR and any necessary files.
3. Should store contact, attendance, and grade data locally in a human-editable text file, and work without a remote server, a database, a user account or an Internet connection.
4. Should support one user operating on their own locally stored data.
5. Should hold up to 1,000 contacts without noticeable sluggishness in typical usage, i.e. a command should show its result within 2 seconds.
6. Should handle a command of up to 5,000 characters, and a CSV file of up to 5 MiB and 5,000 rows, and reject anything larger with an error message and without freezing.
7. Should let a user with above-average typing speed for regular English text (i.e. not code, not system admin commands) accomplish most of the tasks faster using commands than using the mouse.
8. Should have a GUI that is usable at screen resolutions of 1280 × 720 and above, and at display scaling of 100% and 125% at 1920 × 1080 and higher.
9. Should not lose or corrupt existing records because of an invalid command, a failure to save or a damaged data file: a command that fails leaves the data unchanged, and a data file that cannot be read is kept as a backup and not overwritten.

### Glossary

* **Address book**: All the contacts and their data that ClassMates holds, saved in the data file. The name comes from the AddressBook-Level3 project that ClassMates is based on.
* **Assessment**: A named piece of work (e.g. "Assignment 1", "Midterm") that a grade is recorded against for a contact. A contact has at most one grade for each assessment, and assessment names are compared ignoring case and extra spaces.
* **Atomic**: Describes a command that makes either all of its changes or none of them, such as `tag`, `untag`, `edit` and `import`.
* **Attendance record**: A single entry marking a contact as present, absent or late on a given date. A contact has at most one attendance record for a date.
* **CM**: ClassMates, as abbreviated in the use cases.
* **Class**: The tutorial or section that a student belongs to (e.g. `A1`), entered in the class field of a contact. A contact is identified by its name and class together.
* **Contact**: The record of one student in ClassMates, with a name, a class, an optional email, groups, attendance records and grades. The user stories and use cases say "student" where the guide otherwise says "contact".
* **Data file**: The JSON file `data/classmates.json` in which ClassMates saves the address book after every command. Users can edit it by hand.
* **Displayed list**: The list of contacts that the main window shows now, which is all contacts or the result of the last `list` command. An index in a command refers to a position in this list.
* **Grade**: A score from 0 to 100, inclusive, that a contact receives for an assessment.
* **Group**: The contacts that have the same tag, such as the members of a team for an assignment. A group is not stored as an object of its own, so it exists only as long as at least one contact has the tag. A contact can be in several groups, and a group is identified by its name, ignoring case. A contact is added to a group with the `tag` command and removed from it with the `untag` command.
* **Index**: The position of a contact in the displayed list, starting from 1. A command that takes an index must be given a positive integer.
* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **MSS (Main Success Scenario)**: The primary, no-error flow of steps in a use case
* **Prefix**: The word that starts with `/` and introduces a parameter of a command, such as `/name` or `/class`. Prefixes are compared ignoring case.
* **Tag**: The label by which a group is stored on a contact. Tags are compared ignoring case, leading and trailing whitespace, and repeated spaces.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

<box type="info" seamless>

**Note:** Many test cases change the data. To start again with the sample data, close the app, delete the `data` folder in the folder that contains the JAR file, and launch the app again.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. Dealing with a data file that cannot be read

   1. Close the app. Open `data/classmates.json` in a text editor, delete its last `}`, and save the file. Launch the app.<br>
      Expected: The GUI opens with no contacts. The result box says that ClassMates could not read the data file and shows the name of the kept file. The `data` folder has a file named `classmates.json.bad-` followed by the time, which still holds the broken content, and `classmates.json` holds an empty list.

   1. Close the app and launch it again.<br>
      Expected: The GUI opens with no contacts and no message, as the empty `classmates.json` is read.

### Adding a contact

1. Adding a contact successfully

   1. Prerequisites: The app contains the sample data.

   1. Test case: `list yeoh`, followed by `add /name John Doe /class A1 /email johnd@example.com`<br>
      Expected: After the `add` command, all contacts are shown again, even though only Alex Yeoh was shown before, with John Doe at the top of the list. The status message shows `John Doe added to contacts`.

   1. Test case: `add /NAME José Müller (Joe) #2 /Class T11/A`<br>
      Expected: The contact is added with the name and the class as typed. The case of the prefixes does not matter, and accents, digits, symbols and a `/` in a class are allowed.

1. Adding a contact unsuccessfully

   1. Prerequisites: The app contains the sample data, which has Alex Yeoh in class A1.

   1. Test case: `add /name alex  yeoh /class a1`<br>
      Expected: No contact is added. The status message says that the name already exists in the class. Case and extra spaces are ignored.

   1. Test case: `add /name Tan, John /class A1`<br>
      Expected: No contact is added. The status message says that a name can only contain English letters, digits, spaces and keyboard symbols, except commas.

   1. Other incorrect add commands to try: `add /name John Doe`, `add /name 李明 /class A1`, `add /name John Doe/class A1 /class A2` (a parameter stuck to the name), `add /name John Doe /class A1&`, `add /name John Doe /class A1 /email john`<br>
      Expected: Similar to previous. No contact is added, and the status message describes the problem.

### Listing and searching contacts

1. Listing and searching contacts

   1. Prerequisites: The app contains the sample data (Alex Yeoh, Bernice Yu, Charlotte Oliveiro, David Li, Irfan Ibrahim and Roy Balakrishnan).

   1. Test case: `list`<br>
      Expected: All six contacts are shown, from Alex Yeoh at the top to Roy Balakrishnan at the bottom.

   1. Test case: `list friend`<br>
      Expected: Alex Yeoh and Bernice Yu are shown, as their tags contain `friend`. The status message shows that 2 contacts are listed. A keyword matches part of a name, a class or a tag.

   1. Test case: `list yeoh`, followed by `list david`<br>
      Expected: After `list david`, only David Li is shown. The second search covers all contacts, and not only the result of the first search.

   1. Test case: `list zzz`<br>
      Expected: No contact is shown, and the status message shows that 0 contacts are listed.

   1. Other keywords to try: `list a2` (a class), `list ALEX` (the case is ignored), `list rid` (no contact is shown, because the characters of a keyword must appear next to each other)<br>
      Expected: The contacts that match, if any, are shown as described.

### Deleting a contact

1. Deleting a contact while all contacts are being shown

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command.

   1. Test case: `delete 1`<br>
      Expected: Alex Yeoh, the first contact, is deleted from the list. The status message shows `Alex Yeoh in class A1 has been deleted`.

   1. Test case: `delete 0`<br>
      Expected: No contact is deleted. The status message shows `Index must be a positive integer.`

   1. Other incorrect delete commands to try: `delete`, `delete x` (where `x` is not a number), `delete 99` (where 99 is larger than the number of displayed contacts)<br>
      Expected: Similar to previous. No contact is deleted, and an error message is shown.

1. Deleting a contact by name and class

   1. Prerequisites: The app contains the sample data.

   1. Test case: `list david`, followed by `delete /name Bernice Yu /class A1`<br>
      Expected: Bernice Yu is deleted, even though she is not displayed after `list david`. The whole address book is searched.

   1. Test case: `delete /name David Li /class B1`<br>
      Expected: No contact is deleted. The status message shows `Contact not found`, because both the name and the class must match, and David Li is in class A2.

1. _{ more test cases … }_

### Editing a contact

1. Editing the details of a contact

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command. Alex Yeoh is in `friends`, and Bernice Yu is in `colleagues` and `friends`.

   1. Test case: `edit 1 /name Alex Tan /class B2 /email`<br>
      Expected: Alex Yeoh is now Alex Tan in class B2 and has no email, because `/email` with no value removes the email. The contact stays at the top of the list, and the groups are unchanged. The status message shows `Alex Tan in class B2 has been edited`.

   1. Test case: `edit 2 /name Bernice Lim`<br>
      Expected: Bernice Yu is now Bernice Lim, still in `colleagues` and `friends`, and stays 2nd in the list. Editing a contact does not change its groups. The status message shows `Bernice Lim in class A1 has been edited`.

   1. Test case: `attendance 3 /date 01-01-2024 /status present`, then `grade 3 /assessment Quiz 1 /score 80`, then `edit 3 /name Charlotte Lee /class A3`, then `viewA /name Charlotte Lee /class A3` and `viewG /name Charlotte Lee /class A3`<br>
      Expected: The attendance of 01-01-2024 as present, and the grade of 80 for Quiz 1, are still shown, because editing a contact keeps its attendance records and grades.

1. Editing a contact unsuccessfully

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command.

   1. Test case: `edit 1 /name Bernice Yu`<br>
      Expected: Nothing changes. The status message shows `Bernice Yu already exists in class A1`, because Alex Yeoh is in the same class as Bernice Yu.

   1. Other incorrect edit commands to try: `edit 1` (nothing to edit), `edit x /name Alex Tan`, `edit 1 /phone 91234567`, `edit 1 /tag Group A` (groups are changed with `tag` and `untag`), `edit 99 /name Jo`<br>
      Expected: Similar to previous. Nothing changes, and an error message is shown.

### Linking contacts to a group

1. Linking contacts to a group successfully

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command.

   1. Test case: `tag /group Group A /members 1,3`<br>
      Expected: Alex Yeoh and Charlotte Oliveiro both show the label `Group A`, and stay at the same positions in the list. The status message shows `Contacts successfully linked to group Group A: Alex Yeoh (A1), Charlotte Oliveiro (A2)`.

   1. Test case: `list david`, followed by `tag /group Team 2 /members 1`<br>
      Expected: David Li shows the label `Team 2`. The index refers to the contact in the displayed list, which is David Li.

1. Linking contacts to a group unsuccessfully

   1. Prerequisites: Complete the previous test cases. List all contacts using the `list` command. Alex Yeoh, who is at index 1, is in `Group A`.

   1. Test case: `tag /group GROUP A /members 1`<br>
      Expected: No contact is linked. The status message shows `One or more contacts are already in this group: Alex Yeoh (A1)`. Group names are compared ignoring case.

   1. Test case: `tag /group Group B /members 1,99`<br>
      Expected: No contact is linked, not even the contact at index 1. The status message shows `Contact index not found: 99`.

   1. Other incorrect tag commands to try: `tag /group Group B`, `tag /members 1`, `tag /group Group! /members 1`, `tag /group Group B /members 1,1`<br>
      Expected: Similar to previous. No contact is linked, and an error message is shown.

### Removing contacts from a group

1. Removing contacts from a group successfully

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command. Alex Yeoh, who is at index 1, and Bernice Yu, who is at index 2, are in `friends`. Bernice Yu is also in `colleagues`.

   1. Test case: `untag /group friends /members 1,2`<br>
      Expected: Alex Yeoh no longer shows the label `friends`. Bernice Yu no longer shows `friends` but still shows `colleagues`. Both stay at the same positions in the list. The status message shows `Contacts successfully removed from group friends: Alex Yeoh (A1), Bernice Yu (A1)`.

   1. Test case: `list colleagues`, followed by `untag /group colleagues /members 1`<br>
      Expected: Bernice Yu, the first contact in the results, no longer shows `colleagues`, and Roy Balakrishnan still does. The index refers to the contact in the displayed list.

1. Removing contacts from a group unsuccessfully

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command. Alex Yeoh, who is at index 1, is in `friends`, and David Li, who is at index 4, is not.

   1. Test case: `untag /group friends /members 1,4`<br>
      Expected: No contact is changed, not even Alex Yeoh. The status message shows `One or more contacts are not in this group: David Li (A2)`.

   1. Test case: `untag /group friends /members 1,99`<br>
      Expected: No contact is changed, not even the contact at index 1. The status message shows `Contact index not found: 99`.

   1. Other incorrect untag commands to try: `untag /group friends`, `untag /members 1`, `untag /group Group! /members 1`, `untag /group friends /members 1,1`<br>
      Expected: Similar to previous. No contact is changed, and an error message is shown.

### Importing contacts from a file

1. Importing a valid file

   1. Prerequisites: The app contains the sample data. Create a file named `students.csv` in the folder that contains the JAR file, with the following content:
      ```
      name,class,email
      John Doe,A1,johnd@example.com
      Betsy Crowe,A2,
      ```

   1. Test case: `import students.csv`<br>
      Expected: John Doe and Betsy Crowe are added at the top of the list, in the same order as in the file, so John Doe is above Betsy Crowe. The status message shows `Imported 2 contacts from students.csv`.

   1. Test case: `import students.csv` again<br>
      Expected: No contact is added. The status message shows `Duplicate contact on row 2`.

1. Importing an invalid file

   1. Prerequisites: The app contains the sample data. Create the files in the folder that contains the JAR file as described in each test case.

   1. Test case: `import missing.csv`, where `missing.csv` does not exist<br>
      Expected: The status message shows `File not found at missing.csv`.

   1. Test case: `import bad-name.csv`, where the file has the header `name,class` followed by the row `"Tan, John",A1`<br>
      Expected: No contact is added. The status message shows `Invalid contact data on row 2`. If any row of a file is invalid, none of its contacts is added.

   1. Other incorrect import commands to try: `import`, `import students.txt` (where `students.txt` exists), `import empty.csv` (where `empty.csv` is an empty file)<br>
      Expected: Similar to previous. No contact is added, and an error message is shown.

### Recording attendance

1. Recording attendance successfully

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command. Alex Yeoh is at index 1.

   1. Test case: `attendance 1 /date 01-01-2024 /status present`<br>
      Expected: The status message shows `Attendance for Alex Yeoh on 01-01-2024 recorded as present.`. The attendance record is saved in the `attendanceRecords` of Alex Yeoh in `data/classmates.json`.

   1. Test case: `attendance /name Alex Yeoh /class A1 /date 02-01-2024 /status present`<br>
      Expected: The status message shows `Attendance for Alex Yeoh on 02-01-2024 recorded as present.`. The attendance record is saved in the `attendanceRecords` of Alex Yeoh in `data/classmates.json`.

1. Recording attendance unsuccessfully

   1. Prerequisites: Complete the previous test cases. List all contacts using the `list` command.

   1. Test case: `attendance 1 /date 01-01-2024 /status absent`<br>
      Expected: No record is added. The status message shows `Alex Yeoh already has attendance on 01-01-2024 recorded as present. Use deleteA to remove it first.` (with the status that was recorded before), because Alex Yeoh already has a record for that date.

   1. Test case: `attendance /name Unknown Student /class A1 /date 03-01-2024 /status present`<br>
      Expected: No record is added. The status message shows `Contact not found`.

   1. Other incorrect attendance commands to try: `attendance 1 /date 2024-01-01 /status present`, `attendance 1 /date 03-01-2024 /status sick`, `attendance 1 /date 01-01-2999 /status present`, `attendance 99 /date 03-01-2024 /status present`<br>
      Expected: Similar to previous. No record is added, and an error message is shown.

### Viewing attendance

1. Viewing the attendance of a contact

   1. Prerequisites: The app contains the sample data. Record attendance for Alex Yeoh using the `attendance` command above.

   1. Test case: `VIEWa /name Alex Yeoh /class A1`<br>
      Expected: The case of the command word does not matter. A pop-up window with the heading `Attendance History` opens. It shows `Here is the attendance for Alex Yeoh from class A1`, followed by Alex Yeoh's attendance records, with the most recent date first.

   1. Test case: `viewA /name Bernice Yu /class A1`<br>
      Expected: A pop-up window with the heading `Attendance History` opens. It shows `Here is the attendance for Bernice Yu from class A1`, followed by `No attendance records.`, as Bernice Yu has no attendance records.

1. Viewing the attendance of a contact unsuccessfully

   1. Test case: `viewA /name Unknown Student /class A1`<br>
      Expected: The status message shows `Contact not found`.

   1. Other incorrect viewA commands to try: `viewA /class A1`, `viewA /name Alex Yeoh /class A1 /foo bar`<br>
      Expected: Similar to previous. An error message is shown.

### Deleting attendance

1. Deleting attendance by the index of a displayed contact

   1. Prerequisites: The app contains the sample data. Run `list yeoh` then record two attendance entries using `attendance 1 /date 04-01-2024 /status present` and `attendance 1 /date 05-01-2024 /status late`.

   1. Test case: `deleteA 1 /date 04-01-2024`<br>
      Expected: The attendance entry for 4 January 2024 is removed from Alex Yeoh. The entry for 5 January 2024 and all other contact details remain unchanged. The status message shows `Attendance of present for Alex Yeoh on 04-01-2024 has been deleted`.

1. Deleting attendance by name and class

   1. Prerequisites: The app contains the sample data. Run `list bernice`, followed by `attendance 1 /date 03-01-2024 /status absent`, then run `list david` so that Bernice Yu is not displayed.

   1. Test case: `deleteA /name bernice  yu /class a1 /date 03-01-2024`<br>
      Expected: The attendance entry for 3 January 2024 is removed from Bernice Yu, even though she is not displayed. The status message shows `Attendance of absent for Bernice Yu on 03-01-2024 has been deleted`. The whole address book is searched, and case and extra spaces do not matter.

1. Deleting attendance unsuccessfully

   1. Prerequisites: The app contains the sample data. Run `list yeoh`.

   1. Test case: `deleteA 1 /date 06-01-2024`<br>
      Expected: No attendance entry is deleted. The status message shows `Attendance for this date not found`.

   1. Test case: `deleteA /name Alex Yeoh /class B1 /date 01-01-2024`<br>
      Expected: No attendance entry is deleted. The status message shows `Contact not found` because both the name and class must match an existing contact.

   1. Other incorrect commands to try: `deleteA`, `deleteA 0 /date 01-01-2024`, `deleteA 99 /date 01-01-2024`, `deleteA 1 /date 31-02-2024` (the message says that the date does not exist), `deleteA 1/date 01-01-2024` (the message says that `/date` is missing a space before it), `deleteA 1 /status present`<br>
      Expected: Similar to previous. No attendance entry is deleted and the status message describes the problem.

### Recording and viewing grades

1. Recording and viewing the grades of a contact

   1. Prerequisites: The app contains the sample data. Run `list yeoh`, so that Alex Yeoh is the 1st contact displayed.

   1. Test case: `grade 1 /assessment Quiz #1 /score 80`<br>
      Expected: The status message shows `Grade of 80 recorded for Quiz #1 for student Alex Yeoh in class A1.`, and the card of Alex Yeoh shows `Quiz #1: 80`.

   1. Test case: `VIEWG /name alex  yeoh /class a1`<br>
      Expected: A pop-up window with the heading `Grade History` opens. It shows `Here are the grades for Alex Yeoh from class A1`, followed by `Quiz #1: 80`. The case of the command word, and the case and extra spaces of the name and class, do not matter.

1. Recording and viewing grades unsuccessfully

   1. Test case: `grade 1 /assessment  QUIZ   #1 /score 90`<br>
      Expected: No grade is recorded. The status message shows that the contact already has a grade for the assessment and that `deleteG` removes it, as the case and extra spaces of the assessment name are ignored.

   1. Test case: `viewG /name Unknown Student /class A1`<br>
      Expected: The status message shows `Contact not found`.

   1. Other incorrect commands to try: `grade 1 /assessment Quiz 2 /score 101`, `grade 1 /assessment Quiz 2 /score 90 /foo bar` (a parameter that is not accepted), `viewG /class A1`<br>
      Expected: Similar to previous. No grade is recorded, and the status message describes the problem.

### Deleting grades

1. Deleting a grade by the index of a displayed contact

   1. Prerequisites: The app contains the sample data. Run `list yeoh`, then record two grades using `grade 1 /assessment Quiz #1 /score 80` and `grade 1 /assessment Final Exam /score 95`.

   1. Test case: `deleteG 1 /assessment quiz   #1`<br>
      Expected: The grade for Quiz #1 is removed from Alex Yeoh. The grade for Final Exam and all other contact details remain unchanged. The status message shows `Grade of 80 for Quiz #1 for student Alex Yeoh in class A1 has been deleted`. The case and extra spaces of the assessment name do not matter.

1. Deleting a grade by name and class

   1. Prerequisites: The app contains the sample data. Run `list bernice`, followed by `grade 1 /assessment Assignment 1 /score 90`, then run `list david` so that Bernice Yu is not displayed.

   1. Test case: `deleteG /name bernice  yu /class a1 /assessment assignment 1`<br>
      Expected: The grade for Assignment 1 is removed from Bernice Yu, even though she is not displayed. The status message shows `Grade of 90 for Assignment 1 for student Bernice Yu in class A1 has been deleted`. The whole address book is searched, and case and extra spaces do not matter.

1. Deleting a grade unsuccessfully

   1. Prerequisites: The app contains the sample data. Run `list yeoh`.

   1. Test case: `deleteG 1 /assessment Unknown Assessment`<br>
      Expected: No grade is deleted. The status message shows `Grade for this assessment not found`.

   1. Test case: `deleteG /name Alex Yeoh /class B1 /assessment Quiz #1`<br>
      Expected: No grade is deleted. The status message shows `Contact not found`, because both the name and class must match an existing contact.

   1. Other incorrect commands to try: `deleteG`, `deleteG 0 /assessment Quiz #1`, `deleteG 99 /assessment Quiz #1`, `deleteG 1 /assessment 期末考试`, `deleteG 1 /score 80`<br>
      Expected: Similar to previous. No grade is deleted and the status message describes the problem.

### Clearing all contacts

1. Clearing all contacts

   1. Prerequisites: The app contains the sample data.

   1. Test case: `clear`<br>
      Expected: All contacts are removed from the list. The status message shows `ClassMates has been cleared!`.

   1. Test case: `clear 3`<br>
      Expected: No contact is removed. The status message shows `Invalid command format!` followed by the usage of `clear`.

### Viewing help

1. Opening the help window

   1. Test case: `help`<br>
      Expected: The help window opens and shows the summary of all commands, with one line for each command format. The status message shows `Opened help window.`.

   1. Test case: Click the `Copy user guide URL` button in the help window, then paste into any text field<br>
      Expected: The pasted text is `https://ay2627s1-cs2103-f09-1.github.io/tp/UserGuide.html`.

   1. Test case: `help` again while the help window is open<br>
      Expected: The existing help window is brought to the front. No second help window opens.

### Exiting the application

1. Exiting the application

   1. Test case: `exit`<br>
      Expected: The application window and the help window, if open, close.
