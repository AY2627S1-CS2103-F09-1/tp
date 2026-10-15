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
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

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

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

Each `PersonCard` shows the tags of its contact in alphabetical order, ignoring case, with the casing the tag was created with (see `PersonCard#getSortedTags(Person)`).

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

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a contact).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.
* Parameters are introduced by prefixes such as `/name`. `ArgumentTokenizer` only recognizes a prefix that is preceded by a whitespace and followed by a whitespace or the end of the input. A parser can call `ParserUtil#requireNoUnrecognizedPrefixes(String, String, Prefix...)`, which uses `ArgumentTokenizer#findUnrecognizedPrefixes(String, Prefix...)`, to reject tokens that look like prefixes but that its command does not accept. `AddCommandParser`, `DeleteCommandParser` and `TagCommandParser` do this, and `AddCommandParser` reports problems in a fixed order: unrecognized parameters, repeated parameters, text before the first parameter, a missing name, a missing class, then invalid values.
* `ParserUtil#parseGroupName(String)` parses a group name into a `Tag`, and `ParserUtil#parseMemberIndices(String)` parses a comma-separated list such as `1, 3,5` into a list of `Index`. Whitespace around each entry is ignored. The latter reports an empty entry (e.g. a trailing comma) or an entry that is not a positive integer first, checking the entries from left to right, and an index that appears more than once only after that, naming every repeated index.
* A command word does not always map to a single `Command` class. `DeleteCommandParser` returns a `DeleteByIndexCommand` when given an index, which acts on the displayed list, and a `DeleteByNameAndClassCommand` when given `/name` and `/class`, which searches the whole address book using `Person#hasNameAndClass(String, String)`. Both extend the abstract `DeleteCommand`, which holds the shared usage and success messages. `DeleteCommandParser` reports problems in this order: unrecognized parameters, repeated parameters, an invalid index or an index combined with `/name` or `/class`, a missing name, then a missing class. A parameter given without a value counts as missing.
* A parser's argument can be optional. `ListCommandParser` returns a `ListCommand` that lists all contacts when the argument is empty. Otherwise, it returns a `ListCommand` that holds a `PersonMatchesKeyword` predicate built from the whole argument. It accepts any argument, so it never throws a `ParseException`. See [Listing and searching contacts](#listing-and-searching-contacts-feature).
* `AttendanceCommandParser` parses the contact index, date and status for the `attendance` command and creates an `AttendanceCommand`. `ViewAttendanceCommandParser` parses the name and class for the `viewA` command and creates a `ViewAttendanceCommand`. Both parsers use `ArgumentTokenizer` to extract their prefixed arguments.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103-F09-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* identifies a `Person` by its `Name` and `ClassName` (see `Person#isSamePerson(Person)`). Both are compared ignoring case and extra whitespace, so the same name may appear in different classes but not twice in the same class.
* compares `Tag` objects ignoring case, leading and trailing whitespace, and repeated spaces, so `GroupA` and `  groupa ` are the same tag. A `Tag` is 1 to 80 characters long and can contain letters, digits, spaces, hyphens, and underscores. It keeps the casing it was created with, so that is what the UI shows.
* keeps the `Person` objects in the order they are displayed, which is from the most recently added to the oldest (see [Order of contacts](#order-of-contacts-feature)).
* keeps `Person` immutable. `Person#withTag(Tag)` returns a copy with an extra tag, which is how a contact is added to a group, and `Person#hasTag(Tag)` checks whether the contact is already in it.
* stores each contact's attendance records as a list of `Attendance` objects. Each record contains a date and a status (`PRESENT`, `ABSENT` or `LATE`). `Person#withAttendance(Attendance)` returns a copy of the contact with the new record added.
* treats the `Email` of a `Person` as optional. `Person#getEmail()` returns an `Optional<Email>`, which is empty if the contact has no email.
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
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* saves each tag of a contact as a JSON string holding the tag name (see `JsonAdaptedTag`). The name is checked against the rules of `Tag` when it is loaded, and a name with extra spaces is read as the same name without them. A tag name that breaks the rules makes the data file invalid.
* saves each contact's attendance records in the address book JSON file and restores them when the file is loaded (see `JsonAdaptedPerson` and `JsonAdaptedAttendance`).
* saves the contacts in the order they are displayed, and keeps that order when it reads them back (see `JsonSerializableAddressBook#toModelType()`).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Listing and searching contacts feature

#### Implementation

The `list` command shows all contacts, or, when it is given a keyword, only the contacts that match the keyword. It is implemented by `ListCommandParser`, `ListCommand` and `PersonMatchesKeyword`.

* `ListCommandParser#parse(String)` trims the argument. If it is empty, it returns `new ListCommand()`. Otherwise, it returns `new ListCommand(new PersonMatchesKeyword(keyword))`, where `keyword` is the whole trimmed argument, so a full name or class with spaces is one keyword.
* `PersonMatchesKeyword` implements `Predicate<Person>`. Its constructor normalizes the keyword with `StringUtil#toComparisonKey(String)`, which trims it, collapses repeated whitespace and converts it to lower case. `PersonMatchesKeyword#test(Person)` returns true if the keyword is a substring of the normalized name, class name, or any tag name of the contact. The fields, and each tag, are checked separately, so a keyword cannot match across two fields or two tags. Email is not checked. Matching is a plain substring test and not a regular expression, so symbols such as `.` and `/` are matched as typed. A tag name can contain spaces, as in the group `Group A`, so a keyword with several words can match a tag. A blank keyword matches every contact, but `ListCommandParser` never creates one, because a blank argument lists all contacts.
* `ListCommand` holds a `Predicate<Person>`, which is `Model#PREDICATE_SHOW_ALL_PERSONS` when no keyword is given. `ListCommand#execute(Model)` calls `Model#updateFilteredPersonList(Predicate)` with it. This replaces any earlier filter, so `list` always searches the whole address book and a plain `list` clears a previous search.
* Without a keyword, `ListCommand` returns a fixed success message. With a keyword, it returns the number of contacts in the filtered list, using `Messages#MESSAGE_PERSONS_LISTED_OVERVIEW`. An empty result is not an error.
* `ListCommand` only changes which contacts are displayed and never modifies the address book.

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

The `delete` command removes one contact, identified either by its index in the displayed list or by its name and class. `DeleteCommandParser` returns a `DeleteByIndexCommand` or a `DeleteByNameAndClassCommand`, which both extend the abstract `DeleteCommand` (see [Logic component](#logic-component)).

* `DeleteByIndexCommand#execute(Model)` rejects an index that is larger than the displayed list, then deletes the contact at that index.
* `DeleteByNameAndClassCommand#execute(Model)` searches the whole address book, not the displayed list, using `Person#hasNameAndClass(String, String)`. It reports `No contact found` if no contact matches.
* The name and class given to `DeleteByNameAndClassCommand` are not checked against the rules for `Name` and `ClassName`.

#### Design considerations

**Aspect: How the command is structured**

* **Alternative 1 (current choice):** An abstract `DeleteCommand` with one subclass for each way of identifying the contact.
  * Pros: Each `execute` method does one thing and can be tested on its own, and an invalid combination, such as an index together with a name, cannot be represented.
  * Cons: One command word maps to two classes, so the parser must choose between them.
* **Alternative 2:** A single `DeleteCommand` with an optional index and an optional name and class.
  * Pros: Only one command class.
  * Cons: `execute` must branch on which fields are set, and an invalid combination of fields is possible.

**Aspect: Which contacts a delete by name and class searches**

* **Alternative 1 (current choice):** The whole address book.
  * Pros: Works whatever is displayed, so the user does not have to run `list` first.
  * Cons: Can delete a contact that is not currently displayed.
* **Alternative 2:** Only the displayed contacts.
  * Pros: Consistent with deleting by index.
  * Cons: Fails after a search that hides the contact, which defeats the purpose of using a name.

**Aspect: Whether the name and class are validated**

* **Alternative 1 (current choice):** They are only compared with the stored contacts, ignoring case and extra spaces.
  * Pros: The user always gets the same message, `No contact found`, and the rules for `add` can change without affecting `delete`.
  * Cons: A name with a typo in a character that is not allowed gets no hint about the allowed format.
* **Alternative 2:** Validate them with the rules for `add`.
  * Pros: Gives more specific error messages.
  * Cons: Duplicates the validation rules, and adds errors that do not help the user, since no stored contact can have an invalid name.

### Order of contacts feature

#### Implementation

The contacts are displayed from the most recently added to the oldest. The order is kept in the list itself, so the displayed order, the order in memory and the order in the data file are all the same.

* `UniquePersonList#add(Person)` inserts the contact at index 0 instead of at the end. `AddCommand#execute(Model)` reaches it through `Model#addPerson(Person)` and `AddressBook#addPerson(Person)`.
* `ModelManager#addPerson(Person)` also calls `Model#updateFilteredPersonList(Predicate)` with `Model#PREDICATE_SHOW_ALL_PERSONS`, so the new contact is visible at the top even if a search was displayed.
* A filter only hides contacts and never reorders them, so search results are also shown from the most recently added to the oldest.
* `Model#setPerson(Person, Person)` replaces a contact in place, so `edit` and `tag` do not change the position of a contact.
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

### Linking contacts to a group feature

#### Implementation

The `tag` command (`tag /group GROUP_NAME /members INDEX[,INDEX]...`) links the contacts at the given indices of the displayed list to a group. A group is represented as a `Tag` on each of its members, so a contact can be in several groups. It is implemented by `TagCommandParser` and `TagCommand`.

* `TagCommandParser` reports problems in this order: unrecognized parameters, repeated parameters, text before the first parameter, a missing group name, missing members, an invalid group name, then invalid members (see `ParserUtil#parseMemberIndices(String)`). A parameter given without a value counts as missing.
* `TagCommand#execute(Model)` is atomic. It first finds every contact, and reports all indices that are not in the displayed list. It then checks that none of the contacts is already in the group (`Person#hasTag(Tag)`). Only after both checks pass does it replace each contact with `Person#withTag(Tag)` using `Model#setPerson(Person, Person)`. The constructor of `TagCommand` asserts that no index is repeated, because a contact that was already replaced could not be replaced a second time. `TagCommandParser` guarantees this by rejecting repeated indices. The displayed list is left as it was, so the indices stay valid.
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

**Aspect: How the members are identified**

* **Alternative 1 (current choice):** By index in the displayed list.
  * Pros: Short to type, and consistent with `delete` and `attendance`.
  * Cons: The result depends on what is displayed, so the wrong contacts are linked if the list is not what the user expects.
* **Alternative 2:** By name and class.
  * Pros: Does not depend on the displayed list.
  * Cons: A long command for several members, and easy to mistype.

### Importing contacts from a file feature

#### Implementation

The `import` command adds multiple contacts from a CSV file. It is implemented by `ImportCommandParser`, `ImportCommand` and `CsvParser`.

* `ImportCommandParser#parse(String)` removes leading and trailing whitespace from the path while preserving spaces within it. It rejects empty paths and paths containing line breaks.
* `ImportCommand#resolvePath(String)` normalizes the supplied path. An absolute path is used directly. A relative path is resolved from the project working directory. If the application is packaged and run as a JAR file, it is resolved from the folder containing the JAR file.
* `ImportCommand#validatePath(Path)` checks that the path refers to an existing file, has the `.csv` extension and is a readable regular file, and reports the first check that fails in that order. `FileUtil#readFromFile(Path)` then reads the complete file as UTF-8 text. Leading and trailing whitespace surrounding the CSV content is ignored.
* `CsvParser#parse(String)` parses the CSV syntax and validates the header. Its state-based parser supports comma-separated fields, quoted fields, escaped double quotes, embedded line endings, `LF` and `CRLF` record endings, and a UTF-8 byte-order mark at the start of the file.
* The header is converted into a `CsvHeader`, which records the index of each column. Column names are compared without case sensitivity and can appear in any order. The `name` and `class` columns are mandatory, while `email` is optional. Unknown and repeated columns are rejected.
* Each data row is represented by a `CsvRecord`, which retains its row number for error reporting. Blank rows are ignored. `ImportCommand#parsePerson(CsvRecord, CsvHeader)` checks the number of fields and uses the same `ParserUtil` methods as `add` command to validate the name, class and optional email.
* `ImportCommand#parsePersons(ParsedCsv, Model)` validates every contact before modifying the model. It checks for duplicates against both the existing address book and contacts parsed from earlier rows. Validation stops at the first invalid row.
* Only after every non-blank row has been successfully converted does `ImportCommand#execute(Model)` add the contacts. This makes the operation atomic, i.e. if any error occurs, no contact from the file is added.
* Since `Model#addPerson(Person)` inserts a contact at the top of the list, the validated contacts are added from the last row to the first. The imported contacts therefore appear at the top in the same order as in the CSV file.
* CSV syntax and I/O failures are reported as a general import failure, while header, field-count, contact-validation and duplicate errors receive more specific messages.

The CSV import uses `FileUtil` rather than the `Storage` component. The CSV file is external command input, whereas `Storage` is responsible for loading and saving ClassMates's persistent JSON data.

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

The `attendance` command records one attendance entry for a contact on a specified date. It is implemented by `AttendanceCommandParser`, `AttendanceCommand`, and the `Attendance` model class.

* `AttendanceCommandParser#parse(String)` tokenizes the arguments using the `/date` and `/status` prefixes. It reads the contact index from the preamble, checks for missing or repeated parameters, parses the index, validates the date strictly in `DD-MM-YYYY` format, and converts the status to an `Attendance.Status`.
* `AttendanceCommand#execute(Model)` first rejects a date in the future. It then gets the contact at the specified index from `Model#getFilteredPersonList()`. If the index is invalid, it throws a `CommandException`.
* To record attendance, the command creates a new `Attendance` object and calls `Person#withAttendance(Attendance)`. This returns an updated `Person`; the command replaces the old person in the model with `Model#setPerson(Person, Person)`.
* If the contact already has an attendance record for that date, `Person#withAttendance(Attendance)` throws an `IllegalArgumentException`. The command catches it and reports that the attendance record already exists.
* On success, the command returns a message naming the contact, date and status.

#### Design considerations

**Aspect: How attendance updates a contact**

* **Alternative 1 (current choice):** Select the contact by its index in the displayed list.
  * Pros: Consistent with commands such as `delete INDEX` and `tag`; users can first use `list` to narrow down the contacts.
  * Cons: The index depends on the currently displayed list, so users need to check the list before recording attendance.

* **Alternative 2:** Select the contact using `/name NAME /class CLASS`.
  * Pros: Identifies the contact directly and does not depend on the current list or its indices.
  * Cons: Requires entering both the name and class, and is less convenient when recording attendance for several contacts from a displayed list.

### Viewing attendance feature

#### Implementation

The `viewA` command displays all attendance records for a contact. It is implemented by `ViewAttendanceCommandParser` and `ViewAttendanceCommand`.

* `ViewAttendanceCommandParser#parse(String)` uses `ArgumentTokenizer` with the `/name` and `/class` prefixes. It reports unrecognized prefixes first, then repeated prefixes, then text before the first prefix, and finally a missing or blank name or class. It then creates a `ViewAttendanceCommand` with the name and class as plain strings. They are not validated as a `Name` and `ClassName`, in the same way as for `delete /name NAME /class CLASS`.
* `ViewAttendanceCommand#execute(Model)` searches the whole address book for a contact using `Person#hasNameAndClass(String, String)`, which ignores case and extra whitespace. If no contact matches, it throws a `CommandException` with the contact-not-found message.
* If a contact is found, the command formats all of that contact’s attendance records in chronological order and returns them in the command result. If there are no records, it returns the no-attendance-records message instead.

#### Design considerations

**Aspect: How the contact is identified**

* **Alternative 1 (current choice):** Identify the contact by `/name` and `/class`.
  * Pros: Finds the intended contact directly, even if it is not in the currently displayed list or a search is active.
  * Cons: Requires the user to enter both the name and class.

* **Alternative 2:** Identify the contact by its index in the displayed list.
  * Pros: Shorter to enter after displaying or searching for contacts.
  * Cons: The index depends on the current list, so the user may view attendance for a different contact if the list changes.

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

_{more aspects and alternatives to be added}_

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: Teaching assistants in universities who prefer desktop apps over other types of applications. They can type fast, prefer typing to mouse interactions, and are reasonably comfortable using CLI apps.

**Value proposition**: TAs that handle multiple courses and multiple tutorial groups will have many students to manage at the same time, which can make organisation of information difficult. The product can provide fast and organised contact retrieval for TAs.


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
| `* * *`  | TA                                         | view all grades tagged to a student | keep track of the progress of my students                       |
| `* * *`  | TA                                         | view a list of all my contacts | see everyone at a glance                                              |
| `* * *`  | TA                                         | record grades for a specific student | monitor their progress                                          |
| `* * *`  | TA                                         | view a student's attendance  | review their attendance history                                        |
| `* *`    | new user                                   | view a help screen           | learn about the app without going to an external website               |
| `* *`    | TA working with other TAs                  | store colleagues' contacts but clearly distinguished | keep my "work" contacts all in one place but not mix them up |
| `* *`    | TA                                         | edit the information under any contact | keep my information up to date                                |
| `* *`    | TA who might make mistakes                 | receive a clear error message | correct my command                                                    |
| `* *`    | TA (tracking student support)               | attach and edit notes to student profiles | review past consultation details                          |
| `* *`    | experienced user                           | delete contacts in bulk based on tags | clean up my contacts quickly                                  |
| `* *`    | TA                                         | undo an accidental deletion  | restore a contact I removed by mistake                                 |
| `* *`    | TA                                         | sort students consistently   | scan through them quickly                                              |
| `* *`    | TA                                         | favourite certain contacts   | quickly access my most frequent contacts                               |
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

*{More to be added}*

### Use cases

(For all use cases below, the **System** is `ClassMates` and the **Actor** is the `user`, unless specified otherwise)

A use case that needs another use case includes it. An inclusion is written as <u>underlined text</u> followed by the number of the included use case, such as <u>list or search student contacts</u> (UC08).

UC01 and UC09 describe planned features that are not implemented yet. The other use cases describe what the application does now.

**Use case: UC01 - View known student's grades**

*(Planned feature, not implemented yet.)*

**MSS**
1. User requests to view grades of the student.
2. CM displays grades of the student.

   Use case ends.

**Extensions**

* 1a. The command format is invalid or the student does not exist.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.


**Use case: UC02 - Record a student's attendance**

**Guarantees**
* An attendance record is only added if its date and status are valid and the student has no record for that date.

**MSS**
1. User requests to <u>list or search student contacts</u> (UC08) to find the student.
2. CM displays the matching contacts.
3. User requests to record the attendance of a contact in the displayed list, giving the index of the contact, the date and the status (present, absent or late).
4. CM records the attendance and displays a success message.

   Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The command format is invalid, e.g. the index, date or status is missing or repeated, the index is not a positive integer, the date is not a real date in the format DD-MM-YYYY, or the status is not present, absent or late.
  * 3a1. CM displays an error message.

    Use case resumes at step 3.
* 3b. The date is in the future.
  * 3b1. CM displays an error message.

    Use case resumes at step 3.
* 3c. The index is larger than the number of displayed contacts.
  * 3c1. CM displays an error message.

    Use case resumes at step 3.
* 3d. The contact already has an attendance record for the date.
  * 3d1. CM displays an error message.

    Use case resumes at step 3.


**Use case: UC03 - Set up new classes using bulk load**

**Guarantees**
* Contacts are only added if the file and every row in it are valid. If any problem is found, no contact is added.

**MSS**
1. User requests to import contacts from a CSV file, giving the path of the file.
2. CM checks the file, adds all the contacts in it and displays the number of contacts imported.
3. For each class added, user requests to <u>list or search student contacts</u> (UC08) using the class as the keyword.
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


**Use case: UC04 - Add students to groups (by tagging)**

**Guarantees**
* The contacts are only linked to the group if all of them can be linked. If any problem is found, none of them is linked.

**MSS**
1. User requests to <u>list or search student contacts</u> (UC08) to find the students to group.
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


**Use case: UC05 - Delete a student**

**MSS**
1. User requests to <u>list or search student contacts</u> (UC08).
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


**Use case: UC06 - Add a new student contact**

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


**Use case: UC07 - Record grades for a known student**

**MSS**
1. User requests to record a grade, giving the index of a student in the displayed list, an assessment name and a score.
2. CM records the grade for the student and displays a success message.

   Use case ends.

**Extensions**

* 1a. The command format is invalid, e.g. the index, assessment name or score is missing, or the assessment name or score is given more than once.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.
* 1b. The index is not a positive integer, or no student is displayed at that index.
  * 1b1. CM displays an error message.

    Use case resumes at step 1.
* 1c. The assessment name is invalid, e.g. it is empty, longer than 80 characters or has characters other than English letters, digits, spaces and hyphens, or the score is not a number from 0 to 100.
  * 1c1. CM displays an error message.

    Use case resumes at step 1.
* 1d. The student already has a grade for the assessment. Assessment names are compared ignoring case.
  * 1d1. CM keeps the existing grade and displays an error message.

    Use case resumes at step 1.


**Use case: UC08 - List or search student contacts**

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


**Use case: UC09 - View known student's attendance**

*(Planned feature, not implemented yet.)*

**MSS**
1. User requests to view the attendance of the student.
2. CM displays the attendance records of the student in chronological order.

   Use case ends.

**Extensions**

* 1a. The command format is invalid or the student does not exist.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.


**Use case: UC10 - Clear all contacts**

**MSS**
1. User requests to clear all contacts.
2. CM deletes all contacts and displays a success message.

   Use case ends.


**Use case: UC11 - View help**

**MSS**
1. User requests to view help.
2. CM opens the help window, which shows the command summary and a button to copy the link to the user guide.

   Use case ends.

**Extensions**

* 2a. The help window is already open.
  * 2a1. CM brings the existing help window to the front instead of opening another one.

    Use case ends.


**Use case: UC12 - Exit the application**

**MSS**
1. User requests to exit the application.
2. CM saves the window size and position, closes all its windows and exits.

   Use case ends.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2. The application should be usable without an installer.
3. The product should be distributed as a single JAR file, or as a single ZIP file containing the JAR and any necessary files.
4. Contact, attendance, and grade data should be stored locally in a human-editable text file.
5. The application should not require a remote server or database.
6. The application should support one user operating on their own locally stored data.
7. Common operations should be executable with concise commands.
8. The GUI should remain usable at screen resolutions of 1280 × 720 and above.
9. The GUI should work well at 1920 × 1080 and higher, including display scaling of 100% and 125%.
10. Invalid commands or malformed data should not corrupt existing records.
11. The application should be usable without requiring user accounts, external services, or a continuous Internet connection.
12. Core features such as contact management, attendance tracking, and grade tracking should be testable using local sample data.
13. The application should be developed incrementally, with each major update preserving a working version of the product.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **MSS (Main Success Scenario)**: The primary, no-error flow of steps in a use case
* **Student Contact**: A record containing information about a student, such as their name, email, class, tags, attendance records, and grades
* **Module**: Refers to an NUS course (e.g. "CS2103"), not a software module. Disambiguated from the architectural sense of "module" also used elsewhere in this guide
* **Class**: The tutorial or section group a student belongs to within a module (e.g. A1), as entered in a student's class field — distinct from Module, which refers to the course itself (e.g. CS2103)
* **Tag**: Any label attached to a student contact used for grouping except class. Tags are compared ignoring case and extra whitespace
* **Group**: The student contacts that have the same tag, such as the members of a team for an assignment. A group is not stored or modelled as an object of its own, so it exists only as long as at least one contact has the tag. A student can be in several groups, and a group is identified by its tag name, ignoring case
* **Bulk Import**: Loading multiple student contacts at once from a file, typically when setting up a new class
* **Attendance Record**: A single entry marking a student as present/absent/late on a given date
* **Attendance History**: The collection of a student's or class' attendance records over time
* **Assessment**: A named piece of work (e.g. "Assignment 1", "Midterm") that a grade is recorded against for a student
* **Grade**: A single number between 0 to 100 inclusive representing the percentage
* **Grade Entry**: A single recorded grade attached to a student contact
* **Grade History**: The collection of a student's grade entries over time

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

1. _{ more test cases … }_

### Adding a contact

1. Adding a contact successfully

   1. Prerequisites: The app contains the sample data.

   1. Test case: `add /name John Doe /class A1 /email johnd@example.com`<br>
      Expected: John Doe is added at the top of the list. The status message shows `John Doe added to contacts`.

   1. Test case: `add /name Betsy Crowe /class A2`<br>
      Expected: Betsy Crowe is added without an email, at the top of the list above John Doe. The status message shows `Betsy Crowe added to contacts`.

   1. Test case: `add /name Alex Yeoh /class B2`<br>
      Expected: Alex Yeoh is added to class B2 at the top of the list. The same name can be added to a different class.

1. Adding a contact while a search is displayed

   1. Prerequisites: The app contains the sample data.

   1. Test case: `list yeoh`, followed by `add /name Betsy Crowe /class A2`<br>
      Expected: After `list yeoh`, only Alex Yeoh is shown. After the `add` command, all contacts are shown again, with Betsy Crowe at the top, so the new contact is visible.

1. Adding a contact that already exists

   1. Prerequisites: The app contains the sample data, which has Alex Yeoh in class A1.

   1. Test case: `add /name alex  yeoh /class a1`<br>
      Expected: No contact is added. The status message says that the name already exists in the class. Case and extra spaces are ignored.

1. Adding a contact with invalid input

   1. Test case: `add /name John Doe`<br>
      Expected: No contact is added. The status message shows `Command requires a class`.

   1. Test case: `add /class A1`<br>
      Expected: No contact is added. The status message shows `Command requires a name`.

   1. Test case: `add /name John Doe /class A1 /phone 91234567`<br>
      Expected: No contact is added. The status message shows `Unknown parameter. Use /name, /class or /email`.

   1. Test case: `add /name John Doe /name Jane Doe /class A1`<br>
      Expected: No contact is added. The status message shows `Each parameter can only be specified once`.

   1. Test case: `add John Doe /class A1`<br>
      Expected: No contact is added. The status message shows `Invalid command format!` followed by the usage of `add`, because there is text before the first parameter.

   1. Test case: `add /name J0hn /class A1`<br>
      Expected: No contact is added. The status message describes the characters that a name can contain.

   1. Test case: `add /name John Doe /class A1 /email john`<br>
      Expected: No contact is added. The status message describes the valid email format.

### Listing and searching contacts

1. Listing all contacts

   1. Prerequisites: The app contains the sample data (Alex Yeoh, Bernice Yu, Charlotte Oliveiro, David Li, Irfan Ibrahim and Roy Balakrishnan).

   1. Test case: `list`<br>
      Expected: All six contacts are shown, from Alex Yeoh at the top to Roy Balakrishnan at the bottom.

   1. Test case: `list yeoh`, followed by `list`<br>
      Expected: After `list yeoh`, only Alex Yeoh is shown. After `list`, all six contacts are shown again.

1. Searching contacts by keyword

   1. Prerequisites: The app contains the sample data.

   1. Test case: `list ALEX`<br>
      Expected: Only Alex Yeoh is shown. The status message shows that 1 contact is listed. The search ignores case.

   1. Test case: `list li`<br>
      Expected: Charlotte Oliveiro and David Li are shown. A keyword matches part of a name.

   1. Test case: `list a2`<br>
      Expected: Charlotte Oliveiro and David Li are shown. A keyword matches a class.

   1. Test case: `list friend`<br>
      Expected: Alex Yeoh and Bernice Yu are shown. A keyword matches a tag.

   1. Test case: `list alex   yeoh`<br>
      Expected: Only Alex Yeoh is shown. The whole argument is one keyword and extra spaces are ignored.

   1. Test case: `list rid`<br>
      Expected: No contact is shown. The characters of the keyword must appear next to each other, so it does not match the tag `friends`.

   1. Test case: `list zzz`<br>
      Expected: No contact is shown, and the status message shows that 0 contacts are listed.

   1. Test case: `list /class A1`<br>
      Expected: No contact is shown. `list` has no parameters, so the text is searched for as it is typed and does not filter by class.

   1. Test case: `list`, then `tag /group Group A /members 1,2`, then `list group   a`<br>
      Expected: Only the two contacts that were linked to `Group A` are shown. A keyword with several words matches a tag whose name has spaces.

   1. Test case: `list yeoh`, followed by `list david`<br>
      Expected: After `list david`, only David Li is shown. The second search covers all contacts, not only the earlier result.

1. Searching contacts that contain special characters

   1. Prerequisites: The app contains the sample data.

   1. Test case: `add /name Tan s/o Kumar /class C1`, followed by `list s/o`<br>
      Expected: Only Tan s/o Kumar is shown. Characters such as `/` are matched exactly as typed.

1. Order of the listed contacts

   1. Prerequisites: The app contains the sample data.

   1. Test case: `add /name John Doe /class A1`, followed by `add /name Betsy Crowe /class A2`, followed by `list`<br>
      Expected: Betsy Crowe is at the top, followed by John Doe, and then the six sample contacts from Alex Yeoh down. The contacts are shown from the most recently added to the oldest.

   1. Test case: `list a1`, after the test case above<br>
      Expected: John Doe, Alex Yeoh and Bernice Yu are shown, in that order. The search results are in the same order as in the whole list.

### Deleting a contact

1. Deleting a contact while all contacts are being shown

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command.

   1. Test case: `delete 1`<br>
      Expected: Alex Yeoh, the first contact, is deleted from the list. The status message shows `Alex Yeoh in class A1 has been deleted`.

   1. Test case: `delete 0`<br>
      Expected: No contact is deleted. The status message shows `Invalid command format!` followed by the usage of `delete`, because the index is not a positive integer.

   1. Test case: `delete`, or `delete x` where `x` is not a number<br>
      Expected: No contact is deleted. The status message is the same as for `delete 0`.

   1. Test case: `delete 99`, where 99 is larger than the number of displayed contacts<br>
      Expected: No contact is deleted. The status message shows `The contact index provided is invalid.`.

1. Deleting a contact by name and class

   1. Prerequisites: The app contains the sample data.

   1. Test case: `delete /name alex yeoh /class a1`<br>
      Expected: Alex Yeoh is deleted. The status message shows `Alex Yeoh in class A1 has been deleted`. Case is ignored.

   1. Test case: `list david`, followed by `delete /name Bernice Yu /class A1`<br>
      Expected: Bernice Yu is deleted, even though she is not displayed after `list david`. The whole address book is searched.

   1. Test case: `delete /name David Li /class B1`<br>
      Expected: No contact is deleted. The status message shows `No contact found`, because both the name and the class must match, and David Li is in class A2.

   1. Test case: `delete /name David Li`<br>
      Expected: No contact is deleted. The status message shows `Command requires a class`.

   1. Test case: `delete 1 /name David Li /class A2`<br>
      Expected: No contact is deleted. The status message shows `Invalid command format!` followed by the usage of `delete`, because an index cannot be combined with a name and class.

1. _{ more test cases … }_

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

   1. Test case: `tag /group Group B /members 1,1`<br>
      Expected: No contact is linked. The status message shows `Duplicate contact index: 1`.

   1. Test case: `tag /group Group B /members 0`<br>
      Expected: No contact is linked. The status message shows `Contact index must be a positive integer: 0`.

   1. Test case: `tag /group Group B /members 1,,3`<br>
      Expected: No contact is linked. The status message shows `Members cannot contain empty entries`.

   1. Test case: `tag /group Group B`<br>
      Expected: No contact is linked. The status message shows `Missing members`.

   1. Test case: `tag /members 1`<br>
      Expected: No contact is linked. The status message shows `Missing group name`.

   1. Test case: `tag /group Group! /members 1`<br>
      Expected: No contact is linked. The status message starts with `Invalid group name.` and describes the characters that a group name can contain.

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

   1. Test case: `import`<br>
      Expected: The status message shows `Specify file path`.

   1. Test case: `import missing.csv`, where `missing.csv` does not exist<br>
      Expected: The status message shows `File not found at missing.csv`.

   1. Test case: `import students.txt`, where `students.txt` exists<br>
      Expected: The status message shows `Only .csv files are supported`.

   1. Test case: `import missing.txt`, where `missing.txt` does not exist<br>
      Expected: The status message shows `File not found at missing.txt`.

   1. Test case: `import empty.csv`, where `empty.csv` is an empty file<br>
      Expected: The status message shows `File empty`.

   1. Test case: `import no-class.csv`, where the first row of the file is `name,email`<br>
      Expected: The status message shows `CSV header must contain 'name' and 'class' columns`.

   1. Test case: `import short-row.csv`, where the file has the header `name,class,email` followed by the row `Alice Tan,A1`<br>
      Expected: The status message shows `Invalid number of fields on row 2`.

   1. Test case: `import bad-name.csv`, where the file has the header `name,class` followed by the row `J0hn,A1`<br>
      Expected: The status message shows `Invalid contact data on row 2`.

   In every test case above, no contact is added. If any row of a file is invalid, none of its contacts is added.

### Recording attendance

1. Recording attendance successfully

   1. Prerequisites: The app contains the sample data. List all contacts using the `list` command. Alex Yeoh is at index 1.

   1. Test case: `attendance 1 /date 01-01-2024 /status present`<br>
      Expected: The status message shows `Attendance for Alex Yeoh on 01-01-2024 recorded as present.`. The attendance record is saved in the `attendanceRecords` of Alex Yeoh in `data/addressbook.json`.

   1. Test case: `attendance 1 /date 02-01-2024 /status LATE`<br>
      Expected: The status message shows `Attendance for Alex Yeoh on 02-01-2024 recorded as late.`. Case is ignored.

1. Recording attendance unsuccessfully

   1. Prerequisites: Complete the previous test cases. List all contacts using the `list` command.

   1. Test case: `attendance 1 /date 01-01-2024 /status absent`<br>
      Expected: No record is added. The status message shows `Attendance record already exists`, because Alex Yeoh already has a record for that date.

   1. Test case: `attendance 1 /date 2024-01-01 /status present`<br>
      Expected: The status message shows `Invalid date format. Please use the format DD-MM-YYYY.`. The same message is shown for a date that does not exist, such as `30-02-2024`.

   1. Test case: `attendance 1 /date 01-01-2999 /status present`<br>
      Expected: The status message shows `Attendance date cannot be in the future.`.

   1. Test case: `attendance 1 /date 03-01-2024 /status sick`<br>
      Expected: The status message shows `Invalid status. Please use 'present', 'absent' or 'late'.`.

   1. Test case: `attendance 99 /date 03-01-2024 /status present`<br>
      Expected: The status message shows `The contact index provided is invalid.`.

   1. Test case: `attendance 0 /date 03-01-2024 /status present`<br>
      Expected: The status message shows `Index must be a positive integer.`.

   1. Test case: `attendance /date 03-01-2024 /status present`<br>
      Expected: The status message shows `Missing index. Please provide the index of the person.`.

   1. Test case: `attendance 1 /status present`<br>
      Expected: The status message shows `Missing date. Please provide the date of attendance.`.

   1. Test case: `attendance 1 /date 03-01-2024`<br>
      Expected: The status message shows `Missing status. Please provide the attendance status.`.

### Viewing attendance

1. Viewing attendance successfully

   1. Prerequisites: The app contains the sample data. Record attendance for Alex Yeoh using the `attendance` command above.

   1. Test case: `viewA /name Alex Yeoh /class A1`<br>
      Expected: The status message shows `Here is the attendance for Alex Yeoh from class A1`, followed by Alex Yeoh's attendance records in chronological order.

   1. Test case: `viewA /class A1 /name Alex Yeoh`<br>
      Expected: The same attendance records are displayed, because the parameter order does not matter.

1. Viewing attendance for a contact without records

   1. Prerequisites: Choose a sample contact with no attendance records.

   1. Test case: `viewA /name Betsy Crowe /class A2`<br>
      Expected: The status message shows the no-attendance-records message.

1. Viewing attendance unsuccessfully

   1. Test case: `viewA /name Unknown Student /class A1`<br>
      Expected: The status message shows the contact-not-found message.

   1. Test case: `viewA /name Alex Yeoh /class A1 /foo bar`<br>
      Expected: The status message shows `Unknown parameter. Use /name or /class`.

   1. Test case: `viewA /class A1`<br>
      Expected: The status message shows `Command requires a name`.

### Clearing all contacts

1. Clearing all contacts

   1. Prerequisites: The app contains the sample data.

   1. Test case: `clear`<br>
      Expected: All contacts are removed from the list. The status message shows `ClassMates has been cleared!`.

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
