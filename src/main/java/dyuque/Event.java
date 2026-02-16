package dyuque;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task occurring over a date range.
 */
public class Event extends Task{
    /** Date format used when displaying events to the user. */
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate fromDate;
    private final LocalDate toDate;

    /**
     * Creates an event task with the specified description and date range.
     *
     * @param description Description of the task.
     * @param fromDate Start date in ISO-8601 format (YYYY-MM-DD).
     * @param toDate End date in ISO-8601 format (YYYY-MM-DD).
     * @throws DyuqueException If either date is not in ISO-8601 format or the end date is before the start date.
     */
    public Event(String description, String fromDate, String toDate) throws DyuqueException {
        super(description);
        this.fromDate = parseIsoDate(fromDate, "event /from");
        this.toDate = parseIsoDate(toDate, "event /to");

        if (this.toDate.isBefore(this.fromDate)) {
            throw new DyuqueException("End date cannot be before start date.");
        }
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
     * Returns the user-facing string representation of this event task.
     */
    @Override
    public String toString() {
        return "[E]"
                + (isDone ? "[X] " : "[ ] ")
                + description
                + " (from: " + fromDate.format(OUTPUT_FORMAT) + ")"
                + " (to: " + toDate.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the storage-formatted string representation of this event task.
     */
    @Override
    public String toStorageString() {
        return "E | "
                + doneFlag()
                + " | "
                + description
                + " | "
                + fromDate  // Store fromDate in ISO-8601 format
                + " | "
                + toDate;   // Store toDate in ISO-8601 format
    }
}
