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

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103-F09-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your ClassMates.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `list John` : Lists the contacts whose name, class or tags contain `John`.

   * `add /name John Doe /class A1 /email johnd@example.com` : Adds a contact named `John Doe` in class `A1` to ClassMates.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `delete /name John Doe /class A1` : Deletes the contact named `John Doe` in class `A1`.

   * `tag /group Group A /members 1,2` : Links the 1st and 2nd contacts shown in the current list to the group `Group A`.

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
  For example, `/members INDEX[,INDEX]...` can be written as `/members 1` or as `/members 1,3,5`.

* Parameters can be in any order.<br>
  For example, if the command specifies `/name NAME /class CLASS`, `/class CLASS /name NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a contact: `add`

Adds a contact to ClassMates.

Format: `add /name NAME /class CLASS [/email EMAIL]`

* `NAME` must start with a letter and can be at most 80 characters long. It can only contain the English letters `A-Z` and `a-z`, spaces, and the characters `'`, `-`, `.` and `/`. A `/` must be between two letters, as in `Tan s/o Kumar`. Accented and non-English letters are not accepted.
* `CLASS` must start with a letter or a digit and can be at most 80 characters long. It can only contain the English letters `A-Z` and `a-z`, digits, spaces, and the characters `-`, `.` and `_`, as in `Sec 3-2` or `CS2103_T11`.
* A contact is identified by the name and class together. The same name can be added to different classes, but not twice to the same class.
* Names and classes are compared ignoring case and extra spaces. For example, `john  tan` in class `a1` is treated as the same contact as `John Tan` in class `A1`.
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

### Listing and searching contacts: `list`

Shows a list of all contacts in ClassMates, or only the contacts that match a keyword.

Format: `list [KEYWORD]`

* If `KEYWORD` is omitted, all contacts are shown.
* If `KEYWORD` is given, only the contacts whose name, class or tags contain it are shown, and the message shows how many contacts were listed.
* The search is case-insensitive; for example, `john` matches `John`.
* Partial matches are included; for example, `jo` matches `John` and `2103` matches the class `CS2103`.
* The characters of `KEYWORD` must appear next to each other; for example, `ric` does not match the tag `friend`.
* A contact is shown if the keyword matches any one of their name, class or tags. Email is not searched.
* Everything after `list` is treated as one keyword, so it can contain several words, such as a full name or a class. Extra spaces are ignored; for example, `list John   Doe` is the same as `list John Doe`.
* A keyword with several words can match a name or a class, but not a tag, as tags are a single word.
* The keyword must match within a single field; for example, it cannot match the end of a name and the start of a class.
* The search always covers all contacts in ClassMates, even if a previous search is still displayed.
* If no contact matches, an empty list is shown.
* Contacts are currently shown in the order they were added.

Examples:
* `list` shows all contacts.
* `list John` shows `John Doe` and any contact in a class or with a tag containing `john`.
* `list CS2103` shows all contacts in a class containing `CS2103`.
* `list friend` shows all contacts with a tag containing `friend`.
* `list John Doe` shows contacts whose name or class contains `John Doe`.

### Deleting a contact: `delete`

Deletes the specified contact from ClassMates.

Format: `delete INDEX` or `delete /name NAME /class CLASS`

* `delete INDEX` deletes the contact at the specified `INDEX`.
  * The index refers to the index number shown in the displayed contact list.
  * The index **must be a positive integer** 1, 2, 3, ...
* `delete /name NAME /class CLASS` deletes the contact with that name in that class.
  * Both the name and the class must match, because the same name can exist in different classes.
  * Case and extra spaces are ignored. For example, `john  tan` in class `a1` matches `John Tan` in class `A1`.
  * All contacts in ClassMates are searched, even if only some contacts are displayed, for example after a `list` with a keyword.
  * The name and class are not checked against the rules for adding a contact. If nothing matches, ClassMates shows `No contact found`.
* An index cannot be combined with `/name` or `/class` in the same command.
* On success, ClassMates shows `NAME in class CLASS has been deleted`, for example `Alex Yeoh in class A1 has been deleted`.

If the command cannot be carried out, ClassMates shows one of these messages:

Problem | Message
--------|--------
A parameter other than `/name` or `/class` is given, such as `/phone` | `Unknown parameter. Use /name or /class`
A parameter is given more than once | `Each parameter can only be specified once`
No index or parameters are given, the index is not a positive integer, or an index is combined with parameters | `Invalid command format!` followed by the usage of `delete`
`/name` is missing or has no value | `Command requires a name`
`/class` is missing or has no value | `Command requires a class`
No contact has the given name and class | `No contact found`
The index is larger than the number of contacts displayed | `The contact index provided is invalid.`

