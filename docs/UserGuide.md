---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# ClassMates User Guide

ClassMates is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, ClassMates can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your ClassMates.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add /name John Doe /class A1 /email johnd@example.com` : Adds a contact named `John Doe` in class `A1` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add /name NAME`, replace `NAME` with a value such as `John Doe`.

* Each parameter is introduced by a prefix that starts with `/`, such as `/name`.<br>
  The prefix must be separated from the value, and from the preceding parameter, by a space.<br>
  For example, `/name John Doe` is valid, but `/nameJohn Doe` is not.

* Items in square brackets are optional.<br>
  For example, `/name NAME [/email EMAIL]` can be used as `/name John Doe /email johnd@example.com` or as `/name John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[/tag TAG]... ` may be omitted, or written as `/tag friend` or `/tag friend /tag family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `/name NAME /class CLASS`, `/class CLASS /name NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add /name NAME /class CLASS [/email EMAIL]`

* `NAME` must start with a letter and can be at most 80 characters long. It can only contain the English letters `A-Z` and `a-z`, spaces, and the characters `'`, `-`, `.` and `/`. A `/` must be between two letters, as in `Tan s/o Kumar`. Accented and non-English letters are not accepted.
* `CLASS` must start with a letter or a digit and can be at most 80 characters long. It can only contain the English letters `A-Z` and `a-z`, digits, spaces, and the characters `-`, `.` and `_`, as in `Sec 3-2` or `CS2103_T11`.
* A person is identified by their name and class together. The same name can be added to different classes, but not twice to the same class.
* Names and classes are compared ignoring case and extra spaces. For example, `john  tan` in class `a1` is treated as the same person as `John Tan` in class `A1`.
* The email is optional.
* Only `/name`, `/class` and `/email` are accepted, and each can be given at most once.

If the command cannot be carried out, ClassMates shows one of these messages:

Problem | Message
--------|--------
A parameter other than `/name`, `/class` or `/email` is given, such as `/phone` | `Unknown parameter. Use /name, /class or /email`
A parameter is given more than once | `Each parameter can only be specified once`
There is text before the first parameter, such as `add John /class A1` | `Invalid command format!` followed by the usage of `add`
`/name` is missing | `Command requires a name`
`/class` is missing | `Command requires a class`
The name is empty, too long or has characters that are not allowed | `Name cannot be empty`, `Name is too long` or a description of the allowed characters
The class is empty, too long or has characters that are not allowed | `Class name cannot be empty`, `Class name is too long` or a description of the allowed characters
The email is not valid, including `/email` with nothing after it | A description of the valid email format
The same name already exists in the same class | `NAME already exists in class CLASS`

If more than one problem applies, only the first one in the table above is reported.<br>
On success, ClassMates shows `NAME added to contacts`.

Examples:
* `add /name John Doe /class A1 /email johnd@example.com`
* `add /name Betsy Crowe /class A2`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... `

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...
* On success, ClassMates shows `NAME in class CLASS has been deleted`, for example `Alex Yeoh in class A1 has been deleted`.

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

ClassMates automatically saves data after every command. You do not need to save manually.

### Editing the data file

ClassMates data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, ClassMates starts with an empty address book at the next run. The invalid file remains on disk until you run a command (ClassMates saves after every command). Still, we recommend backing up the file before editing it.<br>
Data files from earlier versions of ClassMates, which do not record a class for each person, are also treated as invalid.<br>
Furthermore, certain edits can cause ClassMates to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous ClassMates home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add /name NAME /class CLASS [/email EMAIL]` <br> e.g., `add /name James Ho /class A1 /email jamesho@example.com`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]... `<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Help**   | `help`
