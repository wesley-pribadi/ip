package dyuque;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task with a due date.
 */
public class Deadline extends Task {
    /** Date format used when displaying deadlines to the user. */
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate dueDate;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description Description of the task.
     * @param dueDate Due date in ISO-8601 format (YYYY-MM-DD).
     * @throws DyuqueException If the due date is not in ISO-8601 format.
     */
    public Deadline(String description, String dueDate) throws DyuqueException {
        super(description);
        this.dueDate = parseIsoDate(dueDate, "deadline /by");
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    private static LocalDate parseIsoDate(String dueDateStr, String field) throws DyuqueException {
        try {
            return LocalDate.parse(dueDateStr); // ISO-8601: yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new DyuqueException(
                    "Invalid date format for " + field + ". Please use YYYY-MM-DD (e.g., 2026-12-30).",
                    e
            );
        }
    }

    /**
     * Returns the user-facing string representation of this deadline task.
     */
    @Override
    public String toString() {
        return "[D]"
                + (isDone ? "[X] " : "[ ] ")
                + description
                + " (by: " + dueDate.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the storage-formatted string representation of this deadline task.
     */
    @Override
    public String toStorageString() {
        return "D | "
                + doneFlag()
                + " | "
                + description
                + " | "
                + dueDate; // Store dueDate in ISO-8601 format
    }
}