If more than one problem applies, only the first one in the table above is reported.

Examples:
* `list` followed by `delete 2` deletes the 2nd contact in ClassMates.
* `list Betsy` followed by `delete 1` deletes the 1st contact in the results of the `list Betsy` command.
* `list Betsy` followed by `delete /name Alex Yeoh /class A1` deletes Alex Yeoh in class A1, even though Alex is not in the results of the `list Betsy` command.

### Linking contacts to a group: `tag`

Links one or more contacts to a group, so that you can tell which contacts belong together, such as the members of a team.

Format: `tag /group GROUP_NAME /members INDEX[,INDEX]...`

* `GROUP_NAME` must be 1 to 80 characters long. It can only contain the English letters `A-Z` and `a-z`, digits, spaces, hyphens and underscores, as in `Group A` or `team-1_alpha`.
* Spaces at the start and end of `GROUP_NAME` are ignored, and repeated spaces inside it are treated as a single space.
* Group names are compared ignoring case, so `Group A` and `group  a` are the same group. Each contact shows the group name as you typed it when you linked that contact.
* `INDEX` refers to the index number shown in the displayed contact list, so after a command such as `list Betsy`, it refers to the contacts in the results. Each index **must be a positive integer** 1, 2, 3, ...
* The indices are separated by commas, and spaces around a comma are ignored, so `1,3,5` and `1, 3, 5` are the same. A comma at the start or end, or two commas in a row, is not allowed, and each index can be given only once.
* The group is shown as a label on each linked contact, in alphabetical order ignoring case.
* A contact can be in several groups, but cannot be linked to the same group twice.
* The command is all or nothing. If any problem is found, none of the contacts are linked.
* Only `/group` and `/members` are accepted, and each can be given at most once.

If the command cannot be carried out, ClassMates shows one of these messages:

Problem | Message
--------|--------
A parameter other than `/group` or `/members` is given, such as `/name` | `Unknown parameter. Use /group or /members`
A parameter is given more than once | `Each parameter can only be specified once`
There is text before the first parameter, such as `tag GroupA 1 3` | `Invalid command format!` followed by the usage of `tag`
`/group` is missing or has nothing after it | `Missing group name`
`/members` is missing or has nothing after it | `Missing members`
The group name is empty, too long or has characters that are not allowed | `Invalid group name.` followed by a description of the allowed characters
An entry in `/members` is empty, such as in `1,,3` or `1,3,` | `Members cannot contain empty entries`
An entry in `/members` is not a positive integer, such as `0`, `-1`, `abc` or `1 3` | `Contact index must be a positive integer: ENTRY`
The same index is given more than once | `Duplicate contact index: INDEX`
An index does not match a contact in the displayed list | `Contact index not found: INDEX`
One or more of the contacts are already in the group | `One or more contacts are already in this group: NAME (CLASS)`

If more than one problem applies, only the first one in the table above is reported.<br>
If several indices are repeated or not found, or several contacts are already in the group, all of them are listed in the message, separated by commas.<br>
On success, ClassMates shows `Contacts successfully linked to group GROUP_NAME:` followed by the name and class of each linked contact, in the order that the indices were given.

Examples:
* `tag /group Group A /members 1,3,5` links the 1st, 3rd and 5th contacts in the displayed list to `Group A`.
* `list Betsy` followed by `tag /group Team 2 /members 1` links the 1st contact in the results of the `list Betsy` command to `Team 2`.

### Importing from external file: `import`

Imports contacts specified in a CSV file in bulk.

