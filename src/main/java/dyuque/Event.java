package dyuque;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task{
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate fromDate;
    private final LocalDate toDate;

    public Event(String description, String fromDate, String toDate) throws DyuqueException {
        super(description);
        this.fromDate = parseIsoDate(fromDate, "event /from");
        this.toDate = parseIsoDate(toDate, "event /to");

        if (this.toDate.isBefore(this.fromDate)) {
            throw new DyuqueException("dyuque.Event end date cannot be before start date.");
        }
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
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

    @Override
    public String toString() {
        return "[E]"
                + (isDone ? "[X] " : "[ ] ")
                + description
                + " (from: " + fromDate.format(OUTPUT_FORMAT) + ")"
                + " (to: " + toDate.format(OUTPUT_FORMAT) + ")";
    }

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
