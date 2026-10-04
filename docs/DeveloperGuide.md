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

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

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
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.
* Parameters are introduced by prefixes such as `/name`. `ArgumentTokenizer` only recognizes a prefix that is preceded by a whitespace and followed by a whitespace or the end of the input. A parser can use `ArgumentTokenizer#findUnrecognizedPrefixes(String, Prefix...)` to reject tokens that look like prefixes but that its command does not accept. `AddCommandParser` does this, and reports problems in a fixed order: unrecognized parameters, repeated parameters, text before the first parameter, a missing name, a missing class, then invalid values.
* `ParserUtil#parseGroupName(String)` parses a group name into a `Tag`, and `ParserUtil#parseMemberIndices(String)` parses a comma-separated list such as `1, 3,5` into a list of `Index`. Whitespace around each entry is ignored. The latter reports an empty entry (e.g. a trailing comma) or an entry that is not a positive integer first, checking the entries from left to right, and an index that appears more than once only after that, naming every repeated index.

#### Tag command
The `tag` command (`tag /group GROUP_NAME /members INDEX[,INDEX]...`) links the contacts at the given indices of the displayed list to a group. A group is represented as a `Tag` on each of its members, so a contact can be in several groups.

* `TagCommandParser` reports problems in this order: unrecognized parameters, repeated parameters, text before the first parameter, a missing group name, missing members, an invalid group name, then invalid members (see `ParserUtil#parseMemberIndices(String)`). A parameter given without a value counts as missing.
* `TagCommand#execute(Model)` is atomic. It first finds every contact, and reports all indices that are not in the displayed list. It then checks that none of the contacts is already in the group (`Person#hasTag(Tag)`). Only after both checks pass does it replace each contact with `Person#withTag(Tag)` using `Model#setPerson(Person, Person)`. The displayed list is left as it was, so the indices stay valid.
* The success message lists the linked contacts in the order the indices were given, and shows the group name as it was typed. Group names are compared ignoring case, but each contact keeps the casing it was linked with.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* identifies a `Person` by its `Name` and `ClassName` (see `Person#isSamePerson(Person)`). Both are compared ignoring case and extra whitespace, so the same name may appear in different classes but not twice in the same class.
* compares `Tag` objects ignoring case, leading and trailing whitespace, and repeated spaces, so `GroupA` and `  groupa ` are the same tag. A `Tag` is 1 to 80 characters long and can contain letters, digits, spaces, hyphens, and underscores. It keeps the casing it was created with, so that is what the UI shows.
* keeps `Person` immutable. `Person#withTag(Tag)` returns a copy with an extra tag, which is how a contact is added to a group, and `Person#hasTag(Tag)` checks whether the contact is already in it.
* treats the `Email` of a `Person` as optional. `Person#getEmail()` returns an `Optional<Email>`, which is empty if the person has no email.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add /name David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

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
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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

**Use case: UC01 - View known student's grades**

**MSS**
1. User requests to view grades of the student.
2. CM displays grades of the student.

   Use case ends. 

**Extensions**

* 1a. The command format is invalid or the student does not exist.
  * 1a1. CM displays an error message.

    Use case resumes at step 1.


**Use case: UC02 - Track known student's attendance**

**MSS**
1. User requests to add attendance record for the student.
2. CM records the attendance for the student.

   Use case ends.
   
**Extensions**
   
* 1a. The command format is invalid or the attendance of the student has already been recorded. 
  * 1a1. CM displays an error message. 

    Use case resumes at step 1.


**Use case: UC03 - Set up new classes using bulk load**

**Guarantees**
* Student records will only be updated if all data is in the correct format.
  
**MSS**
1. User requests to load student data from an external CSV file.
2. CM parses data and adds records of all students in all classes.
3. User confirms bulk loading.
4. For each class added or updated, user requests to view students in that class.
5. CM displays search results for each class. 

    Use case ends.
   
**Extensions**
* 2a. The given CSV file is not formatted correctly. 
  * 2a1. CM terminates the import and displays an error message. 
  * 2a2. User externally modifies the CSV file. 
  
    Use case resumes from step 1.
* 3a. User chooses to cancel bulk loading.

    Use case ends.


**Use case: UC04 - Add students to groups (by tagging)**

**Guarantees**
* Specified students will only be grouped if all of them exist and none of them is already in the group.
* If grouping fails, no student is changed.

**MSS**
1. User requests to add certain students from the displayed list into a group, e.g. `tag /group Group A /members 1,3`.
2. CM adds those students to a group and displays a success message listing the students. 

    Use case ends.
   
**Extensions**
   
* 1a. The command format is invalid, e.g. the group name or the members are missing or repeated, the group name is invalid, or a member is not a positive integer or is given twice.
  * 1a1. CM terminates the grouping and displays an error message naming the problem. 
        
    Use case resumes from step 1.
* 1b. One of the indices does not refer to a student in the displayed list.
  * 1b1. CM terminates the grouping and displays an error message listing the indices that were not found. 
        
    Use case resumes from step 1.
* 1c. One or more of the students are already in the specified group. Group names are compared ignoring case and extra spaces.
  * 1c1. CM terminates the grouping and displays an error message listing those students. 
        
    Use case resumes from step 1.


**Use case: UC05 - Delete a student**

**MSS**
1. User requests to view all students.
2. CM displays a list of all students.
3. User requests to delete a specific student from the list.
4. If the student has records of grouping or attendance, CM requests for confirmation.
5. User confirms deletion.
6. CM deletes the student and displays a success message.

    Use case ends.

**Extensions**
* 2a. The list is empty. 

  Use case ends.
   
* 3a. The given index is invalid.
  * 3a1. CM displays an error message. 
    
    Use case resumes at step 3.
  
* 5a. User chooses to cancel the deletion. 

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
* **Bulk Import**: Loading multiple student contacts at once from a file, typically when setting up a new class
* **Attendance Record**: A single entry marking a student as present/absent/late on a given date
* **Attendance History**: The collection of a student's or class' attendance records over time
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

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
