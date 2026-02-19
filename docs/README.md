# Dyuque User Guide

<!-- Product screenshot goes here -->

Dyuque is a **desktop task manager chatbot** for keeping track of todos, deadlines, and events. Interact with it through a simple text-based chat interface.

---

## Table of Contents

- [Quick Start](#quick-start)
- [Managing Tasks](#managing-tasks)
  - [Adding a todo: `todo`](#adding-a-todo-todo)
  - [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an event: `event`](#adding-an-event-event)
  - [Deleting a task: `delete`](#deleting-a-task-delete)
- [Changing Task State](#changing-task-state)
  - [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
  - [Unmarking a task: `unmark`](#unmarking-a-task-unmark)
- [Undoing Changes](#undoing-changes)
  - [Undoing the last action: `undo`](#undoing-the-last-action-undo)
- [Viewing and Searching](#viewing-and-searching)
  - [Listing all tasks: `list`](#listing-all-tasks-list)
  - [Finding tasks: `find`](#finding-tasks-find)
  - [Viewing help: `help`](#viewing-help-help)
- [Exiting](#exiting)
  - [Exiting Dyuque: `bye`](#exiting-dyuque-bye)
- [Command Summary](#command-summary)

---

## Quick Start

1. Ensure you have **Java 17** or later installed.
2. Download the latest `dyuque.jar` from the [releases page]("https://github.com/wesley-pribadi/ip/releases").
3. Open a terminal in the folder containing the jar and run:
   ```
   java -jar dyuque.jar
   ```
4. Type a command and press **Enter** to interact with Dyuque.
5. Refer to the [Command Summary](#command-summary) below for all available commands.

> **Save file:** Dyuque automatically saves your tasks to `dyuque_data/dyuque.txt` in the same directory as the jar. Tasks are loaded automatically on the next startup.

---

## Managing Tasks

### Adding a todo: `todo`

Adds a task with no associated date.

**Format:** `todo <description>` (also accepted: `t`)

Example: `todo buy groceries`

```
Added:
[T][ ] buy groceries

You now have (1) tasks.
```

---

### Adding a deadline: `deadline`

Adds a task with a due date.

**Format:** `deadline <description> /by <YYYY-MM-DD>` (also accepted: `d`)

> Dates must be in **ISO-8601 format**: `YYYY-MM-DD` (e.g., `2026-12-30`).

Example: `deadline submit report /by 2026-12-30`

```
Added:
[D][ ] submit report (by: Jan 03 2026)

You now have (2) tasks.
```

---

### Adding an event: `event`

Adds a task occurring over a date range.

**Format:** `event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>` (also accepted: `e`)

> Dates must be in **ISO-8601 format**: `YYYY-MM-DD`. The end date cannot be before the start date.

Example: `event team offsite /from 2026-01-10 /to 2026-01-12`

```
Added:
[E][ ] team offsite (from: Jan 10 2026 to: Jan 12 2026)

You now have (3) tasks.
```

---

### Deleting a task: `delete`

Permanently removes the task at the given index. Use `list` to find a task's index.

**Format:** `delete <index>` (also accepted: `remove`, `rm`)

Example: `delete 1`

```
Removed:
[T][ ] buy groceries

You now have (2) tasks.
```

---

## Changing Task State

### Marking a task as done: `mark`

Marks the task at the given index as completed.

**Format:** `mark <index>` (also accepted: `m`)

Example: `mark 1`

```
Nice! I've marked this task as done:
[D][X] submit report (by: Jan 03 2026)
```

---

### Unmarking a task: `unmark`

Marks the task at the given index as not yet completed.

**Format:** `unmark <index>` (also accepted: `um`)

Example: `unmark 1`

```
OK, I've marked this task as not done yet:
[D][ ] submit report (by: Jan 03 2026)
```

---

## Undoing Changes

### Undoing the last action: `undo`

Reverses the most recent command that changed task data. This covers all commands from [Managing Tasks](#managing-tasks) and [Changing Task State](#changing-task-state). `undo` can be used multiple times to step back through the history one action at a time.

> **Note:** Undo history is not persisted — it resets when Dyuque is restarted.

**Format:** `undo`

Example: `undo` (after a `delete`)

```
Added:
[T][ ] buy groceries

You now have (3) tasks.
```

If there is nothing left to undo:

```
[ERROR] Nothing to undo.
```

---

## Viewing and Searching

### Listing all tasks: `list`

Shows all tasks currently stored, with their index numbers and completion status.

**Format:** `list` (also accepted: `ls`)

Example: `list`

```
You have (3) tasks:

1. [T][ ] buy groceries
2. [D][X] submit report (by: Jan 03 2026)
3. [E][ ] team offsite (from: Jan 10 2026 to: Jan 12 2026)
```

---

### Finding tasks: `find`

Searches for tasks whose description contains the given keyword.

**Format:** `find <keyword>` (also accepted: `f`)

Example: `find report`

```
You have (1) matching tasks:

1. [D][X] submit report (by: Jan 03 2026)
```

---

### Viewing help: `help`

Displays the list of available commands and their syntax.

**Format:** `help`

---

## Exiting

### Exiting Dyuque: `bye`

Saves all tasks and exits the application.

**Format:** `bye` (also accepted: `exit`)

```
Goodbye, hope to see you again soon!
```

---

## Command Summary

| Action       | Format                                                                                                             | Aliases        |
|--------------|--------------------------------------------------------------------------------------------------------------------|----------------|
| **Todo**     | `todo <description>` — e.g. `todo buy milk`                                                                        | `t`            |
| **Deadline** | `deadline <description> /by <YYYY-MM-DD>` — e.g. `deadline assignment /by 2026-03-01`                              | `d`            |
| **Event**    | `event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>` — e.g. `event hackathon /from 2026-04-10 /to 2026-04-11` | `e`            |
| **Delete**   | `delete <index>` — e.g. `delete 3`                                                                                 | `remove`, `rm` |
| **Mark**     | `mark <index>` — e.g. `mark 2`                                                                                     | `m`            |
| **Unmark**   | `unmark <index>` — e.g. `unmark 2`                                                                                 | `um`           |
| **Undo**     | `undo`                                                                                                             |                |
| **List**     | `list`                                                                                                             | `ls`           |
| **Find**     | `find <keyword>` — e.g. `find report`                                                                              | `f`            |
| **Help**     | `help`                                                                                                             |                |
| **Exit**     | `bye`                                                                                                              | `exit`         |