Format: `import PATH`
* The path to the file is not surrounded by quotes.
* The path can be either absolute (e.g. `C:/Users/tom/Downloads/data/student.csv`) or relative (e.g. `data/students.csv`)
* Accepted path separator(s) depends on the operating system:
  * On Windows, both forward and backward slashes (`/` and `\`) are accepted.
  * However, on Unix and macOS systems, only forward slash `/` is accepted.
* The command is fail-fast, i.e. the whole import is canceled upon the first error.

On success, ClassMates shows `Imported NUMBER contacts from FILEPATH`, e.g. `Imported 10 contacts from data/students.csv`.

The CSV file must follow this format:
* The first row is a header row. It must contain a `name` column and a `class` column, and it can contain an optional `email` column.
* The column names are not case-sensitive, and the columns can be in any order.
* No other columns are accepted, and no column can appear more than once.
* Every other row is one contact, with its values in the same order as the header. Each row must have as many values as the header.
* Each value follows the same rules as the matching parameter of `add`, so a name or class that `add` would reject is also rejected in the file.
* The `email` value of a contact can be left blank if the contact has no email.
* Blank rows are ignored.
* The file must be saved in UTF-8 encoding.

For example, a valid file looks like this:
```
name,class,email
John Doe,A1,johnd@example.com
Betsy Crowe,A2,
```

A relative path is taken from the folder that contains the JAR file.

If the command cannot be carried out, ClassMates shows one of these messages:

| **Problem**                                                                                        | **Message**                                          |
|----------------------------------------------------------------------------------------------------|------------------------------------------------------|
| No file path is provided                                                                           | `Specify file path`                                  |
| The file does not exist                                                                            | `File not found at <filepath>`                       |
| The file does not use the `.csv` extension                                                         | `Only .csv files are supported`                      |
| The file is empty or contains only a header row                                                    | `File empty`                                         |
| The CSV header is invalid or missing required columns                                              | `CSV header must contain 'name' and 'class' columns` |
| A row has an invalid number of fields                                                              | `Invalid number of fields on row <row number>`       |
| A contact duplicates an existing contact or an earlier imported contact                            | `Duplicate contact on row <row number>`              |
| A row contains invalid contact data                                                                | `Invalid contact data on row <row number>`           |
| The file is unreadable, is a directory, contains malformed CSV syntax, or another I/O error occurs | `Failed to import from <filepath>`                   |


### Recording attendance: `attendance`

Records whether a contact was present, absent or late on a date.

Format: `attendance INDEX /date DATE /status STATUS`

* `INDEX` refers to the index number shown in the displayed contact list, so after a command such as `list Betsy`, it refers to the contacts in the results. The index **must be a positive integer** 1, 2, 3, ...
* `DATE` must be in the format `DD-MM-YYYY`, such as `16-09-2026`. It must be a real calendar date, so `30-02-2026` is not accepted, and each part must have the full number of digits, so `16-9-2026` is not accepted. It cannot be in the future.
* `STATUS` must be `present`, `absent` or `late`. Case is ignored, so `Absent` is the same as `absent`.
* A contact can have only one attendance record for each date. A date that already has a record for the contact cannot be recorded again, but records for different dates are separate.
* `/date` and `/status` can each be given at most once.

If the command cannot be carried out, ClassMates shows one of these messages:

Problem | Message
--------|--------
The index is missing | `Missing index. Please provide the index of the person.`
`/date` is missing or has nothing after it | `Missing date. Please provide the date of attendance.`
`/status` is missing or has nothing after it | `Missing status. Please provide the attendance status.`
A parameter is given more than once | `Each parameter can only be specified once`
The index is not a positive integer | `Index must be a positive integer.`
The date is not a real date in the format `DD-MM-YYYY` | `Invalid date format. Please use the format DD-MM-YYYY.`
The status is not `present`, `absent` or `late` | `Invalid status. Please use 'present', 'absent' or 'late'.`
The date is in the future | `Attendance date cannot be in the future.`
The index is larger than the number of contacts displayed | `The contact index provided is invalid.`
The contact already has a record for the date | `Attendance record already exists`

If more than one problem applies, only the first one in the table above is reported.<br>
On success, ClassMates shows `Attendance for NAME on DATE recorded as STATUS.`, for example `Attendance for Alex Yeoh on 16-09-2026 recorded as absent.`

Examples:
* `attendance 5 /date 16-09-2026 /status absent` records the 5th contact in the displayed list as absent on 16 September 2026.
* `list Betsy` followed by `attendance 1 /date 16-09-2026 /status late` records the 1st contact in the results of the `list Betsy` command as late on 16 September 2026.

### Clearing all entries: `clear`

Clears all contacts from ClassMates.

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
If your changes make the data file invalid, ClassMates starts with no contacts at the next run. The invalid file remains on disk until you run a command (ClassMates saves after every command). Still, we recommend backing up the file before editing it.<br>
Data files from earlier versions of ClassMates, which do not record a class for each contact, are also treated as invalid.<br>
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
**Attendance** | `attendance INDEX /date DATE /status STATUS`<br> e.g., `attendance 5 /date 16-09-2026 /status absent`
**Clear**  | `clear`
**Delete** | `delete INDEX` or `delete /name NAME /class CLASS`<br> e.g., `delete 3`, `delete /name John Tan /class A1`
**Tag**    | `tag /group GROUP_NAME /members INDEX[,INDEX]...`<br> e.g., `tag /group Group A /members 1,3,5`
**List**   | `list [KEYWORD]`<br> e.g., `list John`
**Import** | `import PATH`<br> e.g., `import data/students.csv`
**Help**   | `help`
